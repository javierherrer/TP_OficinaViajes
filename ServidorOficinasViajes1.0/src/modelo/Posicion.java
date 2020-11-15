/**
 * Posicion.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import java.util.Scanner;

public class Posicion {
    private int fila;
    private int columna;
    
    /**
     *  Construye una posición
     *  
     */  
    public Posicion(int columna, int fila) {
        this.fila = fila;
        this.columna = columna;
    }
    
    public Posicion(String linea) {
        Scanner scanner = new Scanner(linea);
        this.columna = Integer.parseInt(scanner.next());
        this.fila = Integer.parseInt(scanner.next());
        scanner.close();
    }
  
    /**
     *  Devuelve la fila de la posición
     * 
     */   
    public int devolverFila() {
        return fila;
    }

    /**
     *  Devuelve la columna de la posición
     * 
     */
    public int devolverColumna() {
        return columna;
    }
    
    /**
     * Métodos hashCode() y equals() en los que nos interesa comprobar 
     * los atributos 'fila' y 'columna'
     *
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.fila;
        hash = 53 * hash + this.columna;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){ 
            return true;
        }
        if ( ! (obj instanceof Posicion)){
            return false;
        }
        Posicion tmp = (Posicion)obj;
        return (fila == tmp.fila) && (columna == tmp.columna);
    }
    
    /**
     * Muestra información de la posición
     *
     */
    @Override
    public String toString() {
        return columna + " " + fila;
    }  
}
