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
                .route("auth-service-protected", r -> r
                        .path("/api/auth/logout", "/api/auth/change-email-address", "/api/auth/confirm-email-address")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1))
                        .uri("lb://auth-service"))

                .route("auth-service-public", r -> r
                        .path("/api/auth/login", "/api/auth/register", "/api/auth/confirm-registration",
                                "/api/auth/forgot-password", "/api/auth/validate-reset-code", "/api/auth/reset-password")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))


                .route("user-service-edit", r -> r.path("/api/profile/edit/{id}")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/profile/edit/(?<id>.*)", "/users/${id}"))
                        .uri("lb://user-service"))

                .route("user-service-delete", r -> r.path("/api/profile/delete/{id}")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f.
                                filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/profile/delete/(?<id>.*)", "/users/${id}"))
                        .uri("lb://user-service"))

                .route("user-service-general", r -> r.path("/api/profile/**", "/api/profile")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/profile(?<segment>.*)", "/users${segment}")
                        )
                        .uri("lb://user-service"))


                .route("course-service-create", r -> r.path("/api/courses/create")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/courses/create(?<segment>.*)", "/courses${segment}"))
                        .uri("lb://course-service"))

                .route("course-service-edit", r -> r.path("/api/courses/edit/{id}")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/courses/edit/(?<id>.*)", "/courses/${id}"))
                        .uri("lb://course-service"))

                .route("course-service-delete", r -> r.path("/api/courses/delete/{id}")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/courses/delete/(?<id>.*)", "/courses/${id}"))
                        .uri("lb://course-service"))

                .route("course-service-search", r -> r.path("/api/courses/search")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/courses/search(?<segment>.*)", "/courses${segment}"))
                        .uri("lb://course-service"))

                .route("course-service-general", r -> r.path("/api/courses/**", "/api/courses")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1))
                        .uri("lb://course-service"))


                .route("lesson-service-create", r -> r.path("/api/lessons/create")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/lessons/create(?<segment>.*)", "/lessons${segment}"))
                        .uri("lb://lesson-service"))

                .route("lesson-service-edit", r -> r.path("/api/lessons/edit/{id}")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/lessons/edit/(?<id>.*)", "/lessons/${id}"))
                        .uri("lb://lesson-service"))

                .route("lesson-service-delete", r -> r.path("/api/lessons/delete/{id}")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/lessons/delete/(?<id>.*)", "/lessons/${id}"))
                        .uri("lb://lesson-service"))

                .route("lesson-service-general", r -> r.path("/api/lessons/**", "/api/lessons")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1))
                        .uri("lb://lesson-service"))


                .route("enrollment-service-leave", r -> r
                        .path("/api/leave")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/leave(?<segment>.*)", "/enrollments${segment}"))
                        .uri("lb://enrollment-service"))

                .route("enrollment-service-enroll", r -> r.path("/api/enroll/**", "/api/enroll")
                        .filters(f -> f
                                .filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(1)
                                .rewritePath("/enroll(?<segment>.*)", "/enrollments${segment}"))
                        .uri("lb://enrollment-service"))
                .build();
    }
}
