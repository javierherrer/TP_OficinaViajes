/**
 * Tulpa.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package modelo;

public class Tupla<A, B> {
    public final A a;
    public final B b;
    
    /**
     * Construye una dupla
     * 
     */
    public Tupla(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
