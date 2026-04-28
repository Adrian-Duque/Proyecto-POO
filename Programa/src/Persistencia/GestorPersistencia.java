package Persistencia;

import Modelo.Usuario;
import Modelo.Estadistica;
import java.util.ArrayList;

/**
 * Interfaz que define el contrato para la persistencia de datos.
 *
 * @author JP-Aceves
 * @version 1.0
 */
public interface GestorPersistencia {

    /** Guarda la lista completa de usuarios en el fichero. */
    void guardarUsuarios(ArrayList<Usuario> listaUsuarios);

    /** Carga y devuelve la lista de usuarios del fichero. */
    ArrayList<Usuario> cargarUsuarios();

    /** Añade una estadística al fichero de estadísticas. */
    void agregarEstadistica(Estadistica e);

    /** Carga y devuelve todas las estadísticas del fichero. */
    ArrayList<Estadistica> cargarEstadisticas();

    /** Guarda el estado serializado de una partida pausada. */
    void guardarPartidaPausada(int id, String estado);

    /** Carga el estado de una partida pausada por su id. */
    String cargarPartidaPausada(int id);

    /** Elimina el fichero de una partida pausada. */
    void eliminarPartidaPausada(int id);

    /** Lista los ids de todas las partidas pausadas. */
    ArrayList<Integer> listarPartidasPausadas();
}