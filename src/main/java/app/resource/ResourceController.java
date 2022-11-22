package app.resource;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping
@AllArgsConstructor
public class ResourceController {

    private ResourceAccessService accessService;

    @GetMapping("/{identifier}/{accessCode}")
    public ResponseEntity<byte[]> getResource(@PathVariable("identifier") String identifier,
                                              @PathVariable("accessCode")UUID accessCode) {
        return ResponseEntity.ok(accessService.getResource(identifier, accessCode));
    }

    @PreAuthorize("hasAuthority('GET_CODES')")
    @GetMapping("/{identifier}")
    public ResponseEntity<AccessCodesDto> getAccessCodes(@PathVariable("identifier") String identifier) {
        return ResponseEntity.ok(accessService.getAccessCodes(identifier));
    }
}
