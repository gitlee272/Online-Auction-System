package dao;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    void save(User user);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void deleteByUsername(String username);
}