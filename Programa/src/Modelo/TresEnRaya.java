package Modelo;

/**
 * Implementación del juego Tres en Raya para dos jugadores.
 *
 * <p>Hereda de {@link Juego} e implementa todos sus métodos abstractos.
 * El tablero es 3x3 con fichas 'X' y 'O'. La gestión del turno la lleva
 * {@link Partida} externamente — esta clase solo conoce el estado del tablero.</p>
 *
 * @author Nacho
 * @version 2.1
 */
public class TresEnRaya extends Juego {

    /** Tablero 3x3. Cada celda contiene 'X', 'O' o ' ' si está vacía. */
    private char[][] tablero;

    /**
     * Username del ganador. null si la partida no ha terminado o acabó en empate.
     */
    private String ganador;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    /**
     * Crea una nueva instancia de Tres en Raya.
     * Llamar a {@link #inicializar()} antes de empezar a jugar.
     */
    public TresEnRaya() {
        super("TresEnRaya", "Juego del tres en raya para dos jugadores", false);
        this.tablero = new char[3][3];
        this.ganador = null;
    }

    // ============================================================
    // METODOS ABSTRACTOS DE Juego
    // ============================================================

    /**
     * Inicializa o reinicia la partida.
     * Limpia el tablero, resetea el ganador y marca el juego como no finalizado.
     */
    @Override
    public void inicializar() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                tablero[i][j] = ' ';
        ganador = null;
        juegoFinalizado = false;
    }

    /**
     * Devuelve el estado actual del tablero en formato texto.
     */
    @Override
    public String getEstadoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TRES EN RAYA ===\n\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(tablero[i][j] == ' ' ? " . " : " " + tablero[i][j] + " ");
                if (j < 2) sb.append("|");
            }
            sb.append("\n");
            if (i < 2) sb.append("-----------\n");
        }
        if (isTerminado()) {
            if (ganador != null)
                sb.append("\n¡Ha ganado: ").append(ganador).append("!\n");
            else
                sb.append("\n¡Empate!\n");
        }
        return sb.toString();
    }

    /**
     * Serializa el estado completo a un String.
     * Formato: {@code casilla00,casilla01,...,casilla22;ganador}
     */
    @Override
    public String serializarEstado() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(tablero[i][j]);
                if (!(i == 2 && j == 2)) sb.append(",");
            }
        }
        sb.append(";").append(ganador == null ? "null" : ganador);
        return sb.toString();
    }

    /**
     * Restaura el estado desde un String serializado.
     */
    @Override
    public void deserializarEstado(String s) {
        String[] partes = s.split(";");
        String[] casillas = partes[0].split(",", -1);
        int idx = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = casillas[idx].isEmpty() ? ' ' : casillas[idx].charAt(0);
                idx++;
            }
        }
        ganador = partes[1].equals("null") ? null : partes[1];
    }

    /**
     * Marca la partida como finalizada.
     */
    @Override
    public void terminar() {
        juegoFinalizado = true;
    }

    // ============================================================
    // LÓGICA PRINCIPAL DEL JUEGO
    // ============================================================

    /**
     * Realiza una jugada en la posición indicada.
     *
     * @param username username del jugador
     * @param ficha    'X' o 'O'
     * @param fila     0-2
     * @param columna  0-2
     * @return {@code true} si la jugada fue válida
     */
    public boolean jugarTurno(String username, char ficha, int fila, int columna) {
        if (isTerminado()) return false;
        if (casillaOcupada(fila, columna)) return false;

        tablero[fila][columna] = ficha;

        if (hayVictoria(ficha)) {
            ganador = username;
            sumarPuntos(username, 10);
            terminar();
            return true;
        }

        if (tableroLleno()) {
            terminar();
            return true;
        }

        return true;
    }

    // ============================================================
    // MÉTODOS PRIVADOS DE APOYO
    // ============================================================

    private boolean casillaOcupada(int fila, int columna) {
        return tablero[fila][columna] != ' ';
    }

    private boolean hayVictoria(char ficha) {
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] == ficha && tablero[i][1] == ficha && tablero[i][2] == ficha) return true;
        }
        for (int j = 0; j < 3; j++) {
            if (tablero[0][j] == ficha && tablero[1][j] == ficha && tablero[2][j] == ficha) return true;
        }
        if (tablero[0][0] == ficha && tablero[1][1] == ficha && tablero[2][2] == ficha) return true;
        if (tablero[0][2] == ficha && tablero[1][1] == ficha && tablero[2][0] == ficha) return true;
        return false;
    }

    private boolean tableroLleno() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] == ' ') return false;
        return true;
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public String getGanador() { return ganador; }

    public char[][] getTablero() {
        char[][] copia = new char[3][3];
        for (int i = 0; i < 3; i++) copia[i] = tablero[i].clone();
        return copia;
    }
}
