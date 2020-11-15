/**
 * AutobusVista.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 *
 */
package vista;

import control.Oficina;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Scanner;
import javax.swing.JPanel;
import modelo.Posicion;
import modelo.Viajero;
import modelo.Viajes;

/**
 * Vista del autobús
 */
public class AutobusVista extends JPanel {

    private static final int ALTURA_FILA = 50;
    private static final int ANCHURA_COLUMNA = 50;
    private int filas;
    private int columnas;
    private AsientoVista[][] asientosVista;
    private OficinaVista oficinaVista;
    private Viajes viajes;
    private String codigoViaje;

    public static final boolean RECIBE_EVENTOS_RATON = true;
    public static final boolean NO_RECIBE_EVENTOS_RATON = false;

    /**
     * Construye la vista del autobus
     *
     */
    AutobusVista(OficinaVista oficinaVista, Viajes viajes,
            Dimension dimension, boolean recibeEventosRaton) {
        this.setEnabled(false);
        this.oficinaVista = oficinaVista;
        this.viajes = viajes;
        this.columnas = dimension.width;
        this.filas = dimension.height;

        crearCasillas(recibeEventosRaton);

        this.setPreferredSize(new Dimension(columnas * ANCHURA_COLUMNA,
                filas * ALTURA_FILA));
    }

    /**
     * Construye la vista del autobus vacía
     *
     */
    AutobusVista() {
        this.setEnabled(false);
    }

    /**
     * Crea casillas
     *
     */
    private void crearCasillas(boolean recibeEventosRaton) {
        setLayout(new GridLayout(filas, columnas));
        asientosVista = new AsientoVista[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                asientosVista[fila][columna] = new AsientoVista(oficinaVista,
                        recibeEventosRaton);
                add(asientosVista[fila][columna]);
            }
        }
    }

    /**
     * Buscar asientoVista para un asiento de asiento
     *
     */
    private AsientoVista buscarAsientoVista(String asiento) {
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                String numero = asientosVista[fila][columna].obtenerCodigo();
                if (numero != null && asiento.equals(numero)) {
                    return asientosVista[fila][columna];
                }
            }
        }
        return null;
    }

    /**
     * Asigna asiento
     *
     */
    public void asignarAsiento(String viaje, String asiento,
            Viajero viajero) {
        if (! codigoViaje.equals(viaje)) {
            return;
        }
        
        AsientoVista asientoVista = buscarAsientoVista(asiento);
        if (asientoVista != null) {
            asientoVista.asignarAsiento(viajero);
        }
    }

    /**
     * Desasigna asiento
     *
     */
    public void desasignarAsiento(String viaje, String asiento) {
        if (! codigoViaje.equals(viaje)) {
            return;
        }
        
        AsientoVista asientoVista = buscarAsientoVista(asiento);
        if (asientoVista != null) {
            asientoVista.desasignarAsiento();
        }
    }

    /**
     * Inicia vista del autobus
     *
     */
    private void iniciarAutobusVista() {
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                asientosVista[fila][columna].iniciar();
            }
        }
    }

    /**
     * Pone asientos de autobus vista
     *
     */
    public void ponerAsientos(String viaje) {
        codigoViaje = viaje;
        iniciarAutobusVista();

        for (int columna = 0; columna < columnas; columna++) {
            for (int fila = 0; fila < filas; fila++) {
                AsientoVista asientoVista = asientosVista[fila][columna];
                Posicion posicion = new Posicion(columna + 1, fila + 1);

                try {
                    String asiento = viajes.obtenerNumeroAsiento(codigoViaje,
                                                                posicion);
                    if (asiento != null) {
                        asientoVista.ponerCodigo(asiento);
                        String viajero = 
                                viajes.obtenerViajero(codigoViaje, asiento);
                        if (viajero != null) {
                            asientoVista.asignarAsiento(
                                    new Viajero(new Scanner(viajero)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (Oficina.esModoDebug()) {
                        DebugVista.devolverInstancia().mostrar(
                                oficinaVista.ERROR_OBTENER_ASIENTO, e);
                    }
                }
            }
        }
    }

}
