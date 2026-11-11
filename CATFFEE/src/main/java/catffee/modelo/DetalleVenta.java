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

    public String obtenerInformacion() {
        return producto.getNombre() + " x" + cantidad
                + " @ " + precioUnitario + " = " + calcularSubtotal();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}