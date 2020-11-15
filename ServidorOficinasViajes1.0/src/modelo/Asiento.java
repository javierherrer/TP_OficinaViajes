/**
 * Asiento.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import control.ExcepcionOficinaViajes;
import control.ExcepcionOficinaViajes.Excepcion;
import java.util.Objects;
import java.util.Scanner;

public class Asiento {
    private String numero;
    private boolean asignado; 
    private Viajero viajero;
    private Posicion posicion;
    
    /**
     * Construye un Asiento
     * 
     */
    public Asiento(String numero, Posicion posicion) {
        this.asignado = false;
        this.posicion = posicion;
        this.viajero = null;
        this.numero = numero;
    }
    
    /**
     * Construye un Asiento leyendo de fichero
     * 
     */
    public Asiento (Scanner asientos) throws ExcepcionOficinaViajes{
        if ( ! asientos.hasNext()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.NUMERO_ASIENTO_NO_ENCONTRADO);
        }
        this.numero = asientos.next();
        
        if ( ! asientos.hasNextInt()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.COLUMNA_ASIENTO_NO_ENCONTRADA);
        }
        int columna = asientos.nextInt();
        
        if ( ! asientos.hasNextInt()) {
            throw new ExcepcionOficinaViajes(
                    Excepcion.FILA_ASIENTO_NO_ENCONTRADA);
        }
        int fila = asientos.nextInt();
        
        this.posicion = new Posicion(columna, fila);
        
        this.asignado = false;
        this.viajero = null;
    }
    
    /**
     * Devuelve la posicion del Asiento
     *
     */
    public Posicion devolverPosicion() {
        return posicion;
    }
    
    /**
     * Devuelve el viajero asignado al asiento
     *
     */
    public Viajero obtenerViajero () {
        return viajero;
    }
    
    /**
     * Devuelve verdad si está asignado
     * 
     */
    public boolean estaAsignado() {
        return asignado;
    }
    
    /**
     * Devuelve el numero del asiento
     * 
     */
    public String obtenerNumero() {
        return numero;
    }

    /**
     * Asigna un viajero al Asiento
     *
     */
    void asignar(Viajero viajero){ 
    	this.viajero = viajero;
        asignado = true;
    }
    
    /**
     * Desasigna el asiento
     * 
     */
    void desasignar() {
        this.viajero = null;
        asignado = false;
    }

    /**
     * Muestra la información del viajero
     *
     */
    @Override
    public String toString() {
        String cadena = "";
        
        if (asignado){
            cadena = viajero.toString();
        }
        
        return numero + " " + cadena;
    }
    
    /**
     * Métodos hashCode() y equals() en los que nos interesa comprobar 
     * únicamente el atributo 'posición'
     *
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.numero);
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
        if ( ! (obj instanceof Asiento)){
            return false;
        }
        
        Asiento tmp = (Asiento)obj;
        return (numero.equals(tmp.numero));  
    }
}
