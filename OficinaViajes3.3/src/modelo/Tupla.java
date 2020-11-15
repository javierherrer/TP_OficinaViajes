/**
 * Tulpa.java
 *
 * Versión 3.3 16-6-2020
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
