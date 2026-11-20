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
public class Contacto implements Serializable{
    
    private String nombreCompleto;
    private String cargo;
    private String numeroTelefono;
    private String correoElectronico;

    public Contacto(String nombreCompleto, String cargo, String numeroTelefono, String correoElectronico) {
        this.nombreCompleto = nombreCompleto;
        this.cargo = cargo;
        this.numeroTelefono = numeroTelefono;
        this.correoElectronico = correoElectronico;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCargo() {
        return cargo;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombreCompleto
             + "\nCargo: " + cargo
             + "\nTeléfono: " + numeroTelefono
             + "\nCorreo electrónico: " + correoElectronico;
    }
    
  
    
    
    
}// final
