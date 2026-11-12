package catffee.repositorio;

import catffee.controlador.GestorCache;
import catffee.modelo.Compra;
import catffee.modelo.DetalleCompra;
import catffee.util.Formato;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Repositorio de Compra: guarda las compras confirmadas en cache/compras.txt.
 * Una línea por compra: fecha;proveedor;empleado;total;detalle
 */
public class CompraRepositorio {

    private static final String ARCHIVO = "compras.txt";

    public void guardar(Compra compra) {
        StringBuilder detalle = new StringBuilder();
        for (DetalleCompra d : compra.getDetalles()) {
            if (detalle.length() > 0) detalle.append(" | ");
            detalle.append(Formato.limpiar(d.getInsumo().getDescripcion()))
                   .append(" x").append(Formato.numero(d.getCantidad()))
                   .append(" @").append(Formato.numero(d.getPrecioUnitario()));
        }
        GestorCache.guardarRegistro(ARCHIVO, String.join(";",
                compra.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                Formato.limpiar(compra.getProveedor().getNombre()),
                Formato.limpiar(compra.getAdministrador().getNombre()),
                Formato.numero(compra.getTotal()),
                detalle.toString()));
    }

    // Las compras guardadas, una línea de texto por compra
    public ArrayList<String> listar() {
        return new ArrayList<>(GestorCache.leerRegistros(ARCHIVO));
    }
}