package roomescape.reservation;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        String date,
        String time,
        String status
) {}
