package app.resource;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("${endpoints.resource}")
@AllArgsConstructor
public class ResourceController {

    private ResourceAccessService accessService;

    @GetMapping("/{identifier}/{accessCode}")
    public ResponseEntity<byte[]> getResource(@PathVariable("identifier") String identifier,
                                              @PathVariable("accessCode") UUID accessCode) {
        return ResponseEntity.ok(accessService.getResource(identifier, accessCode));
    }

    @PreAuthorize("hasAuthority('GET_CODES')")
    @GetMapping("/{identifier}")
    public ResponseEntity<AccessCodesDto> getAccessCodes(@PathVariable("identifier") String identifier) {
        return ResponseEntity.ok(accessService.getAccessCodes(identifier));
    }

    @ExceptionHandler(IllegalResourceAccessException.class)
    public ResponseEntity<Problem> illegalAccess(IllegalResourceAccessException ex) {
        Problem problem = Problem.builder()
                .withType(URI.create("illegal-access"))
                .withStatus(Status.FORBIDDEN)
                .withTitle("Unauthorized access")
                .withDetail(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(problem);
    }

    @ExceptionHandler(UserErrorException.class)
    public ResponseEntity<Problem> badRequest(UserErrorException ex) {
        Problem problem = Problem.builder()
                .withType(URI.create("user-error"))
                .withStatus(Status.BAD_REQUEST)
                .withTitle("Bad request")
                .withDetail(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(MissingResourceException.class)
    public ResponseEntity<Problem> missingResource(MissingResourceException ex) {
        Problem problem = Problem.builder()
                .withType(URI.create("missing-resource"))
                .withStatus(Status.NOT_FOUND)
                .withTitle("Resource missing")
                .withDetail(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem);
    }
}
