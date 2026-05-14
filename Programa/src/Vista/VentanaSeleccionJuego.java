package Vista;

import javax.swing.*;
import java.awt.*;
import Controlador.GestorJuegos;

/**
 * Diálogo modal que permite al usuario seleccionar el juego al que desea jugar.
 * <p>
 * Se muestra como ventana bloqueante sobre la ventana padre. Una vez cerrado,
 * el resultado se obtiene mediante {@link #getJuegoSeleccionado()}.
 * </p>
 *
 * @author JP-Aceves
 * @version 1.0
 */
public class VentanaSeleccionJuego extends JDialog {

    /** Nombre del juego elegido por el usuario, o {@code null} si canceló. */
    private String juegoSeleccionado;

    /** Componente visual que muestra la lista de juegos disponibles. */
    private JList<String> listaJuegos;

    /**
     * Construye y muestra el diálogo de selección de juego.
     * <p>
     * La ventana es modal: bloquea la ejecución del hilo llamante hasta que
     * el usuario pulse "Jugar" o "Cancelar".
     * </p>
     *
     * @param padre        ventana propietaria sobre la que se centra el diálogo
     * @param gestorJuegos gestor del que se obtiene la lista de juegos disponibles
     */
    public VentanaSeleccionJuego(JFrame padre, GestorJuegos gestorJuegos) {
        super(padre, "Selecciona un Juego", true);
        setSize(280, 220);
        setLocationRelativeTo(padre);
        setResizable(false);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String nombre : gestorJuegos.getJuegosDisponibles()) {
            modelo.addElement(nombre);
        }

        listaJuegos = new JList<>(modelo);
        listaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJuegos.setSelectedIndex(0);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JScrollPane(listaJuegos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSeleccionar = new JButton("Jugar");
        btnSeleccionar.addActionListener(e -> accionSeleccionar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> accionCancelar()); // ← corregido
        botones.add(btnSeleccionar);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    /**
     * Guarda el juego seleccionado en la lista y cierra el diálogo.
     * Llamado al pulsar el botón "Jugar".
     */
    private void accionSeleccionar() {
        juegoSeleccionado = listaJuegos.getSelectedValue();
        dispose();
    }

    /**
     * Establece el resultado como {@code null} y cierra el diálogo.
     * Llamado al pulsar el botón "Cancelar".
     */
    private void accionCancelar() {
        juegoSeleccionado = null;
        dispose();
    }

    /**
     * Devuelve el nombre del juego que el usuario seleccionó.
     *
     * @return nombre del juego elegido, o {@code null} si el usuario canceló
     */
    public String getJuegoSeleccionado() {
        return juegoSeleccionado;
    }
}