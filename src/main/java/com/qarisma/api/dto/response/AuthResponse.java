package com.qarisma.api.dto.response;

import com.qarisma.api.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private UserDto user;

    @Data
    @Builder
    public static class UserDto {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String role;

        public static UserDto fromEntity(User user) {
            return UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .build();
        }
    }
}
