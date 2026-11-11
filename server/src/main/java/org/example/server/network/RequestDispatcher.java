package org.example.server.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

// TODO: Mở comment các dòng import dưới đây sau khi chốt tên class với Trường và Đăng
 //import org.example.server.service.AuthService;
 import org.example.server.service.AuctionService;
 //import org.example.common.model.User;

public class RequestDispatcher {
    private final Gson gson;
    
    // Khai báo các Service ở cấp toàn cục (Global) để dùng chung
    // private final AuthService authService;
     private final AuctionService auctionService;

    public RequestDispatcher() {
        this.gson = new Gson();
        // this.authService = new AuthService();
         this.auctionService = new AuctionService();
    }

    /**
     * HÀM ĐỊNH TUYẾN CHÍNH (Được ClientHandler gọi)
     */
    public String dispatch(String jsonRequest, ClientHandler client) {
        String requestId = null;
        try {
            // 1. Phân tích chuỗi JSON
            JsonObject requestObj = gson.fromJson(jsonRequest, JsonObject.class);
            
            if (requestObj == null || !requestObj.isJsonObject()) {
                return createErrorResponse(null, "INVALID_JSON", "Định dạng JSON không hợp lệ", null);
            }

            // 2. Trích xuất ID (Bắt buộc phải trả lại đúng ID này)
            if (requestObj.has("id") && !requestObj.get("id").isJsonNull()) {
                requestId = requestObj.get("id").getAsString();
            } else {
                return createErrorResponse(null, "INVALID_JSON", "Thiếu trường id trong request", null);
            }

            // 3. Trích xuất Action
            if (!requestObj.has("action") || requestObj.get("action").isJsonNull()) {
                return createErrorResponse(requestId, "UNKNOWN_ACTION", "Thiếu trường action", null);
            }
            String action = requestObj.get("action").getAsString();

            // 4. Trích xuất Token và Data
            String token = requestObj.has("token") && !requestObj.get("token").isJsonNull() 
                           ? requestObj.get("token").getAsString() : null;
            JsonObject data = requestObj.has("data") && !requestObj.get("data").isJsonObject() == false 
                              ? requestObj.getAsJsonObject("data") : new JsonObject();

            // ==========================================
            // TẦNG BẢO MẬT: KIỂM TRA TOKEN (Mục 3)
            // ==========================================
            if (!action.equals("PING") && !action.equals("REGISTER") && !action.equals("LOGIN")) {
                if (token == null || token.trim().isEmpty()) {
                    return createErrorResponse(requestId, "NOT_AUTHENTICATED", "Thiếu token hoặc token không hợp lệ", null);
                }
                
                // TODO: Gọi AuthService để kiểm tra token có thật/còn hạn không
                // boolean isTokenValid = authService.validateToken(token);
                // if (!isTokenValid) {
                //     return createErrorResponse(requestId, "NOT_AUTHENTICATED", "Token đã hết hạn hoặc bị từ chối", null);
                // }
            }

            // ==========================================
            // TẦNG ĐIỀU HƯỚNG: GỌI SERVICE TƯƠNG ỨNG
            // ==========================================
            switch (action) {
                case "PING":
                    JsonObject pongData = new JsonObject();
                    pongData.addProperty("pong", true);
                    return createSuccessResponse(requestId, pongData);
                    
                case "LOGIN":
                    return handleLogin(requestId, data, client);
                    
                case "CREATE_AUCTION":
                    return handleCreateAuction(requestId, data, token);
                    
                case "PLACE_BID":
                    return handlePlaceBid(requestId, data, token, client);
                    
                // Thêm các case khác (LIST_AUCTIONS, GET_AUCTION...) tại đây
                
                default:
                    return createErrorResponse(requestId, "UNKNOWN_ACTION", "Action không tồn tại: " + action, null);
            }

        } catch (JsonSyntaxException e) {
            return createErrorResponse(requestId, "INVALID_JSON", "Cú pháp JSON bị hỏng hoặc vượt quá 64KB", null);
        } catch (Exception e) {
            e.printStackTrace(); // In ra console của Server để debug
            return createErrorResponse(requestId, "INTERNAL_ERROR", "Lỗi máy chủ nội bộ", null);
        }
    }

    // =========================================================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ (GỌI XUỐNG TẦNG SERVICE)
    // =========================================================================

    private String handleLogin(String requestId, JsonObject data, ClientHandler client) {
        String username = data.has("username") ? data.get("username").getAsString() : "";
        String password = data.has("password") ? data.get("password").getAsString() : "";

        if (username.isEmpty() || password.isEmpty()) {
            return createErrorResponse(requestId, "VALIDATION_ERROR", "Tài khoản và mật khẩu không được để trống", null);
        }

        try {
            // TODO: Gọi AuthService của Trường
            // User user = authService.login(username, password);
            // if (user == null) {
            //     return createErrorResponse(requestId, "INVALID_CREDENTIALS", "Sai tên đăng nhập hoặc mật khẩu", null);
            // }

            // Giả lập thành công theo protocol (Mục 4.3)
            JsonObject resData = new JsonObject();
            resData.addProperty("token", "fake-jwt-token-123"); // Token sinh ra từ AuthService
            resData.addProperty("userId", 3);
            resData.addProperty("username", username);
            resData.addProperty("role", "BIDDER"); // Hoặc lấy từ user.getRole()

            return createSuccessResponse(requestId, resData);
        } catch (Exception e) {
            return createErrorResponse(requestId, "INTERNAL_ERROR", "Lỗi khi xử lý đăng nhập", null);
        }
    }

    private String handleCreateAuction(String requestId, JsonObject data, String token) {
        try {
            // TODO: Trích xuất các trường itemName, description, startingPrice... từ `data`
            // TODO: Gọi kiểm tra quyền (Chỉ SELLER mới được tạo)
            // TODO: Gọi AuctionService của Đăng để lưu vào DB
            
            // Giả lập thành công (Mục 4.7)
            JsonObject resData = new JsonObject();
            resData.addProperty("auctionId", 100);
            
            return createSuccessResponse(requestId, resData);
        } catch (Exception e) {
            return createErrorResponse(requestId, "VALIDATION_ERROR", "Dữ liệu tạo phiên không hợp lệ", null);
        }
    }

    private String handlePlaceBid(String requestId, JsonObject data, String token, ClientHandler client) {
        try {
            if (!data.has("auctionId") || !data.has("amount")) {
                return createErrorResponse(requestId, "VALIDATION_ERROR", "Thiếu auctionId hoặc amount", null);
            }
            
            long auctionId = data.get("auctionId").getAsLong();
            long amount = data.get("amount").getAsLong(); // Protocol quy định dùng long (VND)

            // TODO: Gọi BidService của Đăng (Truyền thêm thông tin user lấy từ token)
            // boolean success = auctionService.placeBid(auctionId, userId, amount);
            
            // Giả lập thất bại (Mục 4.11)
            // Nếu giá quá thấp:
            // JsonObject errData = new JsonObject(); errData.addProperty("minBid", 18600000);
            // return createErrorResponse(requestId, "BID_TOO_LOW", "Giá phải từ 18.600.000 đ trở lên", errData);

            // Giả lập thành công: Trả về đối tượng chi tiết phiên (Như GET_AUCTION)
            JsonObject resData = new JsonObject();
            resData.addProperty("id", auctionId);
            resData.addProperty("currentPrice", amount);
            // ... thêm các trường khác theo GET_AUCTION
            
            return createSuccessResponse(requestId, resData);
        } catch (Exception e) {
            return createErrorResponse(requestId, "VALIDATION_ERROR", "Dữ liệu đặt giá sai định dạng", null);
        }
    }

    // =========================================================================
    // CÁC HÀM TIỆN ÍCH (ĐÓNG GÓI JSON CHUẨN)
    // =========================================================================

    private String createSuccessResponse(String id, JsonObject data) {
        JsonObject response = new JsonObject();
        response.addProperty("id", id);
        response.addProperty("status", "OK");
        response.add("data", data != null ? data : new JsonObject());
        return gson.toJson(response);
    }

    private String createErrorResponse(String id, String code, String message, JsonObject data) {
        JsonObject response = new JsonObject();
        response.addProperty("id", id);
        response.addProperty("status", "ERROR");
        response.addProperty("code", code);
        response.addProperty("message", message);
        if (data != null) {
            response.add("data", data);
        }
        return gson.toJson(response);
    }
}