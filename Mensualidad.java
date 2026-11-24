package com.puj.modelo;

import java.io.Serializable;
import java.time.LocalDate;

import com.puj.excepciones.FechaInvalidaException;

public class Mensualidad implements Serializable {

    private static final long serialVersionUID = 1L;

    private String placa;
    private LocalDate inicio;
    private LocalDate fin;

    public Mensualidad(String placa, LocalDate inicio, LocalDate fin) throws FechaInvalidaException {
        if (fin.isBefore(inicio)) {
            throw new FechaInvalidaException(placa.trim(), inicio, fin);
        }
        this.placa = placa.trim();
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getPlaca() {
        return placa;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public boolean cubre(LocalDate fecha) {
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    @Override
    public String toString() {
        return "Mensualidad " + placa + " del " + inicio + " al " + fin;
    }
}