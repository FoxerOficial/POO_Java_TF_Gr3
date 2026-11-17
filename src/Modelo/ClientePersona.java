/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;


/**
 *
 * @author Sebastian Garcia Cardona
 */
public final class ClientePersona extends Cliente{
    /*
     * Atributos
    */
    private String numeroTelefono;
    private String correoElectronico;
    
    /*
     * Constructor
    */

    public ClientePersona(String numeroTelefono, String correoElectronico, String numeroIdentificacion, String nombre, String nombreCiudad) {
        super(numeroIdentificacion, nombre, nombreCiudad);
        this.numeroTelefono = numeroTelefono;
        this.correoElectronico = correoElectronico;
    }
    
    

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    @Override
    public String toString() {
        return "ClientePersona{" + "numeroTelefono=" + numeroTelefono + ", correoElectronico=" + correoElectronico + '}';
    }
    
    

   
        
    
}//fin class
