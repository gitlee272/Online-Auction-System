package org.example.dao; // Tên package đã được sửa để khớp với cấu trúc thư mục

import java.util.List;
import java.util.Optional;

// LƯU Ý: Nếu class User của bạn nằm ở package khác (ví dụ: org.example.common.model.User),
// hãy sửa lại dòng import dưới đây cho khớp.
import org.example.model.User;

public interface UserDao {

    void save(User user);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void deleteByUsername(String username);

}