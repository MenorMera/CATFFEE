package catffee.modelo;

public class Ingrediente {
    private double cantidad;
    private Insumo insumo;

    public Ingrediente(Insumo insumo, double cantidad) {
        this.insumo = insumo;
        this.cantidad = cantidad;
    }

    public double calcularCosto() {
        return this.cantidad * this.insumo.getPrecioCosto();
    }

    public double getCantidad() { return cantidad; }
    public Insumo getInsumo() { return insumo; }
}