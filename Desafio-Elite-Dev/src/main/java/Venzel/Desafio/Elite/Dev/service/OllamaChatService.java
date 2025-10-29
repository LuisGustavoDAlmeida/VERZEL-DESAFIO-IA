package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.config.ConversationHistory;
import Venzel.Desafio.Elite.Dev.model.Lead;
import Venzel.Desafio.Elite.Dev.model.SlotOption;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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
            8. Se ele quiser agendar a reunião peça para ele responder com "agendar".
            
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
            - "choose_slot": o usuário escolheu um horário específico entre os apresentados (por exemplo, "segunda às 10h", "09:00", "quarta às 15h" etc).
            
            Instruções importantes:
            - Se o usuário disser algo como “quero agendar”, “mostrar horários” ou “ver agenda”, use "schedule_meeting".
            - Se o usuário disser um horário, data, ou escolher um dos slots que foram mostrados, use "choose_slot" e defina o campo "chosenSlot" exatamente com o horário em formato ISO (exemplo: "2025-10-27T12:00:00Z").
            - Sempre mantenha os dados existentes do lead; só atualize os campos novos fornecidos.
            - No final da resposta, inclua **apenas um JSON** com o formato:
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

        String fullHistory = String.join("\n", conversation.getMessages());
        String prompt = String.format(context + "\n\nHistórico da conversa:\n" + fullHistory +
                "\n\nAgora continue a conversa. Última mensagem do usuário: " + userInput +
                "\n\n" + template, conversation.getLead().toString(), userInput);
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


                if ("schedule_meeting".equals(intent) && chosenSlot == null && !conversation.isAwaitingSlotChoice()) {
                    List<String> allSlots = calendarService.getAvailableSlotsForNextWeek("3726710").stream()
                            .filter(slot -> ZonedDateTime.parse(slot).isAfter(ZonedDateTime.now())).toList();

                    List<SlotOption> options = calendarService.buildSlotOptions(allSlots);

                    if (options.isEmpty()) {
                        response += "Me desculpe, mas você não tem horários disponíveis no momento.";
                    } else {
                        conversation.setLastSlotOptions(options);
                        conversation.setAwaitingSlotChoice(true);

                        if (response.contains("{")) {
                            response = response.substring(0, response.indexOf("{")).trim();
                        }

                        response += "Os horários de agendamento disponíveis são:\n";

                        for (int i = 0; i < options.size(); i++) {
                            response += (i + 1) + ". " + options.get(i).getDisplay() + "\n";
                        }

                        response += "Por favor, escolha um número correspondente ao horário.";
                    }
                }

                else if (conversation.isAwaitingSlotChoice()) {
                    try {
                        int choice = Integer.parseInt(userInput.trim()) - 1;
                        List<SlotOption> options = conversation.getLastSlotOptions();

                        if (choice >= 0 && choice < options.size()) {
                            String chosenIso = options.get(choice).getIso();

                            calendarService.createBooking(
                                    "3726710",
                                    chosenIso,
                                    currentLead.getNome(),
                                    currentLead.getEmail(),
                                    "America/Sao_Paulo"
                            );

                            response = "Ok, sua reunião foi agendada para " + options.get(choice).getDisplay();
                            conversation.setAwaitingSlotChoice(false);
                            conversation.setLastSlotOptions(null);
                        } else {
                            response = "Escolha inválida. Por favor selecione um número da lista";
                        }
                    } catch (NumberFormatException e) {
                        if (response.contains("{")) {
                            response = response.substring(0, response.indexOf("{")).trim();
                        }

                        response = "Escolha um dos números fornecidos na lista que condiza com seu horário escolhido";
                    }
                } else {
                    if (response.contains("{")) {
                        response = response.substring(0, response.indexOf("{")).trim();
                    }
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
