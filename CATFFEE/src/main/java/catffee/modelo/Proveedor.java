package catffee.modelo;

import java.util.ArrayList;

public class Proveedor {
    private int id;
    private String nombre;
    private String contacto;
    private ArrayList<Insumo> insumosQueOfrece;

    public Proveedor(String nombre, String contacto) {
        this.nombre = nombre;
        this.contacto = contacto;
        this.insumosQueOfrece = new ArrayList<>();
    }

    public void agregarAlCatalogo(Insumo insumo) {
        this.insumosQueOfrece.add(insumo);
    }
}