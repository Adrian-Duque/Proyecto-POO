package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorJuegos;
import Controlador.GestorPartidas;
import Controlador.GestorUsuarios;
import Persistencia.GestorPersistencia;
import Persistencia.PersistenciaArchivos;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación MiniJuegos.
 * Crea e inyecta todas las dependencias y lanza la ventana de login.
 * Expone getters estáticos para que las ventanas accedan a los gestores
 * sin necesidad de inyección manual en cada constructor.
 *
 * @author JP-Aceves
 * @version 1.1
 */
public class Aplicacion {

    private static GestorUsuarios gestorUsuarios;
    private static GestorJuegos gestorJuegos;
    private static GestorPartidas gestorPartidas;
    private static GestorEstadisticas gestorEstadisticas;

    /**
     * Método principal. Inicializa la capa de persistencia, los gestores
     * y arranca la interfaz gráfica en el hilo de Swing.
     *
     * @param args Argumentos de línea de comandos (no usados).
     */
    public static void main(String[] args) {
        GestorPersistencia persistencia = new PersistenciaArchivos();

        gestorUsuarios = new GestorUsuarios(persistencia);
        gestorEstadisticas = new GestorEstadisticas(persistencia);

        gestorJuegos = new GestorJuegos();
        gestorJuegos.registrarJuego("TresEnRaya");
        gestorJuegos.registrarJuego("Pasapalabra");

        gestorPartidas = new GestorPartidas(persistencia);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new VentanaLogin(gestorUsuarios).setVisible(true));
    }

    public static GestorUsuarios getGestorUsuarios(){
        return gestorUsuarios; 
    }

    public static GestorJuegos getGestorJuegos(){ 
        return gestorJuegos; 
    }

    public static GestorPartidas getGestorPartidas(){ 
        return gestorPartidas; 
    }
    
    public static GestorEstadisticas getGestorEstadisticas(){
        return gestorEstadisticas; 
    }
}
