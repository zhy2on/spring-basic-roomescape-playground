package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.exception.MemberNotFoundException;
import roomescape.exception.ThemeNotFoundException;
import roomescape.exception.TimeNotFoundException;
import roomescape.member.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;
import roomescape.waiting.WaitingService;
import roomescape.waiting.WaitingWithRank;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;
    private final MemberRepository memberRepository;
    private final WaitingService waitingService;

    public ReservationService(ReservationRepository reservationRepository,
                              ThemeRepository themeRepository,
                              TimeRepository timeRepository,
                              MemberRepository memberRepository,
                              WaitingService waitingService) {
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.memberRepository = memberRepository;
        this.waitingService = waitingService;
    }


    public ReservationResponse save(ReservationRequest reservationRequest, LoginMember loginMember) {
        Theme theme = themeRepository.findById(reservationRequest.theme())
                .orElseThrow(() -> new ThemeNotFoundException("Theme not found: " + reservationRequest.theme()));
        Time time = timeRepository.findById(reservationRequest.time())
                .orElseThrow(() -> new TimeNotFoundException("Time not found: " + reservationRequest.time()));

        Member member = Optional.ofNullable(loginMember)
                .map(lm -> memberRepository.findByName(lm.name())
                        .orElseThrow(() -> new MemberNotFoundException("Member not found: " + lm.name())))
                .orElse(null);

        String name = Optional.ofNullable(reservationRequest.name())
                .filter(n -> !n.isEmpty())
                .orElseGet(() -> Optional.ofNullable(member)
                        .map(Member::getName)
                        .orElse(null));

        Reservation reservation = new Reservation(name, reservationRequest.date(), time, theme, member);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.of(savedReservation);
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllWithThemeAndTime().stream()
                .map(ReservationResponse::of)
                .toList();
    }

    public List<MyReservationResponse> findAllByMemberId(Long memberId) {
        List<MyReservationResponse> reservations = reservationRepository.findAllByMemberIdWithThemeAndTime(memberId).stream()
                .map(MyReservationResponse::ofReservation)
                .toList();

        List<WaitingWithRank> waitings = waitingService.findWaitingsWithRankByMemberId(memberId);
        List<MyReservationResponse> waitingResponses = waitings.stream()
                .map(MyReservationResponse::ofWaiting)
                .toList();

        return Stream.concat(reservations.stream(), waitingResponses.stream())
                .toList();
    }
}
