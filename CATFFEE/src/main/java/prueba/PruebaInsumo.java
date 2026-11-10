package prueba;

import catffee.modelo.Insumo;

public class PruebaInsumo {
    
    public static void main(String[] args) {
        System.out.println("=== ☕ INICIANDO PRUEBAS DE INSUMO - CATFFEE ===");

        Insumo cafe = new Insumo("Café en grano premium", 5.0, 2.0, 45000.0);
        cafe.setId(1);

        System.out.println("\n--- Datos Iniciales del Insumo ---");
        System.out.println("ID: " + cafe.getId());
        System.out.println("Descripción: " + cafe.getDescripcion());
        System.out.println("Stock Actual: " + cafe.getStockActual() + " kg");
        System.out.println("Stock Mínimo: " + cafe.getStockMinimo() + " kg");
<<<<<<< OURS
        System.out.println("Precio de Costo: $" + cafe.getPrecioCosto());

        System.out.println("\n--- Probando aumentarStock(3.5 kg) ---");
        boolean aumento = cafe.aumentarStock(3.5);
        System.out.println("¿Se pudo aumentar?: " + aumento);
        System.out.println("Nuevo Stock Actual: " + cafe.getStockActual() + " kg");

        System.out.println("\n--- Probando descontarStock(4.0 kg) ---");
        boolean descuento = cafe.descontarStock(4.0);
        System.out.println("¿Se pudo descontar?: " + descuento);
        System.out.println("Stock Actual tras la venta: " + cafe.getStockActual() + " kg");

        System.out.println("\n--- Probando descuento excesivo (10.0 kg) ---");
        boolean descuentoFallido = cafe.descontarStock(10.0);
        System.out.println("¿Se pudo descontar?: " + descuentoFallido); 
        System.out.println("Stock Actual actual: " + cafe.getStockActual() + " kg");

        System.out.println("\n--- Probando needReponer() ---");
        System.out.println("¿Necesita reposición actualmente?: " + cafe.necesitaReposicion()); 

        System.out.println("\n--- Descontando más stock para activar la alerta ---");
        cafe.descontarStock(3.0); 
        System.out.println("Stock Actual final: " + cafe.getStockActual() + " kg");
        System.out.println("¿Necesita reposición ahora?: " + cafe.necesitaReposicion()); 
=======
        System.out.println("Precio de Costo: $" + cafe.getPrecioCosto());// en este caso el cafe se cuenta por Kg y decidi ponerle de precio 45k

        System.out.println("\n--- Probando aumentarStock(3.5 kg) ---");
        boolean aumento = cafe.aumentarStock(3.5);
        System.out.println("¿Se pudo aumentar?: " + aumento);
        System.out.println("Nuevo Stock Actual: " + cafe.getStockActual() + " kg");

        System.out.println("\n--- Probando descontarStock(4.0 kg) ---");
        boolean descuento = cafe.descontarStock(4.0);
        System.out.println("¿Se pudo descontar?: " + descuento);
        System.out.println("Stock Actual tras la venta: " + cafe.getStockActual() + " kg");

        System.out.println("\n--- Probando descuento excesivo (10.0 kg) ---");
        boolean descuentoFallido = cafe.descontarStock(10.0);
        System.out.println("¿Se pudo descontar?: " + descuentoFallido); 
        System.out.println("Stock Actual actual: " + cafe.getStockActual() + " kg");

        System.out.println("\n--- Probando needReponer() ---");
        System.out.println("¿Necesita reposición actualmente?: " + cafe.needReponer()); 

        System.out.println("\n--- Descontando más stock para activar la alerta ---");
        cafe.descontarStock(3.0); 
        System.out.println("Stock Actual final: " + cafe.getStockActual() + " kg");
        System.out.println("¿Necesita reposición ahora?: " + cafe.needReponer()); 
>>>>>>> THEIRS
        
        System.out.println("\n=== FIN DE LAS PRUEBAS ===");
    }
}