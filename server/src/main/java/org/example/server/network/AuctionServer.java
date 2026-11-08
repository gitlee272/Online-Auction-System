package org.example.server.network;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuctionServer {
    private static final int PORT = 8080;
    // Giới hạn server phục vụ tối đa 100 client cùng lúc
    private static final int MAX_THREADS = 100; 
    
    private final SubscriberRegistry registry;
    private final ExecutorService threadPool;

    public AuctionServer() {
        this.registry = new SubscriberRegistry();
        this.threadPool = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Auction Server is running on port " + PORT);
            
            while (true) {
                // Main thread bị chặn (blocking) tại đây cho đến khi có client mới kết nối
                Socket clientSocket = serverSocket.accept(); 
                
                // Khởi tạo một Worker (ClientHandler) cho socket mới
                ClientHandler handler = new ClientHandler(clientSocket, registry);
                registry.addClient(handler);
                
                // Đẩy Worker vào ThreadPool để chạy song song, Main thread lập tức quay lại vòng lặp accept()
                threadPool.submit(handler);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
    
    // Hàm main để test chạy server độc lập
    public static void main(String[] args) {
        new AuctionServer().start();
    }
}

    

