package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorPartidas;
import Modelo.Partida;
import Modelo.TresEnRaya;
import Modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ventana de juego para Tres en Raya.
 * Extiende VentanaJuego (abstracta) e implementa la vista del tablero 3x3.
 *
 * @author Ignacio del Peso Dominguez
 * @version 2.0
 */
public class VentanaJuegoTresEnRaya extends VentanaJuego {

    private Partida partida;
    private TresEnRaya juego;

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
     * @param ventanaPadre       ventana del menú principal para volver al cerrar
     * @param gestorPartidas     gestor de partidas del sistema
     * @param gestorEstadisticas gestor de estadísticas del sistema
     * @param partida            partida en curso con el juego TresEnRaya asociado
     */
    public VentanaJuegoTresEnRaya(JFrame ventanaPadre, GestorPartidas gestorPartidas,
                                  GestorEstadisticas gestorEstadisticas, Partida partida) {
        super(ventanaPadre, gestorPartidas, gestorEstadisticas);
        this.partida = partida;
        this.juego = (TresEnRaya) partida.getJuego();
        this.botones = new JButton[3][3];

        setTitle("Tres en Raya");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        inicializarComponentes();
        actualizarVista();

        setVisible(true);
    }

    // ============================================================
    // INICIALIZACIÓN DE COMPONENTES
    // ============================================================

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Tema.FONDO_OSCURO);

        // Panel superior: turno y puntuaciones
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 0, 4));
        panelInfo.setBackground(Tema.FONDO_OSCURO);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        labelTurno = new JLabel("", SwingConstants.CENTER);
        labelTurno.setFont(Tema.FUENTE_TURNO);
        labelTurno.setForeground(Tema.ACENTO);

        labelPuntuaciones = new JLabel("", SwingConstants.CENTER);
        labelPuntuaciones.setFont(Tema.FUENTE_PUNTOS);
        labelPuntuaciones.setForeground(Tema.SUBTEXTO);

        panelInfo.add(labelTurno);
        panelInfo.add(labelPuntuaciones);
        add(panelInfo, BorderLayout.NORTH);

        // Panel central: tablero 3x3
        JPanel panelTablero = new JPanel(new GridLayout(3, 3, 6, 6));
        panelTablero.setBackground(Tema.FONDO);
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int fila = i;
                final int columna = j;
                botones[i][j] = new JButton("");
                botones[i][j].setFont(Tema.FUENTE_CELDA);
                botones[i][j].setFocusPainted(false);
                botones[i][j].setBackground(Tema.FONDO_PANEL);
                botones[i][j].setForeground(Tema.TEXTO);
                botones[i][j].setOpaque(true);
                botones[i][j].setBorderPainted(false);
                botones[i][j].addActionListener(e -> manejarJugada(fila, columna));
                panelTablero.add(botones[i][j]);
            }
        }
        add(panelTablero, BorderLayout.CENTER);

        // Panel inferior: botones Pausar y Finalizar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(Tema.FONDO_OSCURO);
        btnPausar = new JButton("Pausar");
        btnFinalizar = new JButton("Finalizar");
        estilizarBoton(btnPausar);
        estilizarBoton(btnFinalizar);
        btnPausar.setPreferredSize(new Dimension(120, 38));
        btnFinalizar.setPreferredSize(new Dimension(120, 38));
        btnPausar.addActionListener(e -> accionPausar());
        btnFinalizar.addActionListener(e -> accionFinalizar());
        panelBotones.add(btnPausar);
        panelBotones.add(btnFinalizar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton btn) {
        btn.setBackground(Tema.GRIS_BOTON);
        btn.setForeground(Tema.TEXTO);
        btn.setFont(Tema.FUENTE_BOTON);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
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
        }

        actualizarVista();

        if (juego.isTerminado()) {
            mostrarResultado(); // llama a accionFinalizar() que registra stats y finaliza vía gestores
        }
    }

    private void mostrarResultado() {
        String ganador = juego.getGanador();
        String mensaje = (ganador != null) ? "¡Ha ganado " + ganador + "!" : "¡Empate!";
        JOptionPane.showMessageDialog(this, mensaje, "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);
        accionFinalizar();
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
                if (celda == 'X') botones[i][j].setForeground(Tema.FICHA_X);
                else if (celda == 'O') botones[i][j].setForeground(Tema.FICHA_O);
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
}
