package catffee.modelo;

public class DetalleCompra {
    private int id;
    private double cantidad;
    private double precioUnitario;
    private Insumo insumo;

    public DetalleCompra(Insumo insumo, double cantidad, double precioUnitario) {
        this.insumo = insumo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;
    }

    public Insumo getInsumo() { return insumo; }
    public double getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}