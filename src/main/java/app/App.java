package app;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import handlers.JWTService;
import handlers.UserService;
import io.javalin.Javalin;
import io.javalin.core.security.RouteRole;
import io.javalin.core.util.FileUtil;
import io.javalin.http.Handler;
import javalinjwt.JWTAccessManager;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import model.MockUser;
import model.Roles;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        Algorithm algorithm = Algorithm.HMAC256("very_secret");
        JWTGenerator<MockUser> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create().withClaim("name", user.name).withClaim("level", user.level);
            return token.sign(alg);
        };
        JWTVerifier verifier = JWT.require(algorithm).build();
        JWTProvider<MockUser> provider = new JWTProvider<>(algorithm, generator, verifier);
        JWTService service = new JWTService(provider);

        Map<String, RouteRole> rolesMapping = new HashMap<>() {{
            put("user", Roles.USER);
            put("admin", Roles.ADMIN);
        }};
        Handler decodeHandler = JavalinJWT.createHeaderDecodeHandler(provider);
        JWTAccessManager accessManager = new JWTAccessManager("level", rolesMapping, Roles.ANYONE);
        Javalin app = Javalin.create(config -> {
            config.accessManager(accessManager);
        }).start(7070);
        app.before(decodeHandler);
        app.get("/users", UserService.getUsers, Roles.ADMIN, Roles.USER);
        app.get("/users/{userId}", UserService.getUserById, Roles.USER);
        app.patch("/users/{userId}", UserService.patchUser);
        app.post("/user", UserService.addUser);

        app.get("/generate/{role}", service.generateHandler(), Roles.ANYONE);
        app.get("/validate", service.validateHandler());

        app.post("/upload", ctx -> {
            ctx.uploadedFiles("files").forEach(uploadedFile -> {
                FileUtil.streamToFile(uploadedFile.getContent(), "upload/" + uploadedFile.getFilename());
            });
        }, Roles.ANYONE);
    }
}
