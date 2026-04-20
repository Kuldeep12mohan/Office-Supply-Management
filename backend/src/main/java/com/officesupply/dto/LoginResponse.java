package com.officesupply.dto;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private String username;
    private String role;
}
