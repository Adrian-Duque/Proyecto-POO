package vista;

import modelo.Partida;
import modelo.TresEnRaya;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ventana de juego para Tres en Raya.
 * Extiende VentanaJuego (abstracta) e implementa la vista del tablero 3x3.
 *
 * @author Ignacio deL peso dominguez
 * @version 2.0
 */
public class VentanaJuegoTresEnRaya extends VentanaJuego {

    private Partida partida;
    private TresEnRaya juego;

    private JFrame frame;
    private JButton[][] botones;
    private JLabel labelTurno;
    private JLabel labelPuntuaciones;
    private JButton btnPausar;
    private JButton btnFinalizar;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    /**
     * Crea la ventana del juego Tres en Raya.
     *
     * @param partida Partida en curso con el juego TresEnRaya asociado.
     */
    public VentanaJuegoTresEnRaya(Partida partida) {
        this.partida = partida;
        this.juego = (TresEnRaya) partida.getJuego();
        this.botones = new JButton[3][3];

        frame = new JFrame("Tres en Raya");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        inicializarComponentes();
        actualizarVista();

        frame.setVisible(true);
    }

    // ============================================================
    // INICIALIZACIÓN DE COMPONENTES
    // ============================================================

    private void inicializarComponentes() {
        frame.setLayout(new BorderLayout(10, 10));

        // Panel superior: turno y puntuaciones
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 0, 4));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        labelTurno = new JLabel("", SwingConstants.CENTER);
        labelTurno.setFont(new Font("Arial", Font.BOLD, 16));

        labelPuntuaciones = new JLabel("", SwingConstants.CENTER);
        labelPuntuaciones.setFont(new Font("Arial", Font.PLAIN, 13));

        panelInfo.add(labelTurno);
        panelInfo.add(labelPuntuaciones);
        frame.add(panelInfo, BorderLayout.NORTH);

        // Panel central: tablero 3x3
        JPanel panelTablero = new JPanel(new GridLayout(3, 3, 6, 6));
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int fila = i;
                final int columna = j;
                botones[i][j] = new JButton("");
                botones[i][j].setFont(new Font("Arial", Font.BOLD, 42));
                botones[i][j].setFocusPainted(false);
                botones[i][j].setBackground(Color.WHITE);
                botones[i][j].addActionListener(e -> manejarJugada(fila, columna));
                panelTablero.add(botones[i][j]);
            }
        }
        frame.add(panelTablero, BorderLayout.CENTER);

        // Panel inferior: botones Pausar y Finalizar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPausar = new JButton("Pausar");
        btnFinalizar = new JButton("Finalizar");
        btnPausar.setPreferredSize(new Dimension(110, 35));
        btnFinalizar.setPreferredSize(new Dimension(110, 35));
        btnPausar.addActionListener(e -> accionPausar());
        btnFinalizar.addActionListener(e -> accionFinalizar());
        panelBotones.add(btnPausar);
        panelBotones.add(btnFinalizar);
        frame.add(panelBotones, BorderLayout.SOUTH);
    }

    // ============================================================
    // LÓGICA DE JUGADA
    // ============================================================

    private void manejarJugada(int fila, int columna) {
        if (juego.isTerminado()) return;

        Usuario jugadorActual = partida.getJugadorActual();
        ArrayList<Usuario> jugadores = partida.getListaJugadores();
        char ficha = (jugadores.indexOf(jugadorActual) == 0) ? 'X' : 'O';

        boolean valida = juego.jugarTurno(jugadorActual.getUsername(), ficha, fila, columna);
        if (!valida) return;

        if (!juego.isTerminado()) {
            partida.siguienteTurno();
        } else {
            partida.finalizar();
        }

        actualizarVista();

        if (juego.isTerminado()) {
            mostrarResultado();
        }
    }

    private void mostrarResultado() {
        String ganador = juego.getGanador();
        String mensaje = (ganador != null) ? "¡Ha ganado " + ganador + "!" : "¡Empate!";
        JOptionPane.showMessageDialog(frame, mensaje, "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }

    // ============================================================
    // MÉTODOS ABSTRACTOS DE VentanaJuego
    // ============================================================

    /**
     * Actualiza todos los componentes visuales con el estado actual del juego.
     */
    @Override
    public void actualizarVista() {
        char[][] tablero = juego.getTablero();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char celda = tablero[i][j];
                botones[i][j].setText(celda == ' ' ? "" : String.valueOf(celda));
                botones[i][j].setEnabled(celda == ' ' && !juego.isTerminado());
                if (celda == 'X') botones[i][j].setForeground(Color.BLUE);
                else if (celda == 'O') botones[i][j].setForeground(Color.RED);
            }
        }

        if (!juego.isTerminado()) {
            labelTurno.setText("Turno: " + partida.getJugadorActual().getUsername());
        } else {
            labelTurno.setText("Partida finalizada");
        }

        StringBuilder sb = new StringBuilder("Puntuaciones — ");
        for (Usuario u : partida.getListaJugadores()) {
            sb.append(u.getUsername())
              .append(": ")
              .append(juego.getPuntuacion(u.getUsername()))
              .append("  ");
        }
        labelPuntuaciones.setText(sb.toString());
    }

    /**
     * Pausa la partida y cierra la ventana.
     */
    @Override
    public void accionPausar() {
        partida.pausar();
        JOptionPane.showMessageDialog(frame, "Partida pausada.", "Pausar", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }

    /**
     * Pide confirmación y finaliza la partida.
     */
    @Override
    public void accionFinalizar() {
        int confirmar = JOptionPane.showConfirmDialog(frame,
                "¿Seguro que quieres finalizar la partida?",
                "Finalizar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            partida.finalizar();
            frame.dispose();
        }
    }
}