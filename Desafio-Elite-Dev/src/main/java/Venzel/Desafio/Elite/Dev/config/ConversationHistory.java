package Venzel.Desafio.Elite.Dev.config;

import Venzel.Desafio.Elite.Dev.model.Lead;
import Venzel.Desafio.Elite.Dev.model.SlotOption;
import Venzel.Desafio.Elite.Dev.service.CalendarService;
import jakarta.annotation.PostConstruct;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SessionScope
@Service
public class ConversationHistory {
    private Lead lead;
    private List<String> messages;
    private boolean awaitingSlotChoice = false;
    private List<SlotOption> lastSlotOptions = new ArrayList<>();

    @Autowired
    private CalendarService calendarService;

    public ConversationHistory() {
        this.messages = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        this.lead = new Lead();
        this.lead.getObservers().add(new LeadSchedulingObserver(calendarService, "3726710"));
    }


    public void addMessage(String message) {
        messages.add(message);
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public boolean isAwaitingSlotChoice() {
        return awaitingSlotChoice;
    }

    public void setAwaitingSlotChoice(boolean awaitingSlotChoice) {
        this.awaitingSlotChoice = awaitingSlotChoice;
    }

    public List<SlotOption> getLastSlotOptions() {
        return lastSlotOptions;
    }

    public void setLastSlotOptions(List<SlotOption> lastSlotOptions) {
        this.lastSlotOptions = lastSlotOptions;
    }
}