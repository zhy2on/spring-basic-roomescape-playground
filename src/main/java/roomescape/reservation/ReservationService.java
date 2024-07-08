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
                .orElseThrow(() -> new ThemeNotFoundException("Theme not found: " + reservationRequest.theme()));
        Time time = timeRepository.findById(reservationRequest.time())
                .orElseThrow(() -> new TimeNotFoundException("Time not found: " + reservationRequest.time()));

        Member member = null;
        String name = reservationRequest.name();

        if (loginMember != null) {
            member = memberRepository.findByName(loginMember.name())
                    .orElseThrow(() -> new MemberNotFoundException("Member not found: " + loginMember.name()));
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
                .map(ReservationResponse::of)
                .toList();
    }

    public List<MyReservationResponse> findAllByMemberId(Long memberId) {
        List<MyReservationResponse> result = new ArrayList<>();

        result.addAll(reservationRepository.findAllByMemberIdWithThemeAndTime(memberId).stream()
                .map(MyReservationResponse::ofReservation)
                .toList());

        List<WaitingWithRank> waitings = waitingService.findWaitingsWithRankByMemberId(memberId);
        result.addAll(waitings.stream()
                .map(MyReservationResponse::ofWaiting)
                .toList());

        return result;
    }
}
