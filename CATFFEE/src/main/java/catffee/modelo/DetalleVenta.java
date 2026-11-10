package catffee.modelo;

public class DetalleVenta {
    private int id;
    private int cantidad;
    private double precioUnitario;
    private Producto producto;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
    }

    public double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
}