package org.example.client.api;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.example.common.protocol.Action;
import org.example.common.protocol.ErrorCode;
import org.example.common.protocol.Event;
import org.example.common.protocol.JsonCodec;
import org.example.common.protocol.ProtocolException;
import org.example.common.protocol.Request;
import org.example.common.protocol.Response;

/**
 * Kết nối socket tới server. Một thread riêng đọc liên tục từng dòng JSON:
 * có id thì là response (hoàn thành CompletableFuture tương ứng), có event thì chuyển cho listener.
 */
public class ServerConnection {

    private static final long TIMEOUT_SECONDS = 10;

    private final String host;
    private final int port;
    private final Map<String, CompletableFuture<Response>> pending = new ConcurrentHashMap<>();
    private volatile Consumer<Event> eventListener = e -> {};
    private volatile Runnable disconnectListener = () -> {};
    private volatile boolean connected;

    private Socket socket;
    private PrintWriter writer;

    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        if (connected) {
            return;
        }
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        connected = true;
        Thread t = new Thread(() -> readLoop(reader), "server-reader");
        t.setDaemon(true);   // không giữ ứng dụng sống khi đóng cửa sổ
        t.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setEventListener(Consumer<Event> listener) {
        this.eventListener = listener == null ? e -> {} : listener;
    }

    public void setDisconnectListener(Runnable listener) {
        this.disconnectListener = listener == null ? () -> {} : listener;
    }

    /** Gửi request, trả về future hoàn thành khi có response (hoặc lỗi/timeout). */
    public CompletableFuture<Response> send(Action action, String token, JsonObject data) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        if (!connected) {
            future.completeExceptionally(new ApiException("CONNECTION_LOST", "Chưa kết nối tới máy chủ"));
            return future;
        }
        String id = UUID.randomUUID().toString();
        pending.put(id, future);
        String line = JsonCodec.toJson(new Request(id, action, token, data));
        synchronized (this) {   // nhiều thread có thể gửi cùng lúc
            writer.println(line);
            if (writer.checkError()) {
                pending.remove(id);
                future.completeExceptionally(new ApiException("CONNECTION_LOST", "Mất kết nối tới máy chủ"));
                return future;
            }
        }
        return future
                .orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .whenComplete((r, e) -> pending.remove(id));
    }

    private void readLoop(BufferedReader reader) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                handle(line);
            }
        } catch (IOException ignored) {
            // socket đóng: rơi xuống phần dọn dẹp
        } finally {
            close();
        }
    }

    private void handle(String line) {
        try {
            JsonObject message = JsonCodec.parseObject(line);
            if (JsonCodec.isEvent(message)) {
                eventListener.accept(JsonCodec.toEvent(message));
                return;
            }
            Response response = JsonCodec.toResponse(message);
            CompletableFuture<Response> future = pending.remove(response.getId());
            if (future != null) {
                future.complete(response);
            }
        } catch (ProtocolException ignored) {
            // bỏ qua dòng hỏng, đọc tiếp
        }
    }

    public synchronized void close() {
        if (!connected) {
            return;
        }
        connected = false;
        try {
            socket.close();
        } catch (IOException ignored) {
            // không làm gì thêm
        }
        // Báo lỗi cho mọi request đang chờ.
        pending.values().forEach(f ->
                f.completeExceptionally(new ApiException("CONNECTION_LOST", "Mất kết nối tới máy chủ")));
        pending.clear();
        disconnectListener.run();
    }

    /** Dùng cho map ErrorCode trong SocketServerApi. */
    static String codeName(ErrorCode code) {
        return code == null ? "INTERNAL_ERROR" : code.name();
    }
}