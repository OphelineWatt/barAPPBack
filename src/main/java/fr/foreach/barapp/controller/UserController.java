package fr.foreach.barapp.controller;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Validated UserCreateRequest request) {
        userService.save(request);
        // on renvoie juste un 201 Created, sans le corps de l'utilisateur créé
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Validated UserUpdateRequest request) {
        userService.update(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
