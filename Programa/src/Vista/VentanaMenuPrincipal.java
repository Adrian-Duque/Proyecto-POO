package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorJuegos;
import Controlador.GestorPartidas;
import Controlador.GestorUsuarios;
import Modelo.Juego;
import Modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ventana principal de navegación de la aplicación MiniJuegos.
 * Se muestra tras el login y da acceso a jugar, estadísticas y administración.
 *
 * @author Adrián, JP, Nacho, Juan Carlos
 * @version 2.2
 */
public class VentanaMenuPrincipal extends JFrame {

    private final GestorUsuarios     gestorUsuarios;
    private final GestorPartidas     gestorPartidas;
    private final GestorEstadisticas gestorEstadisticas;
    private final GestorJuegos       gestorJuegos;
    private final Usuario            usuarioActual;

    private JLabel  lblBienvenida;
    private JButton btnJugar;
    private JButton btnCargarPartida;
    private JButton btnEstadisticas;
    private JButton btnAdmin;
    private JButton btnCerrarSesion;

    /**
     * Construye el menú principal para el usuario con sesión activa.
     *
     * @param gestorUsuarios     gestor de usuarios; se usa para obtener el usuario actual y cerrar sesión
     * @param gestorPartidas     gestor de partidas; se usa para iniciar y cargar partidas
     * @param gestorEstadisticas gestor de estadísticas; se pasa a las ventanas de juego y estadísticas
     * @param gestorJuegos       gestor de juegos; se usa para listar los juegos disponibles
     */
    public VentanaMenuPrincipal(GestorUsuarios gestorUsuarios,
                                GestorPartidas gestorPartidas,
                                GestorEstadisticas gestorEstadisticas,
                                GestorJuegos gestorJuegos) {
        this.gestorUsuarios     = gestorUsuarios;
        this.gestorPartidas     = gestorPartidas;
        this.gestorEstadisticas = gestorEstadisticas;
        this.gestorJuegos       = gestorJuegos;
        this.usuarioActual      = gestorUsuarios.getUsuarioActual();
        initUI();
    }

    private void initUI() {
        setTitle("MiniJuegos — Menú principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 440);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Tema.FONDO);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDE, 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        lblBienvenida = new JLabel("Bienvenido/a, " + usuarioActual.getUsername());
        lblBienvenida.setFont(Tema.FUENTE_TITULO);
        lblBienvenida.setForeground(Tema.ACENTO);
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnJugar         = crearBotonPrimario("Jugar");
        btnCargarPartida = crearBotonPrimario("Cargar partida guardada");
        btnEstadisticas  = crearBotonPrimario("Mis estadisticas");
        btnAdmin         = crearBotonSecundario("Panel de administracion");
        btnCerrarSesion  = crearBotonSecundario("Cerrar sesion");

        btnAdmin.setVisible(gestorUsuarios.esAdministrador());

        btnJugar.addActionListener(e        -> accionJugar());
        btnCargarPartida.addActionListener(e -> accionCargarPartida());
        btnEstadisticas.addActionListener(e  -> accionEstadisticas());
        btnAdmin.addActionListener(e         -> accionAdmin());
        btnCerrarSesion.addActionListener(e  -> accionCerrarSesion());

        panel.add(lblBienvenida);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(btnJugar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnCargarPartida);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnEstadisticas);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnAdmin);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnCerrarSesion);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Tema.FONDO);
        wrapper.add(panel);
        add(wrapper);
    }

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFocusPainted(false);
        btn.setBackground(Tema.ACENTO);
        btn.setForeground(Color.BLACK);
        btn.setFont(Tema.FUENTE_BOTON);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFocusPainted(false);
        btn.setBackground(Tema.GRIS_BOTON);
        btn.setForeground(Tema.TEXTO);
        btn.setFont(Tema.FUENTE_BOTON);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    @Deprecated
    private JButton crearBoton(String texto) {
        return crearBotonPrimario(texto);
    }

    // ───────────────────────────────────────────────────────────────────────────
    // ACCIONES
    // ───────────────────────────────────────────────────────────────────────────

    private void accionJugar() {
        VentanaSeleccionJuego dialogo = new VentanaSeleccionJuego(this, gestorJuegos);
        String nombreJuego = dialogo.getJuegoSeleccionado();
        if (nombreJuego == null) return;
    
        ArrayList<Usuario> jugadores = new ArrayList<>();
        jugadores.add(usuarioActual);
    
        if (gestorJuegos.esMultijugador(nombreJuego)) {
            String username2 = JOptionPane.showInputDialog(this,
                    "Introduce el username del segundo jugador:");
            if (username2 == null || username2.trim().isEmpty()) return;
    
            String password2 = JOptionPane.showInputDialog(this,
                    "Introduce la contraseña del segundo jugador:");
            if (password2 == null) return;
    
            Usuario jugador2 = gestorUsuarios.buscarUsuario(username2.trim());
            if (jugador2 == null || !jugador2.verificarPassword(password2)) {
                JOptionPane.showMessageDialog(this,
                        "Credenciales del segundo jugador incorrectas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (jugador2.getUsername().equals(usuarioActual.getUsername())) {
                JOptionPane.showMessageDialog(this,
                        "El segundo jugador debe ser distinto.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            jugadores.add(jugador2);
        }
    
        int nivel = 1;
        if ("PasaPalabra".equals(nombreJuego)) {
            String[] niveles = {"Infantil", "Fácil", "Medio", "Avanzado"};
            String seleccion = (String) JOptionPane.showInputDialog(
                    this, "Selecciona la dificultad:", "Dificultad",
                    JOptionPane.PLAIN_MESSAGE, null, niveles, "Fácil");
            if (seleccion == null) return;
            switch (seleccion) {
                case "Infantil": nivel = 0; break;
                case "Medio":    nivel = 2; break;
                case "Avanzado": nivel = 3; break;
                default:         nivel = 1; break;
            }
        }
    
        Juego juego = gestorJuegos.crearJuego(nombreJuego, nivel);
        if (juego == null) return;
    
        gestorPartidas.iniciarPartida(juego, jugadores);
        setVisible(false);
    
        if ("TresEnRaya".equals(nombreJuego)) {
            new VentanaJuegoTresEnRaya(this, gestorPartidas, gestorEstadisticas,
                    gestorPartidas.getPartidaActual());
        } else if ("PasaPalabra".equals(nombreJuego)) {
            new VentanaJuegoPasaPalabra(gestorPartidas.getPartidaActual(),
                    gestorPartidas, gestorEstadisticas, this);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ventana para '" + nombreJuego + "' no implementada.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            setVisible(true);
        }
    }

    private void accionCargarPartida() {
        ArrayList<Integer> ids = gestorPartidas.listarPartidasUsuario(usuarioActual.getUsername());

        if (ids == null || ids.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay partidas pausadas.",
                    "Sin partidas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Etiquetas legibles: "Partida 3 — PasaPalabra" en vez de IDs numéricos
        String[] etiquetas = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);
            String datos = gestorPartidas.cargarDatosPartida(id);
            String nombreJuego = (datos != null) ? datos.split("\\|", 2)[0] : "Desconocido";
            etiquetas[i] = "Partida " + id + " — " + nombreJuego;
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona una partida para reanudar:",
                "Partidas pausadas",
                JOptionPane.PLAIN_MESSAGE,
                null,
                etiquetas,
                etiquetas[0]);

        if (seleccion == null) return;

        int idSeleccionado = ids.get(java.util.Arrays.asList(etiquetas).indexOf(seleccion));

        String datos = gestorPartidas.cargarDatosPartida(idSeleccionado);
        String nombreJuego = datos.split("\\|", 2)[0];
        Juego juego = gestorJuegos.crearJuego(nombreJuego);

        ArrayList<Usuario> jugadores = new ArrayList<>();
        jugadores.add(usuarioActual);

        gestorPartidas.reanudarPartida(idSeleccionado, datos, juego, jugadores);
        setVisible(false);

        if ("TresEnRaya".equals(nombreJuego)) {
            new VentanaJuegoTresEnRaya(this, gestorPartidas, gestorEstadisticas,
                    gestorPartidas.getPartidaActual());
        } else if ("PasaPalabra".equals(nombreJuego)) {
            new VentanaJuegoPasaPalabra(gestorPartidas.getPartidaActual(),
                    gestorPartidas, gestorEstadisticas, this);
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de juego desconocido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            setVisible(true);
        }
    }

    private void accionEstadisticas() {
        new VentanaEstadisticas(gestorEstadisticas, gestorUsuarios,
                usuarioActual.getUsername()).setVisible(true);
    }

    private void accionAdmin() {
        if (!gestorUsuarios.esAdministrador()) {
            JOptionPane.showMessageDialog(this, "Acceso denegado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new VentanaAdmin(gestorEstadisticas, gestorUsuarios, gestorJuegos, gestorPartidas).setVisible(true);
    }

    private void accionCerrarSesion() {
        gestorUsuarios.cerrarSesion();
        new VentanaLogin(gestorUsuarios).setVisible(true);
        dispose();
    }
}