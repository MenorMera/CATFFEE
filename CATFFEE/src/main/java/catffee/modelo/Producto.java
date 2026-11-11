package catffee.modelo;

import java.util.ArrayList;

public class Producto {
    private int id;
    private String nombre;
    private double precioVenta;
    private boolean activo;
    private ArrayList<Ingrediente> ingredientes;

    public Producto(String nombre, double precioVenta) {
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.activo = true;
        this.ingredientes = new ArrayList<>();
    }

    public void agregarIngrediente(Insumo insumo, double cantidad) {
        this.ingredientes.add(new Ingrediente(insumo, cantidad));
    }

    public boolean hayExistencias(int cantidadVendida) {
        for (Ingrediente ing : ingredientes) {
            double necesario = ing.getCantidad() * cantidadVendida;
            if (necesario > ing.getInsumo().getStockActual()) {
                return false;
            }
        }
        return true;
    }

    public boolean descontarInsumos(int cantidadVendida) {
        if (!hayExistencias(cantidadVendida)) return false;
        for (Ingrediente ing : ingredientes) {
            ing.getInsumo().descontarStock(ing.getCantidad() * cantidadVendida);
        }
        return true;
    }

    public double calcularCosto() {
    double costo = 0;
    for (Ingrediente ing : ingredientes) costo += ing.calcularCosto();
    return costo;
}
public double calcularUtilidad() { return precioVenta - calcularCosto(); }

    public String getNombre() { return nombre; }
    public double getPrecioVenta() { return precioVenta; }
    public ArrayList<Ingrediente> getIngredientes() { return ingredientes; }
}