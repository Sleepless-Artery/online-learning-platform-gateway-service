package org.sleepless_artery.gateway_service.filter;

import io.jsonwebtoken.Claims;
import org.sleepless_artery.gateway_service.exception.TokenValidationException;
import org.sleepless_artery.gateway_service.jwt.JwtValidator;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtValidator jwtValidator;

    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        super(Config.class);
        this.jwtValidator = jwtValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            if (shouldSkipAuth(path)) {
                return chain.filter(exchange);
            }

            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header")))
                    .filter(authHeader -> authHeader.startsWith("Bearer "))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header")))
                    .flatMap(authHeader -> {
                        String token = authHeader.substring(7);
                        try {
                            Claims claims = jwtValidator.validateToken(token);

                            exchange.getAttributes().put("user.email", claims.getSubject());
                            exchange.getAttributes().put("user.roles", claims.get("roles", List.class));

                            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Email", claims.getSubject())
                                    .header("X-User-Roles", String.join(",", claims.get("roles", List.class)))
                                    .build();

                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        } catch (TokenValidationException e) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage()));
                        }
                    });
        };
    }

    private boolean shouldSkipAuth(String path) {
        return path.startsWith("/api/auth/") || path.startsWith("/actuator/") ;
    }

    public static class Config {}
}