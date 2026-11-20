/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.io.Serializable;



/**
 *
 * @author Sebastian Garcia Cardona
 */
public abstract class Cliente implements Serializable{
    /*
     * Atributos
    */
    protected String numeroIdentificacion;
    protected String nombre; 
    protected String nombreCiudad; 
    
    /*
     * Constructor
    */

    public Cliente(String numeroIdentificacion, String nombreCompleto, String nombreCiudad) {
        this.numeroIdentificacion = numeroIdentificacion;
        this.nombre = nombreCompleto;
        this.nombreCiudad = nombreCiudad;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public void setNombre(String nombreCompleto) {
        this.nombre = nombreCompleto;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    @Override
    public String toString() {
        return "Cliente{" + "numeroIdentificacion=" + numeroIdentificacion + ", nombreCompleto=" + nombre + ", nombreCiudad=" + nombreCiudad + '}';
    }
    
    

    
}//fin class
