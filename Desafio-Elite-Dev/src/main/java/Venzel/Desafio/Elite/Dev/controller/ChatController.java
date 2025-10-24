package Venzel.Desafio.Elite.Dev.controller;

import Venzel.Desafio.Elite.Dev.config.ConversationHistory;
import Venzel.Desafio.Elite.Dev.config.LeadSchedulingObserver;
import Venzel.Desafio.Elite.Dev.model.Lead;
import Venzel.Desafio.Elite.Dev.service.CalendarService;
import Venzel.Desafio.Elite.Dev.service.ChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {
    @Autowired
    private ChatModelService chatService;


    private final ConversationHistory conversationHistory;

    public ChatController(CalendarService calendarService) {
        this.conversationHistory = new ConversationHistory();

        Lead lead = new Lead();
        lead.getObservers().add(new LeadSchedulingObserver(calendarService, "3726710"));
        conversationHistory.setLead(lead);
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message") String message) {
        return chatService.chat(conversationHistory, message);
    }
}
