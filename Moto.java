package com.puj.modelo;

public class Moto extends Vehiculo {

    private static final long serialVersionUID = 1L;

    private static final long TARIFA_MINUTO = 80;

    public Moto(String placa) {
        super(placa);
    }

    @Override
    public String getTipo() {
        return "MOTO";
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
        char caracterActual = p.charAt(3);
        if (!esDigito(caracterActual)) {
            return false;
        }
        caracterActual = p.charAt(4);
        if (!esDigito(caracterActual)) {
            return false;
        }
        caracterActual = p.charAt(5);
        if (!esLetra(caracterActual)) {
            return false;
        }
        return true;
    }

    @Override
    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}