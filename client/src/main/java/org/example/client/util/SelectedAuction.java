package org.example.client.util;

/** Lưu id phiên đấu giá đang được chọn để màn hình chi tiết biết cần hiển thị phiên nào. */
public final class SelectedAuction {

    private static long id;

    private SelectedAuction() {}

    public static void set(long auctionId) {
        id = auctionId;
    }

    public static long get() {
        return id;
    }
}