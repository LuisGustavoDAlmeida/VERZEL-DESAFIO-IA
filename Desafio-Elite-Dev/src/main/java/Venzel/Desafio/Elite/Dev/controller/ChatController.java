package Venzel.Desafio.Elite.Dev.controller;

import Venzel.Desafio.Elite.Dev.config.ConversationHistory;
import Venzel.Desafio.Elite.Dev.service.ChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {
    @Autowired
    private ChatModelService chatService;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message") String message) {
        ConversationHistory conversationHistory = new ConversationHistory();
        return chatService.chat(conversationHistory, message);
    }
}
