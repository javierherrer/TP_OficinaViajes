/**
 * DebugVista.java
 *
 * Versi�n 3.3 16-6-2020
 * 
 * Javier Herrer Torres
 * 
 */
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *  Ventana para informaci�n de debug
 * 
 */
public class DebugVista extends JFrame {
  private static DebugVista instancia = null;  // es singleton    
  private JTextArea texto;
  private JButton botonAceptar;
  
  private static final int FILAS = 20;
  private static final int COLUMNAS = 80;
  
  /** Identificadores de textos dependientes del idioma */          
  private static final String TITULO = "[Debug]";
  private static final String BOTON_ACEPTAR = "OK";  
  
  /**
   *  Construye ventana
   * 
   */    
  private DebugVista() {        
    super(TITULO);
    
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
         setVisible(false);
      }
    }); 
    
    this.setResizable(false);
    
    setLayout(new BorderLayout());
    texto = new JTextArea(FILAS, COLUMNAS);  
    texto.setEditable(false);
    JScrollPane panelSuperior = new JScrollPane(texto);
    add(panelSuperior, BorderLayout.NORTH);
    
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    botonAceptar = new JButton(BOTON_ACEPTAR);
    getRootPane().setDefaultButton(botonAceptar);
  
    botonAceptar.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        DebugVista.devolverInstancia().setVisible(false);
      } 
    });

    panel.add(botonAceptar);
    add(panel, BorderLayout.SOUTH);
     
    pack();  // ajusta ventana y sus componentes
    setLocationRelativeTo(null);  // centra en la pantalla      
  }
  
  /**
   *  Devuelve la instancia de la vista debug
   * 
   */        
  public static synchronized DebugVista devolverInstancia()  {
    if (instancia == null) {
      instancia = new DebugVista();    
    }
    return instancia;
  } 

  /**
   *  Muestra ventana debug
   * 
   */   
  public void mostrar() {
    setVisible(true);
  }  
  
  /**
   *  Muestra ventana debug
   * 
   */   
  public void mostrar(String mensaje) {
    mostrar(mensaje, null);
  }  
  
  /**
   *  Muestra ventana debug con informaci�n de una excepci�n
   * 
   */   
  public void mostrar(String mensaje, Exception e) {
    texto.append(mensaje);
    texto.append("\n");
    
    if (e != null) {
      // redirigimos traza exception a JTextArea
      StringWriter stackTrace = new StringWriter();
      e.printStackTrace(new PrintWriter(stackTrace));
      texto.append(stackTrace.toString());
      texto.append("\n"); 
    }
    
    //texto.setCaretPosition(0);
    
    texto.setCaretPosition(texto.getDocument().getLength());
    
    //setVisible(true);
  }  
}
