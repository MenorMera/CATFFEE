package catffee.modelo;

public class Vendedor extends Empleado {
    public Vendedor(String nombre, String usuario, String contrasena) {
        super(nombre, usuario, contrasena);
    }

    @Override
    public boolean esAdministrador() {
        return false;
    }
}