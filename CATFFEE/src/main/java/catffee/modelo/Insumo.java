package catffee.modelo;

public class Insumo {
    private int id;
    private String descripcion;
    private double stockActual;
    private double stockMinimo;
    private double precioCosto;

    public Insumo(String descripcion, double stockActual, double stockMinimo, double precioCosto) {
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precioCosto = precioCosto;
    }

    public boolean descontarStock(double cantidad) {
        if (cantidad > 0 && cantidad <= this.stockActual) {
            this.stockActual -= cantidad;
            return true;
        }
        return false;
    }

    public boolean aumentarStock(double cantidad) {
        if (cantidad > 0) {
            this.stockActual += cantidad;
            return true;
        }
        return false;
    }

    public boolean necesitaReposicion() {
        return this.stockActual <= this.stockMinimo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public double getStockActual() { return stockActual; }
    public double getStockMinimo() { return stockMinimo; }
    public double getPrecioCosto() { return precioCosto; }

    // Dos insumos son el mismo si tienen la misma descripción (sin importar mayúsculas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insumo)) return false;
        return descripcion.equalsIgnoreCase(((Insumo) o).descripcion);
    }

    @Override
    public int hashCode() { return descripcion.toLowerCase().hashCode(); }
}
