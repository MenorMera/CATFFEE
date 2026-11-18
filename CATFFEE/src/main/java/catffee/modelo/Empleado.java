package catffee.modelo;

import catffee.util.Seguridad;

public class Empleado {
    protected int id;
    protected String nombre;
    protected String usuario;
    protected String contrasenaHash;
    protected boolean activo;

    public Empleado(String nombre, String usuario, String contrasena) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenaHash = Seguridad.hashSHA256(contrasena);
        this.activo = true;
    }

    public boolean verificarContrasena(String contrasena) {
        return this.contrasenaHash.equals(Seguridad.hashSHA256(contrasena));
    }

    public boolean esAdministrador() {
        return false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}