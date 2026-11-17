package Modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author Juan David Bravo
 */
public class Proyecto implements Serializable {

    //----- Atributos
    private String codigo;
    private String titulo;
    private LocalDate fechaInicio;
    private String unidadDuracion;
    private byte duracionEstimada;
    private Cliente suCliente;
    private Empleado suGerente;
    private ArrayList<Empleado> suEquipo;
    private String estado;

    //----- Constructor con parámetros
    public Proyecto(String codigo, String titulo, LocalDate fechaInicio, String unidadDuracion,
                    byte duracionEstimada, Cliente suCliente, Empleado suGerente,
                    ArrayList<Empleado> suEquipo) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.unidadDuracion = unidadDuracion;
        this.duracionEstimada = duracionEstimada;
        this.suCliente = suCliente;
        this.suGerente = suGerente;
        this.suEquipo = suEquipo;
        this.estado = "INICIADO";
    }

    //----- Constructor con parámetros sin equipo
    public Proyecto(String codigo, String titulo, LocalDate fechaInicio, String unidadDuracion,
                    byte duracionEstimada, Cliente suCliente, Empleado suGerente) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.unidadDuracion = unidadDuracion;
        this.duracionEstimada = duracionEstimada;
        this.suCliente = suCliente;
        this.suGerente = suGerente;
        this.suEquipo = new ArrayList<>();
        this.estado = "INICIADO";
    }

    //----- Métodos Getter y Setter
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getUnidadDuracion() {
        return unidadDuracion;
    }

    public void setUnidadDuracion(String unidadDuracion) {
        this.unidadDuracion = unidadDuracion;
    }

    public byte getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(byte duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public Cliente getSuCliente() {
        return suCliente;
    }

    public void setSuCliente(Cliente suCliente) {
        this.suCliente = suCliente;
    }

    public Empleado getSuGerente() {
        return suGerente;
    }

    public void setSuGerente(Empleado suGerente) {
        this.suGerente = suGerente;
    }

    public ArrayList<Empleado> getSuEquipo() {
        return suEquipo;
    }

    public void setSuEquipo(ArrayList<Empleado> suEquipo) {
        this.suEquipo = suEquipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    //----- Método toString
    @Override
    public String toString() {
        String informacionEquipo;

        if (suEquipo.isEmpty()) {
            informacionEquipo = "\n          El proyecto aun no tiene equipo registrado";
        } else {
            informacionEquipo = "";
            for (Empleado objEmpleado : suEquipo) {
                informacionEquipo = informacionEquipo + "\n" + objEmpleado;
            }
        }

        return "Proyecto:"
                + "\n     Codigo: " + codigo
                + "\n     Titulo: " + titulo
                + "\n     Fecha de inicio: " + fechaInicio
                + "\n     Duracion estimada: " + duracionEstimada + " " + unidadDuracion
                + "\n     Fecha de finalizacion: " + determinarFechaFinalizacion()
                + "\n     Estado: " + estado
                + "\n     Cliente: " + suCliente
                + "\n     Gerente: " + suGerente
                + "\n     Equipo: " + informacionEquipo;
    }

    //----- Método para determinar la fecha de finalización del proyecto
    public LocalDate determinarFechaFinalizacion() {
        LocalDate fechaFinalizacion = fechaInicio;

        if (unidadDuracion.equals("semanas")) {
            fechaFinalizacion = fechaInicio.plusWeeks(duracionEstimada);
        } else if (unidadDuracion.equals("meses")) {
            fechaFinalizacion = fechaInicio.plusMonths(duracionEstimada);
        } else if (unidadDuracion.equals("anios")) {
            fechaFinalizacion = fechaInicio.plusYears(duracionEstimada);
        }

        return fechaFinalizacion;
    }

    //----- Método para agregar un integrante al equipo del proyecto
    public void agregarIntegrante(Empleado nuevoIntegrante) {
        suEquipo.add(nuevoIntegrante);
    }

    //----- Método para eliminar un integrante del equipo del proyecto
    public Empleado eliminarIntegrante(String numeroIdentificacion) {
        for (Empleado objEmpleado : suEquipo) {
            if (objEmpleado.getNumeroIdentificacion().equals(numeroIdentificacion)) {
                suEquipo.remove(objEmpleado);
                return objEmpleado;
            }
        }

        return null;
    }
}
