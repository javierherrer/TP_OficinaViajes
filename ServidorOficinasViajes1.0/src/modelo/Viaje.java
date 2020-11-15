/**
 * Viaje.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import control.ExcepcionOficinaViajes;
import control.ExcepcionOficinaViajes.Excepcion;
import control.ServidorOficinas;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Scanner;

public class Viaje {    
    private static String TITULO_HOJA_VIAJE = "*****HOJA DEL VIAJE*****\n";
    private static String FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm";
    private static String DELIMITADOR_ESPACIO = " ";
    private static String DELIMITADOR_GUION = " - ";
    
    private String codigo; 
    private String origen; 
    private String destino; 
    private GregorianCalendar fechaHora; 
    private Autobus autobus;

    /**
     * Construye un viaje leyendo la información del fichero
     *
     */
    public Viaje(Scanner viajes) throws ExcepcionOficinaViajes{     
        if ( ! viajes.hasNext()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.CODIGO_NO_ENCONTRADO);              
        }
        codigo = viajes.next();
        
        if ( ! viajes.hasNext()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.ORIGEN_NO_ENCONTRADO);    
        }
        origen = viajes.next(); 
        
        if ( ! viajes.hasNext()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.DESTINO_NO_ENCONTRADO);    
        }
        destino = viajes.next();
        
        fechaHora = leerFecha(viajes);
        
        if ( ! viajes.hasNext()) {
            throw new ExcepcionOficinaViajes(Excepcion.
                    CONDUCTOR_AUTOBUS_NO_ENCONTRADO);    
        }
        String conductor = viajes.next();
        
        if ( ! viajes.hasNext()) {
            throw new ExcepcionOficinaViajes(Excepcion.
                    MATRICULA_AUTOBUS_NO_ENCONTRADA);    
        }
        String matricula = viajes.next();
        
        autobus = new Autobus(conductor, matricula, 
                              ServidorOficinas.FICHERO_AUTOBUSES);
    }
    
    /**
     * Construye un viaje
     *
     */
    public Viaje(String codigo, String origen, String destino, 
                 GregorianCalendar fechaHora, Autobus autobus) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.fechaHora = fechaHora;
        this.autobus = autobus;
    }
    
    /**
     * Contruye una fecha gregorian calendar
     * 
     */
    private GregorianCalendar leerFecha(Scanner fecha)
                    throws ExcepcionOficinaViajes{
        if (!fecha.hasNextInt()) {
            throw new ExcepcionOficinaViajes(Excepcion.FECHA_NO_ENCONTRADA);
        }
        int dia = fecha.nextInt();
        
        if (!fecha.hasNextInt()) {
            throw new ExcepcionOficinaViajes(Excepcion.FECHA_NO_ENCONTRADA);
        }
        int mes = fecha.nextInt() - 1;
        
        if (!fecha.hasNextInt()) {
            throw new ExcepcionOficinaViajes(Excepcion.FECHA_NO_ENCONTRADA);
        }
        int anyo = fecha.nextInt();
        
        if (!fecha.hasNextInt()) {
            throw new ExcepcionOficinaViajes(Excepcion.FECHA_NO_ENCONTRADA);
        }
        int hora = fecha.nextInt();
        
        if (!fecha.hasNextInt()) {
            throw new ExcepcionOficinaViajes(Excepcion.FECHA_NO_ENCONTRADA);
        }
        int minuto = fecha.nextInt();

        return new GregorianCalendar(anyo, mes, dia, hora, minuto);
    }

    /**
     * Devuelve el código del viaje
     * 
     */
    public String obtenerCodigo() {
        return codigo;
    }
    
    /**
     * Devuelve la fecha del viaje
     * 
     */
    public GregorianCalendar obtenerFecha() {
        return fechaHora;
    }
    
    /**
     * Asigna un asiento a un viajero
     *
     */
    public boolean asignarAsiento(Viajero viajero, String asiento){
        return autobus.asignarAsiento(viajero, asiento);
    }
    
    /**
     * Desasigna un asiento
     * 
     */
    public boolean desasignarAsiento(String asiento) {
        return autobus.desasignarAsiento(asiento);
    }
    
    /**
     * Devuelve el número de asiento asignado a esa posicion
     * 
     */
    public String obtenerNumeroAsiento(Posicion posicion) {
        return autobus.obtenerNumeroAsiento(posicion);
    }
    
    /**
     * Devuelve el viajero asignado a un asiento
     * 
     */
    public Viajero obtenerViajero (String asiento) {
        return autobus.obtenerViajero(asiento);
    }

    /**
     * Genera la hoja de viaje del autobús
     *
     */
    public String generarHojaViaje(){
        return TITULO_HOJA_VIAJE + toString() + '\n' + autobus.toString() + 
                '\n' + autobus.generarHojaViaje();
    }
    
    /**
     * Convertir objeto GregorianCalendar en String
     *
     */
    private String obtenerCadenaFecha(GregorianCalendar fechaHora) {
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_FECHA_HORA);
        formato.setCalendar(fechaHora);
        String cadenaFecha = formato.format(fechaHora.getTime());
        
        return cadenaFecha; 
    }
    
    /**
     * Muestra la información principal del viaje
     *
     */
    @Override
    public String toString() {
        return codigo + DELIMITADOR_ESPACIO + origen + DELIMITADOR_GUION
                + destino + DELIMITADOR_ESPACIO + obtenerCadenaFecha(fechaHora);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.codigo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (this == obj){
            return true;
        }
        if ( ! (obj instanceof Viaje)){
            return false;
        }
        Viaje tmp = (Viaje)obj;
        return (codigo.equals(tmp.codigo));  
    }
}