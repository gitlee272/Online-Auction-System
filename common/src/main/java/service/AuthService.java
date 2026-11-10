package service;
import dao.UserDao;

import model.User;
import payload.AuthResponse;
import payload.LoginRequest;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Xử lý logic Đăng nhập
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return new AuthResponse(false, "Dữ liệu đăng nhập không hợp lệ!", null);
        }

        // Đã sửa: userDao trả về User trực tiếp thay vì Optional
        User user = userDao.findByUsername(request.getUsername());

        // Đã sửa: Thay vì dùng userOpt.isEmpty(), ta kiểm tra user == null
        if (user == null) {
            return new AuthResponse(false, "Tài khoản không tồn tại!", null);
        }

        if (!user.getPassword().equals(request.getPassword())) {
            return new AuthResponse(false, "Mật khẩu không chính xác!", null);
        }

        return new AuthResponse(true, "Đăng nhập thành công!", user);
    }

    // Xử lý logic Đăng ký
    public AuthResponse register(User newUser) {
        if (newUser == null || newUser.getUsername() == null || newUser.getPassword() == null) {
            return new AuthResponse(false, "Thông tin đăng ký không hợp lệ!", null);
        }

        // Đã sửa: Kiểm tra khác null thay vì dùng isPresent()
        if (userDao.findByUsername(newUser.getUsername()) != null) {
            return new AuthResponse(false, "Tên tài khoản đã tồn tại!", null);
        }

        // Đã sửa: Đổi từ save() thành insert() cho khớp với UserDao
        userDao.insert(newUser);
        return new AuthResponse(true, "Đăng ký tài khoản thành công!", newUser);
    }
}