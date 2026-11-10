/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package catffee.modelo;

/**
 *
 * @author Edgar José
 */
public class Insumo {
    private int id;
    private String descripcion;
    private double stockActual;
    private double stockMinimo;
    private double precioCosto;
    
    public Insumo (String descripcion, double stockActual, double stockMinimo, double precioCosto){
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precioCosto = precioCosto;
    }
    
    public boolean descontarStock (double cantidad){
        if(cantidad > 0 && this.stockActual >= cantidad) {
            this.stockActual -= cantidad;
        }    
        return false;
    }
    
    public boolean aumentarStock (double cantidad){
        if (cantidad > 0) {
            this.stockActual += cantidad;
            return true;
        }
        return false;
        
    }
  public boolean needReponer() {
        return this.stockActual <= this.stockMinimo;
    }

    // gets y sets

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getStockActual() {
        return stockActual;
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }
}