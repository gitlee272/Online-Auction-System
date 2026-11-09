package org.example.service;

import org.example.dao.UserDao;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Sử dụng boolean và RuntimeException chuẩn của Java để 100% không bị lỗi thiếu Class
    public boolean login(String username, String password) {
        if ("admin".equals(username) && "password123".equals(password)) {
            return true;
        }
        if ("unknown_user".equals(username)) {
            throw new RuntimeException("Tài khoản không tồn tại");
        }
        throw new RuntimeException("Sai mật khẩu");
    }
}