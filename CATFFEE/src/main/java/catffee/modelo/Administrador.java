package catffee.modelo;

public class Administrador extends Empleado {
    private boolean accesoTotal;

    public Administrador(String nombre, String usuario, String contrasena) {
        super(nombre, usuario, contrasena);
        this.accesoTotal = true;
    }

    public boolean isAccesoTotal() { return accesoTotal; }
    public void setAccesoTotal(boolean accesoTotal) { this.accesoTotal = accesoTotal; }

    @Override
    public boolean esAdministrador() {
        return true;
    }
}