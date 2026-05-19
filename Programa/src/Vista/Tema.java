package Vista;

import java.awt.*;

/**
 * Paleta de colores y tipografía compartida por todas las ventanas de la aplicación.
 * Todas las constantes son públicas y estáticas; importar la clase y referenciar
 * {@code Tema.XXX} en cada ventana.
 *
 * @author JP-Aceves
 * @version 2.0
 */
public final class Tema {

    private Tema() {}

    // ── Fondos ────────────────────────────────────────────────────────────────
    public static final Color FONDO         = new Color(10, 10, 10);
    public static final Color FONDO_PANEL   = new Color(20, 20, 20);
    public static final Color FONDO_OSCURO  = new Color(5, 5, 5);
    public static final Color FONDO_CAMPO   = new Color(30, 30, 30);
    public static final Color FONDO_INPUT   = new Color(22, 22, 22);

    // ── Texto y bordes ────────────────────────────────────────────────────────
    public static final Color ACENTO        = new Color(250, 200, 0);
    public static final Color TEXTO         = new Color(240, 240, 240);
    public static final Color SUBTEXTO      = new Color(160, 160, 160);
    public static final Color ERROR         = new Color(220, 70, 70);
    public static final Color BORDE         = new Color(45, 45, 45);
    public static final Color BORDE_INPUT   = new Color(250, 200, 0);

    // ── Estados del juego ─────────────────────────────────────────────────────
    public static final Color CORRECTO      = new Color(34, 197, 94);
    public static final Color INCORRECTO    = new Color(239, 68, 68);
    public static final Color PASAPALABRA   = new Color(59, 130, 246);
    public static final Color ACTUAL        = new Color(250, 200, 0);
    public static final Color PENDIENTE     = new Color(40, 40, 40);

    // ── Fichas TresEnRaya ─────────────────────────────────────────────────────
    public static final Color FICHA_X       = new Color(250, 200, 0);
    public static final Color FICHA_O       = new Color(220, 220, 220);

    // ── Miscelánea ────────────────────────────────────────────────────────────
    public static final Color GRIS_BOTON    = new Color(60, 60, 60);

    // ── Tipografía ────────────────────────────────────────────────────────────
    public static final Font FUENTE_TITULO      = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FUENTE_LABEL       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_CAMPO       = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON       = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FUENTE_ENLACE      = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_GRANDE      = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FUENTE_CELDA       = new Font("Segoe UI", Font.BOLD,  42);
    public static final Font FUENTE_TURNO       = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FUENTE_PUNTOS      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_TIEMPO      = new Font("SansSerif", Font.BOLD, 36);
    public static final Font FUENTE_LETRA       = new Font("SansSerif", Font.BOLD, 48);
    public static final Font FUENTE_DEFINICION  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FUENTE_RESPUESTA   = new Font("SansSerif", Font.PLAIN, 15);
    public static final Font FUENTE_BOTON_JUEGO = new Font("SansSerif", Font.BOLD,  13);
    public static final Font FUENTE_ROSCO       = new Font("SansSerif", Font.BOLD,  13);
}
