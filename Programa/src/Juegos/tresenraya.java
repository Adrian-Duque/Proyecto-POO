/**
 * Implementación del juego Tres en Raya para dos jugadores.
 *
 * <p>Esta clase hereda de {@link Juego} e implementa todos sus métodos abstractos.
 * El juego consiste en un tablero 3x3 donde dos jugadores se alternan colocando
 * fichas (X y O) intentando conseguir tres en línea.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *     TresEnRaya juego = new TresEnRaya("jugador1", "jugador2");
 *     juego.inicializar();
 *     juego.hacerJugada(0, 0); // jugador1 en fila 0, columna 0
 *     System.out.println(juego.getEstadoTexto());
 * </pre>
 *
 * @author POO - Primer año Ingeniería Informática
 * @version 1.0
 */
public class TresEnRaya extends Juego {

    /**
     * Tablero de juego 3x3.
     * Cada celda contiene {@code null} si está vacía,
     * o el username del jugador que ha colocado su ficha.
     */
    private String[][] tablero;

    /** Username del primer jugador (juega con X). */
    private String jugador1;

    /** Username del segundo jugador (juega con O). */
    private String jugador2;

    /** Username del jugador al que le corresponde jugar en este turno. */
    private String turnoActual;

    /**
     * Username del ganador de la partida.
     * Es {@code null} si la partida no ha terminado o ha terminado en empate.
     */
    private String ganador;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    /**
     * Crea una nueva partida de Tres en Raya con dos jugadores.
     *
     * <p>El tablero se crea vacío. Para iniciar la partida hay que llamar
     * a {@link #inicializar()} después del constructor.</p>
     *
     * @param jugador1 username del primer jugador (jugará primero)
     * @param jugador2 username del segundo jugador
     */
    public TresEnRaya(String jugador1, String jugador2) {
        super("TresEnRaya", "Juego del tres en raya para dos jugadores");

        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.ganador = null;
        this.tablero = new String[3][3];
    }

    // ============================================================
    // MÉTODOS ABSTRACTOS DE Juego
    // ============================================================

    /**
     * Inicializa o reinicia la partida.
     *
     * <p>Limpia todas las casillas del tablero, asigna el turno al jugador 1
     * y resetea el ganador. Debe llamarse antes de empezar a jugar.</p>
     */
    @Override
    public void inicializar() {
        // Vaciamos todas las casillas del tablero
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = null;
            }
        }

        // El jugador 1 siempre empieza
        turnoActual = jugador1;
        ganador = null;
    }

    /**
     * Devuelve el estado actual del tablero en formato texto.
     *
     * <p>Muestra el tablero con:</p>
     * <ul>
     *   <li>{@code .} para casillas vacías</li>
     *   <li>{@code X} para las fichas del jugador 1</li>
     *   <li>{@code O} para las fichas del jugador 2</li>
     * </ul>
     * <p>Si la partida ha terminado, indica si hay ganador o empate.</p>
     *
     * @return String con el tablero dibujado en texto y el estado de la partida
     */
    @Override
    public String getEstadoTexto() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== TRES EN RAYA ===\n");
        sb.append("Turno de: ").append(turnoActual).append("\n\n");

        // Dibujamos el tablero fila por fila
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == null) {
                    sb.append(" . ");           // Casilla vacía
                } else if (tablero[i][j].equals(jugador1)) {
                    sb.append(" X ");           // Ficha del jugador 1
                } else {
                    sb.append(" O ");           // Ficha del jugador 2
                }
                if (j < 2) sb.append("|");     // Separador vertical entre columnas
            }
            sb.append("\n");
            if (i < 2) sb.append("-----------\n"); // Separador horizontal entre filas
        }

        // Mensaje de resultado si la partida ha terminado
        if (isTerminado()) {
            if (ganador != null) {
                sb.append("\n¡Ha ganado: ").append(ganador).append("!\n");
            } else {
                sb.append("\n¡Empate!\n");
            }
        }

        return sb.toString();
    }

    /**
     * Serializa el estado completo de la partida a un String.
     *
     * <p>El formato es el siguiente:</p>
     * <pre>
     *     casilla00,casilla01,...,casilla22;turnoActual;ganador
     * </pre>
     * <p>Las casillas vacías se representan con la cadena {@code "null"}.
     * Si no hay ganador, también se guarda como {@code "null"}.</p>
     *
     * <p>Este String puede usarse con {@link #deserializarEstado(String)}
     * para restaurar la partida más tarde.</p>
     *
     * @return String con el estado completo del juego serializado
     */
    @Override
    public String serializarEstado() {
        StringBuilder sb = new StringBuilder();

        // Guardamos las 9 casillas del tablero separadas por comas
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == null) {
                    sb.append("null");
                } else {
                    sb.append(tablero[i][j]);
                }
                // Añadimos coma separadora salvo en la última casilla
                if (!(i == 2 && j == 2)) {
                    sb.append(",");
                }
            }
        }

        // Añadimos el turno actual y el ganador, separados por punto y coma
        sb.append(";").append(turnoActual);
        sb.append(";").append(ganador == null ? "null" : ganador);

        return sb.toString();
    }

    /**
     * Restaura el estado de la partida a partir de un String serializado.
     *
     * <p>El String debe tener el formato generado por {@link #serializarEstado()}.
     * Reconstruye el tablero, el turno actual y el ganador.</p>
     *
     * @param s String con el estado serializado de la partida
     */
    @Override
    public void deserializarEstado(String s) {
        // Separamos el String en sus tres partes: tablero, turno y ganador
        String[] partes = s.split(";");

        // Reconstruimos el tablero a partir de las 9 casillas
        String[] casillas = partes[0].split(",");
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (casillas[index].equals("null")) {
                    tablero[i][j] = null;   // Casilla vacía
                } else {
                    tablero[i][j] = casillas[index]; // Username del jugador
                }
                index++;
            }
        }

        // Restauramos el turno actual
        turnoActual = partes[1];

        // Restauramos el ganador (null si la cadena es "null")
        ganador = partes[2].equals("null") ? null : partes[2];
    }

    /**
     * Marca la partida como finalizada.
     *
     * <p>Llama al método {@code terminar()} de la clase padre {@link Juego},
     * que se encarga de poner {@code juegoFinalizado = true}.</p>
     */
    @Override
    public void terminar() {
        super.terminar();
    }

    // ============================================================
    // LÓGICA PRINCIPAL DEL JUEGO
    // ============================================================

    /**
     * Realiza una jugada en la posición indicada del tablero.
     *
     * <p>Coloca la ficha del jugador actual en la casilla ({@code fila}, {@code columna}).
     * Después de cada jugada comprueba si hay victoria o empate. Si la partida
     * continúa, cambia el turno al otro jugador.</p>
     *
     * @param fila    fila donde se quiere colocar la ficha (0, 1 o 2)
     * @param columna columna donde se quiere colocar la ficha (0, 1 o 2)
     * @return {@code true} si la jugada fue válida y se realizó correctamente;
     *         {@code false} si la casilla estaba ocupada o el juego ya ha terminado
     */
    public boolean hacerJugada(int fila, int columna) {
        // No se puede jugar si la partida ya ha terminado
        if (isTerminado()) {
            return false;
        }

        // No se puede jugar en una casilla ya ocupada
        if (casillaOcupada(fila, columna)) {
            return false;
        }

        // Colocamos la ficha del jugador actual en el tablero
        tablero[fila][columna] = turnoActual;

        // Comprobamos si el jugador actual ha ganado con esta jugada
        if (hayVictoria(turnoActual)) {
            ganador = turnoActual;
            sumarPuntos(turnoActual, 10); // Sumamos 10 puntos al ganador
            terminar();
            return true;
        }

        // Comprobamos si el tablero está lleno y hay empate
        if (hayEmpate()) {
            terminar(); // La partida termina sin ganador
            return true;
        }

        // Si nadie ha ganado ni hay empate, cambiamos al otro jugador
        cambiarTurno();
        return true;
    }

    // ============================================================
    // MÉTODOS PRIVADOS DE APOYO
    // ============================================================

    /**
     * Comprueba si una casilla del tablero ya tiene una ficha.
     *
     * @param fila    fila de la casilla a comprobar (0, 1 o 2)
     * @param columna columna de la casilla a comprobar (0, 1 o 2)
     * @return {@code true} si la casilla ya está ocupada; {@code false} si está libre
     */
    private boolean casillaOcupada(int fila, int columna) {
        return tablero[fila][columna] != null;
    }

    /**
     * Comprueba si el jugador indicado ha conseguido tres en línea.
     *
     * <p>Revisa las tres filas, las tres columnas, la diagonal principal (↘)
     * y la diagonal secundaria (↙).</p>
     *
     * @param username username del jugador cuya victoria se comprueba
     * @return {@code true} si el jugador tiene tres fichas en línea; {@code false} en caso contrario
     */
    private boolean hayVictoria(String username) {
        // Comprobamos las tres filas
        for (int i = 0; i < 3; i++) {
            if (username.equals(tablero[i][0]) &&
                username.equals(tablero[i][1]) &&
                username.equals(tablero[i][2])) {
                return true;
            }
        }

        // Comprobamos las tres columnas
        for (int j = 0; j < 3; j++) {
            if (username.equals(tablero[0][j]) &&
                username.equals(tablero[1][j]) &&
                username.equals(tablero[2][j])) {
                return true;
            }
        }

        // Comprobamos la diagonal principal: (0,0) → (1,1) → (2,2)
        if (username.equals(tablero[0][0]) &&
            username.equals(tablero[1][1]) &&
            username.equals(tablero[2][2])) {
            return true;
        }

        // Comprobamos la diagonal secundaria: (0,2) → (1,1) → (2,0)
        if (username.equals(tablero[0][2]) &&
            username.equals(tablero[1][1]) &&
            username.equals(tablero[2][0])) {
            return true;
        }

        return false;
    }

    /**
     * Comprueba si la partida ha terminado en empate.
     *
     * <p>Hay empate cuando todas las casillas están ocupadas y ningún
     * jugador ha conseguido tres en línea.</p>
     *
     * @return {@code true} si el tablero está lleno y no hay ganador; {@code false} en caso contrario
     */
    private boolean hayEmpate() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == null) {
                    return false; // Aún hay casillas libres, no hay empate
                }
            }
        }
        return true; // Todas las casillas ocupadas → empate
    }

    /**
     * Cambia el turno al otro jugador.
     *
     * <p>Si le tocaba al jugador 1, pasa al jugador 2, y viceversa.</p>
     */
    private void cambiarTurno() {
        if (turnoActual.equals(jugador1)) {
            turnoActual = jugador2;
        } else {
            turnoActual = jugador1;
        }
    }

    // ============================================================
    // GETTERS
    // ============================================================

    /**
     * Devuelve el username del jugador al que le toca jugar.
     *
     * @return username del jugador con el turno actual
     */
    public String getTurnoActual() {
        return turnoActual;
    }

    /**
     * Devuelve el username del ganador de la partida.
     *
     * @return username del ganador, o {@code null} si no hay ganador
     *         (partida en curso o empate)
     */
    public String getGanador() {
        return ganador;
    }

    /**
     * Devuelve el tablero de juego actual.
     *
     * <p>Cada celda contiene el username del jugador que la ocupa,
     * o {@code null} si está vacía.</p>
     *
     * @return matriz 3x3 con el estado actual del tablero
     */
    public String[][] getTablero() {
        return tablero;
    }
}