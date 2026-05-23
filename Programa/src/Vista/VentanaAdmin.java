package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorJuegos;
import Controlador.GestorPartidas;
import Controlador.GestorUsuarios;
import Modelo.Administrador;
import Modelo.Estadistica;
import Modelo.Partida;
import Modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ventana de administración con ranking de juegos, lista de usuarios,
 * listado de partidas y opción de borrar usuarios.
 *
 * @author Juan Carlos
 * @version 2.0
 */
public class VentanaAdmin extends JFrame {

    /** Gestor de estadísticas para calcular rankings. */
    private GestorEstadisticas gestorEstadisticas;

    /** Gestor de usuarios para obtener la lista completa y borrar usuarios. */
    private GestorUsuarios gestorUsuarios;

    /** Gestor de juegos para obtener los juegos disponibles. */
    private GestorJuegos gestorJuegos;

    /** Gestor de partidas para obtener el historial. */
    private GestorPartidas gestorPartidas;

    /** Modelo de datos de la tabla de ranking. */
    private DefaultTableModel modeloRanking;

    /** Modelo de datos de la tabla de usuarios. */
    private DefaultTableModel modeloUsuarios;

    private DefaultTableModel modeloMenores;

    /** Modelo de datos de la tabla de partidas. */
    private DefaultTableModel modeloPartidas;

    /** Tabla que muestra el ranking de jugadores. */
    private JTable tablaRanking;

    /** Tabla que muestra la lista de usuarios. */
    private JTable tablaUsuarios;

    private JTable tablaMenores;

    /** Tabla que muestra el historial de partidas. */
    private JTable tablaPartidas;

    /** ComboBox para seleccionar el juego del ranking. */
    private JComboBox<String> comboJuegos;

    /**
     * Constructor de VentanaAdmin.
     *
     * @param gestorEstadisticas gestor del que se obtienen los rankings
     * @param gestorUsuarios     gestor del que se obtiene la lista de usuarios
     * @param gestorJuegos       gestor del que se obtienen los juegos disponibles
     * @param gestorPartidas     gestor del que se obtiene el historial de partidas
     */
    public VentanaAdmin(GestorEstadisticas gestorEstadisticas,
                        GestorUsuarios gestorUsuarios,
                        GestorJuegos gestorJuegos,
                        GestorPartidas gestorPartidas) {
        this.gestorEstadisticas = gestorEstadisticas;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorJuegos = gestorJuegos;
        this.gestorPartidas = gestorPartidas;

        setTitle("Panel de Administración");
        setSize(720, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Tema.FONDO);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Tema.FONDO);

        JLabel lblTitulo = new JLabel("PANEL DE ADMINISTRACIÓN", JLabel.CENTER);
        lblTitulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        lblTitulo.setFont(Tema.FUENTE_TITULO);
        lblTitulo.setForeground(Tema.ACENTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panelPrincipal.add(lblTitulo);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI());
        tabs.setOpaque(true);
        tabs.setBackground(Tema.FONDO_PANEL);
        tabs.setForeground(Tema.ACENTO);
        tabs.setFont(Tema.FUENTE_GRANDE);
        tabs.addTab("Ranking", crearPanelRanking());
        tabs.addTab("Usuarios", crearPanelUsuarios());
        tabs.addTab("Menores", crearPanelMenores());
        tabs.addTab("Partidas", crearPanelPartidas());
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panelPrincipal.add(tabs);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnCerrar.setBackground(Tema.GRIS_BOTON);
        btnCerrar.setForeground(Tema.TEXTO);
        btnCerrar.setFont(Tema.FUENTE_BOTON);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setOpaque(true);
        btnCerrar.setBorderPainted(false);
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(Tema.FONDO);
        panelBoton.add(btnCerrar);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        panelPrincipal.add(panelBoton);

        add(panelPrincipal);

        mostrarListaUsuarios();
        mostrarListaMenores();
    }

    /**
     * Crea el panel de ranking con selector de juego y tabla.
     *
     * @return JPanel con los componentes del ranking
     */
    private JPanel crearPanelRanking() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSelector = new JPanel();
        panelSelector.setBackground(Tema.FONDO_PANEL);
        JLabel lblSeleccionar = new JLabel("Seleccionar juego:");
        lblSeleccionar.setForeground(Tema.TEXTO);
        lblSeleccionar.setFont(Tema.FUENTE_LABEL);
        panelSelector.add(lblSeleccionar);

        ArrayList<String> juegosDisponibles = gestorJuegos.getJuegosDisponibles();
        comboJuegos = new JComboBox<>(juegosDisponibles.toArray(new String[0]));
        comboJuegos.setBackground(Tema.FONDO_CAMPO);
        comboJuegos.setForeground(Tema.TEXTO);
        comboJuegos.setFont(Tema.FUENTE_CAMPO);
        comboJuegos.addActionListener(e -> {
            String juegoSeleccionado = (String) comboJuegos.getSelectedItem();
            if (juegoSeleccionado != null) {
                mostrarRanking(juegoSeleccionado);
            }
        });
        panelSelector.add(comboJuegos);
        panel.add(panelSelector);

        String[] columnas = {"Posición", "Usuario", "Puntuación", "Fecha"};
        modeloRanking = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRanking = new JTable(modeloRanking);
        estilizarTabla(tablaRanking);
        JScrollPane scroll = new JScrollPane(tablaRanking);
        scroll.getViewport().setBackground(Tema.FONDO_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        panel.add(scroll);

        if (!juegosDisponibles.isEmpty()) {
            mostrarRanking(juegosDisponibles.get(0));
        }

        return panel;
    }

    /**
     * Crea el panel de usuarios con la tabla y el botón de borrado.
     *
     * @return JPanel con los componentes de usuarios
     */
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"Username", "Tipo", "Año"};
        modeloUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloUsuarios);
        estilizarTabla(tablaUsuarios);
        tablaUsuarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollUsuarios = new JScrollPane(tablaUsuarios);
        scrollUsuarios.getViewport().setBackground(Tema.FONDO_PANEL);
        scrollUsuarios.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        panel.add(scrollUsuarios);

        JButton btnBorrar = new JButton("Borrar usuario seleccionado");
        btnBorrar.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnBorrar.setBackground(Tema.INCORRECTO);
        btnBorrar.setForeground(Color.WHITE);
        btnBorrar.setFont(Tema.FUENTE_BOTON);
        btnBorrar.setFocusPainted(false);
        btnBorrar.setOpaque(true);
        btnBorrar.setBorderPainted(false);
        btnBorrar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnBorrar.addActionListener(e -> {
            int fila = tablaUsuarios.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un usuario de la tabla primero.",
                        "Ningún usuario seleccionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) modeloUsuarios.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que quieres eliminar al usuario '" + username + "'?",
                    "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            String error = gestorUsuarios.borrarUsuario(username);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario '" + username + "' eliminado correctamente.",
                        "Borrado", JOptionPane.INFORMATION_MESSAGE);
                mostrarListaUsuarios();
            }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(Tema.FONDO_PANEL);
        panelBoton.add(btnBorrar);
        panel.add(panelBoton);

        return panel;
    }


    private JPanel crearPanelMenores() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"Username", "Tipo", "Año"};
        modeloMenores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMenores = new JTable(modeloMenores);
        estilizarTabla(tablaMenores);
        tablaMenores.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollUsuarios = new JScrollPane(tablaMenores);
        scrollUsuarios.getViewport().setBackground(Tema.FONDO_PANEL);
        scrollUsuarios.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        panel.add(scrollUsuarios);

        JButton btnBorrar = new JButton("Borrar usuario seleccionado");
        btnBorrar.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnBorrar.setBackground(Tema.INCORRECTO);
        btnBorrar.setForeground(Color.WHITE);
        btnBorrar.setFont(Tema.FUENTE_BOTON);
        btnBorrar.setFocusPainted(false);
        btnBorrar.setOpaque(true);
        btnBorrar.setBorderPainted(false);
        btnBorrar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnBorrar.addActionListener(e -> {
            int fila = tablaMenores.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un usuario de la tabla primero.",
                        "Ningún usuario seleccionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) modeloMenores.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que quieres eliminar al usuario '" + username + "'?",
                    "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            String error = gestorUsuarios.borrarUsuario(username);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario '" + username + "' eliminado correctamente.",
                        "Borrado", JOptionPane.INFORMATION_MESSAGE);
                mostrarListaMenores();
            }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(Tema.FONDO_PANEL);
        panelBoton.add(btnBorrar);
        panel.add(panelBoton);

        return panel;
    }

    /**
     * Crea el panel de partidas con el historial de partidas finalizadas y pausadas.
     *
     * @return JPanel con la tabla de partidas
     */
    private JPanel crearPanelPartidas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Juego", "Jugadores", "Estado"};
        modeloPartidas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPartidas = new JTable(modeloPartidas);
        estilizarTabla(tablaPartidas);
        JScrollPane scrollPartidas = new JScrollPane(tablaPartidas);
        scrollPartidas.getViewport().setBackground(Tema.FONDO_PANEL);
        scrollPartidas.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        panel.add(scrollPartidas);

        mostrarListaPartidas();
        return panel;
    }

    /**
     * Muestra el ranking de un juego en la tabla, ordenado por puntuación.
     *
     * @param juego nombre del juego del que se quiere mostrar el ranking
     */
    public void mostrarRanking(String juego) {
        ArrayList<Estadistica> ranking = gestorEstadisticas.calcularRanking(juego);

        modeloRanking.setRowCount(0);

        int posicion = 1;
        for (Estadistica e : ranking) {
            Object[] fila = {
                posicion++,
                e.getUsername(),
                e.getPuntuacion(),
                e.getFecha()
            };
            modeloRanking.addRow(fila);
        }
    }

    /**
     * Muestra la lista completa de usuarios en la tabla.
     */
    public void mostrarListaUsuarios() {
        ArrayList<Usuario> usuarios = gestorUsuarios.getListaUsuarios();

        modeloUsuarios.setRowCount(0);

        for (Usuario u : usuarios) {
            String tipo = (u instanceof Administrador) ? "ADMIN" : "Usuario";
            int año = u.getAñoNacimiento();
            Object[] fila = {u.getUsername(), tipo, año};
            modeloUsuarios.addRow(fila);
        }
    }

    public void mostrarListaMenores() {
        ArrayList<Usuario> menores = gestorUsuarios.getListaUsuarios();

        modeloMenores.setRowCount(0);

        for (Usuario u : menores) {
            if (u.getAñoNacimiento() > 2007) {
                String tipo = (u instanceof Administrador) ? "ADMIN" : "Usuario";
                int año = u.getAñoNacimiento();
                Object[] fila = {u.getUsername(), tipo, año};

                modeloMenores.addRow(fila);
            }
        }
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setBackground(Tema.FONDO_PANEL);
        tabla.setForeground(Tema.TEXTO);
        tabla.setGridColor(Tema.BORDE);
        tabla.setFont(Tema.FUENTE_CAMPO);
        tabla.setSelectionBackground(Tema.ACENTO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setShowGrid(true);
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(Tema.ACENTO);
        header.setForeground(Color.BLACK);
        header.setFont(Tema.FUENTE_BOTON);
        header.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
    }

    /**
     * Rellena la tabla de partidas con las finalizadas en sesión y las pausadas en disco.
     */
    public void mostrarListaPartidas() {
        modeloPartidas.setRowCount(0);

        // Partidas finalizadas en esta sesión
        ArrayList<Partida> finalizadas = gestorPartidas.getListaPartidas();
        for (Partida p : finalizadas) {
            StringBuilder jugadores = new StringBuilder();
            for (int i = 0; i < p.getListaJugadores().size(); i++) {
                if (i > 0) jugadores.append(", ");
                jugadores.append(p.getListaJugadores().get(i).getUsername());
            }
            Object[] fila = {
                p.getId(),
                p.getJuego().getNombre(),
                jugadores.toString(),
                "FINALIZADA"
            };
            modeloPartidas.addRow(fila);
        }

        // Partidas pausadas en disco
        ArrayList<String[]> pausadas = gestorPartidas.getResumenPartidasPausadas();
        for (String[] datos : pausadas) {
            modeloPartidas.addRow(datos);
        }
    }
}
