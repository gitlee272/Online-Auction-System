package org.example.client.model;

/** Một người dùng hiển thị trong trang quản trị. */
public record UserInfo(long id, String username, String email, String role, boolean active) {}