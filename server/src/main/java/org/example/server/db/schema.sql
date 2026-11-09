-- Bảng chứa thông tin sản phẩm
CREATE TABLE IF NOT EXISTS items (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     name TEXT NOT NULL,
                                     description TEXT,
                                     starting_price REAL NOT NULL,
                                     condition TEXT NOT NULL
);

-- Bảng chứa thông tin phiên đấu giá
CREATE TABLE IF NOT EXISTS auctions (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        item_id INTEGER NOT NULL,
                                        status TEXT NOT NULL,
                                        start_time DATETIME NOT NULL,
                                        end_time DATETIME NOT NULL,
                                        current_highest_bid REAL DEFAULT 0,
                                        highest_bidder_id INTEGER,
                                        FOREIGN KEY (item_id) REFERENCES items(id)
    );

-- Bảng chứa lịch sử đặt giá (BidTransaction)
CREATE TABLE IF NOT EXISTS bids (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    auction_id INTEGER NOT NULL,
                                    bidder_id INTEGER NOT NULL,
                                    bid_amount REAL NOT NULL,
                                    bid_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (auction_id) REFERENCES auctions(id)
    );