package handlers;

import dao.UserDao;
import io.javalin.http.Handler;
import model.User;

import java.util.Objects;
import java.util.Optional;

public class UserService {
    public static Handler getUsers = ctx -> {
        UserDao dao = UserDao.instance();
        ctx.json(dao.getUsers());
    };

    public static Handler getUserById = ctx -> {
        UserDao dao = UserDao.instance();
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("userId")));
        Optional<User> user = dao.getUserById(id);
        if (user.isPresent()) {
            ctx.json(user.get());
        } else {
            ctx.status(404).result("User not found");
        }
    };

    public static Handler addUser = ctx -> {
        UserDao dao = UserDao.instance();
        try {
            User user = ctx.bodyAsClass(User.class);
            dao.addUser(user);
            ctx.status(200).result("User created");
        } catch (UserDao.UserExistException ex) {
            ctx.status(406).result("User already exists");
        } catch (Exception e) {
            ctx.status(400).result("Failed to add a new user");
        }
    };

    public static Handler patchUser = ctx -> {
        ctx.result("User " + ctx.pathParam("userId") + " updated");
    };
}
