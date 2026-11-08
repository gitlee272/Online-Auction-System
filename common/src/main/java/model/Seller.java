package model;

public class Seller extends User {
    private String rating; // Đánh giá độ uy tín người bán

    public Seller() {
        super();
        setRole("SELLER");
    }

    public Seller(String id, String username, String password, String rating) {
        super(id, username, password, "SELLER");
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}