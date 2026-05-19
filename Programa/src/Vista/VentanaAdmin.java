import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**

 * @author Juan Carlos
 * @version 1.0
 */
public class VentanaAdmin extends JFrame {

    /**
     * Modelo de datos de la tabla de ranking.
     */
    private DefaultTableModel modeloRanking;

    /**
     * Modelo de datos de la tabla de usuarios.
     */
    private DefaultTableModel modeloUsuarios;

    /**
     * Tabla que muestra el ranking de jugadores.
     */
    private JTable tablaRanking;

    /**
     * Tabla que muestra la lista de usuarios.
     */
    private JTable tablaUsuarios;

    /**
     * ComboBox para seleccionar el juego del ranking.
     */
    private JComboBox<String> comboJuegos;

    /**
     * Constructor de VentanaAdmin.
     * 
     * Inicializa la ventana de administración con dos pestañas:una para ranking y otra para usuarios que carga automáticamente los datos iniciales.
     */
    public VentanaAdmin() {
        // Configuración de la ventana
        setTitle("Panel de Administración");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal con BoxLayout vertical
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Título
        JLabel lblTitulo = new JLabel("PANEL DE ADMINISTRACIÓN", JLabel.CENTER);
        lblTitulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panelPrincipal.add(lblTitulo);

        // Tabs para separar funcionalidades
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ranking", crearPanelRanking());
        tabs.addTab("Usuarios", crearPanelUsuarios());
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panelPrincipal.add(tabs);

        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        panelPrincipal.add(panelBoton);

        add(panelPrincipal);

        // Cargar datos iniciales
        mostrarListaUsuarios();
    }

    /**
     * Crea el panel de ranking con selector de juego y tabla.
     * 
     * @return JPanel con los componentes del ranking
     */
    private JPanel crearPanelRanking() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con selector de juego
        JPanel panelSelector = new JPanel();
        panelSelector.add(new JLabel("Seleccionar juego:"));
        
        // Obtener juegos disponibles
        ArrayList<String> juegosDisponibles = Aplicacion.getGestorJuegos().getJuegosDisponibles();
        comboJuegos = new JComboBox<>(juegosDisponibles.toArray(new String[0]));
        comboJuegos.addActionListener(e -> {
            String juegoSeleccionado = (String) comboJuegos.getSelectedItem();
            if (juegoSeleccionado != null) {
                mostrarRanking(juegoSeleccionado);
            }
        });
        panelSelector.add(comboJuegos);
        
        panel.add(panelSelector);

        // Tabla de ranking
        String[] columnas = {"Posición", "Usuario", "Puntuación", "Fecha"};
        modeloRanking = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRanking = new JTable(modeloRanking);
        tablaRanking.setRowHeight(25);
        
        JScrollPane scrollRanking = new JScrollPane(tablaRanking);
        panel.add(scrollRanking);

        // Cargar ranking inicial si hay juegos
        if (!juegosDisponibles.isEmpty()) {
            mostrarRanking(juegosDisponibles.get(0));
        }

        return panel;
    }

    /**
     * Crea el panel de usuarios con la tabla.
     * 
     * @return JPanel con los componentes de usuarios
     */
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla de usuarios
        String[] columnas = {"Username", "Tipo"};
        modeloUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.setRowHeight(25);
        
        JScrollPane scrollUsuarios = new JScrollPane(tablaUsuarios);
        panel.add(scrollUsuarios);

        return panel;
    }

    /**
     * Muestra el ranking de un juego en la tabla.
     * 
     * Llama a gestorEstadisticas.calcularRanking(nombreJuego) 
     * y puebla la tabla con los resultados ordenados por puntuación.
     * 
     * @param juego Nombre del juego del que se quiere mostrar el ranking
     */
    public void mostrarRanking(String juego) {
        // Obtener el gestor desde Aplicacion
        GestorEstadisticas gestorEstadisticas = Aplicacion.getGestorEstadisticas();
        
        // Calcular el ranking del juego
        ArrayList<Estadistica> ranking = gestorEstadisticas.calcularRanking(juego);

        // Limpiar tabla
        modeloRanking.setRowCount(0);

        // Poblar la tabla con los resultados ordenados por puntuación
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
     * 
     * Llama a gestorUsuarios.getListaUsuarios() y muestra la lista 
     * en el componente visual.
     */
    public void mostrarListaUsuarios() {
        // Obtener el gestor desde Aplicacion
        GestorUsuarios gestorUsuarios = Aplicacion.getGestorUsuarios();
        
        // Obtener la lista completa de usuarios
        ArrayList<Usuario> usuarios = gestorUsuarios.getListaUsuarios();

        // Limpiar tabla
        modeloUsuarios.setRowCount(0);

        // Mostrar la lista en el componente visual
        for (Usuario u : usuarios) {
            String tipo = (u instanceof Administrador) ? "ADMIN" : "Usuario";
            Object[] fila = {
                u.getUsername(),
                tipo
            };
            modeloUsuarios.addRow(fila);
        }
    }
}