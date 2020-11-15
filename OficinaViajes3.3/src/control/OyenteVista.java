/**
 * OyenteVista.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package control;

/**
 *  Interfaz de oyente para recibir eventos de la interfaz de usuario
 */
public interface OyenteVista {
    public enum Evento {ASIGNAR_ASIENTO, DESASIGNAR_ASIENTO,
                        GENERAR_HOJA_VIAJE, SALIR}
    
    /**
     *  Llamado para notificar un evento de la interfaz de usuario
     */ 
    public void eventoProducido(Evento evento, Object obj) throws Exception;
}
