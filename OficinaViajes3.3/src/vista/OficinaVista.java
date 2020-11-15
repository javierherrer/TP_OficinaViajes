/**
 * OficinaVista.java
 *
 * Versión 3.3 16-6-2020
 *
 * Javier Herrer Torres
 *
 */
package vista;

import control.Oficina;
import control.OyenteVista;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import modelo.Tupla;
import modelo.Viaje;
import modelo.Viajero;
import modelo.Viajes;

/**
 * Vista Swing de la oficina
 */
public class OficinaVista implements ActionListener, ItemListener,
        PropertyChangeListener {

    private OyenteVista oyenteVista;
    private Viajes viajes;

    private static OficinaVista instancia = null;  // es singleton
    private JFrame ventana;
    private AutobusVista autobusVista;
    private JComboBox desplegableAnyos;
    private JComboBox desplegableMeses;
    private JComboBox desplegableDias;
    private JComboBox desplegableViajes;
    private JButton botonBuscarViajes;
    private JButton botonVerAsientos;
    private JButton botonCambioEstado;
    private JButton botonGenerarHojaViaje;
    private JLabel etiquetaConectado;
    private boolean conectado;

    private List<Viaje> viajesDisponibles;
    private GregorianCalendar fechaViaje;
    private Viaje viajeSeleccionado;
    private AsientoVista asientoVistaSeleccionado;

    private static final int ANYOS_MOSTRADOS = 5;

    private static final String ASIGNAR = "Asignar";
    private static final String DESASIGNAR = "Desasignar";
    private static final String BUSCAR_VIAJES = "Buscar viajes";
    private static final String ETIQUETA_VIAJE = "Viaje: ";
    private static final String VER_ASIENTOS = "Ver asientos";
    private static final String GENERAR_HOJA_VIAJE = "Generar hoja viaje";
    private static final String NOMBRE_VIAJERO = "Nombre viajero";
    private static final String DNI_VIAJERO = "DNI viajero";
    private static final String ASIENTO_NUMERO = "Asiento número: ";

    public static final String ESTADO_CONECTADO = "Conectado";
    public static final String ESTADO_DESCONECTADO = "Desconectado";

    private static final String ACERCA_DE = "Acerca de...";
    private static final String DEBUG = "Debug";

    public static final String ERROR_SALIR
            = "Error al salir";
    public static final String ERROR_DIMENSIONES
            = "Error al obtener dimensiones";
    public static final String ERROR_BUSCAR_VIAJES
            = "Error al buscar viajes";
    public static final String ERROR_ASIGNAR_ASIENTO
            = "Error con asignar asiento";
    public static final String ERROR_DESASIGNAR_ASIENTO
            = "Error al desasignar asiento";
    public static final String ERROR_OBTENER_ASIENTO
            = "Error al obtener asiento";
    public static final String ERROR_HOJA_VIAJE
            = "Error al generar la hoja de viaje";

    public static final String ERROR_CONEXION_SERVIDOR
            = "Error de conexión con servidor";
    public static final String ERROR_CONFIGURACION_NO_GUARDADA
            = "Error al guardar configuración";

    /**
     * Constantes para redimensionamiento
     */
    public static final int MARGEN_HORIZONTAL = 50;
    public static final int MARGEN_VERTICAL = 20;

    /**
     * Construye la vista de la oficina
     *
     */
    private OficinaVista(OyenteVista oyenteVista, Viajes viajes) {
        this.oyenteVista = oyenteVista;
        this.viajes = viajes;
        fechaViaje = new GregorianCalendar();
        crearVentana();
    }

    public OyenteVista getOyenteVista() {
        return this.oyenteVista;
    }

    /**
     * Crea la ventana de la vista
     *
     */
    private void crearVentana() {
        ventana = new JFrame(Oficina.VERSION);

        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    oyenteVista.eventoProducido(OyenteVista.Evento.SALIR, null);
                } catch (Exception ex) {
                    mensajeError(ERROR_SALIR, ex);
                }
            }
        });

        ventana.getContentPane().setLayout(new BorderLayout());

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new GridLayout(2, 1));

        //creamos elementos
        crearBarraHerramientas(panelNorte);
        ventana.getContentPane().add(panelNorte, BorderLayout.NORTH);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new FlowLayout());
        crearPanelInferior(panelInferior);
        ventana.getContentPane().add(panelInferior, BorderLayout.SOUTH);

        ventana.setResizable(false);
    }

    /**
     * Devuelve la instancia de la vista
     *
     */
    public static synchronized OficinaVista
            instancia(OyenteVista oyenteIU, Viajes viajes) {
        if (instancia == null) {
            instancia = new OficinaVista(oyenteIU, viajes);
        }
        return instancia;
    }

    /**
     * Crea botón barra de herramientas
     *
     */
    private JButton crearBotonBarraHerramientas(String etiqueta) {
        JButton boton = new JButton(etiqueta);
        boton.addActionListener(this);
        boton.setActionCommand(etiqueta);

        return boton;
    }

    /**
     * Crea desplegable barra de herramientas
     *
     */
    private JComboBox crearDesplegableBarraHerramientas(String[] elementos) {
        JComboBox desplegable = new JComboBox(elementos);
        desplegable.addItemListener(this);

        return desplegable;
    }

    /**
     * Crea la barra de selección de fecha
     *
     */
    private JToolBar crearPrimeraBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);

        String[] anyos = new String[ANYOS_MOSTRADOS];
        for (int i = 0; i < ANYOS_MOSTRADOS; i++) {
            anyos[i] = Integer.toString(fechaViaje.get(Calendar.YEAR));
            fechaViaje.add(Calendar.YEAR, 1);
        }
        desplegableAnyos = crearDesplegableBarraHerramientas(anyos);
        barra.add(desplegableAnyos);

        String[] meses = new String[]{"Enero", "Febrero", "Marzo", "Abril",
            "Mayo", "Junio", "Julio", "Agosto",
            "Septiembre", "Octubre", "Noviembre",
            "Diciembre"};
        desplegableMeses = crearDesplegableBarraHerramientas(meses);
        barra.add(desplegableMeses);

        String[] dias = new String[fechaViaje.getMaximum(
                Calendar.DAY_OF_MONTH)];
        for (int i = 0; i < dias.length; i++) {
            dias[i] = String.valueOf(i + 1);
        }
        desplegableDias = crearDesplegableBarraHerramientas(dias);
        barra.add(desplegableDias);

        barra.add(new JToolBar.Separator());

        botonBuscarViajes = crearBotonBarraHerramientas(BUSCAR_VIAJES);
        botonBuscarViajes.setEnabled(false);
        barra.add(botonBuscarViajes);

        return barra;
    }

    /**
     * Crea la barra de selección de viaje
     *
     */
    private JToolBar crearSegundaBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);

        JLabel etiquetaViaje = new JLabel(ETIQUETA_VIAJE);
        barra.add(etiquetaViaje);

        desplegableViajes = new JComboBox();
        desplegableViajes.addItemListener(this);
        barra.add(desplegableViajes);
        desplegableViajes.setEnabled(false);

        barra.add(new JToolBar.Separator());

        botonVerAsientos = crearBotonBarraHerramientas(VER_ASIENTOS);
        botonVerAsientos.setEnabled(false);
        barra.add(botonVerAsientos);

        barra.add(new JToolBar.Separator());
        etiquetaConectado = new JLabel(ESTADO_CONECTADO);
        etiquetaConectado.setEnabled(false);
        barra.add(etiquetaConectado);

        barra.add(new JToolBar.Separator());

        JButton botonAcercaDe = crearBotonBarraHerramientas(ACERCA_DE);
        barra.add(botonAcercaDe);

        if (Oficina.esModoDebug()) {
            barra.add(new JToolBar.Separator());
            JButton botonDebug = crearBotonBarraHerramientas(DEBUG);
            barra.add(botonDebug);
        }

        return barra;
    }

    /**
     * Crea barra de herramientas
     *
     */
    private void crearBarraHerramientas(JPanel panelNorte) {
        panelNorte.add(crearPrimeraBarraHerramientas());
        panelNorte.add(crearSegundaBarraHerramientas());
    }

    /**
     * Crea el panel inferior
     *
     */
    private void crearPanelInferior(JPanel panelInferior) {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);

        botonCambioEstado = crearBotonBarraHerramientas(ASIGNAR);
        botonCambioEstado.setEnabled(false);
        barra.add(botonCambioEstado);

        barra.add(new JToolBar.Separator());

        botonGenerarHojaViaje = crearBotonBarraHerramientas(GENERAR_HOJA_VIAJE);
        botonGenerarHojaViaje.setEnabled(false);
        barra.add(botonGenerarHojaViaje);

        panelInferior.add(barra);
    }

    /**
     * Inicializa la vista del autobús
     *
     */
    private void inicializarAutobusVista() {
        Dimension dimension = new Dimension();
        try {
            dimension = viajes.obtenerDimensionesAutobus();
        } catch (Exception e) {
            mensajeError(ERROR_DIMENSIONES, e);
        }

        JPanel panelCentral = new JPanel();
        autobusVista = new AutobusVista(this, viajes, dimension,
                AutobusVista.RECIBE_EVENTOS_RATON);
        panelCentral.add(autobusVista);
        ventana.getContentPane().add(panelCentral, BorderLayout.CENTER);

        ventana.pack();  // ajusta ventana y sus componentes
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);  // centra en la pantalla
    }

    /**
     * Activa botón cambio de estado
     *
     */
    private void activarBotonCambioEstado(boolean activar) {
        botonCambioEstado.setEnabled(activar);
    }

    /**
     * Cambia la acción y el texto del botón cambio de estado
     *
     */
    private void cambiarAccionBotonCambioEstado(String texto) {
        botonCambioEstado.setText(texto);
        botonCambioEstado.setActionCommand(texto);
    }

    /**
     * Activa el botón buscar viajes
     *
     */
    private void activarBotonBuscarViajes(boolean activar) {
        botonBuscarViajes.setEnabled(activar);
    }

    /**
     * Activa el botón ver asientos
     *
     */
    private void activarBotonVerAsientos(boolean activar) {
        botonVerAsientos.setEnabled(activar);
    }

    /**
     * Activa el botón generar hoja viaje
     *
     */
    private void activarBotonGenerarHojaViaje(boolean activar) {
        botonGenerarHojaViaje.setEnabled(activar);
    }

    /**
     * Busca los viajes para una fecha
     *
     */
    private void buscarViajes() {
        desplegableViajes.removeAllItems();
        try {
            viajesDisponibles = viajes.obtenerViajes(fechaViaje);
        } catch (Exception ex) {
            mensajeError(ERROR_BUSCAR_VIAJES, ex);
        }
        
        if (viajesDisponibles.size() > 0) {    
            for (Viaje viaje : viajesDisponibles) {
                desplegableViajes.addItem(viaje.toString());
            }

            desplegableViajes.setEnabled(true);
            viajeSeleccionado = viajesDisponibles.get(0);
            activarBotonVerAsientos(true);
        } else {
            activarBotonVerAsientos(false);
        }
    }

    /**
     * Pone la vista del autobús
     *
     */
    private void verAsientos() {
        autobusVista.ponerAsientos(viajeSeleccionado.obtenerCodigo());
        activarBotonCambioEstado(true);
        activarBotonGenerarHojaViaje(true);
    }

    /**
     * Selecciona el viaje con el indice especificado
     *
     */
    private void seleccionarViaje(int indice) {
        if (viajesDisponibles.size() > 0) {
            viajeSeleccionado = viajesDisponibles.get(indice);
        }
    }

    /**
     * Selecciona asiento vista
     *
     */
    public void seleccionarAsientoVista(AsientoVista asientoVista) {
        if (!conectado) {
            return;
        }

        if (asientoVistaSeleccionado != null) {
            asientoVistaSeleccionado.deseleccionar();
        }

        if (asientoVista.obtenerCodigo() != null) {
            asientoVista.seleccionar();
            this.asientoVistaSeleccionado = asientoVista;

            if (asientoVistaSeleccionado.estaAsignado()) {
                cambiarAccionBotonCambioEstado(DESASIGNAR);
            } else {
                cambiarAccionBotonCambioEstado(ASIGNAR);
            }
        }
    }

    /**
     * Solicita información al usuario mediante un cuadro de diálogo
     *
     */
    private String solicitarInformacionUsuario(String mensaje, String titulo) {
        return JOptionPane.showInputDialog(ventana, mensaje, titulo,
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Asignar asiento
     *
     */
    private void asignarAsiento() {
        String nombre = solicitarInformacionUsuario(NOMBRE_VIAJERO,
                ASIENTO_NUMERO + asientoVistaSeleccionado.obtenerCodigo());

        String dni = solicitarInformacionUsuario(DNI_VIAJERO,
                ASIENTO_NUMERO + asientoVistaSeleccionado.obtenerCodigo());

        if (nombre != null && dni != null
                && !nombre.equals("") && !dni.equals("")) {
            String codigo = viajeSeleccionado.obtenerCodigo();
            Viajero viajero = new Viajero(nombre, dni);
            String asiento = asientoVistaSeleccionado.obtenerCodigo();
            try {
                oyenteVista.eventoProducido(
                        OyenteVista.Evento.ASIGNAR_ASIENTO,
                        new Tupla<Tupla<String, String>, Viajero>(
                                new Tupla<String, String>(codigo, asiento),
                                viajero));
            } catch (Exception e) {
                mensajeError(ERROR_ASIGNAR_ASIENTO, e);
            }
        }
    }

    /**
     * Asigna un asiento (vía PropertyChange)
     *
     */
    private void asignarAsiento(PropertyChangeEvent evt) {
        Tupla<Tupla<String, String>, Viajero> tripla
                    = (Tupla<Tupla<String, String>, Viajero>) evt.getNewValue();

            String codigoViaje = (tripla.a).a;
            String asiento = (tripla.a).b;
            Viajero viajero = tripla.b;

            autobusVista.asignarAsiento(codigoViaje, asiento, viajero);
            cambiarAccionBotonCambioEstado(DESASIGNAR);
    }

    /**
     * Desasignar asiento
     *
     */
    private void desasignarAsiento() {
        try {
            oyenteVista.eventoProducido(OyenteVista.Evento.DESASIGNAR_ASIENTO,
                    new Tupla<String, String>(viajeSeleccionado.obtenerCodigo(),
                            asientoVistaSeleccionado.obtenerCodigo()));
        } catch (Exception e) {
            mensajeError(ERROR_DESASIGNAR_ASIENTO, e);
        }
    }
    
    /**
     * Desasigna un asiento (vía PropertyChange)
     * 
     */
    private void desasignarAsiento(PropertyChangeEvent evt) {
        Tupla<String, String> tupla
                    = (Tupla<String, String>) evt.getNewValue();

            String codigoViaje = tupla.a;
            String asiento = tupla.b;

            autobusVista.desasignarAsiento(codigoViaje, asiento);
            cambiarAccionBotonCambioEstado(ASIGNAR);
    }

    /**
     * Genera hoja de viaje
     *
     */
    private void generarHojaViaje() {
        try {
            oyenteVista.eventoProducido(
                    OyenteVista.Evento.GENERAR_HOJA_VIAJE,
                    viajeSeleccionado);
        } catch (Exception e) {
            mensajeError(ERROR_HOJA_VIAJE, e);
        }

    }

    /**
     * Escribe mensaje con diálogo modal
     *
     */
    public void mensajeDialogo(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje,
                Oficina.VERSION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Escribe mensaje error
     *
     */
    public void mensajeError(String mensaje, Exception e) {
        if (Oficina.esModoDebug()) {
            DebugVista.devolverInstancia().mostrar(mensaje, e);
        } else {
            mensajeDialogo(mensaje);
        }
    }

    /**
     * Sobreescribe actionPerformed de ActionListener
     *
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case BUSCAR_VIAJES:
                buscarViajes();
                break;

            case VER_ASIENTOS:
                verAsientos();
                break;

            case ASIGNAR:
                asignarAsiento();
                break;

            case DESASIGNAR:
                desasignarAsiento();
                break;

            case GENERAR_HOJA_VIAJE:
                generarHojaViaje();
                break;

            case ACERCA_DE:
                JOptionPane.showMessageDialog(ventana,
                        Oficina.VERSION + "\n"
                        + "[" + viajes.obtenerIdentificadorConexion() + "]",
                        ACERCA_DE, JOptionPane.INFORMATION_MESSAGE);
                break;

            case DEBUG:
                DebugVista.devolverInstancia().mostrar();
                break;
        }
    }

    /**
     * Sobreescribe itemStateChanged de ItemListener
     *
     */
    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getSource().equals(desplegableViajes)) {
            int indiceViaje = desplegableViajes.getSelectedIndex();
            seleccionarViaje(indiceViaje);

        } else {
            int dia = desplegableDias.getSelectedIndex() + 1;
            int mes = desplegableMeses.getSelectedIndex();
            int anyo = Integer.valueOf((String) desplegableAnyos.
                    getSelectedItem());

            fechaViaje = new GregorianCalendar(anyo, mes, dia);
            activarBotonBuscarViajes(true);
        }
    }
    
    /**
     * Cambia la propiedad de conectado
     * 
     */
    private void cambiarPropiedadConectado(PropertyChangeEvent evt) {
        conectado = (boolean) evt.getNewValue();
            etiquetaConectado.setEnabled(conectado);

            if (conectado) {
                inicializarAutobusVista();
            }
    }

    /**
     * Sobreescribe propertyChange para recibir cambios en modelo
     *
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Viajes.PROPIEDAD_ASIENTO_ASIGNADO)) {
            asignarAsiento(evt);

        } else if (evt.getPropertyName().equals(
                Viajes.PROPIEDAD_ASIENTO_DESASIGNADO)) {
            desasignarAsiento(evt);

        } else if (evt.getPropertyName().equals(Viajes.PROPIEDAD_CONECTADO)) {
            cambiarPropiedadConectado(evt);
        }
    }
}
