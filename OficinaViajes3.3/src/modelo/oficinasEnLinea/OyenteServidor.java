/**
 * OyenteServidor.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 */
package modelo.oficinasEnLinea;


import java.io.IOException;
import java.util.List;

/**
 *  Interfaz de oyente para recibir solicitudes del servidor
 * 
 */
public interface OyenteServidor {
   /**
    *  Llamado para notificar una solicitud del servidor
    * 
    */ 
   public boolean solicitudServidorProducida(
           PrimitivaComunicacion solicitud, 
           List<String> parametros) throws IOException;
}
