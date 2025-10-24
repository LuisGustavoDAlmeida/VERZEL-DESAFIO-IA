package Venzel.Desafio.Elite.Dev.config;

import Venzel.Desafio.Elite.Dev.model.Lead;
import org.apache.catalina.User;

import java.util.ArrayList;
import java.util.List;

public class ConversationHistory {
    private Lead lead;
    private List<String> messages;

    public ConversationHistory() {
        this.lead = new Lead();
        this.messages = new ArrayList<>();
    }

    public Lead getLead() {
        return lead;
    }
    public List<String> getMessages() {
        return messages;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}