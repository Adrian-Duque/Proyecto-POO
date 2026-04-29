package Modelo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase abstracta que representa un usuario del sistema.
 * <p>
 * Gestiona las credenciales de acceso almacenando la contraseña como hash SHA-256,
 * nunca en texto plano. Las subclases concretas definen el tipo de usuario
 * (normal, administrador, etc.) y pueden ampliar su comportamiento.
 * </p>
 *
 * @author Adrián
 * @version 1.0
 */
public abstract class Usuario {

    /** Identificador único del usuario en el sistema. */
    protected String username;

    /** Hash SHA-256 de la contraseña del usuario, en representación hexadecimal. */
    protected String passwordHash;

    /**
     * Crea un nuevo usuario a partir de credenciales en texto plano.
     * La contraseña se hashea internamente antes de almacenarse.
     *
     * @param username identificador único; mínimo 3 caracteres, solo letras, dígitos y guion bajo
     * @param password contraseña en texto plano; mínimo 8 caracteres, sin punto y coma
     * @throws IllegalArgumentException si el username o la contraseña no cumplen los requisitos
     */
    public Usuario(String username, String password) {
        if (username == null || !username.matches("[a-zA-Z0-9_]{3,}")) {
            throw new IllegalArgumentException(
                    "El username solo puede contener letras, dígitos y _ (mínimo 3 caracteres)."
            );
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres."
            );
        }
        if (password.contains(";")) {
            throw new IllegalArgumentException(
                    "La contraseña no puede contener el carácter ';'."
            );
        }

        this.username = username;
        this.passwordHash = hashear(password);
    }

    /**
     * Crea un usuario a partir de datos ya almacenados, típicamente al cargar desde fichero.
     * <p>
     * Usar {@code yaEsHash = true} cuando el segundo parámetro ya es un hash SHA-256
     * (por ejemplo, al reconstruir el objeto desde {@code usuarios.txt}).
     * Usar {@code yaEsHash = false} si se pasa la contraseña en texto plano y se quiere
     * que el constructor la hashee.
     * </p>
     *
     * @param username      identificador único del usuario
     * @param passwordHash  contraseña en texto plano o hash SHA-256, según {@code yaEsHash}
     * @param yaEsHash      {@code true} si {@code passwordHash} ya está hasheado;
     *                      {@code false} si es texto plano y debe hashearse
     */
    public Usuario(String username, String passwordHash, boolean yaEsHash) {
        this.username = username;
        this.passwordHash = yaEsHash ? passwordHash : hashear(passwordHash);
    }

    /**
     * Convierte una cadena de texto a su representación SHA-256 en hexadecimal.
     * <p>
     * SHA-256 está garantizado en toda JVM estándar, por lo que
     * {@link NoSuchAlgorithmException} nunca debería lanzarse en la práctica.
     * </p>
     *
     * @param password texto a hashear
     * @return cadena hexadecimal de 64 caracteres con el hash SHA-256
     * @throws RuntimeException si SHA-256 no está disponible en la JVM (no debería ocurrir)
     */
    private static String hashear(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }

    /**
     * Devuelve el identificador único del usuario.
     *
     * @return el username de este usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Verifica si la contraseña proporcionada coincide con la almacenada.
     * <p>
     * La comparación se realiza entre hashes SHA-256, nunca entre textos en claro.
     * </p>
     *
     * @param introPassword contraseña en texto plano introducida por el usuario
     * @return {@code true} si la contraseña es correcta; {@code false} en caso contrario
     */
    public boolean verificarPassword(String introPassword) {
        return this.passwordHash.equals(hashear(introPassword));
    }

    /**
     * Serializa los datos del usuario en una línea de texto para su almacenamiento en fichero.
     * <p>
     * Formato: {@code username;passwordHash;esAdmin}
     * </p>
     * <p>
     * Las subclases deben sobreescribir este método si añaden campos adicionales,
     * asegurándose de mantener el separador {@code ;} y el orden de los campos.
     * </p>
     *
     * @return cadena con los datos del usuario listos para escribir en {@code usuarios.txt}
     */
    public String toArchivo() {
        return username + ";" + passwordHash + ";" + false;
    }
}