package org.example.client.api;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.example.common.protocol.Action;
import org.example.common.protocol.ErrorCode;
import org.example.common.protocol.JsonCodec;
import org.example.common.protocol.Request;
import org.example.common.protocol.Response;

/** Server giả cực nhỏ: chỉ PING và LOGIN (mật khẩu 123456). Chạy hàm main để thử client. */
public final class FakeServer {

    private FakeServer() {}

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(9999)) {
            System.out.println("FakeServer lắng nghe cổng 9999");
            while (true) {
                Socket s = server.accept();
                new Thread(() -> serve(s)).start();
            }
        }
    }

    private static void serve(Socket s) {
        try (s;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true, StandardCharsets.UTF_8)) {
            String line;
            while ((line = in.readLine()) != null) {
                out.println(JsonCodec.toJson(answer(line)));
            }
        } catch (IOException ignored) {
            // client ngắt
        }
    }

    private static Response answer(String line) {
        Request r = JsonCodec.parseRequest(line);
        if (r.getAction() == Action.PING) {
            JsonObject d = new JsonObject();
            d.addProperty("pong", true);
            return Response.ok(r.getId(), d);
        }
        if (r.getAction() == Action.LOGIN) {
            String pass = r.getData().get("password").getAsString();
            if (!"123456".equals(pass)) {
                return Response.error(r.getId(), ErrorCode.INVALID_CREDENTIALS,
                        "Sai tên đăng nhập hoặc mật khẩu");
            }
            JsonObject d = new JsonObject();
            d.addProperty("token", "t-1");
            d.addProperty("userId", 1);
            d.addProperty("username", r.getData().get("username").getAsString());
            d.addProperty("role", "BIDDER");
            return Response.ok(r.getId(), d);
        }
        return Response.error(r.getId(), ErrorCode.UNKNOWN_ACTION, "Chưa hỗ trợ");
    }
}