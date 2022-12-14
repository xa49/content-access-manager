package app.resource;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class AccessCodes {
    private UUID currentCode;
    private UUID nextCode;

    public void refreshCodes() {
        currentCode = Objects.requireNonNullElseGet(nextCode, UUID::randomUUID);
        nextCode = UUID.randomUUID();
    }

    public boolean checkAccess(UUID uuid) {
        return currentCode.equals(uuid);
    }
}
