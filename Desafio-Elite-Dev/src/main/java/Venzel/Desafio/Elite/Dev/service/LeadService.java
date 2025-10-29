package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.model.Lead;
import org.springframework.stereotype.Service;

@Service
public class LeadService {
//    public void parseUserData(Lead user, String text) {
//        if (text.contains("Nome:")) {
//            user.setNome(extractAfter(text, "Nome:"));
//        }
//        if (text.contains("Email:")) {
//            user.setEmail(extractAfter(text, "Email:"));
//        }
//        if (text.contains("Empresa:")) {
//            user.setEmpresa(extractAfter(text, "Empresa:"));
//        }
//        if (text.contains("Dor:")) {
//            user.setNecessidade(extractAfter(text, "Dor:"));
//        }
//    }

    private String extractAfter(String text, String key) {
        int start = text.indexOf(key) + key.length();
        int end = text.indexOf("\n", start);
        if (end == -1) end = text.length();
        return text.substring(start, end).trim();
    }

    public boolean isLeadComplete(Lead lead) {
        return lead.getNome() != null && !lead.getNome().isEmpty()
                && lead.getEmail() != null && !lead.getEmail().isEmpty()
                && lead.getEmpresa() != null && !lead.getEmpresa().isEmpty()
                && lead.getNecessidade() != null && !lead.getNecessidade().isEmpty();
    }
}
