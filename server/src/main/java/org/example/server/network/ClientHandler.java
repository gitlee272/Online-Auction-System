package org.example.server.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final SubscriberRegistry registry;
    private PrintWriter out;
    private BufferedReader in;
    private final RequestDispatcher dispatcher = new RequestDispatcher();

    public ClientHandler(Socket socket, SubscriberRegistry registry) {
        this.socket = socket;
        this.registry = registry;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Tham số true bật chế độ tự động flush (đẩy data đi luôn không cần gọi flush thủ công)
            out = new PrintWriter(socket.getOutputStream(), true);

            String jsonRequest;
            
            // Lắng nghe dữ liệu JSON từ Client gửi lên
            while ((jsonRequest = in.readLine()) != null) {
                System.out.println("Received: " + jsonRequest);
                // Lắng nghe dữ liệu JSON từ Client gửi lên
while ((jsonRequest = in.readLine()) != null) {
    System.out.println("Received from client: " + jsonRequest);

    // Chuyển chuỗi thô cho Dispatcher phân tích và nhận lại chuỗi kết quả
    String jsonResponse = dispatcher.dispatch(jsonRequest, this);

    // Gửi trả kết quả về cho Client qua đường truyền mạng
    out.println(jsonResponse);
}
                // TODO (Phần tiếp theo cậu sẽ code):
                // 1. Chuyển chuỗi jsonRequest vào RequestDispatcher
                // 2. Dispatcher gọi đến Service để lấy data/ghi db
                // 3. Dispatcher trả về chuỗi jsonResponse
                // 4. out.println(jsonResponse); để gửi trả lại kết quả cho Client
            }
            
        } catch (IOException e) {
            System.err.println("Connection lost/error: " + e.getMessage());
        } finally {
            // Khi client ngắt kết nối (hoặc mạng lỗi), bắt buộc phải dọn dẹp tài nguyên
            cleanup();
        }
    }

    // Hàm public này để SubscriberRegistry gọi tới khi cần đẩy dữ liệu realtime
    public void sendMessage(String jsonMessage) {
        if (out != null) {
            out.println(jsonMessage); 
        }
    }

    private void cleanup() {
        // Xóa client khỏi danh sách theo dõi
        registry.removeClient(this); 
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
}