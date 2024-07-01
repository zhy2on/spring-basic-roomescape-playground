package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ThemeRepository themeRepository,
                              TimeRepository timeRepository) {
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    public ReservationResponse save(ReservationRequest reservationRequest, String name) {
        Theme theme = themeRepository.findById(reservationRequest.theme())
                .orElseThrow(() -> new RuntimeException("Theme not found"));
        Time time = timeRepository.findById(reservationRequest.time())
                .orElseThrow(() -> new RuntimeException("Time not found"));

        Reservation reservation = new Reservation(name, reservationRequest.date(), time, theme);
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation.getId(), name, theme.getName(),
                reservationRequest.date(), time.getValue());
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllWithThemeAndTime().stream()
                .map(it -> new ReservationResponse(
                        it.getId(),
                        it.getName(),
                        it.getTheme().getName(),
                        it.getDate(),
                        it.getTime().getValue()
                ))
                .toList();
    }
}
