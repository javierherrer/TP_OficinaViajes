/**
 * Posicion.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 *
 */
package modelo;

public class Posicion {

    private int fila;
    private int columna;

    /**
     * Construye una posición
     *
     */
    public Posicion(int columna, int fila) {
        this.fila = fila;
        this.columna = columna;
    }
    
    /**
     * toString()
     * 
     */
    @Override
    public String toString() {
        return columna + " " + fila;
    }
}
