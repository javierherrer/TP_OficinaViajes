/**
 * ExcepcionOficinaViajes.java
 *
 * Versión 1.0 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package control;

public class ExcepcionOficinaViajes extends Exception {
    public enum Excepcion {CODIGO_NO_ENCONTRADO, ORIGEN_NO_ENCONTRADO,
                           DESTINO_NO_ENCONTRADO, FECHA_NO_ENCONTRADA,
                           CONDUCTOR_AUTOBUS_NO_ENCONTRADO, 
                           MATRICULA_AUTOBUS_NO_ENCONTRADA, 
                           FICHERO_AUTOBUSES_NO_ENCONTRADO,
                           FICHERO_VIAJES_NO_ENCONTRADO, 
                           ERROR_ESCRITURA_HOJA_VIAJES,
                           NUMERO_ASIENTO_NO_ENCONTRADO,
                           COLUMNA_ASIENTO_NO_ENCONTRADA,
                           FILA_ASIENTO_NO_ENCONTRADA, COLUMNAS_NO_ENCONTRADAS,
                           FILAS_NO_ENCONTRADAS };

    public static final String MENSAJE_CODIGO_NO_ENCONTRADO = 
            "No se ha encontrado el código del viaje.";
    public static final String MENSAJE_ORIGEN_NO_ENCONTRADO =
            "No se ha encontrado el origen del viaje.";
    public static final String MENSAJE_DESTINO_NO_ENCONTRADO = 
            "No se ha contrado el destino del viaje";
    public static final String MENSAJE_FECHA_NO_ENCONTRADA = 
            "No se ha encontrado la fecha del viaje";
    public static final String MENSAJE_CONDUCTOR_AUTOBUS_NO_ENCONTRADO = 
            "No se ha encontrado el conductor del autobús";
    public static final String MENSAJE_MATRICULA_AUTOBUS_NO_ENCONTRADA = 
            "No se ha encontrado la matrícula del autobús";
    public static final String MENSAJE_FICHERO_AUTOBUSES_NO_ENCONTRADO = 
            "No se ha encontrado el fichero de autobuses.";
    public static final String MENSAJE_FICHERO_VIAJES_NO_ENCONTRADO = 
            "No se ha encontrado el fichero de viajes";
    public static final String MENSAJE_ERROR_ESCRITURA_HOJA_VIAJES = 
            "No se ha podido escribir la hoja de viajes.";
    public static final String MENSAJE_NUMERO_ASIENTO_NO_ENCONTRADO = 
            "No se ha encotrado el número de asiento.";
    public static final String MENSAJE_COLUMNA_ASIENTO_NO_ENCONTRADA = 
            "No se ha encontrado la columna del asiento.";
    public static final String MENSAJE_FILA_ASIENTO_NO_ENCONTRADA = 
            "No se ha encontrado la fila del asiento.";
    public static final String MENSAJE_COLUMNAS_NO_ENCONTRADAS = 
            "No se han encontrado las columnas del autobús.";
    public static final String MENSAJE_FILAS_NO_ENCONTRADAS = 
            "No se han encontrado las filas del autobús.";
    public static final String MENSAJE_ERROR = "Error.";                           
    
    private Excepcion error;
    
    /**
     * Construimos una excepción
     * 
     */
    public ExcepcionOficinaViajes (Excepcion error) {
        this.error = error;
    }

    /**
     * Muestra el mensaje de error
     * 
     */
    @Override
    public String toString() {
        switch (error) {
            case CODIGO_NO_ENCONTRADO:
                return MENSAJE_CODIGO_NO_ENCONTRADO;
                
            case ORIGEN_NO_ENCONTRADO:
                return MENSAJE_ORIGEN_NO_ENCONTRADO;
                
            case DESTINO_NO_ENCONTRADO:
                return MENSAJE_DESTINO_NO_ENCONTRADO;
                
            case FECHA_NO_ENCONTRADA:
                return MENSAJE_FECHA_NO_ENCONTRADA;
                
            case CONDUCTOR_AUTOBUS_NO_ENCONTRADO:
                return MENSAJE_CONDUCTOR_AUTOBUS_NO_ENCONTRADO;
                
            case MATRICULA_AUTOBUS_NO_ENCONTRADA:
                return MENSAJE_MATRICULA_AUTOBUS_NO_ENCONTRADA;  
                
            case FICHERO_AUTOBUSES_NO_ENCONTRADO:
                return MENSAJE_FICHERO_AUTOBUSES_NO_ENCONTRADO;
                
            case FICHERO_VIAJES_NO_ENCONTRADO:
                return MENSAJE_FICHERO_VIAJES_NO_ENCONTRADO;
                
            case ERROR_ESCRITURA_HOJA_VIAJES:
                return MENSAJE_ERROR_ESCRITURA_HOJA_VIAJES;
                
            case NUMERO_ASIENTO_NO_ENCONTRADO:
                return MENSAJE_NUMERO_ASIENTO_NO_ENCONTRADO;
                
            case COLUMNA_ASIENTO_NO_ENCONTRADA:
                return MENSAJE_COLUMNA_ASIENTO_NO_ENCONTRADA;
                
            case FILA_ASIENTO_NO_ENCONTRADA:
                return MENSAJE_FILA_ASIENTO_NO_ENCONTRADA;
                
            case COLUMNAS_NO_ENCONTRADAS:
                return MENSAJE_COLUMNAS_NO_ENCONTRADAS;
                
            case FILAS_NO_ENCONTRADAS:
                return MENSAJE_FILAS_NO_ENCONTRADAS;
                
            default:
                return MENSAJE_ERROR;      
        }
    }
    
}
