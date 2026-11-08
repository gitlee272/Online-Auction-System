package service;

import dao.UserDao;
import exception.AuthenticationException;
import exception.UserAlreadyExistsException;
import model.User;
import payload.AuthResponse;
import payload.LoginRequest;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Xử lý Đăng nhập với AuthenticationException
    public AuthResponse login(LoginRequest request) throws AuthenticationException {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new AuthenticationException("Dữ liệu đăng nhập không hợp lệ!");
        }

        Optional<User> userOpt = userDao.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Tài khoản không tồn tại!");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AuthenticationException("Mật khẩu không chính xác!");
        }

        return new AuthResponse(true, "Đăng nhập thành công!", user);
    }

    // Xử lý Đăng ký với UserAlreadyExistsException
    public AuthResponse register(User newUser) throws UserAlreadyExistsException, AuthenticationException {
        if (newUser == null || newUser.getUsername() == null || newUser.getPassword() == null) {
            throw new AuthenticationException("Thông tin đăng ký không hợp lệ!");
        }

        if (userDao.findByUsername(newUser.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Tên tài khoản '" + newUser.getUsername() + "' đã tồn tại!");
        }

        userDao.save(newUser);
        return new AuthResponse(true, "Đăng ký tài khoản thành công!", newUser);
    }
}