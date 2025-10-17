package user;

import dto.UserDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsersManager {
    private Map<String, User> users = new HashMap<>();

    public void addUser(String username) {
        users.putIfAbsent(username, new User(username));
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public Set<UserDTO> getAllUsers() {
        return users.values().stream().map(UserDTO::new).collect(Collectors.toSet());
    }

    public UserDTO getUserData(String username) {
        return new UserDTO(users.get(username));
    }
}
