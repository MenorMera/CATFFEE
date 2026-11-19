package catffee.repositorio;

import catffee.modelo.Insumo;
import catffee.util.ConexionBD;
import catffee.util.Formato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class InsumoRepositorio {

    private static final String ARCHIVO_RESPALDO = "insumos_respaldo.txt";
    private static final String COLUMNAS =
        "id, descripcion, stock_actual, stock_minimo, precio_costo, unidad_compra, unidad_receta, factor";

    public ArrayList<Insumo> listar() {
        ArrayList<Insumo> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM insumo ORDER BY descripcion";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(aInsumo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar insumos: " + e.getMessage());
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
        String sql = "SELECT " + COLUMNAS + " FROM insumo WHERE descripcion = ?";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, descripcion.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return aInsumo(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar insumo: " + e.getMessage());
        }
        return null;
    }

    // false si ya existe un insumo con esa descripción
    public boolean guardar(Insumo nuevo) {
        String sql = "INSERT INTO insumo (descripcion, stock_actual, stock_minimo, precio_costo, "
                   + "unidad_compra, unidad_receta, factor) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevo.getDescripcion().trim());
            ps.setDouble(2, nuevo.getStockActual());
            ps.setDouble(3, nuevo.getStockMinimo());
            ps.setDouble(4, nuevo.getPrecioCosto());
            ps.setString(5, nuevo.getUnidadCompra());
            ps.setString(6, nuevo.getUnidadReceta());
            ps.setDouble(7, nuevo.getFactor());
            ps.executeUpdate();
            exportarRespaldo();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Ya existe un insumo con esa descripción.");
        } catch (SQLException e) {
            System.err.println("Error al guardar insumo: " + e.getMessage());
        }
        return false;
    }

    // Actualiza stock, mínimo y precio. Las unidades y el factor NO se cambian aquí:
    // se administran desde la base para no perder la configuración al editar desde la pantalla.
    public boolean actualizar(String descripcionOriginal, Insumo nuevo) {
        String sql = "UPDATE insumo SET descripcion = ?, stock_actual = ?, stock_minimo = ?, precio_costo = ? "
                   + "WHERE descripcion = ?";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevo.getDescripcion().trim());
            ps.setDouble(2, nuevo.getStockActual());
            ps.setDouble(3, nuevo.getStockMinimo());
            ps.setDouble(4, nuevo.getPrecioCosto());
            ps.setString(5, descripcionOriginal.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) exportarRespaldo();
            return ok;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Ya existe otro insumo con ese nombre.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar insumo: " + e.getMessage());
        }
        return false;
    }

    // Quita primero el insumo de los catálogos de proveedores y luego lo borra.
    // false si está en una receta o en una compra (no se puede borrar).
    public boolean eliminar(String descripcion) {
        String quitarCatalogo = "DELETE pi FROM proveedor_insumo pi "
                              + "JOIN insumo i ON i.id = pi.insumo_id WHERE i.descripcion = ?";
        String borrar = "DELETE FROM insumo WHERE descripcion = ?";
        try (Connection c = ConexionBD.obtenerConexion()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(quitarCatalogo)) {
                    ps.setString(1, descripcion.trim());
                    ps.executeUpdate();
                }
                boolean ok;
                try (PreparedStatement ps = c.prepareStatement(borrar)) {
                    ps.setString(1, descripcion.trim());
                    ok = ps.executeUpdate() > 0;
                }
                c.commit();
                if (ok) exportarRespaldo();
                return ok;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("No se puede eliminar: el insumo está en una receta o en una compra.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar insumo: " + e.getMessage());
        }
        return false;
    }

    private Insumo aInsumo(ResultSet rs) throws SQLException {
        Insumo i = new Insumo(rs.getString("descripcion"), rs.getDouble("stock_actual"),
                              rs.getDouble("stock_minimo"), rs.getDouble("precio_costo"),
                              rs.getString("unidad_compra"), rs.getString("unidad_receta"),
                              rs.getDouble("factor"));
        i.setId(rs.getInt("id"));
        return i;
    }

    // Respaldo en cache/insumos_respaldo.txt: descripcion;stock;minimo;precio;unidadCompra;unidadReceta;factor
    private void exportarRespaldo() {
        ArrayList<String> lineas = new ArrayList<>();
        for (Insumo i : listar()) {
            lineas.add(i.getDescripcion() + ";" + i.getStockActual() + ";" + i.getStockMinimo() + ";"
                     + i.getPrecioCosto() + ";" + i.getUnidadCompra() + ";" + i.getUnidadReceta() + ";"
                     + i.getFactor());
        }
        RespaldoTxt.escribir(ARCHIVO_RESPALDO, lineas);
    }
}