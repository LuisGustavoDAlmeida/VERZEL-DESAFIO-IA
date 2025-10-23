package Venzel.Desafio.Elite.Dev.controller;

import Venzel.Desafio.Elite.Dev.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message") String message) {
        return chatService.chat(message);
    }
}
