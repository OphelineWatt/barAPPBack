package fr.foreach.barapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

//Recevoir et valider la mise à jour d'un utilisateur depuis le front-end
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @Email
    private String email;

    @Size(min = 2, max = 100)
    private String name;

    @Size(min = 6, max = 100)
    private String password;

    private String role;
}
