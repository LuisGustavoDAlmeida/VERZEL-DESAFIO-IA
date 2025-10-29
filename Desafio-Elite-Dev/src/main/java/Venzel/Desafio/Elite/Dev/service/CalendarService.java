package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.model.Booking;
import Venzel.Desafio.Elite.Dev.model.SlotOption;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CalendarService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "https://api.cal.com/v2/slots/available";
    private final String API_VERSION = "2024-08-13";
    private final String API_KEY = "cal_live_a0da26267b8daadba65576e3752eac9e";

    public List<String> getAvailableSlots(String eventTypeId, String start, String end) {
        try {
            String url = String.format("%s?eventTypeId=%s&startTime=%s&endTime=%s",
                    BASE_URL,
                    eventTypeId,
                    start,
                    end);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.set("cal-api-version", API_VERSION);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String json = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();

            JsonNode root = objectMapper.readTree(json);
            JsonNode slotsNode = root.path("data").path("slots");

            List<String> slotsList = new ArrayList<>();

            slotsNode.fieldNames().forEachRemaining(date -> {
                for (JsonNode slot : slotsNode.get(date)) {
                    slotsList.add(slot.get("time").asText());
                }
            });

            return slotsList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getAvailableSlotsForNextWeek(String eventTypeId) {
        if (eventTypeId == null) {
            eventTypeId = "3726710";
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime oneWeekFromNow = now.plusDays(7);

        String startIso = now.toInstant().toString();
        String endIso = oneWeekFromNow.toInstant().toString();

        System.out.println("Buscando slots de: " + startIso + " até " + endIso);

        return getAvailableSlots(eventTypeId, startIso, endIso);
    }

    public String createBooking(String eventTypeId, String start, String name, String email, String timeZone) {
        try {
            String url = "https://api.cal.com/v2/bookings";

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode request = objectMapper.createObjectNode();
            request.put("eventTypeId", eventTypeId);
            request.put("start", start);

            ObjectNode attendee = objectMapper.createObjectNode();
            attendee.put("name", name);
            attendee.put("email", email);
            attendee.put("timeZone", timeZone);

            request.set("attendee", attendee);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.set("Content-Type", "application/json");
            headers.set("cal-api-version", API_VERSION);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);


            String response = restTemplate.postForObject(url, entity, String.class);
            System.out.println("Booking criado: " + response);

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> formatSlotsForUser(List<String> rawSlots) {
        List<String> formatted = new ArrayList<>();

        for (String slot : rawSlots) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(slot);
            String day = String.valueOf(zonedDateTime.getDayOfMonth());
            String month = zonedDateTime.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            String year = String.valueOf(zonedDateTime.getYear());
            String hour = String.format("%02d:%02d", zonedDateTime.getHour(), zonedDateTime.getMinute());

            formatted.add(String.format("%s de %s de %s às %s", day, month, year, hour));
        }
        return formatted;
    }

    public String extractChosenSlot(String userInput, List<SlotOption> slotOptions) {
        if (slotOptions == null || slotOptions.isEmpty()) return null;

        for (SlotOption slot : slotOptions) {
            if (userInput.contains(slot.getDisplay())) {
                return slot.getIso();
            }
        }
        return null;
    }


    public List<SlotOption> buildSlotOptions(List<String> rawSlots) {
        List<SlotOption> options = new ArrayList<>();
        for (String slot : rawSlots) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(slot);
            String display = String.format("%d de %s de %d às %02d:%02d",
            zonedDateTime.getDayOfMonth(),
            zonedDateTime.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")),
            zonedDateTime.getYear(),
            zonedDateTime.getHour(),
            zonedDateTime.getMinute());

            options.add(new SlotOption(slot, display));
        }
        return options;
    }
}
