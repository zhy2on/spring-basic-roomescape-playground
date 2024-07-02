package roomescape.waiting;

public record WaitingResponse(
        Long id,
        String theme,
        String date,
        String time,
        Long rank
) {}
