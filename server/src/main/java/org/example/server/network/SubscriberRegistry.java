package org.example.server.network;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
public class SubscriberRegistry {
    // Sử dụng CopyOnWriteArrayList để tránh ConcurrentModificationException khi duyệt mảng đẩy realtime
    private final List<ClientHandler> activeClients = new CopyOnWriteArrayList<>();

    public void addClient(ClientHandler client) {
        activeClients.add(client);
        System.out.println("[Registry] Client connected. Total active: " + activeClients.size());
    }

    public void removeClient(ClientHandler client) {
        activeClients.remove(client);
        System.out.println("[Registry] Client disconnected. Total active: " + activeClients.size());
    }

    // Hàm này dùng để bắn sự kiện realtime (vd: có người vừa đấu giá thành công) về tất cả Client
    public void broadcast(String jsonMessage) {
        for (ClientHandler client : activeClients) {
            client.sendMessage(jsonMessage);
        }
    }
}