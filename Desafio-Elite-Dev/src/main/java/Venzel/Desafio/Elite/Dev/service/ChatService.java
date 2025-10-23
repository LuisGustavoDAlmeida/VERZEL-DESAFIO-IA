package Venzel.Desafio.Elite.Dev.service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final OllamaChatModel model;

    private final String context = """
            
            """;

    public ChatService(OllamaChatModel model) {
        this.model = model;
    }

    public String chat(String input) {
        String template = """
                <INST> Você é uma IA que atende clientes em um chat que estão querendo agendar uma reunião sobre um produto, você irá conduzir a conversa
            até saber se eles querem agendar essa reunião e se sim, você fingirá que adicionou no calendário. </INST>
            conteúdo: {context}
            pergunta: {input}
                """;

        return model.call(input);
    }
}
