package Venzel.Desafio.Elite.Dev.service;

import Venzel.Desafio.Elite.Dev.config.LeadObserver;
import Venzel.Desafio.Elite.Dev.model.Lead;

public class SchedulerService implements LeadObserver {
    @Override
    public void onLeadComplete(Lead lead) {
        System.out.println("Chamando API PARA AGENDAR REUNIÃO");
        System.out.println(lead);
    }
}
