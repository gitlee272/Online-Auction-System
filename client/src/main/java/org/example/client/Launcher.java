package org.example.client;

/**
 * Lớp khởi động phụ. Chạy trực tiếp class kế thừa Application từ IDE hay từ file jar
 * thường báo "JavaFX runtime components are missing", nên ta chạy qua lớp này.
 */
public final class Launcher {

    private Launcher() {}

    public static void main(String[] args) {
        App.main(args);
    }
}