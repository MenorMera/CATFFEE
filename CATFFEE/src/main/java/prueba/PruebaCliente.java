/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba;
import catffee.modelo.Cliente;

/**
 *
 * @author Edgar José
 */
public class PruebaCliente {
    public static void main(String[] args) {
    System.out.println(" --Prueba cliente-- ");
    
    Cliente cliente1 = new Cliente("4879242", "Ezequiel", "eee@gmail.com", "3025887307");
        
// 2. Probamos el método obtenerInformacion()
        System.out.println(cliente1.obtenerInformacion());

        // 3. Probamos los métodos booleanos que hicimos
        System.out.println("¿Tiene correo registrado?: " + cliente1.confirmarCorreo());
        
        // Probamos verificarCorreo con un correo correcto
        boolean esIgual = cliente1.verificarCorreo("eee@gmail.com");
        System.out.println("¿El correo ingresado coincide (correcto)?: " + esIgual);

        // Probamos verificarCorreo con un correo incorrecto
        boolean esFalso = cliente1.verificarCorreo("carlos@gmail.com");
        System.out.println("¿El correo ingresado coincide (falso)?: " + esFalso);
        
        System.out.println("---------------------------------");
        
        // 4. Probamos el constructor vacío y los sets
        Cliente cliente2 = new Cliente();
        cliente2.setId("99887766");
        cliente2.setNombre("Andres Gaviria");
        cliente2.setCorreo("ana@gmail.com");
        cliente2.setTelefono("3151234568");
        // Dejamos el correo sin llenar a propósito para probar
        
        System.out.println(cliente2.obtenerInformacion());
        System.out.println("¿El segundo cliente tiene correo?: " + cliente2.confirmarCorreo());
    }
}