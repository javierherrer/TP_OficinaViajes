/**
 * AsientoVista.java
 *
 * Versión 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */

package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import modelo.Viajero;

/**
 * Vista de un asiento a partir de un JLabel
 */
public class AsientoVista extends JLabel{
    private OficinaVista vista;  
    private static final Color COLOR_SELECCIONADO = Color.YELLOW;
    private Color colorNoSeleccionado;
    private boolean seleccionado = false;
    private static final Color COLOR_ASIENTO_ASIGNADO = Color.CYAN;
    private boolean asignado = false;
    private String codigo;
    private Viajero viajero;
    private Font fuente;
    private Map atributos;
    private int tamañoNormal;
    public enum Formato {DESTACADO, NORMAL};

    /**
     * Construye la vista del asiento
     * 
     */
    AsientoVista(OficinaVista vista, boolean recibeEventosRaton) {
        this.vista = vista;  

        fuente = getFont();
        atributos = fuente.getAttributes(); 
        tamañoNormal = fuente.getSize();
        colorNoSeleccionado = this.getBackground();

        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        atributos.put(TextAttribute.SIZE, tamañoNormal);  
        setFont(fuente.deriveFont(atributos)); 

        if (recibeEventosRaton) {
            recibirEventosRaton();
        }    
    }
    
    public void ponerCodigo(String codigo){
       this.codigo = codigo;
       ponerTexto(codigo);
    }
    
    /**
     * Recibe los eventos de ratón
     * 
     */  
    private void recibirEventosRaton() {
        addMouseListener(new MouseAdapter() { 
            @Override
            public void mousePressed(MouseEvent e) { 
                AsientoVista asientoVista = (AsientoVista)e.getSource();

                if (asientoVista != null) {                  
                    vista.seleccionarAsientoVista(asientoVista);                 
                }
            }
        });    
    }

    /**
     * Selecciona asiento 
     * 
     */
    public void seleccionar() {
        seleccionado = true;
        setBackground(COLOR_SELECCIONADO);
    }  
  
    /**
     * Quita selección de asiento asignado
     * 
     */
    public void deseleccionar() {
        seleccionado = false;
        if (! asignado) {
            setBackground(colorNoSeleccionado);
        } else {
            setBackground(COLOR_ASIENTO_ASIGNADO);  
        }
    }    

    /**
     * Devuelve el estado de asignación del asiento
     * 
     */
    public boolean estaAsignado() {
        return asignado;
    }
    
    public boolean estaSeleccionado() {
        return seleccionado;    
    }
  
    /**
     * Asigna asiento 
     */
    public void asignarAsiento(Viajero viajero) {
        this.viajero = viajero;
        asignado = true;
        setBackground(COLOR_ASIENTO_ASIGNADO);
        setToolTipText(viajero.toString());
    }  
  
    /**
     * Desasigna asiento
     * 
     */
    public void desasignarAsiento() {
        this.viajero = null;
        asignado = false;
        setBackground(colorNoSeleccionado);    
        setToolTipText(null);
    }

    /**
     * Inicia asiento vista
     * 
     */
    public void iniciar() {
        ponerTexto("");  
        desasignarAsiento();    
        deseleccionar();
        codigo = null;
    }

    /**
     * Pone texto con formato normal
     * 
     */
    private void ponerTexto(String texto) {
        setText(texto);
    }
    
    /**
     * toString
     * 
     */
    public String obtenerCodigo() {
        return codigo;
    }
    
} 
