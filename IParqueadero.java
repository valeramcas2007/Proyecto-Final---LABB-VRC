package com.puj.negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.puj.excepciones.FechaInvalidaException;
import com.puj.excepciones.PlacaInvalidaException;
import com.puj.excepciones.VehiculoYaAdentroException;
import com.puj.excepciones.VehiculoNoEncontradoException;
import com.puj.modelo.Convenio;
import com.puj.modelo.Mensualidad;
import com.puj.modelo.Tiquete;
import com.puj.modelo.Vehiculo;

public interface IParqueadero {

    void agregarMensualidad(Mensualidad m);

    Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada)
            throws PlacaInvalidaException, VehiculoYaAdentroException;

    Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio)
            throws VehiculoNoEncontradoException, FechaInvalidaException;

    List<String> reporteVehiculosAdentro();

    List<String> reporteCierre(LocalDate fecha);

    List<String> reportePlacasFrecuentes();

    List<Tiquete> getTiquetes();
}