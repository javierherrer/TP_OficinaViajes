/**
 * ConexionPushOficina.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 */
package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * ConexionPushOficina
 *
 */
public class ConexionPushOficina {

    private String idConexion;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private CountDownLatch cierreConexion;

    /**
     * Construye conexión push con oficina
     *
     */
    ConexionPushOficina(String idConexion, Socket socket,
            CountDownLatch cierreConexion) throws IOException {
        this.idConexion = idConexion;
        this.socket = socket;
        this.cierreConexion = cierreConexion;

        entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // Autoflush!!
        salida = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);
    }

    /**
     * Obtiene identificador de conexión
     *
     */
    String obtenerIdConexion() {
        return idConexion;
    }

    /**
     * Envia solicitud
     *
     */
    private void enviar(PrimitivaComunicacion solicitud,
            int tiempoEspera,
            String parametros) throws IOException {
        socket.setSoTimeout(tiempoEspera);

        salida.println(solicitud.toString());

        if (parametros != null) {
            salida.println(parametros);
        }

        salida.println(PrimitivaComunicacion.FIN);
    }

    /**
     * Recibe respuesta
     *
     */
    private PrimitivaComunicacion recibir(List<String> resultados)
            throws IOException {
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK;

        String linea = entrada.readLine();
        if (linea != null) {
            respuesta = PrimitivaComunicacion.nueva(
                    new Scanner(new StringReader(linea)));

            resultados.clear();

            while (entrada.ready()) {
                resultados.add(entrada.readLine());
            }
        }
        return respuesta;
    }

    /**
     * Envía solicitud a oficina
     *
     */
    synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud,
            int tiempoEspera,
            String parametros, List<String> resultados) throws IOException {
        enviar(solicitud, tiempoEspera, parametros);

        return recibir(resultados);
    }

    /**
     * Envía solicitud a oficina sin esperar resultados
     *
     */
    synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud, int tiempoEspera, String parametros)
            throws IOException {
        return enviarSolicitud(solicitud, tiempoEspera, parametros,
                new ArrayList());
    }

    /**
     * Envía solicitud a oficina sin parámetros y sin esperar resultados
     *
     */
    synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud, int tiempoEspera)
            throws IOException {
        return enviarSolicitud(solicitud, tiempoEspera, null, new ArrayList());
    }

    /**
     * Cierra la conexión
     *
     */
    synchronized void cerrar() throws IOException {
        entrada.close();
        salida.close();
        socket.close();

        cierreConexion.countDown();
    }

    /**
     * toString
     *
     */
    public String toString() {
        return idConexion;
    }
}
