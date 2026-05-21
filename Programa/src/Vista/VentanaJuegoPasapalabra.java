package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorPartidas;
import Modelo.Partida;
import Modelo.Pasapalabra;
import Modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana gráfica para el juego PasaPalabra.
 *
 * @author Adrián
 * @version 1.1
 */
public class VentanaJuegoPasapalabra extends VentanaJuego {

    // ── Referencias ─────────────────────────────────────────────────────────
    private final Pasapalabra juego;
    private final Usuario     jugador;
    private final Partida     partida;

    // ── Componentes Swing ────────────────────────────────────────────────────
    private PanelRosco panelRosco;
    private JTextArea lblDefinicion;
    private JLabel     lblLetra;
    private JLabel     lblAciertos;
    private JLabel     lblFallos;
    private JLabel     lblPasapalabras;
    private JLabel     lblTiempo;
    private JTextField txtRespuesta;
    private JButton    btnResponder;
    private JButton    btnPasapalabra;
    private JButton    btnPausar;

    // ── Temporizador ─────────────────────────────────────────────────────────
    private Timer timerSwing;
    private int   segundosRestantes;
    private static final int TIEMPO_INICIAL = 150;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * @param partida            Partida activa (contiene el juego y los jugadores).
     * @param gestorPartidas     Para pausar y finalizar.
     * @param gestorEstadisticas Para registrar el resultado al finalizar.
     * @param ventanaPadre       Ventana a la que volver al cerrar.
     */
    public VentanaJuegoPasapalabra(Partida partida, GestorPartidas gestorPartidas,
                                   GestorEstadisticas gestorEstadisticas, JFrame ventanaPadre) {
        super(ventanaPadre, gestorPartidas, gestorEstadisticas);
        this.partida           = partida;
        this.juego             = (Pasapalabra) partida.getJuego();
        this.jugador           = partida.getJugadorActual();
        this.segundosRestantes = TIEMPO_INICIAL;

        construirUI();
        actualizarVista();
        iniciarTimer();

        setTitle("PasaPalabra - " + jugador.getUsername());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { accionPausar(); }
        });
        setMinimumSize(new Dimension(860, 600));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Construcción de la interfaz ──────────────────────────────────────────

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Tema.FONDO_OSCURO);
        ((JPanel) getContentPane()).setOpaque(true);

        panelRosco = new PanelRosco();
        panelRosco.setPreferredSize(new Dimension(500, 500));
        panelRosco.setBackground(Tema.FONDO_OSCURO);
        panelRosco.setOpaque(true);

        add(panelRosco,              BorderLayout.CENTER);
        add(construirPanelDerecho(), BorderLayout.EAST);
        add(construirPanelControl(), BorderLayout.SOUTH);
    }

    private JPanel construirPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.FONDO_OSCURO);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        panel.setPreferredSize(new Dimension(320, 560));

        lblTiempo = crearLabel("2:30", Tema.FUENTE_TIEMPO, Tema.ACTUAL);
        lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTiempo);
        panel.add(Box.createVerticalStrut(16));

        lblLetra = crearLabel("A", Tema.FUENTE_LETRA, Tema.ACTUAL);
        lblLetra.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblLetra);
        panel.add(Box.createVerticalStrut(10));

        lblDefinicion = new JTextArea("...");
        lblDefinicion.setForeground(Tema.TEXTO);
        lblDefinicion.setFont(Tema.FUENTE_DEFINICION);
        lblDefinicion.setBackground(Tema.FONDO_OSCURO);
        lblDefinicion.setWrapStyleWord(true);
        lblDefinicion.setLineWrap(true);
        lblDefinicion.setEditable(false);
        lblDefinicion.setFocusable(false);
        lblDefinicion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDefinicion.setMaximumSize(new Dimension(270, Integer.MAX_VALUE));
        panel.add(lblDefinicion);

        txtRespuesta = new JTextField();
        txtRespuesta.setMaximumSize(new Dimension(260, 35));
        txtRespuesta.setFont(Tema.FUENTE_RESPUESTA);
        txtRespuesta.setBackground(Tema.FONDO_INPUT);
        txtRespuesta.setForeground(Tema.TEXTO);
        txtRespuesta.setCaretColor(Tema.TEXTO);
        txtRespuesta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDE_INPUT, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        txtRespuesta.addActionListener(e -> onResponder());
        panel.add(txtRespuesta);
        panel.add(Box.createVerticalStrut(10));

        btnResponder   = crearBoton("Responder",   Tema.CORRECTO,    e -> onResponder());
        btnPasapalabra = crearBoton("PasaPalabra", Tema.PASAPALABRA, e -> onPasapalabra());
        btnResponder.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPasapalabra.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnResponder);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnPasapalabra);
        panel.add(Box.createVerticalStrut(20));

        lblAciertos     = crearLabel("Aciertos: 0",     Tema.FUENTE_PUNTOS, Tema.CORRECTO);
        lblFallos       = crearLabel("Fallos: 0",       Tema.FUENTE_PUNTOS, Tema.INCORRECTO);
        lblPasapalabras = crearLabel("Pasapalabras: 0", Tema.FUENTE_PUNTOS, Tema.PASAPALABRA);
        lblAciertos.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFallos.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPasapalabras.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblAciertos);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblFallos);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblPasapalabras);

        return panel;
    }

    private JPanel construirPanelControl() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Tema.FONDO_OSCURO);
        btnPausar = crearBoton("Pausar partida", Tema.GRIS_BOTON, e -> accionPausar());
        panel.add(btnPausar);
        return panel;
    }

    // ── Actualización de la vista ────────────────────────────────────────────

    @Override
    public void actualizarVista() {
        if (juego.isTerminado()) {
            accionFinalizar();
            return;
        }

        int idx = juego.getLetraActual();
        String[] datos = juego.getDatosLetra(idx);
        if (datos != null) {
            lblLetra.setText(datos[0] != null ? datos[0] : "?");
            String def = datos[1] != null ? datos[1] : "(sin definición)";
            lblDefinicion.setText(def);
        }

        lblAciertos.setText("Aciertos: "         + juego.getAciertos());
        lblFallos.setText("Fallos: "             + juego.getFallos());
        lblPasapalabras.setText("Pasapalabras: " + juego.getPasaPalabras());

        panelRosco.repaint();
        txtRespuesta.setText("");
        txtRespuesta.requestFocusInWindow();
    }

    // ── Acciones de juego ────────────────────────────────────────────────────

    private void onResponder() {
        String respuesta = txtRespuesta.getText().trim();
        if (respuesta.isEmpty()) return;
        boolean correcta = juego.procesarRespuesta(respuesta);
        Color color = correcta ? Tema.CORRECTO : Tema.INCORRECTO;
        String msg  = correcta ? "Correcto!" : "Incorrecto";
        flashMensaje(msg, color);
        actualizarVista();
    }

    private void onPasapalabra() {
        juego.pasarPalabra();
        flashMensaje("PasaPalabra", Tema.PASAPALABRA);
        actualizarVista();
    }

    // ── Pausar y finalizar ───────────────────────────────────────────────────

    @Override
    protected void accionPausar() {
        detenerTimer();
        gestorPartidas.pausarPartida();
        dispose();
        ventanaPadre.setVisible(true);
    }

    @Override
    protected void accionFinalizar() {
        detenerTimer();
        Partida p = gestorPartidas.getPartidaActual();
        gestorPartidas.finalizarPartida();          // establece fechaFin
        gestorEstadisticas.registrarResultado(p);  // ya tiene fechaFin

        String resumen = String.format(
                "<html><center><h2>Partida terminada</h2>" +
                        "<p>Aciertos: <b>%d</b></p>" +
                        "<p>Fallos: <b>%d</b></p>" +
                        "<p>Pasapalabras: <b>%d</b></p>" +
                        "<p>Puntuación: <b>%d</b></p></center></html>",
                juego.getAciertos(),
                juego.getFallos(),
                juego.getPasaPalabras(),
                juego.getPuntuacion(jugador.getUsername())
        );

        JOptionPane.showMessageDialog(this, resumen, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        ventanaPadre.setVisible(true);
    }

    // ── Temporizador ─────────────────────────────────────────────────────────

    private void iniciarTimer() {
        timerSwing = new Timer(1000, e -> {
            segundosRestantes--;
            mostrarTiempo(segundosRestantes);
            if (segundosRestantes <= 0) {
                detenerTimer();
                JOptionPane.showMessageDialog(this,
                        "Se acabó el tiempo!", "Tiempo", JOptionPane.WARNING_MESSAGE);
                accionFinalizar();
            }
        });
        timerSwing.start();
    }

    private void detenerTimer() {
        if (timerSwing != null && timerSwing.isRunning()) timerSwing.stop();
    }

    private void mostrarTiempo(int segundos) {
        lblTiempo.setText(String.format("%d:%02d", segundos / 60, segundos % 60));
        lblTiempo.setForeground(segundos <= 30 ? Tema.INCORRECTO : Tema.ACTUAL);
    }

    // ── Helpers de UI ────────────────────────────────────────────────────────

    private JLabel crearLabel(String texto, Font fuente, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        return lbl;
    }

    private JButton crearBoton(String texto, Color bgColor, ActionListener listener) {
        JButton btn = new JButton(texto);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(Tema.FUENTE_BOTON_JUEGO);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        btn.setMaximumSize(new Dimension(240, 40));
        return btn;
    }

    private void flashMensaje(String mensaje, Color color) {
        lblDefinicion.setForeground(color);
        lblDefinicion.setText(mensaje);
        Timer t = new Timer(700, e -> {
            lblDefinicion.setForeground(Tema.TEXTO);
            actualizarVista();
        });
        t.setRepeats(false);
        t.start();
    }

    // ── Panel del rosco ──────────────────────────────────────────────────────

    private class PanelRosco extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (juego == null) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int cx          = getWidth()  / 2;
            int cy          = getHeight() / 2;
            int radio       = Math.min(getWidth(), getHeight()) / 2 - 40;
            int totalLetras = juego.getTotalLetras();
            int actual      = juego.getLetraActual();

            for (int i = 0; i < totalLetras; i++) {
                String[] datos = juego.getDatosLetra(i);
                if (datos == null) continue;

                double angulo = (2 * Math.PI * i / totalLetras) - Math.PI / 2;
                int x = (int) (cx + radio * Math.cos(angulo));
                int y = (int) (cy + radio * Math.sin(angulo));

                Color fondo;
                if (i == actual) {
                    fondo = Tema.ACTUAL;
                } else {
                    switch (datos[3] != null ? datos[3] : "") {
                        case Pasapalabra.ESTADO_CORRECTA:    fondo = Tema.CORRECTO;    break;
                        case Pasapalabra.ESTADO_INCORRECTA:  fondo = Tema.INCORRECTO;  break;
                        case Pasapalabra.ESTADO_PASAPALABRA: fondo = Tema.PASAPALABRA; break;
                        default:                             fondo = Tema.PENDIENTE;
                    }
                }

                int r = 20;
                g2.setColor(fondo);
                g2.fillOval(x - r, y - r, r * 2, r * 2);

                if (i == actual) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawOval(x - r, y - r, r * 2, r * 2);
                }

                String letra = datos[0] != null ? datos[0] : "?";
                g2.setFont(Tema.FUENTE_ROSCO);
                g2.setColor(i == actual ? Tema.FONDO_OSCURO : Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letra, x - fm.stringWidth(letra) / 2, y + fm.getAscent() / 2 - 2);
            }

            g2.dispose();
        }
    }
}
