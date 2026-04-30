package Modelo;

/**
 * Clase que representa las estadísticas de una partida jugada.
 * 
 * Almacena la información relativa a una partida incluyendo datos del jugador,
 * el juego jugado, la puntuación obtenida, el resultado de la partida y la fecha.
 * 
 * Esta clase proporciona métodos para acceder a los datos de la estadística
 * y convertirlos a diferentes formatos (CSV para almacenamiento y texto para visualización).
 * 
 * @author 
 * @version 1.0
 * @since 1.0
 */
public class Estadistica {

    /** El nombre de usuario del jugador que participó en la partida */
    private String username;
    
    /** El nombre del juego que se jugó */
    private String nombreJuego;
    
    /** La puntuación obtenida en la partida */
    private int puntuacion;
    
    /** Indica si la partida fue ganada (true) o perdida (false) */
    private boolean victoria;
    
    /** La fecha en que se jugó la partida */
    private String fecha;

    /**
     * Construye una nueva estadística de partida con los datos especificados.
     * 
     * @param username el nombre de usuario del jugador
     * @param nombreJuego el nombre del juego jugado
     * @param puntuacion la puntuación obtenida en la partida
     * @param victoria true si la partida fue ganada, false si fue perdida
     * @param fecha la fecha en que se jugó la partida
     */
    public Estadistica(String username, String nombreJuego, int puntuacion, boolean victoria, String fecha) {
        this.username = username;
        this.nombreJuego = nombreJuego;
        this.puntuacion = puntuacion;
        this.victoria = victoria;
        this.fecha = fecha;
    }

    /**
     * Obtiene el nombre de usuario del jugador.
     * 
     * @return el nombre de usuario como String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtiene el nombre del juego que se jugó.
     * 
     * @return el nombre del juego como String
     */
    public String getNombreJuego() {
        return nombreJuego;
    }

    /**
     * Obtiene la puntuación obtenida en la partida.
     * 
     * @return la puntuación como int
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Obtiene el resultado de la partida.
     * 
     * @return true si la partida fue ganada, false si fue perdida
     */
    public boolean isVictoria() {
        return victoria;
    }

    /**
     * Obtiene la fecha en que se jugó la partida.
     * 
     * @return la fecha como String
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Convierte la estadística a formato CSV para almacenamiento en archivo.
     * 
     * El formato de la línea CSV es: username,nombreJuego,puntuacion,victoria,fecha
     * 
     * @return una línea en formato CSV con todos los datos de la estadística
     */
    public String toArchivo() {
        return username + "," + nombreJuego + "," + puntuacion + "," + victoria + "," + fecha;
    }

    /**
     * Devuelve una representación textual de la estadística para mostrar en pantalla.
     * 
     * El formato de la salida es:
     * Usuario: &lt;username&gt; | Juego: &lt;nombreJuego&gt; | Puntuación: &lt;puntuacion&gt; | Resultado: &lt;resultado&gt; | Fecha: &lt;fecha&gt;
     * 
     * Donde el resultado muestra:
     * <ul>
     *   <li>"VICTORIA, Enhorabuena" si la partida fue ganada</li>
     *   <li>"DERROTA, Sigue intentandolo" si la partida fue perdida</li>
     * </ul>
     * 
     * @return una representación textual formateada de la estadística
     */
    @Override
    public String toString() {
        String resultado = victoria ? "VICTORIA, Enhorabuena" : "DERROTA, Sigue intentandolo";
        return String.format(
            "Usuario: %s | Juego: %s | Puntuación: %d | Resultado: %s | Fecha: %s",
            username, nombreJuego, puntuacion, resultado, fecha
        );
    }
}

