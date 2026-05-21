package Modelo;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Implementación del juego PasaPalabra.
 * Extiende la clase abstracta Juego y adapta la lógica original de consola
 * para integrarse con la arquitectura de capas del proyecto.
 *
 * @author Adrián
 * @version 2.1
 */
public class Pasapalabra extends Juego {

    // ── Constantes de estado de cada letra ──────────────────────────────────
    public static final String ESTADO_PENDIENTE   = "0";
    public static final String ESTADO_CORRECTA    = "1";
    public static final String ESTADO_INCORRECTA  = "2";
    public static final String ESTADO_PASAPALABRA = "3";

    // ── Ruta base de los ficheros de roscos ──────────────────────────────────
    private static final String DIR_ROSCOS = "Programa/data/roscos/";

    // ── Estado interno ───────────────────────────────────────────────────────
    /**
     * rosco[i][0] = letra
     * rosco[i][1] = definición
     * rosco[i][2] = respuesta correcta
     * rosco[i][3] = estado (PENDIENTE / CORRECTA / INCORRECTA / PASAPALABRA)
     */
    private String[][] rosco;
    private int letraActual;
    private int nivel;        // 0=Infantil, 1=Fácil, 2=Medio, 3=Avanzado
    private int aciertos;
    private int fallos;
    private int pasapalabras;

    // ── Constructores ────────────────────────────────────────────────────────

    public Pasapalabra(int nivel) {
        super("Pasapalabra", "Adivina las palabras del rosco letra a letra", false);
        this.nivel = nivel;
    }

    public Pasapalabra() {
        this(1);
    }

    // ── Implementación de métodos abstractos de Juego ────────────────────────

    @Override
    public void inicializar() {
        this.rosco           = cargarDatos(nivel);
        this.letraActual     = 0;
        this.aciertos        = 0;
        this.fallos          = 0;
        this.pasapalabras    = 0;
        this.juegoFinalizado = false;
        avanzarAProximaPendiente();
    }

    @Override
    public String getEstadoTexto() {
        if (rosco == null || letraActual < 0 || letraActual >= rosco.length) {
            return "Juego terminado";
        }
        return "Letra " + rosco[letraActual][0] + " - " + rosco[letraActual][1];
    }

    /**
     * Formato: letraActual,aciertos,fallos,pasapalabras,estado0,...,estado26
     */
    @Override
    public String serializarEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append(letraActual).append(",")
                .append(aciertos).append(",")
                .append(fallos).append(",")
                .append(pasapalabras);
        for (String[] fila : rosco) {
            sb.append(",").append(fila[3]);
        }
        return sb.toString();
    }

    @Override
    public void deserializarEstado(String estado) {
        String[] partes = estado.split(",");
        if (partes.length < 4 + rosco.length) return;
        letraActual  = Integer.parseInt(partes[0]);
        aciertos     = Integer.parseInt(partes[1]);
        fallos       = Integer.parseInt(partes[2]);
        pasapalabras = Integer.parseInt(partes[3]);
        for (int i = 0; i < rosco.length; i++) {
            rosco[i][3] = partes[4 + i];
        }
        if (contarPendientes() == 0) {
            juegoFinalizado = true;
        }
    }

    @Override
    public void terminar() {
        this.juegoFinalizado = true;
    }

    // ── Lógica de juego ──────────────────────────────────────────────────────

    /**
     * Procesa la respuesta del usuario para la letra actual.
     *
     * @return true si es correcta, false si es incorrecta
     */
    public boolean procesarRespuesta(String respuesta) {
        if (juegoFinalizado || rosco == null) return false;

        boolean esCorrecta = limpiarTexto(respuesta).equals(limpiarTexto(rosco[letraActual][2]));

        if (esCorrecta) {
            rosco[letraActual][3] = ESTADO_CORRECTA;
            aciertos++;
            sumarPuntos(obtenerUsernameJugador(), 10);
        } else {
            rosco[letraActual][3] = ESTADO_INCORRECTA;
            fallos++;
        }

        avanzarAProximaPendiente();
        comprobarFinDeJuego();
        return esCorrecta;
    }

    /** Marca la letra actual como pasapalabra y avanza a la siguiente pendiente. */
    public void pasarPalabra() {
        if (juegoFinalizado || rosco == null) return;
        rosco[letraActual][3] = ESTADO_PASAPALABRA;
        pasapalabras++;
        avanzarAProximaPendiente();
        comprobarFinDeJuego();
    }

    // ── Getters de estado para la vista ─────────────────────────────────────

    public int getAciertos()     { return aciertos; }
    public int getFallos()       { return fallos; }
    public int getPasaPalabras() { return pasapalabras; }
    public int getLetraActual()  { return letraActual; }
    public int getNivel()        { return nivel; }

    /** Devuelve una copia de la fila [letra, definición, respuesta, estado] para el índice dado. */
    public String[] getDatosLetra(int indice) {
        if (rosco == null || indice < 0 || indice >= rosco.length) return null;
        return rosco[indice].clone();
    }

    /** Cuántas letras quedan por responder (pendientes + pasapalabras). */
    public int contarPendientes() {
        if (rosco == null) return 0;
        int count = 0;
        for (String[] fila : rosco) {
            if (ESTADO_PENDIENTE.equals(fila[3]) || ESTADO_PASAPALABRA.equals(fila[3])) count++;
        }
        return count;
    }

    public int getTotalLetras() {
        return rosco != null ? rosco.length : 27;
    }

    // ── Métodos privados ─────────────────────────────────────────────────────

    private void avanzarAProximaPendiente() {
        if (rosco == null) return;
        int idx = (letraActual + 1) % rosco.length;
        for (int i = 0; i < rosco.length; i++) {
            if (ESTADO_PENDIENTE.equals(rosco[idx][3]) || ESTADO_PASAPALABRA.equals(rosco[idx][3])) {
                letraActual = idx;
                return;
            }
            idx = (idx + 1) % rosco.length;
        }
        // No hay más pendientes, comprobarFinDeJuego() llamará a terminar()
    }

    private void comprobarFinDeJuego() {
        if (contarPendientes() == 0) terminar();
    }

    private String obtenerUsernameJugador() {
        ArrayList<PuntuacionJugador> lista = getPuntuaciones();
        return lista.isEmpty() ? "jugador" : lista.get(0).getUsername();
    }

    /** Normaliza texto: elimina tildes y convierte a minúsculas. */
    public static String limpiarTexto(String texto) {
        if (texto == null) return "";
        String n = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return n.replaceAll("\\p{Mn}+", "").toLowerCase().trim();
    }

    // ── Resolución de rutas ──────────────────────────────────────────────────

    private static File resolverRuta(String nombreFichero) {
        // 1. Relativo al directorio de trabajo (funciona desde Programa/ con compilar.sh)
        File f = new File(DIR_ROSCOS + nombreFichero);
        if (f.exists()) return f;

        // 2. Relativo al directorio del .class (funciona desde IDE cuyo output es out/)
        try {
            URL url = Pasapalabra.class.getProtectionDomain().getCodeSource().getLocation();
            File classRoot = new File(url.toURI());
            f = new File(classRoot.getParentFile(), DIR_ROSCOS + nombreFichero);
            if (f.exists()) return f;
        } catch (URISyntaxException ignored) {}

        // 3. Devuelve la ruta original aunque no exista (el error lo reporta cargarDatos)
        return new File(DIR_ROSCOS + nombreFichero);
    }

    // ── Carga de datos desde fichero ─────────────────────────────────────────

    private static String[][] cargarDatos(int nivel) {
        Random rand = new Random();
        String[][] rosco = new String[27][4];

        String nombreFichero;
        switch (nivel) {
            case 0:  nombreFichero = "rosco_infantil.txt"; break;
            case 2:  nombreFichero = "rosco_medio.txt";    break;
            case 3:  nombreFichero = "rosco_avanzado.txt"; break;
            default: nombreFichero = "rosco_facil.txt";
        }

        for (int i = 0; i < rosco.length; i++) rosco[i][3] = ESTADO_PENDIENTE;

        File fichero = resolverRuta(nombreFichero);
        String[] banco = new String[300];
        int lineasLeidas = 0;

        try (Scanner lector = new Scanner(fichero, "UTF-8")) {
            while (lector.hasNextLine() && lineasLeidas < banco.length) {
                String linea = lector.nextLine().trim();
                if (!linea.isEmpty()) banco[lineasLeidas++] = linea;
            }
        } catch (IOException e) {
            System.err.println("Fichero no encontrado o error de lectura: " + fichero.getAbsolutePath());
            return rosco;
        }

        for (int i = 0; i < rosco.length; i++) {
            int idx = (i * 10) + rand.nextInt(10);
            if (idx < lineasLeidas) {
                String[] partes = banco[idx].split(";");
                if (partes.length >= 3) {
                    rosco[i][0] = partes[0].trim();
                    rosco[i][1] = partes[1].trim();
                    rosco[i][2] = partes[2].trim();
                }
            }
        }
        return rosco;
    }
}