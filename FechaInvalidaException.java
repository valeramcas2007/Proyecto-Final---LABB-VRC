package com.puj.excepciones;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FechaInvalidaException extends ParqueaderoException {




    public FechaInvalidaException(String placa, LocalDate inicio, LocalDate fin) {
        super("La mensualidad de " + placa + " termina el " + fin + ", antes de su inicio el " + inicio);
    }

    public FechaInvalidaException(String placa, LocalDateTime salida, LocalDateTime entrada) {
        super("La salida de " + placa + " (" + salida.toString().replace('T', ' ') + ") es anterior a su entrada (" + entrada.toString().replace('T', ' ') + ")");
    }

    public FechaInvalidaException(String texto, String formato) {
        super("Fecha invalida: '" + texto + "' (use " + formato + ")");
    }
}
