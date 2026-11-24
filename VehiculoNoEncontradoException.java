package com.puj.excepciones;

public class VehiculoNoEncontradoException extends ParqueaderoException {



    public VehiculoNoEncontradoException(String placa) {
        super("No hay un tiquete abierto para la placa " + placa);
    }
}
