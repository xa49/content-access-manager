package app.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AccessCodesDto {
    private UUID currentCode;
    private UUID nextCode;
}
