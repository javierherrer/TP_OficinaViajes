/**
 * ServidorOficinas.java
 *
 * Versión 1.0 Javier Herrer Torres (06/2020)
 *
 */
package control;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import modelo.OficinaViajes;
import modelo.Posicion;
import modelo.Viaje;
import modelo.Viajero;

/**
 * ServidorOficinas
 *
 */
public class ServidorOficinas extends Thread {
    public static final String FICHERO_AUTOBUSES = "autobuses.txt";
    private static final String FICHERO_VIAJES = "viajes.txt";

    private ServidorOficinas servidorOficinas;

    private OficinaViajes oficinaViajes;

    public static String VERSION = "Trip Server 1.0";
    private static String ARG_DEBUG = "-d";

    private static String FICHERO_CONFIG_ERRONEO
            = "Config file is wrong. Set default values";
    private static String ESPERANDO_SOLICITUD_VIAJE
            = "Waiting for trip requests...";
    private static String ERROR_EJECUCION_SERVIDOR = "Error: Server running in ";
    private static String ERROR_CREANDO_CONEXION_VIAJE
            = "Failed to create trip connection";

    private static int TIEMPO_TEST_CONEXIONES = 10 * 1000;
    public static int TIEMPO_ESPERA_CLIENTE = 1000;

    private static boolean modoDebug = false;

    private Map<String, ConexionPushOficina> conexionesPushOficinas;
    private int numConexion = 0;

    /**
     * Configuración
     */
    private Properties propiedades;
    private static final String FICHERO_CONFIG = "config.properties";

    private static final String NUM_THREADS = "threadsNumber";
    private int numThreads = 16;
    private static final String PUERTO_SERVIDOR = "serverPort";
    private int puertoServidor = 15000;

    /**
     * Construye el servidor de oficinas de viajes
     *
     */
    public ServidorOficinas() {
        servidorOficinas = this;
        conexionesPushOficinas = new ConcurrentHashMap<>();

        leerConfiguracion();

        oficinaViajes = new OficinaViajes();
        
        try {
            oficinaViajes.leerViajes(FICHERO_VIAJES);
            oficinaViajes.leerDimensionesAutobus(FICHERO_AUTOBUSES);
        } catch (ExcepcionOficinaViajes ex) {
            System.out.println(ex);
            System.exit(0);
        }

        envioTestPeriodicosConexionesPushOficinas();

        start();
    }

    /**
     * Indica si aplicación está en modo debug
     *
     */
    public static boolean esModoDebug() {
        return modoDebug;
    }

    /**
     * Lee configuración
     *
     */
    private void leerConfiguracion() {
        try {
            propiedades = new Properties();
            propiedades.load(new FileInputStream(FICHERO_CONFIG));

            numThreads = Integer.parseInt(propiedades.getProperty(NUM_THREADS));
            puertoServidor
                    = Integer.parseInt(propiedades.getProperty(PUERTO_SERVIDOR));
        } catch (Exception e) {
            System.out.println(FICHERO_CONFIG_ERRONEO);
            System.out.println(NUM_THREADS + " = " + numThreads);
            System.out.println(PUERTO_SERVIDOR + " = " + puertoServidor);

            if (esModoDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ejecuta bucle espera conexiones
     *
     */
    @Override
    public void run() {
        System.out.println(VERSION);
        try {
            ExecutorService poolThreads
                    = Executors.newFixedThreadPool(numThreads);

            ServerSocket serverSocket = new ServerSocket(puertoServidor);

            while (true) {
                System.out.println(ESPERANDO_SOLICITUD_VIAJE);

                Socket socket = serverSocket.accept();
                poolThreads.execute(new ServidorOficina(this, socket));
            }

        } catch (BindException e) {
            System.out.println(ERROR_EJECUCION_SERVIDOR + puertoServidor);
            if (esModoDebug()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println(ERROR_CREANDO_CONEXION_VIAJE);
            if (esModoDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Envía tests periódicos para mantener lista conexiones push con oficinas
     *
     */
    private void envioTestPeriodicosConexionesPushOficinas() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (ConexionPushOficina conexionPushOficina
                        : conexionesPushOficinas.values()) {
                    try {
                        conexionPushOficina.enviarSolicitud(
                                PrimitivaComunicacion.TEST,
                                TIEMPO_TEST_CONEXIONES);
                    } catch (IOException e1) {
                        System.out.println(
                                ServidorOficina.ERROR_CONEXION_OFICINA
                                + " " + conexionPushOficina.toString());

                        conexionesPushOficinas.remove(
                                conexionPushOficina.obtenerIdConexion());
                        try {
                            conexionPushOficina.cerrar();
                        } catch (IOException e2) {
                            // No hacemos nada, ya hemos cerrado conexión 
                        }
                        if (ServidorOficinas.esModoDebug()) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }, TIEMPO_TEST_CONEXIONES, TIEMPO_TEST_CONEXIONES);
    }

    /**
     * Genera nuevo identificador de conexión push oficina
     *
     */
    synchronized String generarIdConexionPushOficina() {
        // En esta versión simplemente generamos números consecutivos      
        return String.valueOf(++numConexion);
    }

    /**
     * Obtiene conexión push oficina por id conexión
     *
     */
    ConexionPushOficina obtenerConexionPushOficina(String idConexion) {
        ConexionPushOficina conexionPushOficina
                = conexionesPushOficinas.get(idConexion);

        if (conexionPushOficina != null) {
            return conexionPushOficina;
        }

        return null;
    }

    /**
     * Nueva conexión push oficina
     *
     */
    synchronized void nuevaConexionPushOficina(
            ConexionPushOficina conexionPushOficina) {
        conexionesPushOficinas.put(
                conexionPushOficina.obtenerIdConexion(),
                conexionPushOficina);
    }

    /**
     * Elimina conexión push oficina
     *
     */
    synchronized boolean eliminarConexionPushOficina(String idConexion)
            throws IOException {
        ConexionPushOficina conexionPushOficina
                = conexionesPushOficinas.get(idConexion);

        if (conexionPushOficina == null) {
            return false;
        }

        conexionPushOficina.cerrar();
        conexionesPushOficinas.remove(idConexion);

        return true;
    }

    /**
     * Devuelve las dimensiones de el autobus
     *
     */
    synchronized Dimension obtenerDimensionesAutobus() {
        return oficinaViajes.obtenerDimensionesAutobus();
    }

    /**
     * Devuelve información de viajes programados para ese día
     *
     */
    synchronized List<Viaje> obtenerViajes(GregorianCalendar fecha) {
        return oficinaViajes.obtenerViajes(fecha);
    }

    /**
     * Devuelve el número de asiento asignado a esa posicion
     *
     */
    synchronized String obtenerNumeroAsiento(String codigoViaje, 
                                             Posicion posicion) {
        return oficinaViajes.obtenerNumeroAsiento(codigoViaje, posicion);
    }

    /**
     * Devuelve el viajero asignado a un asiento
     *
     */
    synchronized Viajero obtenerViajero(String codigoViaje, String asiento) {
        return oficinaViajes.obtenerViajero(codigoViaje, asiento);
    }
    
    /**
     * Devuelve la hoja del viaje al que corresponde el código
     *
     */
    synchronized String generarHojaViaje(String codigoViaje) {
        return oficinaViajes.generarHojaViaje(codigoViaje);
    }

    /**
     * Notifica cambio viaje al resto de oficinas
     *
     */
    private void notificarOficinasPush(
            PrimitivaComunicacion primitivaComunicacion,
            String parametros) throws IOException {
        for (ConexionPushOficina conexionPushOficina
                : conexionesPushOficinas.values()) {
            conexionPushOficina.enviarSolicitud(
                    primitivaComunicacion,
                    TIEMPO_ESPERA_CLIENTE,
                    parametros);
        }
    }

    /**
     * Asigna un asiento a una oficina notificando al resto
     *
     */
    synchronized boolean asignar(String codigoViaje, String asiento,
                                 Viajero viajero) throws IOException {
        if ( ! oficinaViajes.asignar(codigoViaje, asiento, viajero)) {
            return false;
        }
        
        String parametros
                = codigoViaje + "\n"
                + asiento + "\n"
                + viajero.toString();

        notificarOficinasPush(PrimitivaComunicacion.ASIGNAR, parametros);
        return true;
    }

    /**
     * Dessigna un asiento a una oficina notificando al resto
     *
     */
    synchronized boolean desasignar(String codigoViaje, String asiento)
            throws IOException {
        if ( ! oficinaViajes.desasignar(codigoViaje, asiento)) {
            return false;
        }

        String parametros
                = codigoViaje + "\n"
                + asiento;

        notificarOficinasPush(PrimitivaComunicacion.DESASIGNAR,
                             parametros);
        return true;
    }

    /**
     * Procesa argumentos de main
     *
     */
    private static void procesarArgsMain(String[] args) {
        List<String> argumentos = new ArrayList<String>(Arrays.asList(args));

        if (argumentos.contains(ARG_DEBUG)) {
            modoDebug = true;
        }
    }

    public static void main(String[] args) {
        procesarArgsMain(args);
        new ServidorOficinas();
    }

}
