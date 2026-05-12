import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * 
 * @author Juan Carlos
 * @version 1.0
 */
public class VentanaEstadisticas extends JFrame {

    /**
     * Nombre de usuario del cual se mostrarán las estadísticas.
     */
    private String username;

    /**
     * Modelo de datos de la tabla, se rellena en mostrarTabla().
     */
    private DefaultTableModel modeloTabla;

    /**
     * Tabla visual donde se muestran las estadísticas.
     */
    private JTable tabla;

    /**
     * Constructor de VentanaEstadisticas.
     * 
     * Inicializa la ventana con los componentes gráficos y carga automáticamente las estadísticas del usuario en la tabla.
     * 
     * @param username Nombre del usuario del que se mostrarán las estadísticas
     */
    public VentanaEstadisticas(String username) {
        this.username = username;
        
        // Configuración de la ventana
        setTitle("Estadísticas - " + username);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal con BoxLayout vertical
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Título
        JLabel lblTitulo = new JLabel("Estadísticas de " + username, JLabel.CENTER);
        lblTitulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panelPrincipal.add(lblTitulo);

        // Crear modelo y tabla
        String[] columnas = {"Juego", "Puntuación", "Resultado", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panelPrincipal.add(scrollPane);

        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        panelPrincipal.add(panelBoton);

        add(panelPrincipal);

        // Cargar las estadísticas en la tabla
        mostrarTabla();
    }

    /**
     * Muestra la tabla con las estadísticas del usuario.
     * 
     * Llama a gestorEstadisticas.getEstadisticasUsuario(username), 
     * puebla el DefaultTableModel con los datos y lo asigna al JTable.
     */
    public void mostrarTabla() {
        // Obtener el gestor desde Aplicacion
        GestorEstadisticas gestorEstadisticas = Aplicacion.getGestorEstadisticas();
        
        // Buscar el usuario
        Usuario usuario = Aplicacion.getGestorUsuarios().buscarUsuario(username);
        
        // Obtener las estadísticas del usuario
        ArrayList<Estadistica> estadisticas = gestorEstadisticas.getEstadisticasUsuario(usuario);

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Poblar el DefaultTableModel con los datos
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