package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.config.ConversationHistory;
import Venzel.Desafio.Elite.Dev.model.Lead;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaChatService implements ChatModelService {

    @Autowired
    LeadService leadService;
    private final OllamaChatModel model;

    public OllamaChatService(OllamaChatModel model) {
        this.model = model;
    }

    private final String context = """
            Você é um SDR virtual. Seu objetivo é:
            1. Coletar informações do lead:
               - nome
               - email
               - empresa
               - necessidade
               
            2. Se apresente primeiro, diga que você é um SDR virtual, que você está aqui para ajudar ele com o seu negócio.
            3. Após sua apresentação, na mesma mensagem você pode pedir os dados básicos dele: Nome, email e empresa
            4. Faça questão de entender bem a necessidade dele e confirme se é isso mesmo, quando ele confirmar você já pode armazenar. 
            5. Sempre faça a análise para saber se você já tem todos os dados que foram citados no passo 1
            6. Responda de forma natural e profissional.
            7. Ao final, quando todos os dados forem coletados, simule o agendamento da reunião.
            """;

    private String template = """
            <INST>
            Você é um SDR virtual. O estado atual do Lead é:
            %s
            
            Mensagem do usuário: %s
            
            Responda de forma natural, conduza a conversa, e no final de cada resposta inclua o Lead atualizado em JSON no formato:
            {
              "nome": "...",
              "email": "...",
              "empresa": "...",
              "necessidade": "..."
            }
            </INST>
            """;

    @Override
    public String chat(ConversationHistory conversation, String userInput) {
        conversation.addMessage("Usuário: " + userInput);

        String prompt = String.format(template, conversation.getLead().toString(), userInput);
        String response = model.call(prompt);

        conversation.addMessage("IA: " + response);

        String leadJson = extractJsonFromResponse(response); // método que você cria
        if (leadJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Lead updatedLead = mapper.readValue(leadJson, Lead.class);
                conversation.setLead(updatedLead);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar Lead: " + e.getMessage());
            }
        }

        if (leadService.isLeadComplete(conversation.getLead())) {
            System.out.println("Lead completo! Dados coletados:");
            System.out.println(conversation.getLead());
        } else {
            System.out.println("Lead ainda incompleto. Continuar perguntando...");
        }

        return response;
    }

    private String extractJsonFromResponse(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return null;
    }

}
