package catffee.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private Cliente cliente;
    private Vendedor vendedor;
    private ArrayList<DetalleVenta> detalles;
    private double total;
    private boolean confirmada;

    public Venta(Cliente cliente, Vendedor vendedor) {
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.confirmada = false;
    }

    public boolean agregarProducto(Producto producto, int cantidad) {
        if (confirmada || cantidad <= 0) return false;
        if (!producto.hayExistencias(cantidad)) return false;

        detalles.add(new DetalleVenta(producto, cantidad));
        calcularTotal();
        return true;
    }

    public double calcularTotal() {
        double suma = 0;
        for (DetalleVenta d : detalles) {
            suma += d.calcularSubtotal();
        }
        this.total = suma;
        return this.total;
    }

    public boolean confirmar() {
        if (confirmada || detalles.isEmpty()) return false;
        for (DetalleVenta d : detalles) {
            if (!d.getProducto().descontarInsumos(d.getCantidad())) {
                return false;
            }
        }
        this.confirmada = true;
        return true;
    }

    public ArrayList<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal() { return total; }
}