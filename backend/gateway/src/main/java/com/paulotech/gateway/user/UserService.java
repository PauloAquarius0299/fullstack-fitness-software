package com.paulotech.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.error("User not found with ID: {}", userId);
                        return Mono.just(false);
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        log.error("Invalid user ID: {}", userId);
                        return Mono.just(false);
                    } else {
                        log.error("Error validating user with ID: {}", userId, e);
                        return Mono.error(e);
                    }
                });
    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest){
        log.info("Registering user with email: {}", registerRequest.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Bad request" + e.getMessage()));
                    } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        return Mono.error(new RuntimeException("Internal server error" + e.getMessage()));
                    } else {
                        log.error("Error validating registers: {}", registerRequest, e);
                        return Mono.error(e);
                    }
                });
    }
}