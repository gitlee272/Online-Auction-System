package org.example.client.model;

/** Thông tin người dùng sau khi đăng nhập. */
public record UserSession(String token, long userId, String username, String role) {}