package roomescape.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.theme t JOIN FETCH r.time ti")
    List<Reservation> findAllWithThemeAndTime();

    List<Reservation> findByDateAndTheme_Id(String date, Long themeId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.theme t JOIN FETCH r.time ti WHERE r.member.id = :memberId")
    List<Reservation> findAllByMemberIdWithThemeAndTime(@Param("memberId") Long memberId);

    boolean existsByDateAndThemeIdAndTimeId(String date, Long themeId, Long timeId);
}
