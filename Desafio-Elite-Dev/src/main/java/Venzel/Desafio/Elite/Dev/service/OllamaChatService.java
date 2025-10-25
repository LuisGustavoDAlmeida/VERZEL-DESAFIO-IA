package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.config.ConversationHistory;
import Venzel.Desafio.Elite.Dev.model.Lead;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OllamaChatService implements ChatModelService {

    @Autowired
    LeadService leadService;
    private final OllamaChatModel model;

    @Autowired
    CalendarService calendarService;

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
            7. Ao final, quando todos os dados forem coletados, pergunte para ele se ele quer agendar a reunião.
            8. Se ele quiser agendar a reunião peça para ele responder com agendar.
            9. Mostre os horários disponíveis caso ele queira agendar
            """;

    private String template = """
            <INST>
            Você é um SDR virtual. O estado atual do Lead é:
            %s
            
            Mensagem do usuário: %s
            
            Responda de forma natural e humana, conduzindo a conversa conforme o contexto.
            Analise a mensagem do usuário e identifique a intenção (intent), que pode ser uma das seguintes:
            
            - "provide_info": o usuário forneceu alguma informação do lead.
            - "schedule_meeting": o usuário quer ver horários para agendar.
            - "choose_slot": o usuário escolheu um horário específico.
            
            Instruções:
            - Mantenha todos os valores existentes do Lead que não forem fornecidos nesta mensagem.
            - Atualize apenas os campos que o usuário fornecer ou confirmar nesta interação.
            - No final, sempre inclua UM BLOCO JSON atualizado do Lead com:
              {
                "intent": "...",
                "chosenSlot": "...",
                "lead": {
                  "nome": "...",
                  "email": "...",
                  "empresa": "...",
                  "necessidade": "..."
                }
              }
            </INST>
            """;

    @Override
    public String chat(ConversationHistory conversation, String userInput) {
        conversation.addMessage("Usuário: " + userInput);

        String prompt = String.format(template, conversation.getLead().toString(), userInput);
        String response = model.call(prompt);

        conversation.addMessage("IA: " + response);

        String leadJson = extractJsonFromResponse(response);
        if (leadJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(leadJson);

                String intent = jsonNode.path("intent").asText();
                String chosenSlot = jsonNode.path("chosenSlot").asText(null);

                JsonNode leadNode = jsonNode.path("lead");
                Lead updatedLead = mapper.treeToValue(leadNode, Lead.class);

                Lead currentLead = conversation.getLead();
                currentLead.setNome(updatedLead.getNome() != null ? updatedLead.getNome() : currentLead.getNome());
                currentLead.setEmail(updatedLead.getEmail() != null ? updatedLead.getEmail() : currentLead.getEmail());
                currentLead.setEmpresa(updatedLead.getEmpresa() != null ? updatedLead.getEmpresa() : currentLead.getEmpresa());
                currentLead.setNecessidade(updatedLead.getNecessidade() != null ? updatedLead.getNecessidade() : currentLead.getNecessidade());


                if ("schedule_meeting".equals(intent) && chosenSlot == null) {
                    List<String> slots = currentLead.getAvailableSlots();

                    if (slots == null) slots = List.of();
                    List<String> formattedSlots = calendarService.formatSlotsForUser(slots);
                    response += "Os horários de agendamento são " + formattedSlots;
                }

                if ("choose_slot".equals(intent) && chosenSlot != null) {
                    String bookingResult = calendarService.createBooking(
                            "3726710",
                            chosenSlot,
                            currentLead.getNome(),
                            currentLead.getEmail(),
                            "America/Sao_Paulo"
                    );
                    response += "Reunião agendada" + bookingResult;
                }

            } catch (Exception e) {
                System.out.println("Erro ao atualizar Lead: " + e.getMessage());
            }
        }

        if (leadService.isLeadComplete(conversation.getLead())) {
            System.out.println("Lead completo! Dados coletados:");
            System.out.println(conversation.getLead());

            if (!conversation.getLead().getObservers().isEmpty()) {
                conversation.getLead().getObservers().forEach(leadObserver ->  leadObserver.onLeadComplete(conversation.getLead()));
            }

            if (userInput.toLowerCase().contains("agendar")) {
                List<String> slots = conversation.getLead().getAvailableSlots();

                if (slots == null) slots = List.of();

                List<String> formattedSlots = calendarService.formatSlotsForUser(slots);
                response += "Os horários de agendamento disponíveis são: \n" + formattedSlots;
            }
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
