package Venzel.Desafio.Elite.Dev.model;

import Venzel.Desafio.Elite.Dev.config.LeadObserver;

import java.util.ArrayList;
import java.util.List;

public class Lead {
    String ID;
    String nome;
    String email;
    String empresa;
    String necessidade;
    String prazo;
    private List<String> availableSlots;

    private final List<LeadObserver> observers = new ArrayList<>();

    public Lead(String ID,
                String nome,
                String email,
                String empresa,
                String necessidade,
                String prazo,
                List<String> availableSlots) {
        this.ID = ID;
        this.nome = nome;
        this.email = email;
        this.empresa = empresa;
        this.necessidade = necessidade;
        this.prazo = prazo;
        this.availableSlots = availableSlots;
    }

    public Lead() {
    }

    public void setID(String ID) {
        this.ID = ID;
        notifyObserverIfComplete();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        notifyObserverIfComplete();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyObserverIfComplete();
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
        notifyObserverIfComplete();
    }

    public String getNecessidade() {
        return necessidade;
    }

    public void setNecessidade(String necessidade) {
        this.necessidade = necessidade;
        notifyObserverIfComplete();
    }

    public List<LeadObserver> getObservers() {
        return observers;
    }

    public List<String> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<String> availableSlots) {
        this.availableSlots = availableSlots;
    }

    private void notifyObserverIfComplete() {
        if (isComplete()) {
            for (LeadObserver observer : observers) {
                observer.onLeadComplete(this);
            }
        }
    }

    private boolean isComplete() {
        return nome != null && !nome.isEmpty()
                && email != null && !email.isEmpty()
                && empresa != null && !empresa.isEmpty()
                && necessidade != null && !necessidade.isEmpty();
    }

    @Override
    public String toString() {
        return "Lead{" +
                "ID='" + ID + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", empresa='" + empresa + '\'' +
                ", necessidade='" + necessidade + '\'' +
                ", observers=" + observers +
                '}';
    }
}