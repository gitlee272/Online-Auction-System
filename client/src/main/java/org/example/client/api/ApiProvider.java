package org.example.client.api;

/** Nơi duy nhất chọn cài đặt ServerApi. Khi nối server thật, chỉ đổi một dòng ở đây. */
public final class ApiProvider {

    private static final ServerApi API = new MockServerApi();

    private ApiProvider() {}

    public static ServerApi get() {
        return API;
    }
}