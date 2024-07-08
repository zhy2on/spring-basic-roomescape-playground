package roomescape;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.reservation.Reservation;
import roomescape.reservation.ReservationRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("test")
public class TestDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public TestDataLoader(MemberRepository memberRepository, ThemeRepository themeRepository,
                          TimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        Member admin = new Member("어드민", "admin@email.com", "password", "ADMIN");
        Member brown = new Member("브라운", "brown@email.com", "password", "USER");
        memberRepository.saveAll(Arrays.asList(admin, brown));

        Theme theme1 = new Theme("테마1", "테마1입니다.");
        Theme theme2 = new Theme("테마2", "테마2입니다.");
        Theme theme3 = new Theme("테마3", "테마3입니다.");
        themeRepository.saveAll(Arrays.asList(theme1, theme2, theme3));

        List<Time> times = Arrays.asList(
                new Time("10:00"),
                new Time("12:00"),
                new Time("14:00"),
                new Time("16:00"),
                new Time("18:00"),
                new Time("20:00")
        );
        timeRepository.saveAll(times);

        Reservation reservation1 = new Reservation("어드민", "2024-03-01", times.get(0), theme1, admin);
        Reservation reservation2 = new Reservation("어드민", "2024-03-01", times.get(1), theme2, admin);
        Reservation reservation3 = new Reservation("어드민", "2024-03-01", times.get(2), theme3, admin);
        Reservation reservation4 = new Reservation("브라운", "2024-03-01", times.get(0), theme2);

        reservationRepository.saveAll(Arrays.asList(reservation1, reservation2, reservation3, reservation4));
    }
}
