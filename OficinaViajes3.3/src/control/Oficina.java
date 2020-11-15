/**
 * Oficina.java
 *
 * Javier Herrer Torres
 *
 * Versi�n 0 (02/2020)
 *  - Esqueleto de c�digo para crear Oficina, Autob�s y Viaje
 *
 * Versi�n 1.1 (03/2020)
 *  - Inicializa las Estructuras de Datos
 *
 * Versi�n 1.2 (03/2020)
 *  - Permite asignar y desasignar asientos
 *
 * Versi�n 1.3 (03/2020)
 *  - Permite ver la ocupaci�n generar la hoja de viaje
 *    con su informaci�n
 *
 * Versi�n 2.0 (04/2020)
 *  - Sustituci�n de vectores por colecciones y organizaci�n de las clases
 *    desarrolladas en la Pr�ctica 1 en los paquetes de Modelo y Control
 *
 * Versi�n 2.1 (04/2020)
 *  - Determinaci�n de posibles errores de sintaxis en ficheros de
 *    viajes y autob�s
 *
 * Versi�n 2.2 (04/2020)
 *  - Implementaci�n de Vista
 *
 * Versi�n 2.2.1 (04/2020)
 *  - Creaci�n de los contenedores y componentes visuales
 *    Swing (prototipo de IU de alto nivel)
 *
 * Versi�n 2.3 (04/2020)
 *  - Integraci�n de bloques Modelo-Vista-Controlador (MVC)
 *
 * Versi�n 2.3.1 (04/2020)
 *  - B�squeda de viajes para una fecha dada
 *
 * Versi�n 2.3.2 (04/2020)
 *  - Presentaci�n del plano de ocupaci�n de los asientos del
 *    autob�s para un viaje concreto
 *
 * Versi�n 2.3.3 (04/2020)
 *  - Asignaci�n/Desasignaci�n de asiento e indicaci�n del viajero
 *    asignado a un asiento
 *
 * Versi�n 2.3.4 (05/2020)
 *  - Generaci�n de hojas de viaje
 *
 * Versi�n 3.0 (06/2020)
 *  - Renombrar las clases de oficina en cliente y servidor.
 *
 * Versi�n 3.1 (06/2020)
 *  - Buscar viajes por fecha. El cliente debe solicitar los viajes para una
 *    fecha al servidor y este se los debe devolver por el socket.
 *
 * Versi�n 3.2 (06/2020)
 *  - Asignar/Desasignar un asiento. El cliente debe solicitar la operaci�n al
 *    servidor que responder� adecuadamente, reflej�ndose en vista su efecto.
 *
 * Versi�n 3.3 (06/2020)
 *  - Generar hoja de viaje. El cliente solicitar� al servidor la operaci�n que
 *    responder�, el fichero de la hoja de viaje debe crearse en el cliente.
 *
 */
package control;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modelo.Viajero;
import modelo.Viajes;
import modelo.Tupla;
import vista.OficinaVista;

public class Oficina implements OyenteVista {

    public static String VERSION = "Venta de billetes 3.3";
    private static String ARG_DEBUG = "-d";
    private static final String FICHERO_SALIDA = "hojaViaje.txt";

    private Viajes viajes;
    private OficinaVista vista;

    private static boolean modoDebug = false;

    /**
     * Construye una oficina
     *
     */
    public Oficina(String[] args) {
        procesarArgsMain(args);

        viajes = new Viajes();

        vista = OficinaVista.instancia(this, viajes);

        viajes.nuevoObservador(vista);

        viajes.conectar();
    }

    /**
     * Recibe eventos de vista
     *
     */
    public void eventoProducido(Evento evento, Object obj) throws Exception {
        switch (evento) {
            case ASIGNAR_ASIENTO:
                Tupla<Tupla<String, String>, Viajero> tripla
                        = (Tupla<Tupla<String, String>, Viajero>) obj;
                viajes.asignarAsiento((tripla.a).a, (tripla.a).b, tripla.b);
                break;

            case DESASIGNAR_ASIENTO:
                Tupla<String, String> tupla = (Tupla<String, String>) obj;
                viajes.desasignarAsiento(tupla.a, tupla.b);
                break;

            case GENERAR_HOJA_VIAJE:
                String codigo = (String) obj;
                generarHojaViaje(codigo, FICHERO_SALIDA);
                break;

            case SALIR:
                System.exit(0);
                break;
        }
    }

    /**
     * Genera la hoja del viaje al que corresponde el c�digo con la informaci�n
     * de los asientos asignados en un fichero
     *
     */
    private void generarHojaViaje(String codigo, String fichero)
            throws Exception {
        List<String> hojaViaje = viajes.generarHojaViaje(codigo);
        String cadena = "";
        
        for (String linea : hojaViaje) {
            cadena = cadena + "\n" + linea;
        }
        
        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(fichero));

        writer.write(cadena.trim());
        writer.close();
    }

    /**
     * Indica si aplicaci�n est� en modo debug
     *
     */
    public static boolean esModoDebug() {
        return modoDebug;
    }

    /**
     * Procesa argumentos de main
     *
     */
    private void procesarArgsMain(String[] args) {
        List<String> argumentos = new ArrayList<String>(Arrays.asList(args));

        if (argumentos.contains(ARG_DEBUG)) {
            modoDebug = true;
        }
    }

    /**
     * Muestra una representaci�n de todos los viajes
     *
     */
    @Override
    public String toString() {
        return viajes.toString();
    }

    /**
     * Metodo main
     *
     */
    public static void main(String[] args) {
        new Oficina(args);
    }
}
