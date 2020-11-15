/**
 * Viajes.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import control.ExcepcionOficinaViajes;
import control.ExcepcionOficinaViajes.Excepcion;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * OficinaViajes de la oficina
 *
 */
public class OficinaViajes {
    public static final String ASIENTO_ASIGNADO = "Asiento asignado";
    public static final String ASIENTO_DESASIGNADO = "Asiento desasignado";
    
    private Map<String, Viaje> viajes;
    private Dimension dimensionesAutobus;
    
    
    /**
     * Construye viajes
     * 
     */
    public OficinaViajes() {
       viajes = new HashMap<>();
    }
    
    /**
     * Obtiene viaje para un código
     * 
     */
    private Viaje obtenerViaje(Object codigoViaje) {
        return viajes.get(codigoViaje);
    }
    
    /**
     * Lee el fichero e inicializa la estructura de datos de viajes
     * 
     */
    public void leerViajes(String fichero) throws ExcepcionOficinaViajes {
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream(fichero));
        } catch (FileNotFoundException ex) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.FICHERO_VIAJES_NO_ENCONTRADO);
        }
        
        Viaje viaje;

        while (scanner.hasNextLine()) {
            viaje = new Viaje(scanner);
            viajes.put(viaje.obtenerCodigo(), viaje);
        }
    }
    
    /**
     * Lee las dimensiones del autobus
     * 
     */
    public void leerDimensionesAutobus(String fichero)
            throws ExcepcionOficinaViajes{
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream(fichero));
        } catch (FileNotFoundException ex) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.FICHERO_AUTOBUSES_NO_ENCONTRADO);
        }
        
        if ( ! scanner.hasNextInt() ) {
            throw new ExcepcionOficinaViajes(Excepcion.COLUMNAS_NO_ENCONTRADAS);
        }
        int columnas = scanner.nextInt();
        
        if ( ! scanner.hasNextInt() ) {
            throw new ExcepcionOficinaViajes(Excepcion.FILAS_NO_ENCONTRADAS);
        }
        int filas = scanner.nextInt();
        
        scanner.close();
    
        dimensionesAutobus = new Dimension(columnas, filas);
    }
    
    /**
     * Asigna un asiento a un viajero en una posición
     * 
     */
    public boolean asignar(String codigoViaje, String asiento, 
                           Viajero viajero) {
        Viaje viaje = obtenerViaje(codigoViaje);
        
        if (viaje == null) {
            return false;
        }
        
        return viaje.asignarAsiento(viajero, asiento);
    }
    
    /**
     * Desasigna un asiento en un viaje
     * 
     */
    public boolean desasignar(String codigoViaje, String asiento) {
        Viaje viaje = obtenerViaje(codigoViaje);
        
        if (viaje == null) {
            return false;
        }
        
        return viaje.desasignarAsiento(asiento);
    }
    
    /**
     * Devuelve las dimensiones del autobus
     * 
     */
    public Dimension obtenerDimensionesAutobus() {
        return dimensionesAutobus;
    }
    
    
    /**
     * Devuelve información de viajes programados para ese día
     * 
     */
    public List<Viaje> obtenerViajes(GregorianCalendar fecha) {
        List<Viaje> viajesObtenidos = new ArrayList<Viaje>();
        
        for (Viaje viaje : viajes.values()) {
            if (compararFechas(fecha, viaje.obtenerFecha())) {
                viajesObtenidos.add(viaje);
            }
        }
        return viajesObtenidos;
    }
    
    /**
     * Devueve verdad si dos fechas son iguales
     * 
     */
    private boolean compararFechas(GregorianCalendar fecha1, 
                                   GregorianCalendar fecha2) {
        return (fecha1.get(GregorianCalendar.YEAR) == 
                    fecha2.get(GregorianCalendar.YEAR) &&
                fecha1.get(GregorianCalendar.MONTH) == 
                    fecha2.get(GregorianCalendar.MONTH) &&
                fecha1.get(GregorianCalendar.DAY_OF_MONTH) == 
                    fecha2.get(GregorianCalendar.DAY_OF_MONTH));
    }
    
    /**
     * Devuelve el número de asiento asignado a esa posicion
     * 
     */
    public String obtenerNumeroAsiento(String codigoViaje, Posicion posicion) {
        Viaje viaje = obtenerViaje(codigoViaje);
        
        if (viaje == null) {
            return null;
        }
        
        return viaje.obtenerNumeroAsiento(posicion);
    }
    
    /**
     * Devuelve el viajero asignado a un asiento
     * 
     */
    public Viajero obtenerViajero (String codigoViaje, String asiento) {
        Viaje viaje = obtenerViaje(codigoViaje);
        
        if (viaje == null) {
            return null;
        }
        
        return viaje.obtenerViajero(asiento);
    }

    /**
     * Devuelve la hoja del viaje al que corresponde el código 
     * 
     */
    public String generarHojaViaje(String codigoViaje) {
        Viaje viaje = obtenerViaje(codigoViaje);
        
        if (viaje  == null) {
            return null;
        }
        
        return viaje.generarHojaViaje();
    }
        
    /**
     * Muestra una representación de todos los autobuses de los viajes
     * diferenciando los asientos asignados de los no asignados
     * 
     */
    @Override
    public String toString() {
        String cadena = "";

        for (Viaje viaje : viajes.values()) {
            cadena = cadena + viaje.toString() + '\n';
        }

        return cadena;
    }
}
