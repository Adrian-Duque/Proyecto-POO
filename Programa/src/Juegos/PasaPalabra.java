import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.text.Normalizer;

package Juegos;

/**
 * Clase principal del pasa palabra que implementa la lógica del juego "Pasapalabra".
 * Permite jugar un rosco de preguntas, válida respuesta y muestra el progreso visualmente.
 * @version 1.1.0
 * @author Adrian Duque
 */

public class PasaPalabra {
    /** Escáner global para la entrada de datos por consola. */
    public static Scanner sc = new Scanner(System.in);

    /**
     * Punto de entrada principal de la aplicación.
     * Gestiona el menú principal, el registro del usuario, la selección de dificultad
     * y el flujo de control entre jugar, ver estadísticas y salir.
     */
    public static void inicializar(String[] args) {
        int opcion;

        // Bucle principal del menú
        do {
            System.out.print("Bienvenido al PasaJava, un PasaPalabra creado en java :)\nMenú.:\n1.Jugar\n2.Estadísticas\n3.Salir\nSeleccione una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1:
                    // --- BLOQUE DE REGISTRO DE USUARIO ---
                    System.out.print("Para jugar primero tiene que registrarse.\nNombre: ");
                    String nombre = sc.next();

                    // Validación de Edad
                    int edad;
                    do {
                        System.out.print("Introduce tu edad: ");
                        edad = sc.nextInt();
                        if (edad < 0) {
                            System.out.println("\u001B[31mError, Introduce una edad positiva\u001B[0m");
                        }
                    } while (edad < 0);

                    // Validación de correo (Debe contener '@' y luego un '.')
                    boolean vAt;    // Tiene arroba
                    boolean vDot;   // Tiene punto después de la arroba
                    String correo = "";
                    do {
                        vAt = false;
                        vDot = false;
                        correo = "";
                        System.out.print("Introduce tu mail: ");
                        correo = sc.next();

                        for (int i = 0; i < correo.length(); i++) {
                            if  (correo.charAt(i) == '@') {
                                vAt = true;
                            }
                            // Solo validamos el punto si ya hemos encontrado la arroba
                            if (vAt && correo.charAt(i) == '.') {
                                vDot = true;
                            }
                        }

                        if (!vAt || !vDot) {
                            System.out.println("\u001B[31mError, Introduce un mail valido\u001B[0m");
                        }
                    } while (!vAt || !vDot);

                    // --- SELECCIÓN DE NIVEL ---
                    System.out.print("Selecciona el nivel:\n0.Infantil\n1.Facil\n2.Medio\n3.Avanzado\nSelecciona Nivel: ");
                    int nivel = sc.nextInt();

                    // --- GENERACIÓN DEL ROSCO ---
                    String[][] rosco = cargarDatos(nivel);

                    // Solo iniciamos si el rosco se cargó correctamente (no está vacío)
                    if (rosco[0][0] != null) {
                        juego(rosco);
                        finalizarPartida(rosco, correo, nivel);
                        mostrarEstadisticas();
                    } else {
                        System.out.println("\u001B[31mError crítico al cargar el rosco. Volviendo al menú.\u001B[0m");
                    }
                    break;

                case 2:
                    mostrarEstadisticas();
                    break;

                case 3:
                    System.out.println("¡Gracias por jugar a PasaJava! Hasta la próxima.");
                    break;

                default:
                    System.out.println("\u001B[31mOpción no válida. Por favor, elija 1, 2 o 3.\u001B[0m");
            }
        } while (opcion != 3); // El programa solo termina si la opción es 3
    }

    /**
     * Carga los datos del juego (letras, definiciones y respuestas) desde un fichero de texto
     * basado en el nivel de dificultad seleccionado.
     * <p>
     * El método asume que los ficheros de texto tienen una estructura específica:
     * están ordenados alfabéticamente y contienen bloques de 10 preguntas por cada letra.
     *
     * @param nivel El nivel de dificultad seleccionado por el usuario:
     * <ul>
     * <li>0: Infantil</li>
     * <li>1: Fácil</li>
     * <li>2: Medio</li>
     * <li>3: Avanzado</li>
     * </ul>
     * @return Una matriz de Strings [27][4] inicializada con las preguntas seleccionadas.
     * Cada fila representa una letra (A-Z) y contiene:
     * <ul>
     * <li>[0]: Letra</li>
     * <li>[1]: Definición</li>
     * <li>[2]: Respuesta correcta</li>
     * <li>[3]: Estado inicial ("0" para no respondida)</li>
     * </ul>
     * Devuelve una matriz vacía o parcialmente llena si ocurre un error de lectura.
     */
    public static String[][] cargarDatos(int nivel) {
        Random rand = new Random();
        String archivo = "";
        String[][] rosco = new String[27][4];

        // Selección del fichero según el nivel de dificultad
        switch (nivel) {
            case 0: //Infantil
                archivo = "rosco_infantil.txt";
                break;
            case 1: //Fácil
                archivo = "rosco_facil.txt";
                break;
            case 2: //Medio
                archivo = "rosco_medio.txt";
                break;
            case 3: //Avanzado
                archivo = "rosco_avanzado.txt";
                break;
            default:
                System.out.println("\u001B[31mNivel no válido, se cargará el nivel fácil por defecto.\u001B[0m");
                archivo = "rosco_facil.txt";
        }

        File fichero = new File("src/roscos/"+archivo);

        // Array temporal (buffer) para cargar el contenido del fichero en memoria.
        // Se reserva espacio para 300 líneas (27 letras * ~10 opciones = 270 líneas mínimo).
        String[] bancoDePreguntasTemporal = new String[300];
        int cantidadLineasLeidas = 0;


        try {
            Scanner lFichero = new Scanner(fichero);

            // 1. Fase de Carga: Leemos el fichero secuencialmente y llenamos el buffer
            while(lFichero.hasNextLine() && cantidadLineasLeidas < bancoDePreguntasTemporal.length) {
                String linea = lFichero.nextLine();

                // Ignoramos líneas vacías para evitar errores de parseo
                if (linea.length() > 0) {
                    bancoDePreguntasTemporal[cantidadLineasLeidas] = linea;
                    cantidadLineasLeidas++;
                }
            }
            lFichero.close();

            // 2. Fase de Selección: Elegimos una pregunta aleatoria para cada letra
            for(int i = 0; i < rosco.length; i++) {

                // Fórmula para acceder al bloque de 10 preguntas correspondiente a la letra 'i'.
                // Ejemplo: Para la 'A' (i=0) busca entre 0-9. Para la 'B' (i=1) busca entre 10-19.
                int linea = (i*10)+rand.nextInt(10);

                // Verificación de límites para evitar NullPointerException o IndexOutOfBounds
                if(linea < cantidadLineasLeidas) {
                    String lineaSeleccionada = bancoDePreguntasTemporal[linea];

                    // Separamos los campos usando el delimitador ;
                    String[] partes = lineaSeleccionada.split(";"); //separamos las filas del archivo para las distintas posiciones del array

                    // Validación de formato: Deben existir al menos 3 partes (Letra, Definición, Respuesta)
                    if (partes.length >= 3) {
                        rosco[i][0] = partes[0].trim();     // Letra
                        rosco[i][1] = partes[1];            // Definición (no hacemos trim interno para respetar formato)
                        rosco[i][2] = partes[2].trim();     // Respuesta (trim esencial para comparaciones exactas)
                        rosco[i][3] = "0";                  // Estado inicial: 0 (No Preguntada)
                    }
                } else {
                    System.out.println("\u001B[31mError: No hay linea suficiente en el fichero para la letra " + rosco[i][0] + "\u001B[0m");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("no se ha encontrado el archivo: " + fichero.getAbsolutePath());
            e.printStackTrace();
        }
        return rosco;
    }

    /**
     * Gestiona el bucle principal del juego.
     * Itera sobre el array de preguntas, gestiona las respuestas del usuario y controla
     * la lógica de repetición para las palabras en estado "Pasapalabra".
     *
     * @param rosco Array bidimensional [27][4] que contiene:
     * <ul>
     * <li>[0]: Letra</li>
     * <li>[1]: Definición</li>
     * <li>[2]: Respuesta correcta</li>
     * <li>[3]: Estado (0:Pendiente, 1:Correcta, 2:Incorrecta, 3:Pasapalabra).</li>
     * </ul>
     */
    public static void juego(String[][] rosco) {
        int respuestaSalida; // Almacenamos el valor si el usuario quiere salir.
        String respuesta; // Almacenamos la respuesta del usuario
        boolean salir = false;
        String mensajeAnterior = ""; // Buffer para mostrar el resultado del turno anterior.

        do {
            for (int i = 0; i < rosco.length; i++) {
                // Verificar si esa palabra está 0 (no preguntada) o 3 (pasapalabra)
                if (rosco[i][3].equals("0") || rosco[i][3].equals("3")) {

                    // 1. Visualización: Pintamos el rosco actualizado
                    colorear(rosco,i);

                    // 2. Feedback: Mostramos el resultado de la letra anterior (si existe)
                    if (!mensajeAnterior.isEmpty()) {
                        System.out.println(mensajeAnterior);
                        mensajeAnterior = ""; // Reseteamos el mensaje
                    }

                    // 3. Interacción: Mostramos definición y pedimos respuesta
                    System.out.print("\n" + rosco[i][1] + "\nRespuesta: ");
                    respuesta = sc.next();

                    // 4. Lógica de verificación
                    if (limpiarTexto(respuesta).equals(limpiarTexto(rosco[i][2]))) {
                        rosco[i][3] = "1"; // Respuesta Correcta
                        mensajeAnterior = "Respuesta Correcta";
                    } else if (respuesta.equalsIgnoreCase("Pasapalabra")) { //ignoramos las mayúsculas y minúsculas del usuario.
                        rosco[i][3] = "3"; // PasaPalabra
                        mensajeAnterior = "Pasapalabra";
                    } else {
                        rosco[i][3] = "2"; // Respuesta Incorrecta
                        mensajeAnterior = "Respuesta Incorrecta, la respuesta era: " + rosco[i][2];
                    }
                }
            }

            // Contar pendientes para verificar si el juego debe continuar
            int pendientes = 0;
            for (String[] strings : rosco) {
                if (strings[3].equals("3")) {
                    pendientes++;
                }
            }

            // Mostramos el estado final de la ronda (importante para ver el resultado de la Z)
            colorear(rosco,-1); //-1 Para pintarlo sin letra seleccionada en amarillo
            System.out.println();
            if (!mensajeAnterior.isEmpty()) {
                System.out.println(mensajeAnterior);
                mensajeAnterior = ""; // Limpiamos
            }

            // Verificamos si quedan preguntas sin responder
            if (pendientes > 0) {
                do {
                    System.out.print("Te quedan " + pendientes + " sin responder, ¿quieres seguir jugando?\n1.Si   2.No\nRespuesta: ");
                    respuestaSalida = sc.nextInt();
                    switch (respuestaSalida) {
                        case 1:
                            // Continúa el bucle externo, ya que salir por defecto es false.
                            break;
                        case 2:
                            salir = true;
                            break;
                        default:
                            System.out.println("Introduce una respuesta valida.");
                    }
                } while (respuestaSalida < 1 || respuestaSalida > 2);
            } else {
                salir = true; // No quedan pendientes, termina el juego
            }
        } while (!salir);
    }

    /**
     * Normaliza un texto para facilitar comparaciones.
     * Separa las tildes de las vocales, elimina los diacríticos y convierte todo a minúsculas.
     *
     * @param texto La cadena de texto original (ej.: "Árbol").
     * @return El texto limpio sin tildes y en minúsculas (ej.: "arbol"), o null si la entrada es null.
     */
    public static String limpiarTexto(String texto) {
        if (texto == null) return null;

        // Separamos la tilde de la letra
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);

        // Eliminamos las tildes usando Regex
        textoNormalizado = textoNormalizado.replaceAll("\\p{Mn}+", "");

        return textoNormalizado.toLowerCase();
    }

    /**
     * Muestra visualmente el estado del rosco en la consola utilizando códigos de colores ANSI.
     *
     * @param rosco        El array de datos del juego para consultar el estado de cada letra.
     * @param indiceActual El índice de la letra que se está jugando actualmente (se pintará de amarillo).
     */
    public static void colorear(String[][] rosco, int indiceActual) {
        // Códigos ANSI para los colores
        final String RESET = "\u001B[0m";
        final String ROJO = "\u001B[31m";
        final String VERDE = "\u001B[32m";
        final String AMARILLO = "\u001B[33m";
        final String BLANCO = "\u001B[37m";

        System.out.print("\n+------------------ Estado del Rosco -------------------+\n| ");

        // Recorremos todo el rosco para imprimir cada letra con su color correspondiente
        for (int i = 0; i < rosco.length; i++) {
            String letra = rosco[i][0];
            String estado = rosco[i][3];
            String color = BLANCO;

            if (i == indiceActual) {
                color = AMARILLO; // Letra actual
            } else {
                // Si no es la actual, verificamos su estado
                switch (estado) {
                    case "1":
                        color = VERDE; // Correcta
                        break;
                    case "2":
                        color = ROJO; // Incorrecta
                        break;
                    // Los casos "0" y "3" ya están cubiertos por el color por defecto (BLANCO)
                }
            }

            // Imprimimos la letra con su color y luego reseteamos el color
            System.out.print(color + letra + " " + RESET);
        }
        System.out.println("|\n+-------------------------------------------------------+\n");
    }

    /**
     * Finaliza la partida actual, calcula las estadísticas recorriendo el rosco
     * y muestra el resumen al usuario. Finalmente, guarda los datos en el historial.
     *
     * @param datosRosco Matriz [27][4] que contiene el estado del juego.
     * Se evalúa la columna [3] para determinar el estado:
     * <ul>
     * <li>"1" (Acierto)</li>
     * <li>"2" (Fallo)</li>
     * <li>"3" (Pasapalabra)</li>
     * </ul>
     * @param correoUsuario Identificador del usuario (email o nick).
     * @param nivel Nivel de dificultad jugado (0-3).
     */
    public static void finalizarPartida(String[][] datosRosco, String correoUsuario, int nivel) {
        // Inicializamos contadores
        int aciertos = 0;
        int fallos = 0;
        int pasapalabras = 0;

        // Recorremos el rosco para contar los resultados
        for (int i = 0; i < datosRosco.length; i++) {
            String estado = datosRosco[i][3];

            switch (estado) {
                case "1":
                        aciertos++;
                        break;
                case "2":
                        fallos++;
                        break;
                case "3":
                        pasapalabras++; // Contamos los PasaPalabras no resueltos al final.
                        break;
            }
        }

        // Imprimir al usuario el resumen por consola.
        System.out.println("Resultados para " + correoUsuario + ":");
        System.out.println("Aciertos: " + aciertos);
        System.out.println("Fallos: " + fallos);
        System.out.println("Pasapalabras: " + pasapalabras);

        // Llamamos a la función encargada del guardado de datos
        guardarDatosPartida(correoUsuario, aciertos, fallos, pasapalabras, nivel);
    }

    /**
     * Guarda los resultados de una partida en un archivo de texto (marcadorUsuario.txt).
     * Los datos se guardan en formato TXT (separados por punto y coma) añadiendo una nueva línea
     * al final del archivo sin borrar el contenido anterior.
     *
     * @param correoUsuario Identificador del jugador.
     * @param aciertos Número total de aciertos.
     * @param fallos Número total de fallos.
     * @param pasapalabras Número total de letras dejadas en "Pasapalabra".
     * @param nivel Nivel de dificultad de la partida.
     */
    public static void guardarDatosPartida(String correoUsuario, int aciertos, int fallos, int pasapalabras, int nivel) {
        String ruta = "src/data/marcadorUsuario.txt";

        // Usamos try-with-resources para asegurar que el PrintWriter se cierre automáticamente.
        // FileWriter(ruta, true) activa el modo 'append' para no sobrescribir el fichero.
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta, true))) {
            pw.println(correoUsuario + ";" + aciertos + ";" + fallos + ";" + pasapalabras + ";" + nivel + ";");
        } catch (IOException e) {
            System.out.println("\u001B[31mError al guardar el archivo.\u001B[0m");
        }
    }

    /**
     * Lee el archivo de puntuaciones, calcula el ranking basado en una fórmula de puntuación
     * y muestra el TOP 10 de mejores partidas en la consola.
     * Fórmula de puntuación:
     * <ul>
     *     <Li>5 pts Aciertos</Li>
     *     <Li>1 pts No Respondida</Li>
     *     <Li>0 pts Fallada</Li>
     * </ul>
     * <p>
     * El ordenamiento se realiza mediante el algoritmo de la burbuja.
     */
    public static void mostrarEstadisticas() {
        String ruta = "src/data/marcadorUsuario.txt";

        // Arrays paralelos para almacenar los datos en memoria antes de ordenar.
        // Se asume un límite máximo de 1000 partidas registradas para este ejercicio.
        String[] rankingCorreos = new String[1000];
        int[] rankingPuntos = new int[1000];
        int totalPartidas = 0;

        //FileReader abre el archivo y BufferedReader crea un buffer para lectura
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            System.out.println("\n========== TOP 10 RANKING ==============");

            // Fase 1: Lectura y Carga de datos
            while ((linea = br.readLine()) != null && totalPartidas < 1000) {
                // Separamos la línea: correo;aciertos;fallos;pasapalabras;nivel;
                String[] datos = linea.split(";");

                // Validación básica para evitar errores si hay líneas vacías o mal formadas
                String correo = datos[0];
                int aciertos = Integer.parseInt(datos[1]);
                int pasapalabras = Integer.parseInt(datos[3]);

                // Calculamos la puntuación total para el ranking
                int calculoPuntos = (aciertos * 5) + pasapalabras;

                // Almacenamos en los arrays
                rankingPuntos[totalPartidas] = calculoPuntos;
                rankingCorreos[totalPartidas] = correo;
                totalPartidas++;
            }

            // Fase 2: Ordenamiento (Algoritmo de la Burbuja - Descendente)
            // Ordenamos por puntos de mayor a menor
            for (int i = 0; i < totalPartidas - 1; i++) {
                for (int j = 0; j < totalPartidas - i - 1; j++) {
                    if (rankingPuntos[j] < rankingPuntos[j + 1]) {
                        // Intercambio de puntos
                        int Puntos = rankingPuntos[j];
                        rankingPuntos[j] = rankingPuntos[j + 1];
                        rankingPuntos[j + 1] = Puntos;

                        // Intercambio de correos (para mantener la consistencia)
                        String Correo = rankingCorreos[j];
                        rankingCorreos[j] = rankingCorreos[j + 1];
                        rankingCorreos[j + 1] = Correo;
                    }
                }
            }

            // Fase 3: Visualización (Solo el Top 10 o el total disponible si es menor)
            int partidas;
            if (totalPartidas < 10) {
                partidas = totalPartidas;
            } else {
                partidas = 10;
            }

            for (int i = 0; i < partidas; i++) {
                System.out.println((i + 1) + "º Lugar: " + rankingPuntos[i] + " puntos | " + rankingCorreos[i]);
            }
            System.out.println("=========================================");

        } catch (IOException e) {
            System.out.println("\u001B[31mError al leer el archivo.\u001B[0m");
        } catch (NumberFormatException e) {
            System.out.println("\u001B[31mError: El archivo contiene datos corruptos o no numéricos.\u001B[0m");
        }
    }
}