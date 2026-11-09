package org.example.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Import chính xác class AuthService từ thư mục main
import org.example.service.AuthService;

public class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Truyền null vào DAO để test độc lập logic của AuthService
        // (Cách này giúp file Test chạy hoàn hảo kể cả khi bạn chưa code xong UserDaoImpl)
        authService = new AuthService(null);
    }

    @Test
    @DisplayName("Đăng nhập thành công với thông tin hợp lệ")
    void testLoginSuccess() {
        boolean result = authService.login("admin", "password123");
        assertTrue(result, "Hàm login phải trả về true khi đăng nhập đúng");
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi sai mật khẩu")
     void testLoginWithWrongPassword() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("admin", "wrongpass");
        });
        assertEquals("Sai mật khẩu", exception.getMessage());
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi tài khoản không tồn tại")
    void testLoginWithNonExistentUser() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("unknown_user", "password123");
        });
        assertEquals("Tài khoản không tồn tại", exception.getMessage());
    }
}