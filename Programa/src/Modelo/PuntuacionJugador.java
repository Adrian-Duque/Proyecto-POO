package Modelo;

/**
 * Representa la puntuación de un jugador dentro de una partida.
 * Cada instancia almacena el username del jugador y sus puntos acumulados.
 * 
 *    @author JP-Aceves
 *    @version 1.0
 */
public class PuntuacionJugador {

    private String username;
    private int puntos;

    /**
     * Crea una nueva puntuación para el jugador indicado, con 0 puntos iniciales.
     *
     * @param username nombre de usuario del jugador
     */
    public PuntuacionJugador(String username) {
        this.username = username;
        this.puntos = 0;
    }

    /**
     * Devuelve el nombre de usuario del jugador.
     *
     * @return username del jugador
     */
    public String getUsername() {
        return username;
    }

    /**
     * Devuelve los puntos actuales del jugador.
     *
     * @return puntos acumulados
     */
    public int getPuntos() {
        return puntos;
    }

    /**
     * Incrementa los puntos del jugador en la cantidad indicada.
     *
     * @param n cantidad de puntos a sumar
     */
    public void sumarPuntos(int n) {
        this.puntos += n;
    }
}