package com.puj.excepciones;

public class PlacaInvalidaException extends ParqueaderoException {



    public PlacaInvalidaException(String placa, String tipo) {
        super("La placa " + placa + " no es valida para un vehiculo " + tipo);
    }
}
