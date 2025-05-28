package org.sleepless_artery.gateway_service.config;

import org.sleepless_artery.gateway_service.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;


@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth/logout", "/auth/change-email-address", "/auth/confirm-email-address")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://auth-service"))

                .route("auth-service", r -> r
                        .path("/auth/login", "/auth/register", "/auth/confirm-registration",
                                "/auth/forgot-password", "/auth/validate-reset-code", "/auth/reset-password")
                        .uri("lb://auth-service"))


                .route("user-service", r -> r.path("/profile/edit/{id}")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/profile/edit/(?<id>.*)", "/users/${id}"))
                        .uri("lb://user-service"))

                .route("user-service", r -> r.path("/profile/delete/{id}")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f.
                                filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/profile/delete/(?<id>.*)", "/users/${id}"))
                        .uri("lb://user-service"))

                .route("user-service", r -> r.path("/profile/**", "/profile")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/profile(?<segment>.*)", "/users${segment}")
                        )
                        .uri("lb://user-service"))


                .route("course-service", r -> r.path("/courses/create")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/courses/create(?<segment>.*)", "/courses${segment}"))
                        .uri("lb://course-service"))

                .route("course-service", r -> r.path("/courses/edit/{id}")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/courses/edit/(?<id>.*)", "/courses/${id}"))
                        .uri("lb://course-service"))

                .route("course-service", r -> r.path("/courses/delete/{id}")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/courses/delete/(?<id>.*)", "/courses/${id}"))
                        .uri("lb://course-service"))

                .route("course-service", r -> r.path("/courses/search")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/courses/search(?<segment>.*)", "/courses${segment}"))
                        .uri("lb://course-service"))

                .route("course-service", r -> r.path("/courses/**", "/courses")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://course-service"))


                .route("lesson-service", r -> r.path("/lessons/create")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/lessons/create(?<segment>.*)", "/lessons${segment}"))
                        .uri("lb://lesson-service"))

                .route("lesson-service", r -> r.path("/lessons/edit/{id}")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/lessons/edit/(?<id>.*)", "/lessons/${id}"))
                        .uri("lb://lesson-service"))

                .route("lesson-service", r -> r.path("/lessons/delete/{id}")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/lessons/delete/(?<id>.*)", "/lessons/${id}"))
                        .uri("lb://lesson-service"))

                .route("lesson-service", r -> r.path("/lessons/**", "/lessons")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://lesson-service"))


                .route("enrollment-service", r -> r
                        .path("/leave")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/leave(?<segment>.*)", "/enrollments${segment}"))
                        .uri("lb://enrollment-service"))

                .route("enrollment-service", r -> r.path("/enroll/**", "/enroll")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .rewritePath("/enroll(?<segment>.*)", "/enrollments${segment}"))
                        .uri("lb://enrollment-service"))
                .build();
    }
}
