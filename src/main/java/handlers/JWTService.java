package handlers;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Handler;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import javalinjwt.examples.JWTResponse;
import model.MockUser;

import java.util.Objects;
import java.util.Optional;

public record JWTService(JWTProvider<MockUser> provider) {

    public Handler generateHandler() {
        return context -> {
            String role = Objects.requireNonNull(context.pathParam("role"));
            MockUser mockUser = new MockUser("Mocky McMockface", role);
            String token = provider.generateToken(mockUser);
            context.json(new JWTResponse(token));
        };
    }

    public Handler validateHandler() {
        return context -> {

            Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(context)
                    .flatMap(provider::validateToken);

            if (!decodedJWT.isPresent()) {
                context.status(401).result("Missing or invalid token");
            } else {
                context.result("Hi " + decodedJWT.get().getClaim("name").asString());
            }
        };
    }
}
