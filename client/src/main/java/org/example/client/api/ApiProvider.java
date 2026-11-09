package org.example.client.api;

import java.io.IOException;

/**
 * Nơi duy nhất chọn cài đặt ServerApi.
 * Mặc định dùng mock. Chạy với server thật: thêm tham số JVM -Dauction.server=localhost:9999
 */
public final class ApiProvider {

    private static final ServerApi API = create();

    private ApiProvider() {}

    public static ServerApi get() {
        return API;
    }

    private static ServerApi create() {
        String server = System.getProperty("auction.server");
        if (server == null || server.isBlank()) {
            return new MockServerApi();
        }
        String[] parts = server.split(":");
        ServerConnection connection = new ServerConnection(parts[0], Integer.parseInt(parts[1]));
        try {
            connection.connect();
        } catch (IOException e) {
            throw new IllegalStateException("Không kết nối được tới " + server, e);
        }
        return new SocketServerApi(connection);
    }
}