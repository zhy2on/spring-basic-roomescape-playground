package roomescape.waiting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.MemberNotFoundException;
import roomescape.exception.ThemeNotFoundException;
import roomescape.exception.TimeNotFoundException;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.reservation.ReservationRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.List;

@Service
public class WaitingService {
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public WaitingService(WaitingRepository waitingRepository, MemberRepository memberRepository,
                          ThemeRepository themeRepository, TimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public WaitingResponse createWaiting(WaitingRequest waitingRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
        Theme theme = themeRepository.findById(waitingRequest.theme())
                .orElseThrow(() -> new ThemeNotFoundException("Theme not found: " + waitingRequest.theme()));
        Time time = timeRepository.findById(waitingRequest.time())
                .orElseThrow(() -> new TimeNotFoundException("Time not found: " + waitingRequest.time()));

        if (!isSlotReserved(waitingRequest.date(), theme.getId(), time.getId())) {
            throw new IllegalStateException("해당 시간대에 예약이 없어 예약 대기를 할 수 없습니다.");
        }

        if (hasExistingWaiting(memberId, waitingRequest.date(), theme.getId(), time.getId())) {
            throw new IllegalStateException("이미 예약 대기 중입니다.");
        }

        Waiting waiting = new Waiting(member, waitingRequest.date(), time, theme);
        Waiting savedWaiting = waitingRepository.save(waiting);

        long rank = waitingRepository.countByDateAndThemeIdAndTimeId(waitingRequest.date(), theme.getId(), time.getId());

        return new WaitingResponse(savedWaiting.getId(), theme.getName(),
                waitingRequest.date(), time.getValue(), rank);
    }

    @Transactional
    public void deleteWaiting(Long id) {
        waitingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<WaitingWithRank> findWaitingsWithRankByMemberId(Long memberId) {
        return waitingRepository.findWaitingsWithRankByMemberId(memberId);
    }

    private boolean isSlotReserved(String date, Long themeId, Long timeId) {
        return reservationRepository.existsByDateAndThemeIdAndTimeId(date, themeId, timeId);
    }

    private boolean hasExistingWaiting(Long memberId, String date, Long themeId, Long timeId) {
        return waitingRepository.existsByMemberIdAndDateAndThemeIdAndTimeId(memberId, date, themeId, timeId);
    }
}
