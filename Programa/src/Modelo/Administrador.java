package Modelo;

public class Administrador extends Usuario{
    public Administrador(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "username='" + username + '\'' +
                '}';
    }

    @Override
    public String toArchivo() {
        return super.toArchivo();
    }
}
