package com.puj.excepciones;

public class VehiculoYaAdentroException extends ParqueaderoException {



    public VehiculoYaAdentroException(String placa) {
        super("El vehiculo " + placa + " ya esta dentro del parqueadero");
    }
}
