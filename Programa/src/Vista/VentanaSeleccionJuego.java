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
     * @param gestorJuegos gestor del que obtiene la lista de juegos disponibles
     */
    public VentanaSeleccionJuego(JFrame padre, GestorJuegos gestorJuegos) {
        super(padre, "Selecciona un Juego", true);
        setSize(300, 240);
        setLocationRelativeTo(padre);
        setResizable(false);
        getContentPane().setBackground(Tema.FONDO);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String nombre : gestorJuegos.getJuegosDisponibles()) {
            modelo.addElement(nombre);
        }

        listaJuegos = new JList<>(modelo);
        listaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJuegos.setSelectedIndex(0);
        listaJuegos.setBackground(Tema.FONDO_CAMPO);
        listaJuegos.setForeground(Tema.TEXTO);
        listaJuegos.setSelectionBackground(Tema.ACENTO);
        listaJuegos.setSelectionForeground(Color.BLACK);
        listaJuegos.setFont(Tema.FUENTE_CAMPO);
        listaJuegos.setFixedCellHeight(32);

        JScrollPane scroll = new JScrollPane(listaJuegos);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Tema.FONDO_CAMPO);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.ACENTO, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(Tema.FONDO_PANEL);

        JButton btnSeleccionar = new JButton("Jugar");
        estilizarBoton(btnSeleccionar, Tema.ACENTO, Color.BLACK);
        btnSeleccionar.addActionListener(e -> accionSeleccionar());

        JButton btnCancelar = new JButton("Cancelar");
        estilizarBoton(btnCancelar, Tema.GRIS_BOTON, Tema.TEXTO);
        btnCancelar.addActionListener(e -> accionCancelar());

        botones.add(btnSeleccionar);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void estilizarBoton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(Tema.FUENTE_BOTON);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
    }

    /**
     * Guarda el juego seleccionado en la lista y cierra el diálogo.
     */
    private void accionSeleccionar() {
        juegoSeleccionado = listaJuegos.getSelectedValue();
        dispose();
    }

    /**
     * Establece el resultado como {@code null} y cierra el diálogo.
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
