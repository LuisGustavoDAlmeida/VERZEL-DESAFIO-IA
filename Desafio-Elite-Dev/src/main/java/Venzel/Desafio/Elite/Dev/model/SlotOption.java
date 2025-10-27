package Venzel.Desafio.Elite.Dev.model;

import Venzel.Desafio.Elite.Dev.service.CalendarService;

import java.util.ArrayList;
import java.util.List;

public class SlotOption {
    private String iso;
    private String display;

    public SlotOption(String iso, String display) {
        this.iso = iso;
        this.display = display;
    }

    public SlotOption() {
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "SlotOption{" +
                "iso='" + iso + '\'' +
                ", display='" + display + '\'' +
                '}';
    }
}
