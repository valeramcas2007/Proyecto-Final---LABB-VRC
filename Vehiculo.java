package com.puj.modelo;

import java.io.Serializable;

public abstract class Vehiculo implements Serializable {


    private String placa;

    public Vehiculo(String placa) {
        if (placa == null) {
            placa = "";
        }
        this.placa = placa.trim();
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null) {
            placa = "";
        }
        this.placa = placa.trim();
    }

    protected boolean esLetra(char c) {
        return c >= 'A' && c <= 'Z';
    }

    protected boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    public abstract String getTipo();

    public abstract boolean placaValida();

    public abstract long calcularTarifa(long minutos);

    public long cargoAdicional(long minutos) {
        return 0;
    }

    @Override
    public String toString() {
        return getTipo() + " " + placa;
    }
}