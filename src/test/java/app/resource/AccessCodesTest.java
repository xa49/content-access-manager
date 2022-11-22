package app.resource;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccessCodesTest {

    @Test
    void checkAccess() {
        AccessCodes accessCodes = new AccessCodes();
        accessCodes.refreshCodes();

        assertTrue(accessCodes.checkAccess(accessCodes.getCurrentCode()));

        UUID wrongCode = UUID.randomUUID();
        while (accessCodes.getCurrentCode().equals(wrongCode)) {
            wrongCode = UUID.randomUUID();
        }
        assertFalse(accessCodes.checkAccess(wrongCode));
    }
}