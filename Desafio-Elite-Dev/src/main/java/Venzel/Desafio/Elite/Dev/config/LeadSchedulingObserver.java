package Venzel.Desafio.Elite.Dev.config;

import Venzel.Desafio.Elite.Dev.model.Lead;
import Venzel.Desafio.Elite.Dev.service.CalendarService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LeadSchedulingObserver implements LeadObserver {
    private final CalendarService calendarService;
    private final String eventTypeId;

    public LeadSchedulingObserver(CalendarService calendarService, String eventTypeId) {
        this.calendarService = calendarService;
        this.eventTypeId = eventTypeId;
    }

    @Override
    public void onLeadComplete(Lead lead) {
        System.out.println("Buscando horarios completos para o lead");

        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusDays(7);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String startStr = formatter.format(start.toInstant());
        String endStr = formatter.format(end.toInstant());

        List<String> availableSlots = calendarService.getAvailableSlots(eventTypeId, startStr, endStr);

        lead.setAvailableSlots(availableSlots);
    }
}
