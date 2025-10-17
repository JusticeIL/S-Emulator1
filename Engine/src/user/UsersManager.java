package user;

import program.Program;

import java.util.HashMap;
import java.util.Map;

public class UsersManager {
    private Map<String, User> users = new HashMap<>();

    public void addUser(String username) {
        users.putIfAbsent(username, new User(username));
    }

    public User getUser(String username) {
        return users.get(username);
    }
}
