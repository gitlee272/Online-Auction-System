package org.example.common.model;

public class Admin extends User {
    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(String id, String username, String password) {
        super(id, username, password, "ADMIN");
    }
}
