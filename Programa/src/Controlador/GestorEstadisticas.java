package Controlador;

import Modelo.Estadistica;
import Modelo.Partida;
import Modelo.PuntuacionJugador;
import Modelo.Usuario;
import Persistencia.GestorPersistencia;

import java.util.ArrayList;

/**
 * Gestor de estadísticas del sistema.
 * Coordina el registro, consulta y ranking de resultados de partidas.
 * Delega toda la persistencia en GestorPersistencia.
 *
 * @author juan Carlos
 * @version 3.0
 */
public class GestorEstadisticas {

    private GestorPersistencia persistencia;
    private ArrayList<Estadistica> listaEstadisticas;

    /**
     * Constructor de GestorEstadisticas.
     *
     * @param persistencia implementación de GestorPersistencia a utilizar para guardar y cargar estadísticas
     */
    public GestorEstadisticas(GestorPersistencia persistencia) {
        this.persistencia = persistencia;
        this.listaEstadisticas = persistencia.cargarEstadisticas();
    }

    /**
     * Registra el resultado de una partida finalizada.
     * Extrae los datos de cada jugador, crea una Estadistica por cada uno
     * y la guarda en persistencia y en la lista en memoria.
     *
     * @param partida partida finalizada de la que se extrae el resultado
     */
    public void registrarResultado(Partida partida) {
        Usuario ganador = partida.getGanador();
    
        for (Usuario u : partida.getListaJugadores()) {
            String username = u.getUsername();
            String nombreJuego = partida.getJuego().getNombre();
            int puntuacion = partida.getPuntuacion(u);
            boolean victoria;
                if (partida.getJuego() instanceof Modelo.Pasapalabra) {
                    Modelo.Pasapalabra pp = (Modelo.Pasapalabra) partida.getJuego();
                    victoria = pp.contarPendientes() == 0 && pp.getFallos() == 0;
                    } else {
                    victoria = ganador != null && ganador.getUsername().equals(username);
                    }
    
            Estadistica e = new Estadistica(username, nombreJuego, puntuacion, victoria,
                    partida.getFechaFin());
    
            persistencia.agregarEstadistica(e);
            listaEstadisticas.add(e);
        }
    }

    /**
     * Devuelve todas las estadísticas de un usuario concreto.
     *
     * @param u usuario del que se quieren las estadísticas
     * @return lista con las estadísticas del usuario
     */
    public ArrayList<Estadistica> getEstadisticasUsuario(Usuario u) {
        ArrayList<Estadistica> resultado = new ArrayList<>();
        for (Estadistica e : listaEstadisticas) {
            if (e.getUsername().equals(u.getUsername())) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /**
     * Devuelve las últimas n estadísticas de un usuario, ordenadas de más reciente a más antigua.
     * Si el usuario tiene menos de n partidas, devuelve todas.
     * El ordenamiento aprovecha el método {@code isBefore} de {@code LocalDate}
     * para una comparación semánticamente clara.
     *
     * @param u usuario del que se quieren las estadísticas
     * @param n número máximo de resultados a devolver
     * @return lista con las últimas n estadísticas del usuario
     */
    public ArrayList<Estadistica> getUltimasPartidas(Usuario u, int n) {
        ArrayList<Estadistica> todas = getEstadisticasUsuario(u);

        for (int i = 0; i < todas.size() - 1; i++) {
            for (int j = i + 1; j < todas.size(); j++) {
                if (todas.get(i).getFecha().isBefore(todas.get(j).getFecha())) {
                    Estadistica temp = todas.get(i);
                    todas.set(i, todas.get(j));
                    todas.set(j, temp);
                }
            }
        }

        ArrayList<Estadistica> resultado = new ArrayList<>();
        for (int i = 0; i < n && i < todas.size(); i++) {
            resultado.add(todas.get(i));
        }
        return resultado;
    }

    /**
     * Calcula el ranking de un juego concreto ordenado por puntuación de mayor a menor.
     *
     * @param nombreJuego nombre del juego del que se quiere el ranking
     * @return lista de estadísticas ordenadas por puntuación descendente
     */
    public ArrayList<Estadistica> calcularRanking(String nombreJuego) {
        ArrayList<Estadistica> ranking = new ArrayList<>();

        for (Estadistica e : listaEstadisticas) {
            if (e.getNombreJuego().equals(nombreJuego)) {
                ranking.add(e);
            }
        }

        for (int i = 0; i < ranking.size() - 1; i++) {
            for (int j = i + 1; j < ranking.size(); j++) {
                if (ranking.get(i).getPuntuacion() < ranking.get(j).getPuntuacion()) {
                    Estadistica temp = ranking.get(i);
                    ranking.set(i, ranking.get(j));
                    ranking.set(j, temp);
                }
            }
        }

        return ranking;
    }

    /**
     * Cuenta el total de partidas jugadas por un usuario.
     *
     * @param username nombre del usuario
     * @return número de partidas jugadas
     */
    public int contarPartidas(String username) {
        int contador = 0;
        for (Estadistica e : listaEstadisticas) {
            if (e.getUsername().equals(username)) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Cuenta el total de victorias de un usuario.
     *
     * @param username nombre del usuario
     * @return número de victorias
     */
    public int contarVictorias(String username) {
        int contador = 0;
        for (Estadistica e : listaEstadisticas) {
            if (e.getUsername().equals(username) && e.isVictoria()) {
                contador++;
            }
        }
        return contador;
    }
}