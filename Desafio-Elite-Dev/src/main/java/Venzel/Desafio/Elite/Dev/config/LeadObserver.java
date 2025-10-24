package Venzel.Desafio.Elite.Dev.config;

import Venzel.Desafio.Elite.Dev.model.Lead;

public interface LeadObserver {
    void onLeadComplete(Lead lead);
}
