package roomescape.reservation;

public record ReservationResponse(
        Long id,
        String name,
        String theme,
        String date,
        String time
) {
    public static ReservationResponse of(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getValue()
        );
    }
}
