package app.resource;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class AccessCodesDto {
    private UUID currentCode;
    private UUID nextCode;
}
