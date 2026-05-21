package Controlador;

import Modelo.Juego;
import Modelo.Pasapalabra;
import Modelo.TresEnRaya;

import java.util.ArrayList;

/**
 * Controlador encargado de gestionar los juegos disponibles en el sistema.
 * Permite registrar juegos, instanciarlos y consultar sus propiedades.
 *
 * Autor: Ignacio del Peso Dominguez
 * Versión: 2.0
 * Descripción: Clase que gestiona los juegos del sistema.
 */
public class GestorJuegos {

    /**
     * Lista con los nombres de los juegos registrados en el sistema.
     */
    private ArrayList<String> juegosDisponibles;

    /**
     * Constructor por defecto. Inicializa la lista de juegos disponibles vacía.
     */
    public GestorJuegos() {
        this.juegosDisponibles = new ArrayList<>();
    }

    /**
     * Registra un juego en el sistema por su nombre si no estaba ya registrado.
     *
     * @param nombre nombre del juego a registrar
     */
    public void registrarJuego(String nombre) {
        if (!juegosDisponibles.contains(nombre)) {
            juegosDisponibles.add(nombre);
        }
    }

    /**
     * Crea e inicializa una nueva instancia del juego indicado por nombre.
     *
     * @param nombre nombre del juego ("PasaPalabra", "TresEnRaya", …)
     * @return instancia de Juego correspondiente, o null si el nombre no existe
     */
    public Juego crearJuego(String nombre) {
        if ("Pasapalabra".equals(nombre)) return new Pasapalabra();
        if ("TresEnRaya".equals(nombre)) return new TresEnRaya();
        System.err.println("GestorJuegos: juego desconocido -> " + nombre);
        return null;
    }

    /**
     * Crea e inicializa una nueva instancia del juego indicado por nombre y nivel (metodo con sobrecarga).
     *
     * @param nombre nombre del juego ("PasaPalabra", "TresEnRaya", …)
     * @return instancia de Juego y Nivel correspondiente, o null si el nombre no existe
     */
    public Juego crearJuego(String nombre, int nivel) {
        if ("Pasapalabra".equals(nombre)) return new Pasapalabra(nivel);
        if ("TresEnRaya".equals(nombre)) return new TresEnRaya();
        return null;
    }

    /**
     * Indica si un juego es multijugador.
     *
     * @param nombre nombre del juego a consultar
     * @return true si el juego admite varios jugadores, false si es para un solo jugador
     */
    public boolean esMultijugador(String nombre) {
        return "TresEnRaya".equals(nombre);
    }

    /**
     * Devuelve el número máximo de jugadores permitidos para un juego.
     *
     * @param nombre nombre del juego a consultar
     * @return número máximo de jugadores (2 para TresEnRaya, 1 para el resto)
     */
    public int getMaxJugadores(String nombre) {
        if ("TresEnRaya".equals(nombre)) return 2;
        return 1;
    }

    /**
     * Devuelve una copia de la lista de nombres de juegos registrados en el sistema.
     *
     * @return ArrayList con los nombres de los juegos disponibles
     */
    public ArrayList<String> getJuegosDisponibles() {
        return new ArrayList<>(juegosDisponibles);
    }
}