package service;

import dao.UserDao;
import model.User;
import payload.AuthResponse;
import payload.LoginRequest;
import java.util.Optional;

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

        Optional<User> userOpt = userDao.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return new AuthResponse(false, "Tài khoản không tồn tại!", null);
        }

        User user = userOpt.get();
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

        if (userDao.findByUsername(newUser.getUsername()).isPresent()) {
            return new AuthResponse(false, "Tên tài khoản đã tồn tại!", null);
        }

        userDao.save(newUser);
        return new AuthResponse(true, "Đăng ký tài khoản thành công!", newUser);
    }
}
