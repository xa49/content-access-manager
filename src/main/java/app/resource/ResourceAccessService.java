package app.resource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResourceAccessService {

    private final Map<String, ProtectedResource> accessDetails = new HashMap<>();

    private AccessCodeMapper mapper;

    public AccessCodesDto registerNewResource(String identifier, Path path) {
        if (accessDetails.containsKey(identifier)) {
            throw new IllegalArgumentException("Duplicate identifier: " + identifier);
        }
        ProtectedResource addition = ProtectedResource.of(identifier, path);
        addition.refreshCodes();
        accessDetails.put(identifier, addition);
        return mapper.toDto(addition.getAccessCodes());
    }

    public byte[] getResource(String identifier, UUID accessCode) {
        verifyResourceExists(identifier);

        ProtectedResource details = accessDetails.get(identifier);
        if (details.checkAccess(accessCode)) {
            return getFileBytes(details, accessCode);
        } else {
            throw new IllegalArgumentException("Wrong access code " + accessCode + " for resource: " + identifier);
        }
    }

    public AccessCodesDto getAccessCodes(String identifier) {
        verifyResourceExists(identifier);
        return mapper.toDto(accessDetails.get(identifier).getAccessCodes());
    }

    private void verifyResourceExists(String identifier) {
        if (!accessDetails.containsKey(identifier)) {
            throw new IllegalArgumentException("No resource with identifier: " + identifier);
        }
    }

    private byte[] getFileBytes(ProtectedResource details, UUID accessCode) {
        Path path = details.getPathToFile(accessCode);

        try (InputStream in = Files.newInputStream(path)) {
            return in.readAllBytes();
        } catch (NoSuchFileException e) {
throw new IllegalStateException("Resource file not found on server for: " + details.getIdentifier());
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException during getting resource file bytes", e);
        }
    }
}
