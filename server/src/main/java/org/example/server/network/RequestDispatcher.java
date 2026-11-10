package org.example.server.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class RequestDispatcher {
    private final Gson gson;

    public RequestDispatcher() {
        this.gson = new Gson();
    }

    // Hàm tổng đài viên: Hứng JSON thô -> Trả về JSON kết quả
    public String dispatch(String jsonRequest, ClientHandler client) {
        try {
            // 1. Phân tích (Parse) chuỗi thô thành Đối tượng JSON
            JsonObject requestObj = gson.fromJson(jsonRequest, JsonObject.class);
            
            // Kiểm tra xem Client có gửi thiếu trường "action" không
            if (!requestObj.has("action")) {
                return "{\"status\": \"ERROR\", \"message\": \"Thiếu action\"}";
            }

            // Trích xuất action và payload
            String action = requestObj.get("action").getAsString();
            JsonObject payload = requestObj.has("payload") ? requestObj.getAsJsonObject("payload") : new JsonObject();

            // 2. Điều hướng tới các hàm xử lý tương ứng
            switch (action) {
                case "LOGIN":
                    return handleLogin(payload);
                    
                case "CREATE_AUCTION":
                    return handleCreateAuction(payload);
                    
                case "PLACE_BID":
                    return handlePlaceBid(payload, client);
                    
                default:
                    return "{\"status\": \"ERROR\", \"message\": \"Action không hợp lệ: " + action + "\"}";
            }
        } catch (JsonSyntaxException e) {
            // Lỗi này xảy ra khi Client gửi lên một chuỗi không đúng định dạng JSON
            return "{\"status\": \"ERROR\", \"message\": \"Sai định dạng JSON\"}";
        } catch (Exception e) {
            return "{\"status\": \"ERROR\", \"message\": \"Lỗi máy chủ nội bộ\"}";
        }
    }

    // --- CÁC HÀM GỌI TỚI TẦNG SERVICE (CỦA TRƯỜNG & ĐĂNG) ---

    private String handleLogin(JsonObject payload) {
        // Bóc tách dữ liệu từ payload
        String username = payload.has("username") ? payload.get("username").getAsString() : "";
        String password = payload.has("password") ? payload.get("password").getAsString() : "";

        // TODO: Chỗ này sau này cậu sẽ gọi AuthService của Trường
        // Ví dụ: User user = AuthService.login(username, password);
        
        // Hiện tại tớ tạo JSON phản hồi mẫu giả lập thành công theo đúng protocol.md
        JsonObject response = new JsonObject();
        response.addProperty("status", "SUCCESS");
        response.addProperty("action", "LOGIN");
        response.addProperty("message", "Đăng nhập thành công");
        
        JsonObject resPayload = new JsonObject();
        resPayload.addProperty("userId", 1);
        resPayload.addProperty("role", "BIDDER");
        response.add("payload", resPayload);

        // Chuyển Đối tượng JSON thành Chuỗi để gửi qua mạng
        return gson.toJson(response);
    }

    private String handleCreateAuction(JsonObject payload) {
        // TODO: Gọi AuctionService của Đăng
        return "{\"status\": \"SUCCESS\", \"action\": \"CREATE_AUCTION\", \"message\": \"Tạo phiên đấu giá thành công\"}";
    }

    private String handlePlaceBid(JsonObject payload, ClientHandler client) {
        // TODO: Gọi BidService của Đăng
        return "{\"status\": \"SUCCESS\", \"action\": \"PLACE_BID\", \"message\": \"Bạn đã đặt giá cao nhất\"}";
    }
}