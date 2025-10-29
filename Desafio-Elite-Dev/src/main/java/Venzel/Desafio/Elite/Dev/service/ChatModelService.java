package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.config.ConversationHistory;

public interface ChatModelService {
    String chat(ConversationHistory conversation, String userInput);
}
