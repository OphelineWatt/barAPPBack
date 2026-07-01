package fr.foreach.barapp.mapper;

import fr.foreach.barapp.dtos.UserCreateRequest;
import fr.foreach.barapp.dtos.UserResponse;
import fr.foreach.barapp.dtos.UserUpdateRequest;
import fr.foreach.barapp.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UserUpdateRequest dto, @MappingTarget User entity);

    @Mapping(target = "role", source = "role")
    UserResponse toResponse(User user);
}
