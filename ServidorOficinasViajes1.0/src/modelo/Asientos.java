/**
 * Asientos.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import control.ExcepcionOficinaViajes;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Asientos de un autobús
 * 
 */
public class Asientos {
    private static final String CADENA_PASILLO = "_";
    private static final String CADENA_ASIGNADO = "o";
    private static final String CADENA_SIN_ASIGNAR = "X";
    private static final String FILA_ENCABEZADO = "n.º asiento | viajero\n";
    public static final String FILA_SEPARACION = "------------------";
    
    private Map<String, Asiento> asientos;
    
    /**
     * Construye asientos
     * 
     */
    public Asientos() {
        asientos = new HashMap<>();
    }
    
    /**
     * Lee el fichero e inicializa la estructura de datos
     * 
     */
    public void leerAsientos(Scanner autobus) throws ExcepcionOficinaViajes{
        Asiento asiento;
        
        while (autobus.hasNext()) {
            asiento = new Asiento(autobus);
            asientos.put(asiento.obtenerNumero(), asiento);
        }
    }
    
    /**
     * Devuelve el numero del asiento
     * 
     */
    public String obtenerNumeroAsiento (Posicion posicion) {
        for (Asiento asiento : asientos.values()) {
            if (posicion.equals(asiento.devolverPosicion())) {
                return asiento.obtenerNumero();
            }
        }

        return null;
    }
    
    /**
     * Obtiene asiento para un código
     * 
     */
    private Asiento obtenerAsiento(Object numero) {
        return asientos.get(numero);
    }
    
    /**
     * Devuelve el estado de asignacion del asiento
     * 
     */
    public Viajero obtenerViajero(String numero) {
        Asiento asiento;
        
        if ( (asiento = obtenerAsiento(numero)) == null) {
            return null;
        }

        return asiento.obtenerViajero();
    }
    
    /**
     * Asigna un asiento a un viajero
     * 
     */
    public boolean asignarAsiento(Viajero viajero, String numero) {
        Asiento asiento = obtenerAsiento(numero);
        
        if (asiento == null) {
            return false;
        }
        
        asiento.asignar(viajero);
        return true;
    }
    
    /**
     * Desasigna un asiento
     * 
     */
    public boolean desasignarAsiento(String numero) {
        Asiento asiento = obtenerAsiento(numero);
        
        if (asiento == null) {
            return false;
        }
        
        asiento.desasignar();
        return true;
    }
    
    /**
     * Información de asientos asignados
     *
     */
    public String generarHojaViaje(){
        String cadena = FILA_ENCABEZADO;
        
        for (Asiento asiento : asientos.values()) {
            cadena = cadena + asiento.toString() + '\n' + 
                    FILA_SEPARACION + '\n';
        }
        
        return cadena;
    }
    
    /**
     * Devuelve la cadena correspondiente al asiento en esa posición
     * 
     */
    public String obtenerCadenaAsiento(Posicion posicion) {
        for (Asiento asiento : asientos.values()) {
            if (posicion.equals(asiento.devolverPosicion())) {
                if (asiento.estaAsignado()) {
                    return CADENA_ASIGNADO;
                } else{
                    return CADENA_SIN_ASIGNAR;
                }
            }
        }

        return CADENA_PASILLO;
    }
    
}
