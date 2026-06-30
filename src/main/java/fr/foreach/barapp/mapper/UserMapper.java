package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.entities.Role;
import fr.foreach.barapp.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "stringToRole")
    @Mapping(target = "passwordHash", source = "password")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    UserResponse toResponse(User user);

    @Named("stringToRole")
    default Role stringToRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.CLIENT;
        }
        return Role.valueOf(role.toUpperCase());
    }

    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }
}
