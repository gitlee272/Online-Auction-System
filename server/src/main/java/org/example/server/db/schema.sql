CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     username TEXT NOT NULL UNIQUE,
                                     password TEXT NOT NULL,
                                     email TEXT,
                                     role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     name TEXT NOT NULL,
                                     description TEXT,
                                     starting_price REAL NOT NULL,
                                     condition TEXT NOT NULL,
                                     category TEXT NOT NULL,
                                     seller_id INTEGER NOT NULL,
                                     extra_info_1 TEXT DEFAULT '',
                                     extra_info_2 REAL DEFAULT 0,
                                     FOREIGN KEY (seller_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS auctions (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        item_id INTEGER NOT NULL,
                                        start_time TEXT NOT NULL,
                                        end_time TEXT NOT NULL,
                                        current_highest_bid REAL NOT NULL,
                                        highest_bidder_id INTEGER DEFAULT 0,
                                        max_auto_bid REAL DEFAULT 0,
                                        status TEXT NOT NULL,
                                        FOREIGN KEY (item_id) REFERENCES items(id)
    );

CREATE TABLE IF NOT EXISTS bids (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    auction_id INTEGER NOT NULL,
                                    bidder_id INTEGER NOT NULL,
                                    amount REAL NOT NULL,
                                    timestamp TEXT NOT NULL,
                                    FOREIGN KEY (auction_id) REFERENCES auctions(id),
    FOREIGN KEY (bidder_id) REFERENCES users(id)
    );