package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.model.Booking;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public String createBooking(String eventTypeId, String start, String name, String email, String timeZone) {
        try {
            String url = "hhtps://api.cal.com/v2/bookings";

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

            Booking booking = new Booking(eventTypeId, start, name, email, timeZone);

            String response = restTemplate.postForObject(url, entity, String.class);
            System.out.println("Booking criado");

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
            String month = String.valueOf(zonedDateTime.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
            String year = String.valueOf(zonedDateTime.getYear());
            String hour = String.format("%02d:%02d", zonedDateTime.getHour(), zonedDateTime.getMinute());

            formatted.add(String.format("%s de %s de %s às %s", day, month, year, hour));
        }
        return formatted;
    }

    public String extractChosenSlot(String userInput, List<String> availableSlots) {
        if (availableSlots == null || availableSlots.isEmpty()) return null;

        for (String slot : availableSlots) {
            if (userInput.contains(slot.substring(11, 16))) {
                return  slot;
            }
        }
        return userInput;
    }
}
