package Vista;

import Controlador.GestorEstadisticas;
import Controlador.GestorPartidas;
import Modelo.Partida;

import javax.swing.*;

/**
 * Clase abstracta base para todas las ventanas de juego.
 * Define las acciones comunes (pausar y finalizar) que cada ventana concreta hereda,
 * y declara {@link #actualizarVista()} como método abstracto que cada subclase implementa
 * para repintar su interfaz específica.
 *
 * @author JP-Aceves
 * @version 1.0
 */
public abstract class VentanaJuego extends JFrame {

    protected GestorPartidas gestorPartidas;
    protected GestorEstadisticas gestorEstadisticas;
    protected JFrame ventanaPadre;

    /**
     * Construye la ventana de juego con sus gestores y la ventana padre.
     *
     * @param ventanaPadre       Ventana de menú principal para volver al cerrar.
     * @param gestorPartidas     Gestor de partidas del sistema.
     * @param gestorEstadisticas Gestor de estadísticas del sistema.
     */
    public VentanaJuego(JFrame ventanaPadre, GestorPartidas gestorPartidas, GestorEstadisticas gestorEstadisticas) {
        this.ventanaPadre = ventanaPadre;
        this.gestorPartidas = gestorPartidas;
        this.gestorEstadisticas = gestorEstadisticas;
    }

    /**
     * Actualiza la interfaz gráfica con el estado actual del juego.
     * Cada subclase implementa su propia lógica de pintado.
     */
    public abstract void actualizarVista();

    /**
     * Pausa la partida actual, guarda su estado en disco y vuelve al menú principal.
     */
    protected void accionPausar() {
        gestorPartidas.pausarPartida();
        JOptionPane.showMessageDialog(this, "Partida pausada y guardada.");
        dispose();
        ventanaPadre.setVisible(true);
    }

    /**
     * Finaliza la partida, registra las estadísticas y vuelve al menú principal.
     * El orden importa: primero finalizarPartida() para que fechaFin quede establecida,
     * luego registrarResultado() que necesita esa fecha.
     */
    protected void accionFinalizar() {
        Partida partida = gestorPartidas.getPartidaActual();
        gestorPartidas.finalizarPartida();          // establece fechaFin y nullea partidaActual
        gestorEstadisticas.registrarResultado(partida); // ya tiene fechaFin
        dispose();
        ventanaPadre.setVisible(true);
    }
}