package app.resource;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.UUID;

@ToString
@Slf4j
public class ProtectedResource {

    private final String identifier;
    private final Path path;
    private final AccessCodes accessCodes = new AccessCodes();

    public static ProtectedResource of(String identifier, Path path) {
        return new ProtectedResource(identifier, path);
    }

    private ProtectedResource(String identifier, Path path) {
        this.identifier = identifier;
        this.path = path;
    }

    public String getIdentifier() {
        return identifier;
    }

    public AccessCodes getAccessCodes() {
        return accessCodes;
    }

    public void refreshCodes() {
        accessCodes.refreshCodes();
        log.debug("New access code for {}: {}", identifier, accessCodes.getCurrentCode());
    }

    public boolean checkAccess(UUID accessCode) {
        return accessCodes.checkAccess(accessCode);
    }

    public Path  getPathToFile(UUID accessCode) {
        if (checkAccess(accessCode)) {
            return path;
        }
        throw new IllegalStateException("Invalid access code used in request for resource path: " + accessCode);
    }
}
