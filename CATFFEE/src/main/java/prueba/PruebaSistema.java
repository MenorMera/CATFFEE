package prueba;

import catffee.modelo.*;

public class PruebaSistema {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE CATFFEE ===");
        Insumo cafe = new Insumo("Café", 1000.0, 100.0, 50.0);
        System.out.println("Insumo creado con éxito: " + cafe.getDescripcion());
    }
}