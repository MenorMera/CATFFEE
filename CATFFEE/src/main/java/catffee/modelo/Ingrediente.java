package catffee.modelo;

public class Ingrediente {
    private double cantidad;        // en la unidad de receta (g, ml o unidad)
    private Insumo insumo;

    public Ingrediente(Insumo insumo, double cantidad) {
        this.insumo = insumo;
        this.cantidad = cantidad;
    }

    // El precio está por unidad de compra, así que la cantidad se convierte primero
    public double calcularCosto() {
        return this.insumo.aUnidadCompra(this.cantidad) * this.insumo.getPrecioCosto();
    }

    public double getCantidad() { return cantidad; }
    public Insumo getInsumo() { return insumo; }
}