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

    private final List<LeadObserver> observers = new ArrayList<>();

    public Lead(String ID, String nome, String email, String empresa, String necessidade) {
        this.ID = ID;
        this.nome = nome;
        this.email = email;
        this.empresa = empresa;
        this.necessidade = necessidade;
    }

    public Lead() {
    }

    public String getID() {
        return ID;
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