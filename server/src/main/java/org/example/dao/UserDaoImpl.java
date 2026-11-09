package dao;

import org.example.common.exception.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private final Map<String, User> userMap = new HashMap<>();

    @Override
    public void save(User user) {
        if (user != null && user.getUsername() != null) {
            userMap.put(user.getUsername(), user);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMap.get(username));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteByUsername(String username) {
        userMap.remove(username);
    }
}