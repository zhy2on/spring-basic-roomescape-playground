package roomescape.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.theme t JOIN FETCH r.time ti")
    List<Reservation> findAllWithThemeAndTime();

    List<Reservation> findByDateAndTheme_Id(String date, Long themeId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.theme t JOIN FETCH r.time ti WHERE r.date = :date AND r.theme.id = :themeId")
    List<Reservation> findByDateAndThemeIdWithThemeAndTime(@Param("date") String date, @Param("themeId") Long themeId);
}
