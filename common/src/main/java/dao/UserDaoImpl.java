package dao;

// Import class User giống hệt như bên UserDao
import model.User;
import java.util.List;
import java.util.ArrayList;

public class UserDaoImpl implements UserDao {

    @Override
    public User findById(int id) {
        // TODO: Viết code truy vấn cơ sở dữ liệu (JDBC/Hibernate) để tìm user theo ID
        return null; // Tạm thời trả về null để code biên dịch thành công
    }

    @Override
    public User findByUsername(String username) {
        // TODO: Viết code truy vấn cơ sở dữ liệu để tìm user theo username
        return null;
    }

    @Override
    public void insert(User user) {
        // TODO: Viết code lưu object user mới vào cơ sở dữ liệu
        System.out.println("Đang thêm user: " + user);
    }

    @Override
    public void update(User user) {
        // TODO: Viết code cập nhật thông tin user vào cơ sở dữ liệu
        System.out.println("Đang cập nhật user: " + user);
    }

    @Override
    public void delete(int id) {
        // TODO: Viết code xóa user khỏi cơ sở dữ liệu dựa vào ID
        System.out.println("Đang xóa user có id: " + id);
    }

    @Override
    public List<User> findAll() {
        // TODO: Viết code lấy toàn bộ danh sách user từ cơ sở dữ liệu
        return new ArrayList<>(); // Tạm thời trả về danh sách rỗng để không bị lỗi
    }
}