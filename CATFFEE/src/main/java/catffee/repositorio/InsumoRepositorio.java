package catffee.repositorio;

import catffee.controlador.GestorCache;
import catffee.modelo.Insumo;
import catffee.util.Formato;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de Insumo: guarda y lee los insumos del archivo cache/insumos.txt.
 * Formato de cada línea: descripcion;stockActual;stockMinimo;estado;precioCosto
 */
public class InsumoRepositorio {

    private static final String ARCHIVO = "insumos.txt";

    public ArrayList<Insumo> listar() {
        ArrayList<Insumo> lista = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            Insumo i = aInsumo(linea);
            if (i != null) lista.add(i);
        }
        return lista;
    }

    // Busca por texto (sin tildes ni mayúsculas); opcionalmente solo los que hay que reponer
    public ArrayList<Insumo> buscar(String texto, boolean soloReponer) {
        ArrayList<Insumo> resultado = new ArrayList<>();
        String buscado = Formato.normalizar(texto);
        for (Insumo i : listar()) {
            boolean coincide = Formato.normalizar(i.getDescripcion()).contains(buscado);
            if (coincide && (!soloReponer || i.necesitaReposicion())) {
                resultado.add(i);
            }
        }
        return resultado;
    }

    public Insumo buscarPorDescripcion(String descripcion) {
        for (Insumo i : listar()) {
            if (i.getDescripcion().equalsIgnoreCase(descripcion.trim())) return i;
        }
        return null;
    }

    // false si ya existe un insumo con esa descripción
    public boolean guardar(Insumo nuevo) {
        if (buscarPorDescripcion(nuevo.getDescripcion()) != null) return false;
        GestorCache.guardarRegistro(ARCHIVO, aLinea(nuevo));
        return true;
    }

    // Reemplaza el insumo 'original' por 'nuevo'. false si no existe o el nuevo nombre ya lo usa otro.
    public boolean actualizar(String descripcionOriginal, Insumo nuevo) {
        Insumo otro = buscarPorDescripcion(nuevo.getDescripcion());
        boolean mismoInsumo = otro != null && otro.getDescripcion().equalsIgnoreCase(descripcionOriginal);
        if (otro != null && !mismoInsumo) return false;

        List<String> lineas = new ArrayList<>();
        boolean encontrado = false;
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            if (linea.split(";")[0].equalsIgnoreCase(descripcionOriginal)) {
                lineas.add(aLinea(nuevo));
                encontrado = true;
            } else {
                lineas.add(linea);
            }
        }
        if (encontrado) GestorCache.sobrescribirRegistros(ARCHIVO, lineas);
        return encontrado;
    }

    public boolean eliminar(String descripcion) {
        List<String> lineas = new ArrayList<>();
        boolean encontrado = false;
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            if (linea.split(";")[0].equalsIgnoreCase(descripcion)) {
                encontrado = true;
            } else {
                lineas.add(linea);
            }
        }
        if (encontrado) GestorCache.sobrescribirRegistros(ARCHIVO, lineas);
        return encontrado;
    }

    // ---------- conversión línea <-> objeto ----------

    private static Insumo aInsumo(String linea) {
        String[] p = linea.split(";");
        if (p.length < 4) return null;
        try {
            double costo = p.length >= 5 ? Formato.aNumero(p[4]) : 0;
            return new Insumo(p[0], Formato.aNumero(p[1]), Formato.aNumero(p[2]), costo);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String aLinea(Insumo i) {
        return String.join(";",
                Formato.limpiar(i.getDescripcion()),
                Formato.numero(i.getStockActual()),
                Formato.numero(i.getStockMinimo()),
                i.necesitaReposicion() ? "REPONER" : "OK",
                Formato.numero(i.getPrecioCosto()));
    }
}
