/**
 * Viaje.java
 *
 * Versión 3.3 16-6-2020
 *
 * Javier Herrer Torres
 *
 */
package modelo;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Scanner;

public class Viaje {
    private static String FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm";
    private static String DELIMITADOR_ESPACIO = " ";
    private static String DELIMITADOR_GUION = " - ";
    private static String DELIMITADOR_BARRA = "/";
    private static String DELIMITADOR_PUNTOS= ":";

    private String codigo;
    private String origen;
    private String destino;
    private GregorianCalendar fechaHora;

    /**
     * Construye un viaje
     *
     */
    public Viaje(String codigo, String origen, String destino,
            GregorianCalendar fechaHora) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.fechaHora = fechaHora;
    }

    /**
     * Construye un viaje leyendo de Scanner
     *
     */
    public Viaje(Scanner scanner) throws Exception{
        scanner.useDelimiter(DELIMITADOR_GUION + "|" + DELIMITADOR_ESPACIO);

        this.codigo = scanner.next();
        this.origen = scanner.next();
        this.destino = scanner.next();
        this.fechaHora = parsearFechaHora(scanner.next(), scanner.next());
    }

    /**
     * Devuelve el código del viaje
     *
     */
    public String obtenerCodigo() {
        return codigo;
    }

    /**
     * Parsea fecha en un string
     *
     */
    private GregorianCalendar parsearFechaHora(String fecha, String hora) 
            throws Exception{
        Scanner scannerFecha = new Scanner(fecha);
        scannerFecha.useDelimiter(DELIMITADOR_BARRA);

        int dia = scannerFecha.nextInt();
        int mes = scannerFecha.nextInt() - 1;  // GC meses de 0 a 11
        int anyo = scannerFecha.nextInt();
        
        scannerFecha.close();
        
        Scanner scannerHora = new Scanner(hora);
        scannerHora.useDelimiter(DELIMITADOR_PUNTOS);
        
        int horas = scannerHora.nextInt();
        int minutos = scannerHora.nextInt();
        
        scannerHora.close();
        
        return new GregorianCalendar(anyo, mes, dia, horas, minutos);
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
