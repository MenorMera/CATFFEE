package catffee.repositorio;

import catffee.controlador.GestorCache;
import catffee.modelo.Ingrediente;
import catffee.modelo.Insumo;
import catffee.modelo.Producto;
import catffee.util.Formato;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de Producto y de su receta (ingredientes).
 *  - cache/productos.txt -> nombre;precioVenta
 *  - cache/recetas.txt   -> producto;insumo;cantidad   (una línea por ingrediente)
 */
public class ProductoRepositorio {

    private static final String ARCHIVO = "productos.txt";
    private static final String RECETAS = "recetas.txt";

    private final InsumoRepositorio insumos = new InsumoRepositorio();

    // Si todavía no hay productos, crea los de ejemplo (sin receta)
    public void sembrarSiHaceFalta() {
        if (GestorCache.leerRegistros(ARCHIVO).isEmpty()) {
            guardar(new Producto("Capuchino", 8500));
            guardar(new Producto("Cheesecake", 9000));
            guardar(new Producto("Latte Macchiato", 7500));
            guardar(new Producto("Café Americano", 5000));
            guardar(new Producto("Tarta de Manzana", 8000));
        }
    }

    // Todos los productos, cada uno con su receta cargada
    public ArrayList<Producto> listar() {
        ArrayList<Producto> lista = new ArrayList<>();
        // Un único objeto Insumo por insumo: así, si dos productos usan leche, comparten
        // el mismo objeto y el stock que se descuente se refleja en ambos.
        Map<String, Insumo> catalogo = new HashMap<>();
        for (Insumo i : insumos.listar()) {
            catalogo.put(i.getDescripcion().toLowerCase(), i);
        }
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            String[] p = linea.split(";");
            if (p.length < 2) continue;
            try {
                Producto prod = new Producto(p[0], Formato.aNumero(p[1]));
                cargarReceta(prod, catalogo);
                lista.add(prod);
            } catch (NumberFormatException e) {
                // línea dañada: se ignora
            }
        }
        return lista;
    }

    // Productos cuyo nombre contiene el texto (sin tildes ni mayúsculas). Texto vacío = todos.
    public ArrayList<Producto> buscar(String texto) {
        ArrayList<Producto> resultado = new ArrayList<>();
        String buscado = Formato.normalizar(texto);
        for (Producto p : listar()) {
            if (Formato.normalizar(p.getNombre()).contains(buscado)) resultado.add(p);
        }
        return resultado;
    }

    public Producto obtener(String nombre) {
        for (Producto p : listar()) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) return p;
        }
        return null;
    }

    // false si ya existe un producto con ese nombre
    public boolean guardar(Producto nuevo) {
        if (obtener(nuevo.getNombre()) != null) return false;
        GestorCache.guardarRegistro(ARCHIVO, aLinea(nuevo));
        for (String linea : lineasDeReceta(nuevo)) {
            GestorCache.guardarRegistro(RECETAS, linea);
        }
        return true;
    }

    // Reemplaza el producto (y su receta). false si no existe o el nuevo nombre ya lo usa otro.
    public boolean actualizar(String nombreOriginal, Producto nuevo) {
        Producto otro = obtener(nuevo.getNombre());
        boolean mismo = otro != null && otro.getNombre().equalsIgnoreCase(nombreOriginal);
        if (otro != null && !mismo) return false;

        List<String> lineas = new ArrayList<>();
        boolean encontrado = false;
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            if (linea.split(";")[0].equalsIgnoreCase(nombreOriginal)) {
                lineas.add(aLinea(nuevo));
                encontrado = true;
            } else {
                lineas.add(linea);
            }
        }
        if (!encontrado) return false;
        GestorCache.sobrescribirRegistros(ARCHIVO, lineas);

        List<String> receta = recetasSin(nombreOriginal);
        receta.addAll(lineasDeReceta(nuevo));
        GestorCache.sobrescribirRegistros(RECETAS, receta);
        return true;
    }

    public boolean eliminar(String nombre) {
        List<String> lineas = new ArrayList<>();
        boolean encontrado = false;
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            if (linea.split(";")[0].equalsIgnoreCase(nombre)) {
                encontrado = true;
            } else {
                lineas.add(linea);
            }
        }
        if (!encontrado) return false;
        GestorCache.sobrescribirRegistros(ARCHIVO, lineas);
        GestorCache.sobrescribirRegistros(RECETAS, recetasSin(nombre));
        return true;
    }

    // Nombres de los productos que usan ese insumo en su receta
    public ArrayList<String> productosQueUsan(String insumo) {
        ArrayList<String> nombres = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(RECETAS)) {
            String[] p = linea.split(";");
            if (p.length >= 3 && p[1].equalsIgnoreCase(insumo) && !nombres.contains(p[0])) {
                nombres.add(p[0]);
            }
        }
        return nombres;
    }

    // Cuando se cambia el nombre de un insumo, las recetas lo siguen
    public void renombrarInsumo(String viejo, String nuevo) {
        List<String> lineas = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(RECETAS)) {
            String[] p = linea.split(";");
            if (p.length >= 3 && p[1].equalsIgnoreCase(viejo)) {
                lineas.add(p[0] + ";" + nuevo + ";" + p[2]);
            } else {
                lineas.add(linea);
            }
        }
        GestorCache.sobrescribirRegistros(RECETAS, lineas);
    }

    // ---------- auxiliares ----------

    private void cargarReceta(Producto prod, Map<String, Insumo> catalogo) {
        for (String linea : GestorCache.leerRegistros(RECETAS)) {
            String[] p = linea.split(";");
            if (p.length >= 3 && p[0].equalsIgnoreCase(prod.getNombre())) {
                Insumo insumo = catalogo.get(p[1].trim().toLowerCase());
                if (insumo == null) continue;   // el insumo ya no existe
                try {
                    prod.agregarIngrediente(insumo, Formato.aNumero(p[2]));
                } catch (NumberFormatException e) {
                    // línea dañada: se ignora
                }
            }
        }
    }

    private List<String> lineasDeReceta(Producto prod) {
        List<String> lineas = new ArrayList<>();
        for (Ingrediente ing : prod.getIngredientes()) {
            lineas.add(Formato.limpiar(prod.getNombre()) + ";"
                    + Formato.limpiar(ing.getInsumo().getDescripcion()) + ";"
                    + Formato.numero(ing.getCantidad()));
        }
        return lineas;
    }

    private List<String> recetasSin(String nombreProducto) {
        List<String> lineas = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(RECETAS)) {
            if (!linea.split(";")[0].equalsIgnoreCase(nombreProducto)) lineas.add(linea);
        }
        return lineas;
    }

    private static String aLinea(Producto p) {
        return Formato.limpiar(p.getNombre()) + ";" + Formato.numero(p.getPrecioVenta());
    }
}
