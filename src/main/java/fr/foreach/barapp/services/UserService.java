package fr.foreach.barapp.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.UserMapper;
import fr.foreach.barapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    public UserResponse find(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return userMapper.toResponse(user);
    }

    public UserResponse create(UserCreateRequest request) {
        // on vérifie que l'email n'est pas déjà utilisé
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already in use");
        });

        User user = userMapper.toEntity(request);

        // on récupère le rôle envoyé, sinon CLIENT par défaut
        if (request.getRole() != null) {
            try {
                user.setRole(Role.valueOf(request.getRole()));
            } catch (IllegalArgumentException ex) {
                user.setRole(Role.CLIENT);
            }
        } else {
            user.setRole(Role.CLIENT);
        }

        // on chiffre le mot de passe avant de le sauvegarder
        if (request.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        user.setCreatedAt(Instant.now());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public void save(UserCreateRequest request) {
        create(request);
    }

    public UserResponse findById(Long id) {
        return find(id);
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        // on met à jour seulement les champs envoyés (les autres restent inchangés)
        if (request.getEmail() != null) existing.setEmail(request.getEmail());
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            try {
                existing.setRole(Role.valueOf(request.getRole()));
            } catch (IllegalArgumentException ignored) { /* rôle invalide : on garde l'ancien */ }
        }

        User updatedUser = userRepository.save(existing);
        return userMapper.toResponse(updatedUser);
    }

    public void update(UserUpdateRequest request, Long id) {
        update(id, request);
    }

    public void delete(Long id) {
        remove(id);
    }

    public void remove(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(existing);
    }

    public UserResponse toResponse(User user) {
        return userMapper.toResponse(user);
    }
}
