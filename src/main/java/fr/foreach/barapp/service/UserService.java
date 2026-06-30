package fr.foreach.barapp.service;

import fr.foreach.barapp.entities.User;
import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.mapper.UserMapper;
import fr.foreach.barapp.repository.UserRepository;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponse toResponse(User u) {
        return userMapper.toResponse(u);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        User u = userMapper.toEntity(req);
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        return toResponse(userRepository.save(u));
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

        return toResponse(userRepository.save(u));
    }

    @Transactional
    public void delete(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(u);
    }
}
