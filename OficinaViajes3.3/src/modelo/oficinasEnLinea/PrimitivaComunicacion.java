/**
 * PrimitivaComunicacion.java
 * 
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 */

package modelo.oficinasEnLinea;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * PrimitivaComunicacion
 *
 */
public enum PrimitivaComunicacion {

    CONECTAR_PUSH("connect"),
    DESCONECTAR_PUSH("disconnect"),
    NUEVO_ID_CONEXION("new_id_conection"),
    TEST("test"),
    ASIGNAR("asign_seat"),
    DESASIGNAR("deallocate_seat"),
    OBTENER_DIMENSIONES("get_bus_size"),
    OBTENER_VIAJES("get_trips"),
    OBTENER_NUMERO_ASIENTO("get_seat_num"),
    OBTENER_VIAJERO("get_traveler"),
    HOJA_VIAJE("trip_info"),
    FIN("end"),
    OK("ok"),
    NOK("nok");

    private String simbolo;
    private static final Pattern expresionRegular
            = Pattern.compile(CONECTAR_PUSH.toString() + "|"
                    + DESCONECTAR_PUSH.toString() + "|"
                    + NUEVO_ID_CONEXION + "|"
                    + TEST.toString() + "|"
                    + ASIGNAR.toString() + "|"
                    + DESASIGNAR.toString() + "|"
                    + OBTENER_DIMENSIONES.toString() + "|"
                    + OBTENER_VIAJES.toString() + "|"
                    + OBTENER_NUMERO_ASIENTO.toString() + "|"
                    + OBTENER_VIAJERO.toString() + "|"
                    + HOJA_VIAJE.toString() + "|"
                    + FIN.toString() + "|"
                    + OK.toString() + "|"
                    + NOK.toString());

    /**
     * Construye una primitiva
     *
     */
    PrimitivaComunicacion(String simbolo) {
        this.simbolo = simbolo;
    }

    /**
     * Devuelve una nueva primitiva leída de un scanner
     *
     */
    public static PrimitivaComunicacion nueva(Scanner scanner)
            throws InputMismatchException {
        String token = scanner.next();

        if (token.equals(CONECTAR_PUSH.toString())) {
            return CONECTAR_PUSH;
        } 
        else if (token.equals(DESCONECTAR_PUSH.toString())) {
            return DESCONECTAR_PUSH;
        } 
        else if (token.equals(NUEVO_ID_CONEXION.toString())) {
            return NUEVO_ID_CONEXION;
        } 
        else if (token.equals(TEST.toString())) {
            return TEST;
        } 
        else if (token.equals(ASIGNAR.toString())) {
            return ASIGNAR;
        } 
        else if (token.equals(DESASIGNAR.toString())) {
            return DESASIGNAR;
        } 
        else if (token.equals(OBTENER_DIMENSIONES.toString())) {
            return OBTENER_DIMENSIONES;
        } 
        else if (token.equals(OBTENER_VIAJES.toString())) {
            return OBTENER_VIAJES;
        } 
        else if (token.equals(OBTENER_NUMERO_ASIENTO.toString())) {
            return OBTENER_NUMERO_ASIENTO;
        } 
        else if (token.equals(OBTENER_VIAJERO.toString())) {
            return OBTENER_VIAJERO;
        }
        else if (token.equals(HOJA_VIAJE.toString())) {
            return HOJA_VIAJE;
        } 
        else if (token.equals(FIN.toString())) {
            return FIN;
        } 
        else if (token.equals(OK.toString())) {
            return OK;
        } 
        else {
            return NOK;
        }
    }

    /**
     * toString
     *
     */
    @Override
    public String toString() {
        return simbolo;
    }
}

