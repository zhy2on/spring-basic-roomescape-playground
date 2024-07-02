package roomescape.waiting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.member.LoginMember;

import java.net.URI;

@RestController
public class WaitingController {
    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> createWaiting(@RequestBody WaitingRequest waitingRequest, LoginMember loginMember) {
        if (loginMember == null) {
            return ResponseEntity.status(401).build();
        }
        WaitingResponse waiting = waitingService.createWaiting(waitingRequest, loginMember.id());
        return ResponseEntity.created(URI.create("/waitings/" + waiting.id())).body(waiting);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable Long id) {
        waitingService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
