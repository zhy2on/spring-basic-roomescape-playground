package roomescape.time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AvailableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeId;
    private String time;
    private boolean booked;

    public AvailableTime(Long timeId, String time, boolean booked) {
        this.timeId = timeId;
        this.time = time;
        this.booked = booked;
    }

    public AvailableTime() {
    }

    public Long getTimeId() {
        return timeId;
    }

    public String getTime() {
        return time;
    }

    public boolean isBooked() {
        return booked;
    }
}
