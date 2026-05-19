package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorUsuarios;
import Modelo.Estadistica;
import Modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ventana que muestra el historial de estadísticas de un usuario.
 *
 * @author Juan Carlos
 * @version 1.0
 */
public class VentanaEstadisticas extends JFrame {

    /** Nombre de usuario del cual se mostrarán las estadísticas. */
    private String username;

    /** Gestor de estadísticas del que se obtienen los datos. */
    private GestorEstadisticas gestorEstadisticas;

    /** Gestor de usuarios para buscar el objeto Usuario. */
    private GestorUsuarios gestorUsuarios;

    /** Modelo de datos de la tabla, se rellena en mostrarTabla(). */
    private DefaultTableModel modeloTabla;

    /** Tabla visual donde se muestran las estadísticas. */
    private JTable tabla;

    /**
     * Constructor de VentanaEstadisticas.
     *
     * @param gestorEstadisticas gestor del que se obtienen las estadísticas
     * @param gestorUsuarios     gestor para buscar el objeto Usuario
     * @param username           nombre del usuario del que se mostrarán las estadísticas
     */
    public VentanaEstadisticas(GestorEstadisticas gestorEstadisticas,
                               GestorUsuarios gestorUsuarios,
                               String username) {
        this.gestorEstadisticas = gestorEstadisticas;
        this.gestorUsuarios = gestorUsuarios;
        this.username = username;

        setTitle("Estadísticas - " + username);
        setSize(620, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Tema.FONDO);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Tema.FONDO);

        JLabel lblTitulo = new JLabel("Estadísticas de " + username, JLabel.CENTER);
        lblTitulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        lblTitulo.setFont(Tema.FUENTE_TITULO);
        lblTitulo.setForeground(Tema.ACENTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panelPrincipal.add(lblTitulo);

        String[] columnas = {"Juego", "Puntuación", "Resultado", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
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

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createLineBorder(Tema.BORDE, 1)
        ));
        scrollPane.getViewport().setBackground(Tema.FONDO_PANEL);
        panelPrincipal.add(scrollPane);

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

        mostrarTabla();
    }

    /**
     * Puebla la tabla con las estadísticas del usuario.
     */
    public void mostrarTabla() {
        Usuario usuario = gestorUsuarios.buscarUsuario(username);
        ArrayList<Estadistica> estadisticas = gestorEstadisticas.getEstadisticasUsuario(usuario);

        modeloTabla.setRowCount(0);

        for (Estadistica e : estadisticas) {
            String resultado = e.isVictoria() ? "VICTORIA" : "DERROTA";
            Object[] fila = {
                e.getNombreJuego(),
                e.getPuntuacion(),
                resultado,
                e.getFecha()
            };
            modeloTabla.addRow(fila);
        }
    }
}
