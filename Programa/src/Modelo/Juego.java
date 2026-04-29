package Modelo;

import java.util.ArrayList;

/**
 * Clase abstracta que representa un juego genérico dentro de la aplicación.
 * <p>
 * Define la estructura común a todos los juegos (nombre, estado de finalización
 * y puntuaciones por jugador) y declara los métodos abstractos que cada
 * subclase debe implementar con su propia lógica de juego.
 * </p>
 *
 * @author JP
 * @version 1.0
 */
public abstract class Juego {

    /** Nombre identificador del juego (ej: "Pasapalabra", "TresEnRaya"). */
    protected String nombreJuego;

    /** Descripción breve del juego mostrada en el menú de selección. */
    protected String descripcion;

    /** Indica si el juego ha finalizado. Se pone a true al llamar a terminar(). */
    protected boolean juegoFinalizado;

    /** Lista de puntuaciones de cada jugador participante en la partida actual. */
    ArrayList<PuntuacionJugador> listaPuntuacionPorJugador = new ArrayList<>();


    /**
     * Constructor de la clase Juego.
     *
     * @param nombreJuego Nombre del juego.
     * @param descripcion Descripción breve del juego.
     * @param juegoFinalizado Estado inicial del juego (siempre se inicializa a false).
     */
    public Juego(String nombreJuego, String descripcion, boolean juegoFinalizado) {
        this.nombreJuego = nombreJuego;
        this.descripcion = descripcion;
        this.juegoFinalizado = false;
    }


    /**
     * Inicializa el estado del juego antes de empezar una partida.
     * Cada subclase define qué significa inicializar: cargar preguntas,
     * limpiar el tablero, resetear contadores, etc.
     */
    public abstract void inicializar();

    /**
     * Devuelve una representación en texto del estado actual del juego.
     * La vista utiliza este método para mostrar la información al jugador
     * sin conocer los detalles internos de cada juego.
     *
     * @return String con el estado actual del juego.
     */
    public abstract String getEstadoTexto();

    /**
     * Serializa el estado actual del juego en un String para guardarlo en disco
     * cuando la partida se pausa. Cada subclase define su propio formato.
     *
     * @return String con el estado serializado.
     */
    public abstract String serializarEstado();

    /**
     * Reconstruye el estado del juego a partir del String guardado en disco.
     * Es el proceso inverso a serializarEstado().
     *
     * @param estado String con el estado serializado previamente.
     */
    public abstract void deserializarEstado(String estado);

    /**
     * Marca el juego como finalizado.
     * Cada subclase puede añadir lógica adicional (calcular ganador,
     * registrar resultado, etc.) antes o después de poner juegoFinalizado a true.
     */
    public abstract void terminar();

    /**
     * Suma n puntos al jugador con el username indicado.
     * Si el jugador no tiene aún entrada en la lista de puntuaciones,
     * se crea una nueva para él automáticamente.
     *
     * @param username Username del jugador al que sumar puntos.
     * @param n        Cantidad de puntos a sumar.
     */
    public void sumarPuntos(String username, int n) {
        for (PuntuacionJugador p : listaPuntuacionPorJugador) {
            if (p.getUsername().equals(username)) {
                p.sumarPuntos(n);
                return;
            }
        }
        PuntuacionJugador nuevoJugador = new PuntuacionJugador(username);
        nuevoJugador.sumarPuntos(n);
        listaPuntuacionPorJugador.add(nuevoJugador);
    }

    /**
     * Devuelve la puntuación actual del jugador con el username indicado.
     * Si el jugador no tiene entrada en la lista, devuelve 0 por lo mismo que está obligado a devolver un int.
     *
     * @param username Username del jugador a consultar.
     * @return Puntuación del jugador, o 0 si no existe en la lista.
     */
    public int getPuntuacion(String username) {
        for (PuntuacionJugador p : listaPuntuacionPorJugador) {
            if (p.getUsername().equals(username)) {
                return p.getPuntos();
            }
        }
        return 0;
    }

    /**
     * Devuelve la lista completa de puntuaciones de todos los jugadores.
     * Utilizado por Partida al finalizar para registrar los resultados.
     *
     * @return ArrayList con todas las PuntuacionJugador de la partida.
     */
    public ArrayList<PuntuacionJugador> getPuntuaciones() {
        return listaPuntuacionPorJugador;
    }

    /**
     * Indica si el juego ha finalizado.
     * La vista lo consulta después de cada turno para saber si debe
     * cerrar la ventana de juego.
     *
     * @return true si el juego ha terminado, false si sigue en curso.
     */
    public boolean isTerminado() {
        return juegoFinalizado;
    }

    /**
     * Devuelve el nombre del juego.
     *
     * @return Nombre del juego.
     */
    public String getNombre() {
        return nombreJuego;
    }

    /**
     * Devuelve la descripción del juego.
     *
     * @return Descripción del juego.
     */
    public String getDescripcion() {
        return descripcion;
    }
}