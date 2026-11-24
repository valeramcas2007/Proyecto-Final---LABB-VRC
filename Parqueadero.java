package com.puj.negocio;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.puj.excepciones.FechaInvalidaException;
import com.puj.excepciones.PlacaInvalidaException;
import com.puj.excepciones.VehiculoNoEncontradoException;
import com.puj.excepciones.VehiculoYaAdentroException;
import com.puj.modelo.Convenio;
import com.puj.modelo.Mensualidad;
import com.puj.modelo.Tiquete;
import com.puj.modelo.Vehiculo;

public class Parqueadero implements IParqueadero, Serializable {

    private static final long serialVersionUID = 1L;

    private int siguienteNumero = 1;
    private ArrayList<Tiquete> tiquetes;
    private ArrayList<Mensualidad> mensualidades;

    public Parqueadero() {
        tiquetes = new ArrayList<Tiquete>();
        mensualidades = new ArrayList<Mensualidad>();
    }

    @Override
    public void agregarMensualidad(Mensualidad m) {
        mensualidades.add(m);
    }

    @Override
    public Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada)
            throws PlacaInvalidaException, VehiculoYaAdentroException {

        if (!v.placaValida()) {
            throw new PlacaInvalidaException(v.getPlaca(), v.getTipo());
        }

        if (buscarAbierto(v.getPlaca()) != null) {
            throw new VehiculoYaAdentroException(v.getPlaca());
        }

        Tiquete t = new Tiquete(siguienteNumero, v, entrada);
        siguienteNumero++;
        tiquetes.add(t);
        return t;
    }

    @Override
    public Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio)
            throws VehiculoNoEncontradoException, FechaInvalidaException {

        Tiquete t = buscarAbierto(placa);
        if (t == null) {
            throw new VehiculoNoEncontradoException(placa);
        }

        if (salida.isBefore(t.getEntrada())) {
            throw new FechaInvalidaException(t.getVehiculo().getPlaca(), salida, t.getEntrada());
        }
        boolean mensual = tieneMensualidad(t.getVehiculo().getPlaca(), t.getEntrada().toLocalDate());
        t.cerrar(salida, convenio, mensual);
        return t;
    }

    @Override
    public List<String> reporteVehiculosAdentro() {
        ArrayList<Tiquete> abiertos = new ArrayList<Tiquete>();
        for (Tiquete t : tiquetes) {
            if (t.estaAbierto()) {
                abiertos.add(t);
            }
        }

        for (int i = 0; i < abiertos.size() - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < abiertos.size(); j++) {
                if (entraAntes(abiertos.get(j), abiertos.get(menor))) {
                    menor = j;
                }
            }
            Collections.swap(abiertos, i, menor);
        }

        List<String> lineas = new ArrayList<String>();
        for (Tiquete t : abiertos) {
            lineas.add(t.getNumero() + " " + t.getVehiculo().getPlaca() + " "
                    + t.getVehiculo().getTipo() + " "
                    + t.getEntrada().toString().replace('T', ' '));
        }

        return lineas;
    }

    private boolean entraAntes(Tiquete a, Tiquete b) {
        if (a.getEntrada().isBefore(b.getEntrada())) {
            return true;
        }
        if (a.getEntrada().isAfter(b.getEntrada())) {
            return false;
        }
        return a.getVehiculo().getPlaca().compareTo(b.getVehiculo().getPlaca()) < 0;
    }

    @Override
    public List<String> reporteCierre(LocalDate fecha) {
        String[] tipos = { "CARRO", "MOTO", "ELECTRICO" };
        int[] cantidadTipo = new int[tipos.length];
        long[] totalTipo = new long[tipos.length];

        int cantidad = 0;
        long parqueo = 0;
        long recarga = 0;
        long total = 0;

        for (Tiquete t : tiquetes) {
            if (!t.estaAbierto() && t.getSalida().toLocalDate().equals(fecha)) {
                cantidad++;
                parqueo += t.getValorParqueo();
                recarga += t.getValorRecarga();
                total += t.getTotal();
                for (int i = 0; i < tipos.length; i++) {
                    if (t.getVehiculo().getTipo().equals(tipos[i])) {
                        cantidadTipo[i]++;
                        totalTipo[i] += t.getTotal();
                    }
                }
            }
        }

        List<String> lineas = new ArrayList<String>();
        lineas.add("CIERRE " + fecha);
        lineas.add("tiquetes=" + cantidad);
        lineas.add("parqueo=" + parqueo);
        lineas.add("recarga=" + recarga);
        lineas.add("total=" + total);
        for (int i = 0; i < tipos.length; i++) {
            lineas.add(tipos[i] + " tiquetes=" + cantidadTipo[i] + " total=" + totalTipo[i]);
        }
        return lineas;
    }

    @Override
    public List<String> reportePlacasFrecuentes() {
        ArrayList<String> placas = new ArrayList<String>();
        ArrayList<Integer> cantidades = new ArrayList<Integer>();
        ArrayList<Long> totales = new ArrayList<Long>();

        for (Tiquete t : tiquetes) {
            if (!t.estaAbierto()) {
                String placa = t.getVehiculo().getPlaca();
                int pos = placas.indexOf(placa);
                if (pos == -1) {
                    placas.add(placa);
                    cantidades.add(1);
                    totales.add(t.getTotal());
                } else {
                    cantidades.set(pos, cantidades.get(pos) + 1);
                    totales.set(pos, totales.get(pos) + t.getTotal());
                }
            }
        }

        for (int i = 0; i < placas.size() - 1; i++) {
            int mejor = i;
            for (int j = i + 1; j < placas.size(); j++) {
                if (vaAntes(cantidades.get(j), totales.get(j), placas.get(j),
                        cantidades.get(mejor), totales.get(mejor), placas.get(mejor))) {
                    mejor = j;
                }
            }
            Collections.swap(placas, i, mejor);
            Collections.swap(cantidades, i, mejor);
            Collections.swap(totales, i, mejor);
        }

        List<String> lineas = new ArrayList<String>();
        int limite = placas.size();
        if (limite > 3) {
            limite = 3;
        }
        for (int i = 0; i < limite; i++) {
            lineas.add((i + 1) + ". " + placas.get(i) + " visitas=" + cantidades.get(i) + " pagado=" + totales.get(i));
        }
        return lineas;
    }

    private boolean vaAntes(int cantA, long totA, String placaA, int cantB, long totB, String placaB) {
        if (cantA != cantB) {
            return cantA > cantB;
        }
        if (totA != totB) {
            return totA > totB;
        }
        return placaA.compareTo(placaB) < 0;
    }

    @Override
    public List<Tiquete> getTiquetes() {
        return new ArrayList<Tiquete>(tiquetes);
    }

    private Tiquete buscarAbierto(String placa) {
        for (Tiquete t : tiquetes) {
            if (t.estaAbierto() && t.getVehiculo().getPlaca().equals(placa.trim())) {
                return t;
            }
        }
        return null;
    }

    private boolean tieneMensualidad(String placa, LocalDate fecha) {
        for (Mensualidad m : mensualidades) {
            if (m.getPlaca().equals(placa) && m.cubre(fecha)) {
                return true;
            }
        }
        return false;
    }
}