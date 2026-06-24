package com.vttish.bookstore.clients.mapper;

import com.vttish.bookstore.clients.dto.AdminClientResponseDto;
import com.vttish.bookstore.clients.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "isBlocked", source = "user.blocked")
    AdminClientResponseDto toAdminClientDto(Client client);
}
