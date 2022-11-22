package app.resource;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccessCodeMapper {

    AccessCodesDto toDto(AccessCodes accessCodes);
}
