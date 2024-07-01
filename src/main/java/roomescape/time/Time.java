package roomescape.time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String time_value;

    public Time(Long id, String value) {
        this.id = id;
        this.time_value = value;
    }

    public Time(String value) {
        this.time_value = value;
    }

    public Time() {
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return time_value;
    }
}
