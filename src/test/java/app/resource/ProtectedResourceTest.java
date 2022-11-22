package app.resource;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProtectedResourceTest {

    @Test
    void generatingFirstAccessCodesWorks() {
        ProtectedResource details = ProtectedResource.of("file", Path.of("."));

        assertNull(details.getAccessCodes().getCurrentCode());
        assertNull(details.getAccessCodes().getNextCode());

        details.refreshCodes();

        assertNotNull(details.getAccessCodes().getCurrentCode());
        assertNotNull(details.getAccessCodes().getNextCode());
    }

    @Test
    void refreshCodes_movesNextUUIDToCurrentUUID() {
        ProtectedResource details = ProtectedResource.of("file", Path.of("."));
        details.refreshCodes();

        UUID next = details.getAccessCodes().getNextCode();

        details.refreshCodes();

        assertEquals(next, details.getAccessCodes().getCurrentCode());
    }

    @Test
    void pathToResource_returnedOnValidAccess() {
        Path  path = Path.of("/path/to/file");
        ProtectedResource details = ProtectedResource.of("file", path);
        details.refreshCodes();

        UUID current = details.getAccessCodes().getCurrentCode();

        assertEquals(path, details.getPathToFile(current));
    }

    @Test
    void pathToResource_exceptionOnInvalidAccess() {
        Path  path = Path.of("/path/to/file");
        ProtectedResource details = ProtectedResource.of("file", path);
        details.refreshCodes();

        UUID wrong = UUID.randomUUID();
        while (wrong.equals(details.getAccessCodes().getCurrentCode())) {
            wrong = UUID.randomUUID();
        }

        UUID finalWrong = wrong;
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> details.getPathToFile(finalWrong));
        assertEquals("Invalid access code used in request for resource path: " + wrong, ex.getMessage());
    }

}