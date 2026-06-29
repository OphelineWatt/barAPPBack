package fr.foreach.barapp.dtos;

import lombok.*;
import java.time.Instant;

//Renvoyer au client sans données sensibles 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String role;
    private Instant createdAt;
}
