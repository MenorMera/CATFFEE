package catffee.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Compra {
    private int id;
    private LocalDateTime fecha;
    private Administrador administrador;
    private Proveedor proveedor;
    private ArrayList<DetalleCompra> detalles;
    private double total;
    private boolean confirmada;

    public Compra(Administrador administrador, Proveedor proveedor) {
        this.administrador = administrador;
        this.proveedor = proveedor;
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.confirmada = false;
    }

    public boolean agregarInsumo(Insumo insumo, double cantidad, double precioUnitario) {
        if (confirmada) return false;
        detalles.add(new DetalleCompra(insumo, cantidad, precioUnitario));
        calcularTotal();
        return true;
    }

    public double calcularTotal() {
        double suma = 0;
        for (DetalleCompra d : detalles) {
            suma += d.calcularSubtotal();
        }
        this.total = suma;
        return this.total;
    }

    public boolean confirmar() {
        if (confirmada || detalles.isEmpty()) return false;
        for (DetalleCompra d : detalles) {
            d.getInsumo().aumentarStock(d.getCantidad());
        }
        this.confirmada = true;
        return true;
    }
}