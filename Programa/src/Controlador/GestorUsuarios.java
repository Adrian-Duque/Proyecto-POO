package Controlador;

import Modelo.Administrador;
import Modelo.Jugador;
import Modelo.Usuario;
import Persistencia.GestorPersistencia;
import java.util.ArrayList;

/**
 * Controlador responsable de la gestión de usuarios.
 * <p>
 * Coordina el registro, inicio y cierre de sesión, validaciones
 * y consultas sobre usuarios. Actúa como intermediario entre la
 * capa de vista y la capa de persistencia.
 * </p>
 *
 * @author Adrián
 * @version 1.1
 */
public class GestorUsuarios {

    /** Implementación de persistencia inyectada por constructor. */
    private GestorPersistencia persistencia;

    /** Lista de usuarios cargada una sola vez al iniciar el gestor. */
    private ArrayList<Usuario> listaUsuarios;

    /** Usuario que ha iniciado sesión actualmente. Null si no hay sesión activa. */
    private Usuario usuarioActual;

    /**
     * Crea un nuevo GestorUsuarios con la implementación de persistencia indicada.
     * Carga la lista de usuarios desde disco una única vez.
     *
     * @param persistencia implementación de {@link GestorPersistencia} a utilizar
     */
    public GestorUsuarios(GestorPersistencia persistencia) {
        this.persistencia = persistencia;
        this.listaUsuarios = persistencia.cargarUsuarios(); // una sola lectura de disco
        this.usuarioActual = null;
    }

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * <p>
     * Busca el username en {@code listaUsuarios} y verifica la contraseña.
     * Si las credenciales son correctas, guarda el usuario en {@code usuarioActual}.
     * </p>
     *
     * @param username   nombre de usuario
     * @param contrasena contraseña en texto plano
     * @return el {@link Usuario} autenticado, o {@code null} si las credenciales son incorrectas
     */
    public Usuario iniciarSesion(String username, String contrasena) {
        for (Usuario u : listaUsuarios) {
            if (u.getUsername().equals(username)) {
                if (u.verificarPassword(contrasena)) {
                    usuarioActual = u;
                    return u;
                }
                // Username encontrado pero contraseña incorrecta: no seguir buscando
                return null;
            }
        }
        return null; // Username no existe
    }

    /**
     * Registra un nuevo jugador si las credenciales superan las validaciones
     * y el username no está ya en uso.
     *
     * @param username   nombre de usuario deseado
     * @param contrasena contraseña deseada
     * @return {@code null} si el registro fue exitoso, o un mensaje de error si falló
     */
    public String registrarUsuario(String username, String contrasena) {
        // Validar formato antes de tocar la lista
        String errorUsername = validarUsername(username);
        if (errorUsername != null) return errorUsername;

        String errorPassword = validarPassword(contrasena);
        if (errorPassword != null) return errorPassword;

        // Comprobar duplicado
        for (Usuario u : listaUsuarios) {
            if (u.getUsername().equals(username)) {
                return "El nombre de usuario ya está en uso.";
            }
        }

        // Jugador es la subclase concreta de Usuario para usuarios normales
        listaUsuarios.add(new Jugador(username, contrasena));
        persistencia.guardarUsuarios(listaUsuarios);
        return null;
    }

    /**
     * Valida el formato del nombre de usuario.
     * <p>
     * Reglas: no vacío, mínimo 3 caracteres, solo letras, números y guiones.
     * </p>
     *
     * @param username nombre de usuario a validar
     * @return {@code null} si es válido, o mensaje de error si no lo es
     */
    private String validarUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "El nombre de usuario no puede estar vacío.";
        }
        if (username.length() < 3) {
            return "El nombre de usuario debe tener al menos 3 caracteres.";
        }
        // Solo letras (mayúsculas y minúsculas), dígitos y guión medio
        if (!username.matches("[a-zA-Z0-9-]+")) {
            return "El nombre de usuario solo puede contener letras, números y guiones.";
        }
        return null;
    }

    /**
     * Valida el formato de la contraseña.
     * <p>
     * Reglas: no vacía, mínimo 8 caracteres, sin punto y coma.
     * El punto y coma está prohibido porque es el separador usado en los
     * ficheros de persistencia; permitirlo rompería la deserialización.
     * </p>
     *
     * @param password contraseña a validar
     * @return {@code null} si es válida, o mensaje de error si no lo es
     */
    private String validarPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "La contraseña no puede estar vacía.";
        }
        if (password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        if (password.contains(";")) {
            return "La contraseña no puede contener el carácter ';'.";
        }
        return null;
    }

    /**
     * Cierra la sesión del usuario actual.
     * Tras llamar a este método, {@link #getUsuarioActual()} devolverá {@code null}.
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Devuelve el usuario que tiene la sesión activa.
     *
     * @return el {@link Usuario} autenticado, o {@code null} si no hay sesión
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Busca un usuario por su nombre de usuario en {@code listaUsuarios}.
     *
     * @param username nombre de usuario a buscar
     * @return el {@link Usuario} encontrado, o {@code null} si no existe
     */
    public Usuario buscarUsuario(String username) {
        for (Usuario u : listaUsuarios) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Comprueba si el usuario con sesión activa es administrador.
     *
     * @return {@code true} si {@code usuarioActual} es instancia de {@link Administrador}
     */
    public boolean esAdministrador() {
        return usuarioActual instanceof Administrador;
    }

    /**
     * Devuelve la lista completa de usuarios registrados en el sistema.
     * Usado principalmente por {@code VentanaAdmin} para mostrar el panel de gestión.
     *
     * @return {@link ArrayList} con todos los {@link Usuario} registrados
     */
    public ArrayList<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }
}