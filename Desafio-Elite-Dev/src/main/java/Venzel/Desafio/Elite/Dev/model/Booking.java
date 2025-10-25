package Venzel.Desafio.Elite.Dev.model;

public class Booking {
    private String eventTypeId;
    private String start;
    private String name;
    private String email;
    private String timezone;

    public Booking(String eventTypeId, String start, String name, String email, String timezone) {
        this.eventTypeId = eventTypeId;
        this.start = start;
        this.name = name;
        this.email = email;
        this.timezone = timezone;
    }

    public Booking() {
    }

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "eventTypeId='" + eventTypeId + '\'' +
                ", start='" + start + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}