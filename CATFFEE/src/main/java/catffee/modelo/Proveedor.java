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

    public boolean ofrece(Insumo insumo) { return insumosQueOfrece.contains(insumo); }
    
    // Un insumo no se repite en el catálogo del proveedor
    public void agregarAlCatalogo(Insumo insumo) {
        if (!insumosQueOfrece.contains(insumo)) {
            this.insumosQueOfrece.add(insumo);
        }
    }

    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public ArrayList<Insumo> getInsumosQueOfrece() { return insumosQueOfrece; }
}