package catffee.repositorio;

import catffee.controlador.GestorCache;
import catffee.modelo.Insumo;
import catffee.modelo.Proveedor;
import catffee.util.Formato;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de Proveedor y de su catálogo de insumos.
 *  - cache/proveedores.txt           -> nombre;telefono
 *  - cache/catalogo_proveedores.txt  -> proveedor;insumo   (un proveedor ofrece muchos insumos
 *                                                          y un insumo lo ofrecen muchos proveedores)
 */
public class ProveedorRepositorio {

    private static final String ARCHIVO = "proveedores.txt";
    private static final String CATALOGO = "catalogo_proveedores.txt";

    private final InsumoRepositorio insumos = new InsumoRepositorio();

    // Devuelve los proveedores con su catálogo de insumos ya cargado
    public ArrayList<Proveedor> listar() {
        ArrayList<Proveedor> lista = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            String[] p = linea.split(";");
            if (p.length < 2) continue;
            Proveedor prov = new Proveedor(p[0], p[1]);
            for (String descripcion : insumosDe(p[0])) {
                Insumo i = insumos.buscarPorDescripcion(descripcion);
                if (i != null) prov.agregarAlCatalogo(i);
            }
            lista.add(prov);
        }
        return lista;
    }

    public ArrayList<String> nombres() {
        ArrayList<String> nombres = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(ARCHIVO)) {
            String[] p = linea.split(";");
            if (p.length >= 2) nombres.add(p[0]);
        }
        return nombres;
    }

    // false si ya existe un proveedor con ese nombre
    public boolean guardar(Proveedor nuevo) {
        for (String nombre : nombres()) {
            if (nombre.equalsIgnoreCase(nuevo.getNombre())) return false;
        }
        GestorCache.guardarRegistro(ARCHIVO,
                Formato.limpiar(nuevo.getNombre()) + ";" + Formato.limpiar(nuevo.getContacto()));
        return true;
    }

    // ---------- Catálogo ----------

    public ArrayList<String> insumosDe(String proveedor) {
        ArrayList<String> lista = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(CATALOGO)) {
            String[] p = linea.split(";");
            if (p.length >= 2 && p[0].equalsIgnoreCase(proveedor)) lista.add(p[1]);
        }
        return lista;
    }

    public ArrayList<String> proveedoresDe(String insumo) {
        ArrayList<String> lista = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(CATALOGO)) {
            String[] p = linea.split(";");
            if (p.length >= 2 && p[1].equalsIgnoreCase(insumo)) lista.add(p[0]);
        }
        return lista;
    }

    // false si ya estaba en el catálogo
    public boolean agregarAlCatalogo(String proveedor, String insumo) {
        for (String linea : GestorCache.leerRegistros(CATALOGO)) {
            String[] p = linea.split(";");
            if (p.length >= 2 && p[0].equalsIgnoreCase(proveedor) && p[1].equalsIgnoreCase(insumo)) {
                return false;
            }
        }
        GestorCache.guardarRegistro(CATALOGO, proveedor + ";" + insumo);
        return true;
    }

    // false si ese proveedor no ofrecía ese insumo
    public boolean quitarDelCatalogo(String proveedor, String insumo) {
        List<String> lineas = new ArrayList<>();
        boolean encontrado = false;
        for (String linea : GestorCache.leerRegistros(CATALOGO)) {
            String[] p = linea.split(";");
            if (p.length >= 2 && p[0].equalsIgnoreCase(proveedor) && p[1].equalsIgnoreCase(insumo)) {
                encontrado = true;
            } else {
                lineas.add(linea);
            }
        }
        if (encontrado) GestorCache.sobrescribirRegistros(CATALOGO, lineas);
        return encontrado;
    }

    // Cuando se elimina un insumo, desaparece de todos los catálogos
    public void quitarInsumo(String insumo) {
        List<String> lineas = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(CATALOGO)) {
            String[] p = linea.split(";");
            if (!(p.length >= 2 && p[1].equalsIgnoreCase(insumo))) lineas.add(linea);
        }
        GestorCache.sobrescribirRegistros(CATALOGO, lineas);
    }

    // Cuando se cambia el nombre de un insumo, el catálogo lo sigue
    public void renombrarInsumo(String viejo, String nuevo) {
        List<String> lineas = new ArrayList<>();
        for (String linea : GestorCache.leerRegistros(CATALOGO)) {
            String[] p = linea.split(";");
            if (p.length >= 2 && p[1].equalsIgnoreCase(viejo)) {
                lineas.add(p[0] + ";" + nuevo);
            } else {
                lineas.add(linea);
            }
        }
        GestorCache.sobrescribirRegistros(CATALOGO, lineas);
    }
}
