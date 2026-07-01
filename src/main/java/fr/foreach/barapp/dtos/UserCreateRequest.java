package fr.foreach.barapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

//Recevoir et valider la création d'un utilisateur depuis le front-end
@Data
@Builder
public class UserCreateRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String name;

    private String role; // rôle de l'utilisateur : "BARMAKER" ou "CLIENT"
}
