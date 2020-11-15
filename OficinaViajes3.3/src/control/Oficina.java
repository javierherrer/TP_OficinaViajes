/**
 * Oficina.java
 *
 * Javier Herrer Torres
 *
 * Versión 0 (02/2020)
 *  - Esqueleto de código para crear Oficina, Autobús y Viaje
 *
 * Versión 1.1 (03/2020)
 *  - Inicializa las Estructuras de Datos
 *
 * Versión 1.2 (03/2020)
 *  - Permite asignar y desasignar asientos
 *
 * Versión 1.3 (03/2020)
 *  - Permite ver la ocupación generar la hoja de viaje
 *    con su información
 *
 * Versión 2.0 (04/2020)
 *  - Sustitución de vectores por colecciones y organización de las clases
 *    desarrolladas en la Práctica 1 en los paquetes de Modelo y Control
 *
 * Versión 2.1 (04/2020)
 *  - Determinación de posibles errores de sintaxis en ficheros de
 *    viajes y autobús
 *
 * Versión 2.2 (04/2020)
 *  - Implementación de Vista
 *
 * Versión 2.2.1 (04/2020)
 *  - Creación de los contenedores y componentes visuales
 *    Swing (prototipo de IU de alto nivel)
 *
 * Versión 2.3 (04/2020)
 *  - Integración de bloques Modelo-Vista-Controlador (MVC)
 *
 * Versión 2.3.1 (04/2020)
 *  - Búsqueda de viajes para una fecha dada
 *
 * Versión 2.3.2 (04/2020)
 *  - Presentación del plano de ocupación de los asientos del
 *    autobús para un viaje concreto
 *
 * Versión 2.3.3 (04/2020)
 *  - Asignación/Desasignación de asiento e indicación del viajero
 *    asignado a un asiento
 *
 * Versión 2.3.4 (05/2020)
 *  - Generación de hojas de viaje
 *
 * Versión 3.0 (06/2020)
 *  - Renombrar las clases de oficina en cliente y servidor.
 *
 * Versión 3.1 (06/2020)
 *  - Buscar viajes por fecha. El cliente debe solicitar los viajes para una
 *    fecha al servidor y este se los debe devolver por el socket.
 *
 * Versión 3.2 (06/2020)
 *  - Asignar/Desasignar un asiento. El cliente debe solicitar la operación al
 *    servidor que responderá adecuadamente, reflejándose en vista su efecto.
 *
 * Versión 3.3 (06/2020)
 *  - Generar hoja de viaje. El cliente solicitará al servidor la operación que
 *    responderá, el fichero de la hoja de viaje debe crearse en el cliente.
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
     * Genera la hoja del viaje al que corresponde el código con la información
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
     * Indica si aplicación está en modo debug
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
     * Muestra una representación de todos los viajes
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
