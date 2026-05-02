package Modelo;

/**
 * Representa un usuario estándar del sistema sin privilegios de administración.
 * <p>
 * Extiende {@link Usuario} y constituye el tipo de cuenta habitual con el que
 * los jugadores se registran, inician sesión y participan en las partidas.
 * Su campo {@code esAdmin} se serializa siempre como {@code false}.
 * </p>
 *
 * @author Adrián
 * @version 1.0
 */
public class Jugador extends Usuario {

    /**
     * Crea un nuevo jugador con las credenciales indicadas.
     *
     * @param username nombre de usuario único; no debe estar vacío ni contener espacios.
     * @param password contraseña en texto plano asociada a esta cuenta.
     */
    public Jugador(String username, String password) {
        super(username, password);
    }

    /**
     * Devuelve una representación legible del jugador para depuración y logs.
     *
     * @return cadena con el formato {@code Jugador{username='<username>'}}
     */
    @Override
    public String toString() {
        return "Jugador{" +
                "username='" + username + '\'' +
                '}';
    }

    /**
     * Serializa el jugador en una línea CSV para su almacenamiento en fichero.
     * <p>
     * El campo {@code esAdmin} se escribe como {@code false} para distinguirlo
     * de los registros de {@link Administrador} al cargar el fichero.
     * </p>
     *
     * @return cadena con el formato {@code username;passwordHash;false}
     */
    @Override
    public String toArchivo() {
        return username + ";" + passwordHash + ";" + false;
    }
}