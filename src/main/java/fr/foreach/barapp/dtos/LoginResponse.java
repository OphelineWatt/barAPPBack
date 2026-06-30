package fr.foreach.barapp.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String name;
    private String role;
}
