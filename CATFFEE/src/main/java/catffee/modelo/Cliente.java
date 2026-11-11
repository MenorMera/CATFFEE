/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package catffee.modelo;

/**
 *
 * @author Edgar José
 */
public class Cliente {
    
    
    private String id, nombre, correo, telefono;
    
    public Cliente() {    
}

    public Cliente(String id, String nombre, String correo, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.id = id;
    }
    
public String obtenerInformacion(){
    return "ID: " + id + " Nombre: " + nombre + " Correo: " + correo + " Telefono: "+ telefono;
    
}

public boolean verificarCorreo(String correoIngresado) {
return this.correo.equals(correoIngresado);

}


public boolean confirmarCorreo() {
if (this.correo == null || this.correo.isEmpty() ){
    return false;
        }
return true;
    }
// gets y sets
public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
    


