/**
 * Viajero.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

import java.util.Scanner;

public class Viajero {
    private String nombre; 
    private String dni; 

    /**
     * Construye un viajero
     * 
     */
    public Viajero(String dni, String nombre){
        this.dni = dni;
        this.nombre = nombre;
    }
    
    /**
     * Construye un viajero
     * 
     */
    public Viajero(Scanner scanner) throws Exception {
        this.dni = scanner.nextLine();
        this.nombre = scanner.nextLine();
    }

    /**
     * Muestra la información del viajero
     */
    @Override
    public String toString() {
        return dni + "\n" + nombre;
    }
}
