package catffee.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private Cliente cliente;      // opcional (0..1): puede ser null
    private Vendedor vendedor;    // obligatorio (1)
    private ArrayList<DetalleVenta> detalles;
    private double total;
    private boolean confirmada;

    public Venta(Cliente cliente, Vendedor vendedor) {
        if (vendedor == null) {
            throw new IllegalArgumentException("Una venta necesita un vendedor");
        }
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.total = 0;
        this.confirmada = false;
    }

    // ---------- Carrito ----------

    public boolean agregarProducto(Producto producto, int cantidad) {
        if (confirmada || producto == null || cantidad <= 0) return false;

        DetalleVenta existente = buscarDetalle(producto);

        if (existente != null) {
            // El producto ya está en el carrito: se suma la cantidad
            existente.setCantidad(existente.getCantidad() + cantidad);
            if (!alcanzanLosInsumos()) {
                existente.setCantidad(existente.getCantidad() - cantidad); // se deshace
                return false;
            }
        } else {
            DetalleVenta nuevo = new DetalleVenta(producto, cantidad);
            detalles.add(nuevo);
            if (!alcanzanLosInsumos()) {
                detalles.remove(nuevo); // se deshace
                return false;
            }
        }

        calcularTotal();
        return true;
    }

    public void quitarDetalle(int indice) {
        if (confirmada) return;
        if (indice < 0 || indice >= detalles.size()) return;
        detalles.remove(indice);
        calcularTotal();
    }

    public double calcularTotal() {
        double suma = 0;
        for (DetalleVenta d : detalles) {
            suma += d.calcularSubtotal();
        }
        this.total = suma;
        return this.total;
    }

    // ---------- Confirmación ----------

    public boolean confirmar() {
        if (confirmada || detalles.isEmpty()) return false;

        // 1) Verificar TODO antes de tocar el inventario
        if (!alcanzanLosInsumos()) return false;

        // 2) Descontar (ya se sabe que alcanza para todos)
        for (DetalleVenta d : detalles) {
            d.getProducto().descontarInsumos(d.getCantidad());
        }

        // 3) Cerrar la venta
        calcularTotal();
        this.confirmada = true;
        return true;
    }

    public String generarComprobante() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("===== CATFFEE - COMPROBANTE DE VENTA =====\n");
        sb.append("Fecha: ").append(fecha.format(formato)).append("\n");
        sb.append("Vendedor: ").append(vendedor.getNombre()).append("\n");
        sb.append("Cliente: ")
          .append(cliente != null ? cliente.obtenerInformacion() : "Sin cliente")
          .append("\n");
        sb.append("------------------------------------------\n");
        for (DetalleVenta d : detalles) {
            sb.append(d.obtenerInformacion()).append("\n");
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format("TOTAL: $%.2f%n", calcularTotal()));
        sb.append("Estado: ").append(confirmada ? "CONFIRMADA" : "PENDIENTE");
        return sb.toString();
    }

    // ---------- Métodos auxiliares (privados, no van en el diagrama) ----------

    private DetalleVenta buscarDetalle(Producto producto) {
        for (DetalleVenta d : detalles) {
            if (d.getProducto() == producto) return d;
        }
        return null;
    }

    // Suma lo que necesita TODO el carrito, insumo por insumo
    private Map<Insumo, Double> insumosNecesarios() {
        Map<Insumo, Double> necesarios = new LinkedHashMap<>();
        for (DetalleVenta d : detalles) {
            for (Ingrediente ing : d.getProducto().getIngredientes()) {
                double cantidad = ing.getCantidad() * d.getCantidad();
                necesarios.merge(ing.getInsumo(), cantidad, Double::sum);
            }
        }
        return necesarios;
    }

    private boolean alcanzanLosInsumos() {
        for (Map.Entry<Insumo, Double> e : insumosNecesarios().entrySet()) {
            if (e.getValue() > e.getKey().getStockActual()) return false;
        }
        return true;
    }

    // ---------- Getters / setters ----------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public Vendedor getVendedor() { return vendedor; }
    public ArrayList<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal() { return total; }
    public boolean isConfirmada() { return confirmada; }
}