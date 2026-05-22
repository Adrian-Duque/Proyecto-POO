package Modelo;

/**
 * Representa un usuario con privilegios de administración en el sistema.
 * <p>
 * Extiende {@link Usuario} sin añadir atributos adicionales; la distinción
 * respecto a un usuario normal se establece mediante el tipo de objeto
 * (polimorfismo). Un {@code Administrador} tiene acceso al panel de gestión
 * de usuarios y a las estadísticas globales del sistema.
 * </p>
 *
 * @author Adrián
 * @version 1.0
 */
public class Administrador extends Usuario {

    /**
     * Crea un nuevo administrador con las credenciales indicadas.
     *
     * @param username nombre de usuario único; no debe estar vacío ni contener espacios.
     * @param password contraseña en texto plano asociada a esta cuenta.
     */
    public Administrador(String username, String password, int añoNacimiento) {
        super(username, password, añoNacimiento);
    }

    /**
     * Constructor de carga para reconstruir un {@code Administrador} desde fichero.
     *
     * <p>Usar exclusivamente en cargarUsuarios() cuando
     * el hash de la contraseña ya está almacenado en disco. Pasar {@code true} en
     * {@code yaEsHash} evita que la contraseña sea hasheada una segunda vez,
     * lo que corrompería las credenciales y haría imposible iniciar sesión.</p>
     *
     * @param username   nombre de usuario tal como aparece en el fichero
     * @param passwordHash hash SHA-256 de la contraseña, leído directamente del fichero
     * @param yaEsHash   {@code true} si {@code passwordHash} ya es un hash y no debe
     *                   procesarse; {@code false} si es texto plano y debe hashearse
     * @see Usuario#Usuario(String, String, boolean)
     */
    public Administrador(String username, String passwordHash, boolean yaEsHash, int añoNacimiento) {
        super(username, passwordHash, añoNacimiento, yaEsHash);
    }

    /**
     * Devuelve una representación legible del administrador para depuración y logs.
     *
     * @return cadena con el formato {@code Administrador{username='<username>'}}
     */
    @Override
    public String toString() {
        return "Administrador{" +
                "username='" + username + '\'' +
                '}';
    }

    /**
     * Serializa el administrador en una línea CSV para su almacenamiento en fichero.
     * <p>
     * El campo {@code esAdmin} se escribe como {@code true} para distinguir
     * este registro de los usuarios normales al cargar el fichero.
     * </p>
     *
     * @return cadena con el formato {@code username;password;true}
     */
    @Override
    public String toArchivo() {
        return username + ";" + passwordHash + ";" + añoNacimiento + ";" + true;
    }
}