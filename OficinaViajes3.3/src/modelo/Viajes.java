/**
 * Viajes.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 *
 */
package modelo;

import control.Oficina;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import modelo.oficinasEnLinea.Cliente;
import modelo.oficinasEnLinea.OyenteServidor;
import modelo.oficinasEnLinea.PrimitivaComunicacion;
import vista.DebugVista;
import vista.OficinaVista;

/**
 * Viajes de la oficina
 *
 */
public class Viajes implements OyenteServidor {

    private OyenteServidor oyenteServidor;
    private PropertyChangeSupport observadores;

    public static String PROPIEDAD_ASIENTO_ASIGNADO = "Asiento asignado";
    public static String PROPIEDAD_ASIENTO_DESASIGNADO
            = "Asiento desasignado";
    public static String PROPIEDAD_CONECTADO = "Conectado";

    private Cliente cliente;
    private boolean conectado;
    private String idConexion;

    private static String FICHERO_CONFIG_ERRONEO
            = "Fichero configuración erróneo. Usando parámetros por defecto";
    private Properties configuracion;
    private static final String FICHERO_CONFIG = "config.properties";
    private static String COMENTARIO_CONFIG
            = Oficina.VERSION + " configuración conexión Servidor";

    public static final String URL_SERVIDOR = "URLServidor";
    private String URLServidor = "<ponAquiURLServidor>";
    public static final String PUERTO_SERVIDOR = "puertoServidor";
    private int puertoServidor = 15000;
    
    private static final String ID_CONEXION_VACIO = "---";
    private static final int TAMANYO_DIMENSIONES = 2;
    private static final int INDICE_ID_CONEXION = 0;
    private static final int INDICE_CODIGO_VIAJE = 0;
    private static final int INDICE_ASIENTO = 1;
    private static final int INDICE_VIAJERO_DNI = 2;
    private static final int INDICE_VIAJERO_NOMBRE = 3;
    private static final int INDICE_COLUMNAS = 0;
    private static final int INDICE_FILAS = 1;

    /**
     * Construye viajes
     *
     */
    public Viajes() {
        oyenteServidor = this;
        conectado = false;
        observadores = new PropertyChangeSupport(this);

        leerConfiguracion();

        cliente = new Cliente(URLServidor, puertoServidor);
    }

    /**
     * Lee la configuración de viajes
     *
     */
    private void leerConfiguracion() {
        try {
            configuracion = new Properties();
            configuracion.load(new FileInputStream(FICHERO_CONFIG));

            URLServidor = configuracion.getProperty(URL_SERVIDOR);
            puertoServidor = Integer.parseInt(
                    configuracion.getProperty(PUERTO_SERVIDOR));
        } catch (Exception e) {
            configuracion.setProperty(URL_SERVIDOR, URLServidor);
            configuracion.setProperty(PUERTO_SERVIDOR,
                    Integer.toString(puertoServidor));

            guardarConfiguracion();

            if (Oficina.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                        FICHERO_CONFIG_ERRONEO, e);
            }
        }
    }

    /**
     * Guarda configuración de recordatorios
     *
     */
    private void guardarConfiguracion() {
        try {
            FileOutputStream fichero = new FileOutputStream(FICHERO_CONFIG);
            configuracion.store(fichero, COMENTARIO_CONFIG);
            fichero.close();
        } catch (IOException e) {
            if (Oficina.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                        OficinaVista.ERROR_CONFIGURACION_NO_GUARDADA, e);
            }
        }
    }

    /**
     * Obtiene identificador conexión
     *
     */
    public String obtenerIdentificadorConexion() {
        return conectado ? idConexion : ID_CONEXION_VACIO;
    }

    /**
     * Añade nuevo observador de los recordatorios
     *
     */
    public void nuevoObservador(PropertyChangeListener observador) {
        this.observadores.addPropertyChangeListener(observador);
    }

    /**
     * Conecta con servidor mediante long polling
     *
     */
    public void conectar() {
        new Thread() {
            @Override
            public void run() {
                Cliente cliente = new Cliente(URLServidor, puertoServidor);

                while (true) {
                    try {
                        cliente.enviarSolicitudLongPolling(
                                PrimitivaComunicacion.CONECTAR_PUSH,
                                Cliente.TIEMPO_ESPERA_LARGA_ENCUESTA,
                                null,
                                oyenteServidor);
                    } catch (Exception e) {
                        conectado = false;
                        observadores.firePropertyChange(
                                PROPIEDAD_CONECTADO, null, conectado);

                        if (Oficina.esModoDebug()) {
                            DebugVista.devolverInstancia().mostrar(
                                    OficinaVista.ERROR_CONEXION_SERVIDOR, e);
                        }

                        // Volvemos a intentar conexión pasado un tiempo
                        try {
                            sleep(Cliente.TIEMPO_REINTENTO_CONEXION_SERVIDOR);
                        } catch (InterruptedException e2) {
                            // Propagamos a la máquina virtual
                            new RuntimeException();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Desconecta del servidor
     *
     */
    public void desconectar() throws Exception {
        if ( ! conectado) {
            return;
        }

        cliente.enviarSolicitud(PrimitivaComunicacion.DESCONECTAR_PUSH,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                idConexion);
    }

    /**
     * Asigna un asiento a un viajero en una posición
     *
     */
    public void asignarAsiento(String codigoViaje, String asiento, 
                               Viajero viajero) throws Exception {
        if ( ! conectado) {
            return;
        }

        String parametros = codigoViaje + "\n" + asiento + "\n" 
                            + viajero.toString();

        cliente.enviarSolicitud(PrimitivaComunicacion.ASIGNAR,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                parametros);
    }

    /**
     * Desasigna un asiento en un viaje
     *
     */
    public void desasignarAsiento(String codigoViaje, String asiento)
            throws Exception {
        if ( ! conectado) {
            return;
        }

        String parametros = codigoViaje + "\n" + asiento;

        cliente.enviarSolicitud(PrimitivaComunicacion.DESASIGNAR,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                parametros);
    }

    /**
     * Devuelve las dimensiones del autobús
     *
     */
    public Dimension obtenerDimensionesAutobus() throws Exception {
        if ( ! conectado) {
            return null;
        }

        List<String> resultados = new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_DIMENSIONES,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                null,
                resultados);

        if (resultados.isEmpty() || resultados.size() != TAMANYO_DIMENSIONES
                || respuesta.equals(PrimitivaComunicacion.NOK.toString())) {
            return null;
        } else {
            try {
                int columnas = Integer.parseInt(resultados.get(INDICE_COLUMNAS));
                int filas = Integer.parseInt(resultados.get(INDICE_FILAS));
                
                return new Dimension(columnas, filas);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    /**
     * Devuelve información de viajes programados para ese día
     *
     */
    public List<Viaje> obtenerViajes(GregorianCalendar fecha) throws Exception {
        if ( ! conectado) {
            return null;
        }

        String parametros = fecha.get(Calendar.DAY_OF_MONTH) + " "
                            // GC meses de 0 a 11 
                            + (fecha.get(Calendar.MONTH) + 1) + " "     
                            + fecha.get(Calendar.YEAR);

        List<String> resultados = new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_VIAJES,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                parametros,
                resultados);

        if (resultados.isEmpty()
                || respuesta.equals(PrimitivaComunicacion.NOK.toString())) {
            return null;
        } else {
            List<Viaje> viajes = new ArrayList<>();
            for (String viaje : resultados) {
                viajes.add(new Viaje(new Scanner(viaje)));
            }
            return viajes;
        }
    }

    /**
     * Devuelve el número de asiento asignado a esa posicion
     *
     */
    public String obtenerNumeroAsiento(String codigoViaje, Posicion posicion) 
            throws Exception {
        if ( ! conectado) {
            return null;
        }

        String parametros = codigoViaje + "\n" + posicion.toString();

        List<String> resultados = new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_NUMERO_ASIENTO,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                parametros,
                resultados);

        return resultados.isEmpty()
                || respuesta.equals(PrimitivaComunicacion.NOK.toString())
                ? null : resultados.get(0);
    }

    /**
     * Devuelve el viajero asignado a un asiento
     *
     */
    public String obtenerViajero(String codigoViaje, String asiento)
            throws Exception {
        if ( ! conectado) {
            return null;
        }

        String parametros = codigoViaje + "\n" + asiento;

        List<String> resultados = new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_VIAJERO,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                parametros,
                resultados);

        return resultados.isEmpty()
                || respuesta.equals(PrimitivaComunicacion.NOK.toString())
                ? null : resultados.get(0) + "\n" + resultados.get(1);
    }

    /**
     * Devuelve la hoja del viaje al que corresponde el código
     *
     */
    public List<String> generarHojaViaje(String codigoViaje) throws Exception {
        if ( ! conectado) {
            return null;
        }

        String parametros = codigoViaje;

        List<String> resultados = new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.HOJA_VIAJE,
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                parametros,
                resultados);

        return resultados.isEmpty()
                || respuesta.equals(PrimitivaComunicacion.NOK.toString())
                ? null : resultados;
    }

    /**
     * Recibe solicitud del servidor de nuevo idConexion
     *
     */
    private boolean solicitudServidorNuevoIdConexion(List<String> resultados)
            throws IOException {
        idConexion = resultados.get(INDICE_ID_CONEXION);
        if (idConexion == null) {
            return false;
        }

        conectado = true;

        observadores.firePropertyChange(PROPIEDAD_CONECTADO, null, conectado);

        return true;
    }

    /**
     * Recibe solicitud de servidor de asignar asiento
     *
     */
    private boolean solicitudServidorAsignarAsiento(String propiedad,
            List<String> resultados) throws IOException {
        try {
            String codigoViaje = resultados.get(INDICE_CODIGO_VIAJE);
            String asiento = resultados.get(INDICE_ASIENTO);
            Viajero viajero = new Viajero(resultados.get(INDICE_VIAJERO_DNI),
                                          resultados.get(INDICE_VIAJERO_NOMBRE));

            Tupla<Tupla<String, String>, Viajero> tupla
                    = new Tupla(new Tupla(codigoViaje, asiento), viajero);

            observadores.firePropertyChange(propiedad, null, tupla);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Recibe solicitud de servidor de deasignar asiento
     *
     */
    private boolean solicitudServidorDesasignarAsiento(String propiedad,
            List<String> resultados) throws IOException {
        try {
            String codigoViaje = resultados.get(INDICE_CODIGO_VIAJE);
            String asiento = resultados.get(INDICE_ASIENTO);

            Tupla<String, String> tupla = new Tupla(codigoViaje, asiento);

            observadores.firePropertyChange(propiedad, null, tupla);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Recibe solicitudes servidor oficina en línea
     *
     */
    @Override
    public boolean solicitudServidorProducida(PrimitivaComunicacion solicitud,
            List<String> parametros)
            throws IOException {
        if (parametros.isEmpty()) {
            return false;
        }

        switch (solicitud) {
            case NUEVO_ID_CONEXION:
                return solicitudServidorNuevoIdConexion(parametros);

            case ASIGNAR:
                return solicitudServidorAsignarAsiento(
                        PROPIEDAD_ASIENTO_ASIGNADO, parametros);

            case DESASIGNAR:
                return solicitudServidorDesasignarAsiento(
                        PROPIEDAD_ASIENTO_DESASIGNADO, parametros);

            default:
                return false;
        }
    }
}
