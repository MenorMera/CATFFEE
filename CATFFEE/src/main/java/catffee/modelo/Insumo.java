package catffee.modelo;

public class Insumo {
    private int id;
    private String descripcion;
    private double stockActual;     // en la unidad de compra (kg, L o unidad)
    private double stockMinimo;     // en la unidad de compra
    private double precioCosto;     // precio por unidad de compra
    private String unidadCompra = "unidad";
    private String unidadReceta = "unidad";
    private double factor = 1;      // cuántas unidades de receta caben en una de compra (1 kg = 1000 g)

    public Insumo(String descripcion, double stockActual, double stockMinimo, double precioCosto) {
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precioCosto = precioCosto;
    }

    public Insumo(String descripcion, double stockActual, double stockMinimo, double precioCosto,
                  String unidadCompra, String unidadReceta, double factor) {
        this(descripcion, stockActual, stockMinimo, precioCosto);
        this.unidadCompra = unidadCompra;
        this.unidadReceta = unidadReceta;
        this.factor = factor;
    }

    // Convierte una cantidad de la receta (g, ml o unidad) a la unidad de compra (kg, L o unidad)
    // Ejemplo: 18 g con factor 1000 -> 0.018 kg
    public double aUnidadCompra(double cantidadReceta) {
        return factor > 0 ? cantidadReceta / factor : cantidadReceta;
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
    public String getUnidadCompra() { return unidadCompra; }
    public void setUnidadCompra(String unidadCompra) { this.unidadCompra = unidadCompra; }
    public String getUnidadReceta() { return unidadReceta; }
    public void setUnidadReceta(String unidadReceta) { this.unidadReceta = unidadReceta; }
    public double getFactor() { return factor; }
    public void setFactor(double factor) { this.factor = factor; }

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