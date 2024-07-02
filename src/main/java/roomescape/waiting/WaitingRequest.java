package roomescape.waiting;

public record WaitingRequest(
        Long theme,
        String date,
        Long time
) {}
