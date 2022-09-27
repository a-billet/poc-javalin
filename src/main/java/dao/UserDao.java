package dao;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private List<User> users = new ArrayList<>();

    private static UserDao userDao = null;

    private UserDao() {
        users.add(new User(1, "Amaury", "Billet"));
        users.add(new User(2, "Ioana", "Margineanu"));
        users.add(new User(3, "John", "Doe"));
    }

    public static UserDao instance() {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }

    public Optional<User> getUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findAny();
    }

    public Iterable<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) throws UserExistException {
        Optional<User> uus = users.stream().filter(u -> u.getId() == user.getId()).findAny();
        if (uus.isPresent()) {
            throw new UserExistException("User already in the database");
        }
        users.add(user);
    }

    public static class UserExistException extends Exception {
        public UserExistException(String err) {
            super(err);
        }
    }
}
