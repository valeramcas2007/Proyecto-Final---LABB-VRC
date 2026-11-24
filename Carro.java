package com.puj.modelo;

public class Carro extends Vehiculo {


    private static final long TARIFA_MINUTO = 120;

    public Carro(String placa) {
        super(placa);
    }

    @Override
    public String getTipo() {
        return "CARRO";
    }

    @Override
    public boolean placaValida() {
        String p = getPlaca();
        if (p.length() != 6) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            char caracterActual = p.charAt(i);
            if (!esLetra(caracterActual)) {
                return false;
            }
        }
        for (int i = 3; i < 6; i++) {
            char caracterActual = p.charAt(i);
            if (!esDigito(caracterActual)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}