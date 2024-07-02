package roomescape.waiting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {
    @Query("SELECT new roomescape.waiting.WaitingWithRank(" +
            "w, " +
            "(SELECT COUNT(w2) " +
            "FROM Waiting w2 " +
            "WHERE w2.theme = w.theme " +
            "AND w2.date = w.date " +
            "AND w2.time = w.time " +
            "AND w2.id < w.id)) " +
            "FROM Waiting w " +
            "WHERE w.member.id = :memberId")
    List<WaitingWithRank> findWaitingsWithRankByMemberId(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndDateAndThemeIdAndTimeId(Long memberId, String date, Long themeId, Long timeId);

    long countByDateAndThemeIdAndTimeId(String date, Long themeId, Long timeId);
}
