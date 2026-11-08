package org.example.client.util;

import org.example.client.model.UserSession;

/** Lưu người dùng đang đăng nhập trong suốt phiên làm việc của ứng dụng. */
public final class SessionContext {

    private static UserSession current;

    private SessionContext() {}

    public static void set(UserSession session) {
        current = session;
    }

    public static UserSession get() {
        return current;
    }

    public static void clear() {
        current = null;
    }
}