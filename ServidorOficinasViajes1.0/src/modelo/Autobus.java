/**
 * Autobus.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import control.ExcepcionOficinaViajes;
import control.ExcepcionOficinaViajes.Excepcion;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Autobus {
    public static final String TEXTO_CONDUCTOR = "Conductor: ";
    public static final String TEXTO_MATRICULA = "Matrícula: ";
    
    private Asientos asientos; 
    private String conductor; 
    private String matricula;
    private int columnas;
    private int filas;

    /**
     * Construye un autobús
     *
     */
    public Autobus(String conductor, String matricula, String fichero) 
           throws ExcepcionOficinaViajes{
        this.conductor = conductor;
        this.matricula = matricula;
        asientos = new Asientos();
        leerAsientos(fichero);      
    }
   
    /**
     * Lee el fichero e inicializa la estructura de datos de asientos
     *
     */
    private void leerAsientos(String fichero) throws ExcepcionOficinaViajes {
        Scanner autobuses;
        
        try {
            autobuses = new Scanner(new FileInputStream(fichero));
        } catch (FileNotFoundException ex) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.FICHERO_AUTOBUSES_NO_ENCONTRADO);
        }
        
        if ( ! autobuses.hasNextInt()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.COLUMNAS_NO_ENCONTRADAS);
        }
        this.columnas = autobuses.nextInt();
        
        if ( ! autobuses.hasNextInt()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.FILAS_NO_ENCONTRADAS);
        }
        this.filas = autobuses.nextInt();

        asientos.leerAsientos(autobuses);
            
        autobuses.close();
    }

    /**
     * Asigna un asiento a un viajero
     * Si ya está asignado, actualizará su ocupante
     *
     */
    public boolean asignarAsiento(Viajero viajero, String asiento){
        return asientos.asignarAsiento(viajero, asiento);
    }
    
    /**
     * Desasigna un asiento
     * 
     */
    public boolean desasignarAsiento(String asiento) {
        return asientos.desasignarAsiento(asiento);
    }
    
    /**
     * Devuelve el número de asiento asignado a esa posicion
     * 
     */
    public String obtenerNumeroAsiento(Posicion posicion) {
        return asientos.obtenerNumeroAsiento(posicion);
    }
    
    /**
     * Devuelve el viajero asignado a un asiento
     * 
     */
    public Viajero obtenerViajero (String asiento) {
        return asientos.obtenerViajero(asiento);
    }

    /**
     * Información de asientos asignados
     *
     */
    public String generarHojaViaje(){
        return TEXTO_CONDUCTOR + conductor + '\n' + TEXTO_MATRICULA + 
               matricula + "\n\n" + asientos.generarHojaViaje();
    }
    
    /**
     * Muestra el estado de asignación
     *
     */
    @Override
    public String toString() {
        String cadena = "";
        Posicion posicion;
        for(int fila = 1; fila <= filas; fila++) {
            for (int columna = 1; columna <= columnas; columna++) {
                posicion = new Posicion(columna, fila);
                cadena = cadena + asientos.obtenerCadenaAsiento(posicion);
            }
            cadena = cadena + '\n';
        }
        return cadena;
    }
}
