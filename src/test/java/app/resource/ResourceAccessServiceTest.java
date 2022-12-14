package app.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceAccessServiceTest {

    @Spy
    AccessCodeMapperImpl mapper;

    @InjectMocks
    ResourceAccessService service;

    @TempDir
    File tempDir;

    @Test
    void newResourceGetsAddedToMap_withCodesAdded() {
        AccessCodesDto codes = service.registerNewResource("file", Path.of("."));
        assertEquals(36, codes.getCurrentCode().toString().length());
        assertEquals(36, codes.getNextCode().toString().length());
    }

    @Test
    void addingResourceWithExistingIdentifier_throwsException() {
        service.registerNewResource("file", Path.of("."));

        UserErrorException ex = assertThrows(UserErrorException.class,
                () -> service.registerNewResource("file", Path.of("/hello")));
        assertEquals("Duplicate identifier: file", ex.getMessage());
    }

    @Test
    void resourceBytesReturnedOnValidAccess() throws FileNotFoundException {
        File file = new File(tempDir, "validfile.txt");
        try (PrintWriter w = new PrintWriter(file)) {
            w.print("hello");
        }

        AccessCodesDto codes = service.registerNewResource("resource", file.toPath());
        byte[] response = service.getResource("resource", codes.getCurrentCode());
        assertEquals("hello", new String(response, StandardCharsets.UTF_8));
    }

    @Test
    void exceptionOnInvalidIdentifier() {
        UserErrorException ex = assertThrows(UserErrorException.class,
                () -> service.getResource("non-existing", UUID.randomUUID()));
        assertEquals("No resource with identifier: non-existing", ex.getMessage());
    }

    @Test
    void exceptionOnInvalidResourceAccessCode() {
        File file = new File(tempDir, "validfile.txt");

        UUID currentCode = service.registerNewResource("resource", file.toPath())
                .getCurrentCode();

        UUID wrongCode = UUID.randomUUID();
        while (wrongCode.equals(currentCode)) {
            wrongCode = UUID.randomUUID();
        }

        UUID finalWrongCode = wrongCode;
        IllegalResourceAccessException ex = assertThrows(IllegalResourceAccessException.class,
                () -> service.getResource("resource", finalWrongCode));
        assertEquals("Wrong access code " + wrongCode + " for resource: resource", ex.getMessage());
    }

    @Test
    void exceptionOnMissingResourceFile() {
        File file = new File(tempDir, UUID.randomUUID().toString());

        AccessCodesDto codes = service.registerNewResource("resource", file.toPath());
        MissingResourceException ex = assertThrows(MissingResourceException.class,
                () -> service.getResource("resource", codes.getCurrentCode()));
        assertEquals("Resource file not found on server for: resource", ex.getMessage());
    }

    @Test
    void accessCodeChangesOnRequest() throws FileNotFoundException {
        File file = new File(tempDir, "validfile.txt");
        try (PrintWriter w = new PrintWriter(file)) {
            w.print("hello");
        }

        AccessCodesDto codes = service.registerNewResource("resource", file.toPath());
        service.getResource("resource", codes.getCurrentCode());
        assertNotEquals(codes, service.getAccessCodes("resource"));
    }

    @Test
    void getAccessCodes_forExistingResource() {
        AccessCodesDto code = service.registerNewResource("hello", Path.of("."));

        assertEquals(code, service.getAccessCodes("hello"));
    }

    @Test
    void getAccessCodes_forMissingResource() {
        UserErrorException ex = assertThrows(UserErrorException.class,
                () -> service.getAccessCodes("hello"));
        assertEquals("No resource with identifier: hello", ex.getMessage());
    }

}