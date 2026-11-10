package dao;

// Import class User của bạn.
// LƯU Ý: Hãy sửa lại "model.User" thành đúng tên package chứa file User.java của bạn
// (Ví dụ: import org.example.model.User; hoặc import com.auction.model.User;)
import model.User;
import java.util.List;

public interface UserDao {

    /**
     * Lấy thông tin người dùng dựa vào ID
     */
    User findById(int id);

    /**
     * Lấy thông tin người dùng dựa vào tên đăng nhập (username)
     */
    User findByUsername(String username);

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu
     */
    void insert(User user);

    /**
     * Cập nhật thông tin người dùng
     */
    void update(User user);

    /**
     * Xóa người dùng dựa vào ID
     */
    void delete(int id);

    /**
     * Lấy danh sách tất cả người dùng
     */
    List<User> findAll();
}