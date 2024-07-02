package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.member.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;
import roomescape.waiting.WaitingService;
import roomescape.waiting.WaitingWithRank;

import java.util.ArrayList;
import java.util.List;

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
                .orElseThrow(() -> new RuntimeException("Theme not found"));
        Time time = timeRepository.findById(reservationRequest.time())
                .orElseThrow(() -> new RuntimeException("Time not found"));

        Member member = null;
        String name = reservationRequest.name();

        if (loginMember != null) {
            member = memberRepository.findByName(loginMember.name())
                    .orElseThrow(() -> new RuntimeException("Member not found"));
            name = member.getName();
        }

        Reservation reservation = new Reservation(name, reservationRequest.date(), time, theme, member);
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

    public List<MyReservationResponse> findAllByMemberId(Long memberId) {
        List<MyReservationResponse> result = new ArrayList<>();

        result.addAll(reservationRepository.findAllByMemberIdWithThemeAndTime(memberId).stream()
                .map(it -> new MyReservationResponse(
                        it.getId(),
                        it.getTheme().getName(),
                        it.getDate(),
                        it.getTime().getValue(),
                        "예약"
                ))
                .toList());

        List<WaitingWithRank> waitings = waitingService.findWaitingsWithRankByMemberId(memberId);
        result.addAll(waitings.stream()
                .map(it -> new MyReservationResponse(
                        it.getWaiting().getId(),
                        it.getWaiting().getTheme().getName(),
                        it.getWaiting().getDate(),
                        it.getWaiting().getTime().getValue(),
                        (it.getRank() + 1) + "번째 예약대기"
                ))
                .toList());

        return result;
    }
}
