package fr.foreach.barapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

//Recevoir et valider la création d'un utilisateur depuis le front-end
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    // optional: role as string, default CLIENT if null
    private String role;

}
