/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author Sebastian Garcia Cardona
 */
public final class ClienteEmpresa extends Cliente{
    
      private Contacto suContacto;
    
    /*
     * Constructor
    */

    public ClienteEmpresa(Contacto suContacto, String numeroIdentificacion, String nombre, String nombreCiudad) {
        super(numeroIdentificacion, nombre, nombreCiudad);
        this.suContacto = suContacto;
    }
    
    
 
    public Contacto getSuContacto() { 
        return suContacto; 
    }
    
    public void setSuContacto(Contacto suContacto) { 
        this.suContacto = suContacto; }

    @Override
    public String toString() {
        return super.toString() + " Empresa{suContacto=" + suContacto + "}";
    }

  
    

   
        
    
    
}//final
