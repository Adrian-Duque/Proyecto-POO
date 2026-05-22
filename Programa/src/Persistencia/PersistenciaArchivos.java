package Persistencia;

import Modelo.Administrador;
import Modelo.Estadistica;
import Modelo.Jugador;
import Modelo.Usuario;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Implementación de {@link GestorPersistencia} que almacena y recupera datos
 * mediante ficheros de texto plano en el sistema de archivos local.
 *
 * <p>Gestiona tres tipos de almacenamiento:</p>
 * <ul>
 *   <li>Usuarios registrados → {@code data/usuarios.txt}</li>
 *   <li>Estadísticas de partidas → {@code data/estadisticas.txt}</li>
 *   <li>Estados de partidas pausadas → {@code data/partidas/id.dat}</li>
 * </ul>
 *
 * <p>Todos los ficheros utilizan formato CSV separado por punto y coma ({@code ;}).
 * Los directorios necesarios se crean automáticamente en el constructor
 * si no existen previamente.</p>
 *
 * @author JP
 * @version 1.0
 */
public class PersistenciaArchivos implements GestorPersistencia {

    /** Ruta del fichero que almacena los usuarios registrados. */
    private static final String DATA_USUARIOS     = "Programa/data/usuarios.txt";

    /** Ruta del fichero que almacena el historial de estadísticas. */
    private static final String DATA_ESTADISTICAS = "Programa/data/estadisticas.txt";

    /** Ruta de la carpeta que contiene los ficheros de partidas pausadas. */
    private static final String CARPETA_PARTIDAS  = "Programa/data/partidas/";

    /**
     * Construye una instancia de {@code PersistenciaArchivos} y garantiza
     * que los directorios de trabajo existen antes de cualquier operación de E/S.
     *
     * <p>Si {@code data/} o {@code data/partidas/} no existen, los crea.
     * Esto evita {@link IOException} en la primera ejecución de la aplicación.</p>
     */
    public PersistenciaArchivos() {
        new File("Programa/data").mkdirs();
        new File(CARPETA_PARTIDAS).mkdirs();
    }

    // ─────────────────────────────────────────────
    //  USUARIOS
    // ─────────────────────────────────────────────

    /**
     * Sobrescribe el fichero de usuarios con la lista completa proporcionada.
     *
     * <p>Cada usuario se serializa mediante su método {@link Usuario#toArchivo()},
     * que produce una línea con el formato {@code username;password;esAdmin}.
     * Se llama a este método cada vez que se registra un nuevo usuario.</p>
     *
     * @param listaUsuarios lista de {@link Usuario} a persistir. No debe ser {@code null}.
     */
    @Override
    public void guardarUsuarios(ArrayList<Usuario> listaUsuarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_USUARIOS))) {
            for (Usuario u : listaUsuarios) {
                bw.write(u.toArchivo());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    /**
     * Lee el fichero de usuarios y reconstruye la lista de objetos del modelo.
     *
     * <p>El formato esperado por línea es {@code username;password;esAdmin}.
     * Si el tercer campo es {@code true}, se instancia un {@link Administrador};
     * en caso contrario, un {@link Jugador}.
     * Las líneas vacías o malformadas (menos de 3 campos) se ignoran silenciosamente.</p>
     *
     * @return lista de {@link Usuario} cargados desde disco.
     *         Devuelve una lista vacía si el fichero no existe todavía.
     */
    @Override
    public ArrayList<Usuario> cargarUsuarios() {
        ArrayList<Usuario> lista = new ArrayList<>();
        File fichero = new File(DATA_USUARIOS);

        if (!fichero.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] campos = linea.split(";");
                if (campos.length < 4) continue;

                String  username = campos[0];
                String  password = campos[1];
                boolean esAdmin  = Boolean.parseBoolean(campos[3]);
                int añoNacimiento = Integer.parseInt(campos[2]);

                if (esAdmin) {
                    lista.add(new Administrador(username, password,true, añoNacimiento));
                } else {
                    lista.add(new Jugador(username, password, true, añoNacimiento));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // ─────────────────────────────────────────────
    //  ESTADÍSTICAS
    // ─────────────────────────────────────────────

    /**
     * Añade una estadística al final del fichero de estadísticas sin sobrescribir
     * los registros existentes (modo <em>append</em>).
     *
     * <p>La estadística se serializa mediante {@link Estadistica#toArchivo()},
     * que produce una línea con el formato
     * {@code username;nombreJuego;puntuacion;victoria;fecha}.
     * La fecha se escribe en formato ISO ({@code yyyy-MM-dd}) gracias a
     * {@code LocalDate.toString()}.</p>
     *
     * @param e {@link Estadistica} a persistir. No debe ser {@code null}.
     */
    @Override
    public void agregarEstadistica(Estadistica e) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_ESTADISTICAS, true))) {
            bw.write(e.toArchivo());
            bw.newLine();
        } catch (IOException ex) {
            System.err.println("Error al guardar estadística: " + ex.getMessage());
        }
    }

    /**
     * Carga todas las estadísticas almacenadas en disco y las devuelve como lista.
     *
     * <p>El formato esperado por línea es
     * {@code username;nombreJuego;puntuacion;victoria;fecha}.
     * La fecha se deserializa con {@link LocalDate#parse(CharSequence)},
     * que espera formato ISO ({@code yyyy-MM-dd}), consistente con lo que
     * escribe {@link Estadistica#toArchivo()}.
     * Las líneas vacías o con menos de 5 campos se ignoran silenciosamente.</p>
     *
     * @return lista de {@link Estadistica} cargadas desde disco.
     *         Devuelve una lista vacía si el fichero no existe todavía.
     */
    @Override
    public ArrayList<Estadistica> cargarEstadisticas() {
        ArrayList<Estadistica> lista = new ArrayList<>();
        File fichero = new File(DATA_ESTADISTICAS);

        if (!fichero.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] campos = linea.split(";");
                if (campos.length < 5) continue;

                String    username    = campos[0];
                String    nombreJuego = campos[1];
                int       puntuacion  = Integer.parseInt(campos[2]);
                boolean   victoria    = Boolean.parseBoolean(campos[3]);
                LocalDate fecha       = LocalDate.parse(campos[4]);

                lista.add(new Estadistica(username, nombreJuego, puntuacion, victoria, fecha));
            }
        } catch (IOException e) {
            System.err.println("Error al cargar estadísticas: " + e.getMessage());
        }

        return lista;
    }

    // ─────────────────────────────────────────────
    //  PARTIDAS PAUSADAS
    // ─────────────────────────────────────────────

    /**
     * Guarda el estado serializado de una partida pausada en un fichero
     * denominado {@code id.dat} dentro de {@code data/partidas/}.
     *
     * <p>El contenido del fichero es el String devuelto por
     * {@code Juego.serializarEstado()}, cuyo formato concreto depende
     * de cada subclase de {@link Modelo.Juego}.</p>
     *
     * @param id     identificador único de la partida
     * @param estado String con el estado serializado del juego
     */
    @Override
    public void guardarPartidaPausada(int id, String estado) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CARPETA_PARTIDAS + id + ".dat"))) {
            bw.write(estado);
        } catch (IOException e) {
            System.err.println("Error al guardar partida pausada: " + e.getMessage());
        }
    }

    /**
     * Carga el estado serializado de una partida pausada desde su fichero {@code id.dat}.
     *
     * <p>El String devuelto se pasa directamente a {@code Juego.deserializarEstado()}
     * para reconstruir el estado interno del juego al reanudar la partida.</p>
     *
     * @param id identificador de la partida a cargar
     * @return String con el estado serializado, o {@code null} si el fichero no existe
     */
    @Override
    public String cargarPartidaPausada(int id) {
        File fichero = new File(CARPETA_PARTIDAS + id + ".dat");

        if (!fichero.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            return br.readLine();
        } catch (IOException e) {
            System.err.println("Error al cargar partida pausada: " + e.getMessage());
            return null;
        }
    }

    /**
     * Elimina el fichero {@code id.dat} de una partida pausada.
     *
     * <p>Se invoca cuando la partida se reanuda o se descarta, para no
     * acumular ficheros huérfanos en {@code data/partidas/}.</p>
     *
     * @param id identificador de la partida cuyo fichero se eliminará
     */
    @Override
    public void eliminarPartidaPausada(int id) {
        File fichero = new File(CARPETA_PARTIDAS + id + ".dat");
        if (fichero.exists()) {
            fichero.delete();
        }
    }

    /**
     * Lista los identificadores de todas las partidas pausadas que hay en disco.
     *
     * <p>Recorre {@code data/partidas/} buscando ficheros con extensión {@code .dat}
     * y extrae el id del nombre de cada fichero. Usado por {@code GestorPartidas}
     * para mostrar al usuario las partidas que puede reanudar.</p>
     *
     * @return lista de ids ({@code int}) de partidas pausadas.
     *         Devuelve una lista vacía si no hay ninguna.
     */
    @Override
    public ArrayList<Integer> listarPartidasPausadas() {
        ArrayList<Integer> ids = new ArrayList<>();
        File carpeta = new File(CARPETA_PARTIDAS);
        File[] ficheros = carpeta.listFiles();

        if (ficheros == null) return ids;

        for (File f : ficheros) {
            if (!f.getName().endsWith(".dat")) continue;
            String nombre = f.getName().replace(".dat", "");
            try {
                ids.add(Integer.parseInt(nombre));
            } catch (NumberFormatException e) {
                // fichero con nombre inesperado, lo ignoramos
            }
        }

        return ids;
    }
}