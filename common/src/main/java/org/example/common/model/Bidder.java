package org.example.common.model;

public class Bidder extends User {
    private double balance; // Số dư tài khoản để đặt giá

    public Bidder() {
        super();
        setRole("BIDDER");
    }

    public Bidder(String id, String username, String password, double balance) {
        super(id, username, password, "BIDDER");
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}