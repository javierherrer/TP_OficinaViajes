/**
 * ServidorOficina.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 */
package control;

import static control.ServidorOficinas.TIEMPO_ESPERA_CLIENTE;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import modelo.Posicion;
import modelo.Viaje;
import modelo.Viajero;

/**
 * ServidorOficina
 *
 */
public class ServidorOficina implements Runnable {

    public static String ERROR_CONEXION_OFICINA = "Closed office connection";
    private static String FORMATO_FECHA_CONEXION = "kk:mm:ss EEE d MMM yy";

    private ServidorOficinas servidorOficinas;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    private static String DEBUG_SOLICITUD = "Request:";
    private static String DEBUG_ERROR_SOLICITUD
            = DEBUG_SOLICITUD + " wrong from ";

    /**
     * Construye el servidor de OficinaViajes
     *
     */
    ServidorOficina(ServidorOficinas servidorOficinas, Socket socket)
            throws IOException {
        this.servidorOficinas = servidorOficinas;
        this.socket = socket;

        entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // Autoflush!!
        salida = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);
    }

    /**
     * Cierra conexión
     *
     */
    private void cerrarConexion() throws IOException {
        entrada.close();
        salida.close();
        socket.close();
    }

    /**
     * Atiende solicitudes de oficina
     *
     */
    @Override
    public void run() {
        try {
            PrimitivaComunicacion solicitud = PrimitivaComunicacion.nueva(
                    new Scanner(new StringReader(entrada.readLine())));

            switch (solicitud) {
                case CONECTAR_PUSH:
                    conectarPushOficina();
                    break;

                case DESCONECTAR_PUSH:
                    desconectarPushOficina();
                    break;

                case ASIGNAR:
                    asignarAsiento();
                    break;

                case DESASIGNAR:
                    desasignarAsiento();
                    break;

                case OBTENER_DIMENSIONES:
                    obtenerDimensionesAutobus();
                    break;

                case OBTENER_VIAJES:
                    obtenerViajes();
                    break;

                case OBTENER_NUMERO_ASIENTO:
                    obtenerNumeroAsiento();
                    break;

                case OBTENER_VIAJERO:
                    obtenerViajero();
                    break;

                case HOJA_VIAJE:
                    generarHojaViaje();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ERROR_CONEXION_OFICINA + ": " + e.toString());

            if (ServidorOficinas.esModoDebug()) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            if (ServidorOficinas.esModoDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtiene fecha de hoy como string
     *
     */
    private String obtenerFechaHoy() {
        return new SimpleDateFormat(FORMATO_FECHA_CONEXION,
                Locale.getDefault()).format(new Date());
    }

    /**
     * Conecta oficina push
     *
     */
    private void conectarPushOficina()
            throws IOException, InterruptedException {
        CountDownLatch cierreConexion = new CountDownLatch(1);

        String idConexion = servidorOficinas.generarIdConexionPushOficina();

        ConexionPushOficina conexionPushOficina
                = new ConexionPushOficina(idConexion, socket, cierreConexion);

        PrimitivaComunicacion respuesta = conexionPushOficina.enviarSolicitud(
                PrimitivaComunicacion.NUEVO_ID_CONEXION,
                TIEMPO_ESPERA_CLIENTE,
                idConexion);

        if (respuesta.equals(PrimitivaComunicacion.OK)) {
            servidorOficinas.nuevaConexionPushOficina(conexionPushOficina);

            // Esperamos hasta cierre conexión push oficina
            cierreConexion.await();
        } else {
            conexionPushOficina.cerrar();
        }
    }

    /**
     * Desconecta oficina push
     *
     */
    private void desconectarPushOficina() throws IOException {
        String idConexion = entrada.readLine();

        if (ServidorOficinas.esModoDebug()) {
            ConexionPushOficina conexionPushOficina
                    = servidorOficinas.obtenerConexionPushOficina(idConexion);

            if (conexionPushOficina != null) {
                System.out.println(DEBUG_SOLICITUD + " "
                        + PrimitivaComunicacion.DESCONECTAR_PUSH + " "
                        + conexionPushOficina.toString() + " "
                        + obtenerFechaHoy());
            }
        }

        if (servidorOficinas.eliminarConexionPushOficina(idConexion)) {
            salida.println(PrimitivaComunicacion.OK);
        } else {
            salida.println(PrimitivaComunicacion.NOK);
        }

        cerrarConexion();
    }

    /**
     * Lee el codigo de viaje
     *
     */
    private String leerCodigoViaje(PrimitivaComunicacion primitivaComunicacion)
            throws IOException {
        String codigoViaje = entrada.readLine();

        if (ServidorOficinas.esModoDebug()) {
            System.out.println(DEBUG_SOLICITUD + " "
                    + primitivaComunicacion.toString() + " "
                    + codigoViaje + " " + obtenerFechaHoy());
        }
        return codigoViaje;
    }

    /**
     * Parsea fecha de un string
     *
     */
    private GregorianCalendar parsearFecha(String linea) {
        Scanner scanner = new Scanner(linea);

        try {
            int dia = scanner.nextInt();
            int mes = scanner.nextInt() - 1;  // GC meses de 0 a 11
            int año = scanner.nextInt();

            return new GregorianCalendar(año, mes, dia);

        } catch (InputMismatchException e) {
            return null;
        }
    }

    /**
     * Lee fecha de entrada
     *
     */
    private GregorianCalendar leerFecha() throws IOException {
        GregorianCalendar fecha = parsearFecha(entrada.readLine());

        if (fecha == null) {
            if (ServidorOficinas.esModoDebug()) {
                System.out.println(DEBUG_ERROR_SOLICITUD + " "
                        + socket.getInetAddress() + " "
                        + obtenerFechaHoy());
            }
        }

        return fecha;
    }

    /**
     * Asigna asiento en la oficina
     *
     */
    private void asignarAsiento() throws IOException {
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK;

        String codigoViaje = leerCodigoViaje(PrimitivaComunicacion.ASIGNAR);
        String asiento = entrada.readLine();
        Viajero viajero = new Viajero(entrada.readLine(),
                entrada.readLine());
        
        if (codigoViaje != null) {
            if (servidorOficinas.asignar(codigoViaje, asiento, viajero)) {
                respuesta = PrimitivaComunicacion.OK;
            }
        }
        
        salida.println(respuesta);

        cerrarConexion();
    }

    /**
     * Desasigna asiento en la oficina
     *
     */
    private void desasignarAsiento() throws IOException {
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK;

        String codigoViaje = leerCodigoViaje(PrimitivaComunicacion.ASIGNAR);
        String asiento = entrada.readLine();
        
        if (codigoViaje != null) {
            if (servidorOficinas.desasignar(codigoViaje, asiento)) {
                respuesta = PrimitivaComunicacion.OK;
            }
        }
        
        salida.println(respuesta);

        cerrarConexion();
    }

    /**
     * Devuelve las dimensiones del autobús
     *
     */
    private void obtenerDimensionesAutobus() throws IOException {
        salida.println(PrimitivaComunicacion.OBTENER_DIMENSIONES);

        Dimension dimensiones = servidorOficinas.obtenerDimensionesAutobus();
        String columnas = Integer.toString(dimensiones.width);
        String filas = Integer.toString(dimensiones.height);

        if (dimensiones != null) {
            salida.println(columnas + "\n" + filas);
        } else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }

        cerrarConexion();
    }

    /**
     * Devuelve información de viajes para ese día
     *
     */
    private void obtenerViajes() throws IOException {
        GregorianCalendar fecha = leerFecha();

        if (fecha != null) {
            salida.println(PrimitivaComunicacion.OBTENER_VIAJES);

            List<Viaje> viajes = servidorOficinas.obtenerViajes(fecha);

            if (viajes != null) {
                String cadena = "";
                for (Viaje viaje : viajes) {
                    cadena = cadena + viaje.toString() + "\n";
                }   
                salida.println(cadena.trim());
            }
        } else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }

        cerrarConexion();
    }

    /**
     * Devuelve el número de asiento asignado a esa posicion
     *
     */
    private void obtenerNumeroAsiento() throws IOException {
        String codigoViaje
                = leerCodigoViaje(PrimitivaComunicacion.OBTENER_NUMERO_ASIENTO);

        Posicion posicion = new Posicion(entrada.readLine());

        if (codigoViaje != null) {
            salida.println(PrimitivaComunicacion.OBTENER_NUMERO_ASIENTO);

            String asiento
                    = servidorOficinas.obtenerNumeroAsiento(codigoViaje, posicion);

            if (asiento != null) {
                salida.println(asiento);
            }

        } else {
            salida.println(PrimitivaComunicacion.NOK);
        }

        cerrarConexion();
    }

    /**
     * Devuelve el viajero asignado a un asiento
     *
     */
    private void obtenerViajero() throws IOException {
        String codigoViaje
                = leerCodigoViaje(PrimitivaComunicacion.OBTENER_VIAJERO);
        String asiento = entrada.readLine();

        if (codigoViaje != null) {
            salida.println(PrimitivaComunicacion.OBTENER_VIAJERO);

            Viajero viajero 
                    = servidorOficinas.obtenerViajero(codigoViaje, asiento);
            
            if (viajero != null) {
                salida.println(viajero.toString());
            }
        } else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }

        cerrarConexion();
    }

    /**
     * Devuelve la hoja del viaje al que corresponde el código
     *
     */
    private void generarHojaViaje() throws IOException {
        String codigoViaje
                = leerCodigoViaje(PrimitivaComunicacion.HOJA_VIAJE);

        if (codigoViaje != null) {
            salida.println(PrimitivaComunicacion.HOJA_VIAJE);

            String hojaViaje = servidorOficinas.generarHojaViaje(codigoViaje);

            if (hojaViaje != null) {
                salida.println(hojaViaje);
            }
        } else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }

        cerrarConexion();
    }
}
