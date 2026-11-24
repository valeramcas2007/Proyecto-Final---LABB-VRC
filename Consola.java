package com.puj.presentacion;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.puj.excepciones.ParqueaderoException;
import com.puj.modelo.Carro;
import com.puj.modelo.CarroElectrico;
import com.puj.modelo.Convenio;
import com.puj.modelo.Moto;
import com.puj.modelo.Tiquete;
import com.puj.modelo.Vehiculo;
import com.puj.negocio.IParqueadero;
import com.puj.negocio.Parqueadero;
import com.puj.persistencia.ManejoArchivos;
import java.time.format.ResolverStyle;

public class Consola {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final String ARCHIVO_SISTEMA = "sistema.dat";

    private IParqueadero sistema;
    private Scanner lector;

    public Consola() {
        sistema = new Parqueadero();
        lector = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.iniciar();
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Opcion: ");
            System.out.println();
            switch (opcion) {
                case 1:
                    cargarMensualidades();
                    break;
                case 2:
                    procesarMovimientos();
                    break;
                case 3:
                    registrarEntrada();
                    break;
                case 4:
                    registrarSalida();
                    break;
                case 5:
                    verVehiculosAdentro();
                    break;
                case 6:
                    cierreDeCaja();
                    break;
                case 7:
                    placasFrecuentes();
                    break;
                case 8:
                    guardarSistema();
                    break;
                case 9:
                    cargarSistema();
                    break;
                case 0:
                    System.out.println("Hasta pronto.");
                    break;
                default:
                    System.out.println("Opción invalida.");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("Parqueadero Plaza Central");
        System.out.println("1. Cargar mensualidades");
        System.out.println("2. Procesar movimientos");
        System.out.println("3. Registrar una entrada");
        System.out.println("4. Registrar una salida");
        System.out.println("5. Ver los vehiculos adentro");
        System.out.println("6. Cierre de caja de un día");
        System.out.println("7. Placas frecuentes");
        System.out.println("8. Guardar el sistema");
        System.out.println("9. Cargar el sistema");
        System.out.println("0. Salir");
    }

    private void cargarMensualidades() {
        String carpeta = leerTexto("Carpeta de los archivos: ");
        String ruta = new File(carpeta, "Mensualidades.txt").getPath();
        try {
            List<String> errores = ManejoArchivos.cargarMensualidades(ruta, sistema);
            mostrarResultadoCarga("Mensualidades", errores);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void procesarMovimientos() {
        String carpeta = leerTexto("Carpeta de los archivos: ");
        String ruta = new File(carpeta, "Movimientos.txt").getPath();
        try {
            List<String> errores = ManejoArchivos.procesarMovimientos(ruta, sistema);
            mostrarResultadoCarga("Movimientos", errores);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void mostrarResultadoCarga(String nombre, List<String> errores) {
        System.out.println(nombre + " procesados. Lineas rechazadas: " + errores.size());
        for (String error : errores) {
            System.out.println("  " + error);
        }
    }

    private void registrarEntrada() {
        String placa = leerTexto("Placa: ");
        String tipo = leerTexto("Tipo (CARRO, MOTO, ELECTRICO): ");
        Vehiculo v = crearVehiculo(tipo, placa);
        if (v == null) {
            System.out.println("Tipo de vehiculo desconocido: " + tipo);
            return;
        }
        LocalDateTime fecha = leerFechaHora("Fecha y hora (aaaa-mm-dd hh:mm): ");
        if (fecha == null) {
            return;
        }
        try {
            Tiquete t = sistema.registrarEntrada(v, fecha);
            System.out.println("Entrada registrada:");
            System.out.println(t);
        } catch (ParqueaderoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarSalida() {
        String placa = leerTexto("Placa: ");
        LocalDateTime fecha = leerFechaHora("Fecha y hora de salida (aaaa-mm-dd hh:mm): ");
        if (fecha == null) {
            return;
        }
        String textoConvenio = leerTexto("Convenio (NINGUNO, ALMACEN, RESTAURANTE, CINE): ");
        Convenio convenio = buscarConvenio(textoConvenio);
        if (convenio == null) {
            System.out.println("Convenio desconocido: " + textoConvenio);
            return;
        }
        try {
            Tiquete t = sistema.registrarSalida(placa, fecha, convenio);
            System.out.println("Salida registrada:");
            System.out.println(t);
            System.out.println("Minutos:  " + t.getMinutos());
            System.out.println("Parqueo:  " + t.getValorParqueo());
            System.out.println("Recarga:  " + t.getValorRecarga());
            System.out.println("TOTAL:    " + t.getTotal());
        } catch (ParqueaderoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void verVehiculosAdentro() {
        List<String> lineas = sistema.reporteVehiculosAdentro();
        if (lineas.isEmpty()) {
            System.out.println("No hay vehiculos adentro.");
        } else {
            for (String linea : lineas) {
                System.out.println(linea);
            }
        }
    }

    private void cierreDeCaja() {
        String texto = leerTexto("Fecha (aaaa-mm-dd): ");
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha invalida: " + texto);
            return;
        }
        List<String> lineas = sistema.reporteCierre(fecha);
        for (String linea : lineas) {
            System.out.println(linea);
        }
        String ruta = "Cierre_" + fecha + ".txt";
        try {
            ManejoArchivos.exportarReporte(lineas, ruta);
            System.out.println("Reporte exportado en " + ruta);
        } catch (IOException e) {
            System.out.println("No se pudo exportar el reporte: " + e.getMessage());
        }
    }

    private void placasFrecuentes() {
        List<String> lineas = sistema.reportePlacasFrecuentes();
        if (lineas.isEmpty()) {
            System.out.println("Aun no hay tiquetes cerrados.");
        } else {
            for (String linea : lineas) {
                System.out.println(linea);
            }
        }
    }

    private void guardarSistema() {
        try {
            ManejoArchivos.guardarSistema(sistema, ARCHIVO_SISTEMA);
            System.out.println("Sistema guardado en " + ARCHIVO_SISTEMA);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el sistema: " + e.getMessage());
        }
    }

    private void cargarSistema() {
        try {
            sistema = ManejoArchivos.cargarSistema(ARCHIVO_SISTEMA);
            System.out.println("Sistema recuperado desde " + ARCHIVO_SISTEMA);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("El archivo no contiene un sistema valido.");
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return lector.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private LocalDateTime leerFechaHora(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return LocalDateTime.parse(texto, FORMATO);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha y hora invalidas: " + texto);
            return null;
        }
    }

    private Vehiculo crearVehiculo(String tipo, String placa) {
        if (tipo.equalsIgnoreCase("CARRO")) {
            return new Carro(placa);
        } else if (tipo.equalsIgnoreCase("MOTO")) {
            return new Moto(placa);
        } else if (tipo.equalsIgnoreCase("ELECTRICO")) {
            return new CarroElectrico(placa);
        }
        return null;
    }

    private Convenio buscarConvenio(String texto) {
        if (texto.length() == 0) {
            return Convenio.NINGUNO;
        }
        for (Convenio c : Convenio.values()) {
            if (c.name().equalsIgnoreCase(texto)) {
                return c;
            }
        }
        return null;
    }
}