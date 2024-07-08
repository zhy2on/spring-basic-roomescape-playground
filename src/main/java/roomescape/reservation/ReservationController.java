package roomescape.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.LoginMember;

import java.net.URI;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> list() {
        return reservationService.findAll();
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MyReservationResponse>> listMyReservations(LoginMember loginMember) {
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MyReservationResponse> reservations = reservationService.findAllByMemberId(loginMember.id());
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest reservationRequest, LoginMember loginMember) {
        if (reservationRequest.date() == null
                || reservationRequest.theme() == null
                || reservationRequest.time() == null) {
            return ResponseEntity.badRequest().build();
        }

        ReservationResponse reservation = reservationService.save(reservationRequest, loginMember);

        return ResponseEntity.created(URI.create("/reservations/" + reservation.id())).body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
