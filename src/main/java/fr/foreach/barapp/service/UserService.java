package fr.foreach.barapp.service;

import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.repository.UserRepository;
import fr.foreach.barapp.dtos.*;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .name(u.getName())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .createdAt(u.getCreatedAt())
                .build();
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse findById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return toResponse(u);
    }

    @Transactional
    public UserResponse create(UserCreateRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() != null ? Role.valueOf(req.getRole()) : Role.CLIENT)
                .createdAt(Instant.now())
                .build();
        User saved = userRepository.save(u);
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (req.getEmail() != null && !req.getEmail().equals(u.getEmail())) {
            userRepository.findByEmail(req.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) throw new IllegalArgumentException("Email already in use");
            });
            u.setEmail(req.getEmail());
        }
        if (req.getName() != null) u.setName(req.getName());
        if (req.getPassword() != null) u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        if (req.getRole() != null) u.setRole(Role.valueOf(req.getRole()));

        User saved = userRepository.save(u);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(u);
    }
}
