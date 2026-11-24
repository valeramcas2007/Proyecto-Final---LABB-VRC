package com.puj.persistencia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.puj.excepciones.FechaInvalidaException;
import com.puj.excepciones.ParqueaderoException;
import com.puj.modelo.Carro;
import com.puj.modelo.CarroElectrico;
import com.puj.modelo.Convenio;
import com.puj.modelo.Mensualidad;
import com.puj.modelo.Moto;
import com.puj.modelo.Vehiculo;
import com.puj.negocio.IParqueadero;
import java.time.format.ResolverStyle;

public class ManejoArchivos {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    public static List<String> cargarMensualidades(String ruta, IParqueadero sistema) throws IOException {
        List<String> errores = new ArrayList<String>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                linea = linea.trim();
                if (linea.length() > 0 && !linea.startsWith("#")) {
                    try {
                        procesarMensualidad(linea, sistema);
                    } catch (ParqueaderoException e) {
                        errores.add("Linea " + numero + ": " + e.getMessage());
                    }
                }
            }
        }
        return errores;
    }

    public static List<String> procesarMovimientos(String ruta, IParqueadero sistema) throws IOException {
        List<String> errores = new ArrayList<String>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                linea = linea.trim();
                if (linea.length() > 0 && !linea.startsWith("#")) {
                    try {
                        procesarMovimiento(linea, sistema);
                    } catch (ParqueaderoException e) {
                        errores.add("Linea " + numero + ": " + e.getMessage());
                    }
                }
            }
        }
        return errores;
    }

    public static void exportarReporte(List<String> lineas, String ruta) throws IOException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ruta))) {
            for (String linea : lineas) {
                escritor.println(linea);
            }
        }
    }

    public static void guardarSistema(IParqueadero sistema, String ruta) throws IOException {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta))) {
            salida.writeObject(sistema);
        }
    }

    public static IParqueadero cargarSistema(String ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta))) {
            return (IParqueadero) entrada.readObject();
        }
    }

    private static void procesarMensualidad(String linea, IParqueadero sistema) throws ParqueaderoException {
        String[] campos = linea.split("\\*", -1);
        if (campos.length != 3) {
            throw new ParqueaderoException("FORMATO invalido: se esperaban 3 campos y llegaron " + campos.length);
        }
        String placa = campos[0].trim();
        if (placa.length() == 0) {
            throw new ParqueaderoException("La placa está vacia");
        }
        LocalDate inicio = leerFecha(campos[1].trim());
        LocalDate fin = leerFecha(campos[2].trim());
        sistema.agregarMensualidad(new Mensualidad(placa, inicio, fin));
    }

    private static void procesarMovimiento(String linea, IParqueadero sistema) throws ParqueaderoException {
        String[] campos = linea.split("\\*", -1);
        String clase = campos[0].trim();

        if (clase.equals("E")) {
            if (campos.length != 4) {
                throw new ParqueaderoException("FORMATO invalido: una entrada trae 4 campos y llegaron " + campos.length);
            }
            Vehiculo v = crearVehiculo(campos[2].trim(), campos[1].trim());
            LocalDateTime fecha = leerFechaHora(campos[3].trim());
            sistema.registrarEntrada(v, fecha);
        } else if (clase.equals("S")) {
            if (campos.length != 4) {
                throw new ParqueaderoException("FORMATO invalido: una salida trae 4 campos y llegaron " + campos.length);
            }
            LocalDateTime fecha = leerFechaHora(campos[2].trim());
            Convenio convenio = buscarConvenio(campos[3].trim());
            sistema.registrarSalida(campos[1].trim(), fecha, convenio);
        } else {
            throw new ParqueaderoException("Tipo de movimiento desconocido: '" + clase + "'");
        }
    }

    private static Vehiculo crearVehiculo(String tipo, String placa) throws ParqueaderoException {
        if (tipo.equalsIgnoreCase("CARRO")) {
            return new Carro(placa);
        } else if (tipo.equalsIgnoreCase("MOTO")) {
            return new Moto(placa);
        } else if (tipo.equalsIgnoreCase("ELECTRICO")) {
            return new CarroElectrico(placa);
        }
        throw new ParqueaderoException("Tipo de vehiculo desconocido: '" + tipo + "'");
    }

    private static Convenio buscarConvenio(String texto) throws ParqueaderoException {
        for (Convenio c : Convenio.values()) {
            if (c.name().equals(texto)) {
                return c;
            }
        }
        throw new ParqueaderoException("Convenio desconocido: '" + texto + "'");
    }

    private static LocalDate leerFecha(String texto) throws FechaInvalidaException {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException(texto, "aaaa-mm-dd");
        }
    }

    private static LocalDateTime leerFechaHora(String texto) throws FechaInvalidaException {
        try {
            return LocalDateTime.parse(texto, FORMATO);
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException(texto, "aaaa-mm-dd hh:mm");
        }
    }
}