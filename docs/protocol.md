# Giao thức Client–Server

Tài liệu này là hợp đồng chung giữa client (JavaFX) và server. Muốn đổi giao thức thì phải sửa file này trong cùng Pull Request với code.

Phiên bản: 1.0 (bản nháp, chờ Khôi duyệt)

## 1. Quy ước chung

| Mục | Quy ước |
|---|---|
| Kênh truyền | TCP Socket, cổng mặc định `9999` |
| Mã hóa | UTF-8 |
| Khung message | Mỗi message là **một dòng JSON**, kết thúc bằng ký tự `\n`. JSON không được chứa ký tự xuống dòng thật (Gson mặc định đã đảm bảo) |
| Kết nối | Một client giữ một kết nối suốt phiên làm việc. Server có thể gửi event xuống bất cứ lúc nào |
| Tiền | Số nguyên `long`, đơn vị đồng (VND). Không dùng số thực |
| Thời gian | Chuỗi ISO-8601 không múi giờ, giờ của server (Asia/Ho_Chi_Minh), ví dụ `2026-11-20T20:00:00` |
| Tên trường | `camelCase` |
| Enum | Chuỗi viết hoa, ví dụ `RUNNING`, `BIDDER` |
| Trường không có giá trị | `null` hoặc bỏ qua trường |

### Enum dùng chung

| Enum | Giá trị |
|---|---|
| `role` | `BIDDER`, `SELLER`, `ADMIN` |
| `status` (phiên) | `OPEN`, `RUNNING`, `FINISHED`, `PAID`, `CANCELED` |
| `itemType` | `ELECTRONICS`, `ART`, `VEHICLE` |
| `condition` | `NEW`, `LIKE_NEW`, `USED` |

Chuyển trạng thái hợp lệ: `OPEN → RUNNING → FINISHED → PAID`; `OPEN` hoặc `RUNNING` có thể sang `CANCELED`.

## 2. Ba loại message

Client phân biệt message nhận được theo trường có mặt: có `id` thì là response, có `event` thì là event.

### 2.1. Request (client gửi)

```json
{"id":"r1","action":"LOGIN","token":null,"data":{"username":"an","password":"123456"}}
```

| Trường | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `id` | string | có | Do client sinh, duy nhất trong kết nối (ví dụ UUID). Server trả lại nguyên vẹn trong response |
| `action` | string | có | Tên hành động (mục 4) |
| `token` | string | tùy action | Token nhận được khi đăng nhập. `null` với `PING`, `REGISTER`, `LOGIN` |
| `data` | object | không | Tham số của action. Không có tham số thì để `{}` |

### 2.2. Response (server trả)

Thành công:

```json
{"id":"r1","status":"OK","data":{"token":"abc","userId":3,"username":"an","role":"BIDDER"}}
```

Lỗi:

```json
{"id":"r2","status":"ERROR","code":"BID_TOO_LOW","message":"Giá phải từ 18.600.000 đ trở lên","data":{"minBid":18600000}}
```

| Trường | Kiểu | Mô tả |
|---|---|---|
| `id` | string | Trùng `id` của request |
| `status` | string | `OK` hoặc `ERROR` |
| `code` | string | Chỉ có khi `ERROR`, thuộc bảng mã lỗi (mục 5) |
| `message` | string | Chỉ có khi `ERROR`. Câu tiếng Việt có thể hiển thị trực tiếp cho người dùng |
| `data` | object | Dữ liệu kết quả (khi `OK`) hoặc thông tin bổ sung (khi `ERROR`, tùy mã lỗi) |

Mỗi request có **đúng một** response. Response có thể đến không theo thứ tự gửi, client ghép theo `id`.

### 2.3. Event (server chủ động đẩy)

```json
{"event":"NEW_BID","data":{"auctionId":5,"amount":1500000,"bidder":"an","time":"2026-11-20T19:59:10","endTime":"2026-11-20T20:00:00"}}
```

Event không có `id` và client không trả lời. Chỉ client đã `SUBSCRIBE` phiên đó mới nhận event của phiên đó (xem mục 6).

## 3. Xác thực và phân quyền

1. Client gọi `LOGIN` (hoặc `REGISTER`), nhận `token`.
2. Mọi action khác (trừ `PING`, `REGISTER`, `LOGIN`) phải gửi kèm `token`. Thiếu hoặc sai token thì server trả `NOT_AUTHENTICATED`.
3. Server kiểm tra vai trò cho từng action (cột "Vai trò" ở mục 4). Sai vai trò thì trả `NOT_AUTHORIZED`.
4. Tài khoản bị khóa không đăng nhập được (`ACCOUNT_LOCKED`) và token đang dùng bị vô hiệu.
5. Token mất hiệu lực khi `LOGOUT` hoặc khi kết nối bị đóng.

Việc ẩn nút trên giao diện chỉ để tiện dùng. **Server luôn kiểm tra lại mọi quyền và mọi dữ liệu đầu vào.**

## 4. Danh sách action

Vai trò: `*` là mọi người (kể cả chưa đăng nhập), `đăng nhập` là mọi vai trò đã đăng nhập.

| Action | Vai trò |
|---|---|
| `PING` | `*` |
| `REGISTER` | `*` |
| `LOGIN` | `*` |
| `LOGOUT` | đăng nhập |
| `LIST_AUCTIONS` | đăng nhập |
| `GET_AUCTION` | đăng nhập |
| `CREATE_AUCTION` | SELLER |
| `UPDATE_AUCTION` | SELLER (chủ phiên) |
| `CANCEL_AUCTION` | ADMIN, hoặc SELLER là chủ phiên |
| `SUBSCRIBE` | đăng nhập |
| `UNSUBSCRIBE` | đăng nhập |
| `PLACE_BID` | BIDDER |
| `SET_AUTO_BID` | BIDDER |
| `LIST_USERS` | ADMIN |
| `SET_USER_ACTIVE` | ADMIN |

### 4.1. `PING`

| | |
|---|---|
| Request `data` | `{}` |
| Response `data` | `{"pong":true}` |

### 4.2. `REGISTER`

| | |
|---|---|
| Request `data` | `username` (string, 3–30 ký tự), `password` (string, tối thiểu 6 ký tự), `email` (string, đúng dạng email), `role` (`BIDDER` hoặc `SELLER`) |
| Response `data` | Như `LOGIN` (đăng ký xong đăng nhập luôn) |
| Lỗi có thể có | `USERNAME_TAKEN`, `VALIDATION_ERROR` |

Không cho tự đăng ký `ADMIN`: gửi `role = ADMIN` thì trả `VALIDATION_ERROR`.

### 4.3. `LOGIN`

| | |
|---|---|
| Request `data` | `username`, `password` |
| Response `data` | `token` (string), `userId` (long), `username` (string), `role` |
| Lỗi có thể có | `INVALID_CREDENTIALS`, `ACCOUNT_LOCKED` |

Đây chính là `UserSession` của client.

### 4.4. `LOGOUT`

| | |
|---|---|
| Request `data` | `{}` |
| Response `data` | `{}` |

### 4.5. `LIST_AUCTIONS`

| | |
|---|---|
| Request `data` | `status` (tùy chọn, lọc theo trạng thái), `keyword` (tùy chọn, tìm theo tên sản phẩm) |
| Response `data` | `{"auctions":[ {...}, {...} ]}` |

Mỗi phần tử (khớp `AuctionSummary` của client):

| Trường | Kiểu | Mô tả |
|---|---|---|
| `id` | long | Mã phiên |
| `itemName` | string | Tên sản phẩm |
| `currentPrice` | long | Giá hiện tại cao nhất (chưa có bid thì bằng giá khởi điểm) |
| `status` | string | Trạng thái phiên |
| `endTime` | string | Thời điểm kết thúc dự kiến |

Không truyền `status` và `keyword` thì trả tất cả phiên.

### 4.6. `GET_AUCTION`

| | |
|---|---|
| Request `data` | `auctionId` (long) |
| Response `data` | Một đối tượng chi tiết phiên (bảng dưới) |
| Lỗi có thể có | `AUCTION_NOT_FOUND` |

Khớp `AuctionDetail` của client:

| Trường | Kiểu | Mô tả |
|---|---|---|
| `id` | long | Mã phiên |
| `itemName` | string | Tên sản phẩm |
| `description` | string | Mô tả |
| `itemType` | string | `ELECTRONICS`, `ART`, `VEHICLE` |
| `startingPrice` | long | Giá khởi điểm |
| `currentPrice` | long | Giá hiện tại |
| `leader` | string hoặc null | Tên người đang dẫn đầu, `null` nếu chưa có bid |
| `status` | string | Trạng thái |
| `endTime` | string | Thời điểm kết thúc hiện tại (có thể đã được gia hạn) |
| `bids` | array | Lịch sử đặt giá, **cũ nhất trước** |

Mỗi phần tử của `bids` (khớp `BidEntry`): `bidder` (string), `amount` (long), `time` (string).

### 4.7. `CREATE_AUCTION`

| | |
|---|---|
| Request `data` | `itemName`, `description`, `itemType`, `condition`, `startingPrice` (long > 0), `startTime`, `endTime` |
| Response `data` | `{"auctionId":100}` |
| Lỗi có thể có | `NOT_AUTHORIZED`, `VALIDATION_ERROR` |

Server kiểm tra `endTime` phải sau `startTime` và sau thời điểm hiện tại. Trạng thái ban đầu là `OPEN`; đến `startTime` server tự chuyển sang `RUNNING` và đẩy event `AUCTION_STARTED`.

### 4.8. `UPDATE_AUCTION`

| | |
|---|---|
| Request `data` | `auctionId` và các trường muốn sửa trong `itemName`, `description`, `condition`, `startingPrice`, `startTime`, `endTime` |
| Response `data` | Chi tiết phiên sau khi sửa (như `GET_AUCTION`) |
| Lỗi có thể có | `NOT_AUTHORIZED`, `AUCTION_NOT_FOUND`, `INVALID_STATE`, `VALIDATION_ERROR` |

Chỉ được sửa khi phiên còn `OPEN` và chưa có bid. Ngược lại trả `INVALID_STATE`.

### 4.9. `CANCEL_AUCTION`

| | |
|---|---|
| Request `data` | `auctionId` |
| Response `data` | `{}` |
| Lỗi có thể có | `NOT_AUTHORIZED`, `AUCTION_NOT_FOUND`, `INVALID_STATE` |

Chỉ hủy được phiên đang `OPEN` hoặc `RUNNING`. Thành công thì server đẩy event `AUCTION_CANCELED`.

### 4.10. `SUBSCRIBE` và `UNSUBSCRIBE`

| | |
|---|---|
| Request `data` | `auctionId` |
| Response `data` | `{}` |
| Lỗi có thể có | `AUCTION_NOT_FOUND` |

Sau `SUBSCRIBE`, server đẩy event của phiên đó tới client này cho đến khi `UNSUBSCRIBE` hoặc kết nối đóng. Client gọi `SUBSCRIBE` khi mở màn hình chi tiết và `UNSUBSCRIBE` khi rời màn hình. Gọi `SUBSCRIBE` lặp lại là vô hại.

### 4.11. `PLACE_BID`

| | |
|---|---|
| Request `data` | `auctionId` (long), `amount` (long) |
| Response `data` | Chi tiết phiên sau khi đặt (như `GET_AUCTION`) |
| Lỗi có thể có | `BID_TOO_LOW`, `AUCTION_CLOSED`, `AUCTION_NOT_FOUND`, `NOT_AUTHORIZED`, `VALIDATION_ERROR` |

Quy tắc, kiểm tra trong cùng một khối khóa của phiên (xem mục 7):

1. Phiên phải ở `RUNNING` và chưa quá `endTime`, nếu không trả `AUCTION_CLOSED`.
2. `amount` phải **lớn hơn giá hiện tại và đạt bước giá tối thiểu**, nếu không trả `BID_TOO_LOW` kèm `data.minBid`.
3. Người đang dẫn đầu không đặt tiếp được lên chính mình (trả `VALIDATION_ERROR`).
4. Seller không đặt giá được phiên của chính mình.

Bước giá tối thiểu: **100.000 đ** (hằng số của server, mock của client đang dùng cùng giá trị).

### 4.12. `SET_AUTO_BID` (tính năng nâng cao)

| | |
|---|---|
| Request `data` | `auctionId`, `maxBid` (long), `increment` (long > 0) |
| Response `data` | `{}` |
| Lỗi có thể có | `AUCTION_CLOSED`, `VALIDATION_ERROR` |

Server tự đặt giá thay người dùng khi có bid mới của người khác, không vượt `maxBid`. Mỗi lượt auto-bid cũng sinh event `NEW_BID` như bid thường (thêm `auto: true`).

### 4.13. `LIST_USERS`

| | |
|---|---|
| Request `data` | `{}` |
| Response `data` | `{"users":[ {...} ]}` |

Mỗi phần tử (khớp `UserInfo`): `id` (long), `username`, `email`, `role`, `active` (boolean).

### 4.14. `SET_USER_ACTIVE`

| | |
|---|---|
| Request `data` | `userId` (long), `active` (boolean) |
| Response `data` | `{}` |
| Lỗi có thể có | `NOT_AUTHORIZED`, `VALIDATION_ERROR` |

Không cho khóa tài khoản `ADMIN` (trả `VALIDATION_ERROR`).

## 5. Mã lỗi

| Mã | Ý nghĩa |
|---|---|
| `INVALID_JSON` | Dòng nhận được không phải JSON hợp lệ hoặc thiếu trường bắt buộc |
| `UNKNOWN_ACTION` | `action` không tồn tại |
| `VALIDATION_ERROR` | Dữ liệu sai (rỗng, âm, sai định dạng, quy tắc nghiệp vụ) |
| `INVALID_CREDENTIALS` | Sai tên đăng nhập hoặc mật khẩu |
| `ACCOUNT_LOCKED` | Tài khoản bị khóa |
| `USERNAME_TAKEN` | Tên đăng nhập đã tồn tại |
| `NOT_AUTHENTICATED` | Thiếu token hoặc token không hợp lệ |
| `NOT_AUTHORIZED` | Không đủ quyền với action này |
| `AUCTION_NOT_FOUND` | Không có phiên với `auctionId` đó |
| `AUCTION_CLOSED` | Phiên không còn nhận bid (không `RUNNING` hoặc đã quá giờ) |
| `BID_TOO_LOW` | Giá không đủ cao (kèm `data.minBid`) |
| `INVALID_STATE` | Trạng thái hiện tại không cho phép thao tác |
| `INTERNAL_ERROR` | Lỗi phía server. Server **không** gửi stack trace, chỉ ghi vào log |

## 6. Danh sách event

Event chỉ gửi tới client đã `SUBSCRIBE` phiên liên quan. Tất cả có `auctionId`.

| Event | Khi nào | `data` |
|---|---|---|
| `NEW_BID` | Có bid hợp lệ mới | `auctionId`, `amount`, `bidder`, `time`, `endTime` (đã tính gia hạn), `auto` (tùy chọn) |
| `AUCTION_STARTED` | `OPEN → RUNNING` | `auctionId`, `endTime` |
| `AUCTION_EXTENDED` | Chống bid phút chót (anti-sniping) gia hạn phiên | `auctionId`, `endTime` (mới) |
| `AUCTION_ENDED` | `RUNNING → FINISHED` | `auctionId`, `winner` (null nếu không có bid), `finalPrice` |
| `AUCTION_CANCELED` | Phiên bị hủy | `auctionId` |

Client dùng event để cập nhật màn hình chi tiết ngay lập tức, **không polling**. Nếu client mất kết nối rồi kết nối lại, phải `LOGIN` lại, `SUBSCRIBE` lại và gọi `GET_AUCTION` để lấy trạng thái mới nhất (không dựa vào event đã bỏ lỡ).

## 7. Ghi chú cho người viết server

1. Mỗi phiên có một khóa riêng. Toàn bộ chuỗi "kiểm tra trạng thái, kiểm tra giá, cập nhật giá và người dẫn đầu, thêm bid" nằm trong cùng một khối khóa.
2. Gửi event **ngoài** khối khóa (hoặc qua hàng đợi gửi riêng của từng client) để client mạng chậm không làm kẹt cả phiên.
3. Ghi ra socket của một client phải được đồng bộ vì response và event có thể gửi gần như cùng lúc.
4. Một dòng JSON hỏng hoặc một action lỗi không được làm sập server hay đóng kết nối của client khác. Trả `INVALID_JSON` hoặc `INTERNAL_ERROR` rồi đọc tiếp.
5. Khi client ngắt kết nối: đóng socket, gỡ khỏi mọi danh sách subscriber, hủy token.

## 8. Ví dụ một phiên làm việc

```text
C -> {"id":"1","action":"LOGIN","token":null,"data":{"username":"an","password":"123456"}}
S -> {"id":"1","status":"OK","data":{"token":"t-9f2","userId":3,"username":"an","role":"BIDDER"}}

C -> {"id":"2","action":"GET_AUCTION","token":"t-9f2","data":{"auctionId":5}}
S -> {"id":"2","status":"OK","data":{"id":5,"itemName":"iPhone 15 Pro","description":"Máy mới 99%","itemType":"ELECTRONICS","startingPrice":18000000,"currentPrice":18500000,"leader":"binh","status":"RUNNING","endTime":"2026-11-20T20:00:00","bids":[{"bidder":"an","amount":18200000,"time":"2026-11-20T19:50:10"},{"bidder":"binh","amount":18500000,"time":"2026-11-20T19:52:45"}]}}

C -> {"id":"3","action":"SUBSCRIBE","token":"t-9f2","data":{"auctionId":5}}
S -> {"id":"3","status":"OK","data":{}}

C -> {"id":"4","action":"PLACE_BID","token":"t-9f2","data":{"auctionId":5,"amount":18500000}}
S -> {"id":"4","status":"ERROR","code":"BID_TOO_LOW","message":"Giá phải từ 18.600.000 đ trở lên","data":{"minBid":18600000}}

C -> {"id":"5","action":"PLACE_BID","token":"t-9f2","data":{"auctionId":5,"amount":19000000}}
S -> {"id":"5","status":"OK","data":{ ...chi tiết phiên sau khi đặt... }}
S -> {"event":"NEW_BID","data":{"auctionId":5,"amount":19000000,"bidder":"an","time":"2026-11-20T19:59:50","endTime":"2026-11-20T20:00:40"}}
S -> {"event":"AUCTION_EXTENDED","data":{"auctionId":5,"endTime":"2026-11-20T20:00:40"}}
```

## 9. Điểm còn mở (cần Khôi, Trường, Đăng chốt)

| Câu hỏi | Đề xuất tạm |
|---|---|
| Cổng mặc định | `9999`, đọc từ cấu hình |
| Bước giá tối thiểu | 100.000 đ, hằng số trong server |
| Anti-sniping: X giây cuối, gia hạn Y giây | X = 30, Y = 60, cấu hình được |
| Thanh toán (`PAID`) có action riêng không | Thêm `PAY_AUCTION` (BIDDER là người thắng) ở phiên bản sau |
| Token hết hạn theo thời gian không | Chưa, chỉ hết khi `LOGOUT` hoặc đóng kết nối |
| Người dẫn đầu có đặt tiếp được không | Không |