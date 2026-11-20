/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.Cliente;
import Modelo.ClienteEmpresa;
import Modelo.ClientePersona;
import Modelo.Contacto;
import Modelo.Empleado;
import Modelo.Proyecto;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Juan David Perafan
 * @version 1.0 - noviembre 11 de 2026
 */
public class UsaProyecto extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UsaProyecto.class.getName());
    
    // Listas donde se guarda toda la información del aplicativo (según el diagrama)
    private ArrayList<Proyecto> losProyectos = new ArrayList<>();
    private ArrayList<Cliente> losClientes = new ArrayList<>();

    /** Pide los datos de un empleado (el gerente o un integrante del equipo). */
    public Empleado solicitarEmpleado(String rol) {
        String id = JOptionPane.showInputDialog("Número de identificación del " + rol + ":");
        String nombre = JOptionPane.showInputDialog("Nombre completo del " + rol + ":");
        String titulo = JOptionPane.showInputDialog("Título profesional del " + rol + ":");
        String textoAnios = JOptionPane.showInputDialog("Años de experiencia del " + rol + ":");
        if (id == null || nombre == null || titulo == null || textoAnios == null) {
            return null; // El usuario canceló
        }
        try {
            return new Empleado(id, nombre, titulo, Byte.parseByte(textoAnios));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Los años de experiencia deben ser un número.");
            return null;
        }
    }

    /** Pide el cliente: si ya existe en la lista se reutiliza; si no, se crea y se agrega a la lista. */
    public Cliente solicitarCliente(ArrayList<Cliente> clientes) {
        String id = JOptionPane.showInputDialog("Número de identificación del cliente:");
        if (id == null || id.isBlank()) {
            return null;
        }
        id = id;

        // Si el cliente ya está registrado, no se vuelve a pedir
        for (Cliente objCliente : clientes) {
            if (objCliente.getNumeroIdentificacion().equals(id)) {
                JOptionPane.showMessageDialog(null, "Cliente encontrado: " + objCliente.getNombre());
                return objCliente;
            }
        }

        // Cliente nuevo: se pregunta si es empresa o persona natural
        String[] tipos = {"Empresa", "Persona natural"};
        String tipo = (String) JOptionPane.showInputDialog(null, "Cliente nuevo. Tipo de cliente:",
                "Registrar cliente", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
        String nombre = JOptionPane.showInputDialog("Nombre del cliente:");
        String ciudad = JOptionPane.showInputDialog("Ciudad del cliente:");
        if (tipo == null || nombre == null || ciudad == null) {
            return null;
        }

        Cliente nuevo;
        if (tipo.equals("Empresa")) {
            String nombreContacto = JOptionPane.showInputDialog("Nombre completo del contacto en la empresa:");
            String cargo = JOptionPane.showInputDialog("Cargo del contacto:");
            String telefono = JOptionPane.showInputDialog("Teléfono del contacto:");
            String correo = JOptionPane.showInputDialog("Correo del contacto:");
            if (nombreContacto == null || cargo == null || telefono == null || correo == null) {
                return null;
            }
            nuevo = new ClienteEmpresa(new Contacto(nombreContacto, cargo, telefono, correo), id, nombre, ciudad);
        } else {
            String telefono = JOptionPane.showInputDialog("Teléfono del cliente:");
            String correo = JOptionPane.showInputDialog("Correo del cliente:");
            if (telefono == null || correo == null) {
                return null;
            }
            nuevo = new ClientePersona(telefono, correo, id, nombre, ciudad);
        }
        clientes.add(nuevo);
        return nuevo;
    }

    /** Realiza el proceso de registro de un proyecto y lo agrega a la lista (según el diagrama). */
    public ArrayList<Proyecto> registrarProyecto(ArrayList<Proyecto> datos) {
        String codigo = generarCodigoProyecto(datos);

        // Título: entre 20 y 100 caracteres
        String titulo = "";
        while (titulo.length() < 20 || titulo.length() > 100) {
            titulo = JOptionPane.showInputDialog("Título del proyecto (entre 20 y 100 caracteres):");
            if (titulo == null) {
                return datos; // El usuario canceló el registro
            }
            titulo = titulo;
            if (titulo.length() < 20 || titulo.length() > 100) {
                JOptionPane.showMessageDialog(null, "El título debe tener entre 20 y 100 caracteres. El suyo tiene "
                        + titulo.length() + ".");
            }
        }

        // Fecha de inicio en formato aaaa-mm-dd
        LocalDate fechaInicio = null;
        while (fechaInicio == null) {
            String textoFecha = JOptionPane.showInputDialog("Fecha de inicio (aaaa-mm-dd), ej. 2026-11-17:");
            if (textoFecha == null) {
                return datos;
            }
            try {
                fechaInicio = LocalDate.parse(textoFecha);
            } catch (java.time.format.DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Fecha inválida. Use el formato aaaa-mm-dd.");
            }
        }

        // Unidad y duración estimada
        String[] unidades = {"Semanas", "Meses", "Anios"};
        String unidad = (String) JOptionPane.showInputDialog(null, "Unidad de duración:", "Registrar proyecto",
                JOptionPane.QUESTION_MESSAGE, null, unidades, unidades[0]);
        if (unidad == null) {
            return datos;
        }
        byte duracion = 0;
        String rangoValido = " (entre 1 y 3):";
        if (unidad.equals("Meses")) {
            rangoValido = " (entre 1 y 35, sin 12 ni 24):";
        }
        while (!esDuracionValida(unidad, duracion)) {
            String textoDuracion = JOptionPane.showInputDialog("Duración estimada en " + unidad + rangoValido);
            if (textoDuracion == null) {
                return datos;
            }
            try {
                duracion = Byte.parseByte(textoDuracion);
            } catch (NumberFormatException e) {
                duracion = 0; // No es un número: se vuelve a pedir
            }
            if (!esDuracionValida(unidad, duracion)) {
                JOptionPane.showMessageDialog(null, "Duración inválida. Escriba solo el número dentro del rango indicado.");
            }
        }

        // Cliente y gerente
        Cliente cliente = solicitarCliente(losClientes);
        if (cliente == null) {
            return datos;
        }
        Empleado gerente = solicitarEmpleado("gerente");
        if (gerente == null) {
            return datos;
        }

        // El proyecto se registra sin equipo y con estado INICIADO por defecto
        datos.add(new Proyecto(codigo, titulo, fechaInicio, unidad, duracion, cliente, gerente, "INICIADO"));
        JOptionPane.showMessageDialog(null, "Proyecto registrado con el código " + codigo);
        return datos;
    }

    /** Revisa la duración según el diagrama: semanas y años entre 1 y 3; meses entre 1 y 35 sin 12 ni 24. */
    public boolean esDuracionValida(String unidad, byte duracion) {
        if (unidad.equals("Meses")) {
            return duracion >= 1 && duracion <= 35 && duracion % 12 != 0;
        }
        return duracion >= 1 && duracion <= 3;
    }

    /** Busca un proyecto por su código; devuelve null si no existe. */
    public Proyecto buscarProyectoPorCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        for (Proyecto objProyecto : datos) {
            if (objProyecto.getCodigo().equals(codigoProyecto)) {
                return objProyecto;
            }
        }
        return null;
    }

    /** Revisa si un empleado ya hace parte del equipo del proyecto (un integrante no se puede repetir). */
    public boolean estaEnElEquipo(Proyecto proyecto, String numeroIdentificacion) {
        for (Empleado objEmpleado : proyecto.getSuEquipo()) {
            if (objEmpleado.getNumeroIdentificacion().equals(numeroIdentificacion)) {
                return true;
            }
        }
        return false;
    }

    /** Registra UN integrante en el equipo del proyecto indicado (según el diagrama). */
    public String registrarIntegranteProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto proyecto = buscarProyectoPorCodigo(datos, codigoProyecto);
        if (proyecto == null) {
            return "No existe un proyecto con el código " + codigoProyecto;
        }

        Empleado integrante = solicitarEmpleado("integrante");
        if (integrante == null) {
            return "Registro cancelado.";
        }
        if (estaEnElEquipo(proyecto, integrante.getNumeroIdentificacion())) {
            return "El empleado " + integrante.getNumeroIdentificacion() + " ya hace parte del equipo del proyecto.";
        }

        proyecto.agregarIntegrante(integrante);
        return "Integrante " + integrante.getNombreCompleto() + " agregado al proyecto " + proyecto.getCodigo()
                + ".\nEl equipo ahora tiene " + proyecto.getSuEquipo().size() + " integrante(s).";
    }

    /** Registra VARIOS integrantes: pide la cantidad y luego los datos de cada uno (según el diagrama). */
    public String registrarEquipoProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto proyecto = buscarProyectoPorCodigo(datos, codigoProyecto);
        if (proyecto == null) {
            return "No existe un proyecto con el código " + codigoProyecto;
        }

        String textoCantidad = JOptionPane.showInputDialog("¿Cuántos integrantes desea registrar?");
        if (textoCantidad == null) {
            return "Registro cancelado.";
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            return "La cantidad de integrantes debe ser un número.";
        }

        int agregados = 0;
        for (int i = 1; i <= cantidad; i++) {
            Empleado integrante = solicitarEmpleado("integrante " + i);
            if (integrante == null) {
                break; // El usuario canceló: se guardan los que ya se registraron
            }
            if (estaEnElEquipo(proyecto, integrante.getNumeroIdentificacion())) {
                JOptionPane.showMessageDialog(null, "El empleado " + integrante.getNumeroIdentificacion()
                        + " ya hace parte del equipo. No se agrega.");
            } else {
                proyecto.agregarIntegrante(integrante);
                agregados++;
            }
        }
        return "Se agregaron " + agregados + " integrante(s) al proyecto " + proyecto.getCodigo()
                + ".\nEl equipo ahora tiene " + proyecto.getSuEquipo().size() + " integrante(s).";
    }

    /** Elimina un integrante del equipo del proyecto indicado (según el diagrama). */
    public String eliminarIntegranteProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto proyecto = buscarProyectoPorCodigo(datos, codigoProyecto);
        if (proyecto == null) {
            return "No existe un proyecto con el código " + codigoProyecto;
        }
        if (proyecto.getSuEquipo().isEmpty()) {
            return "El proyecto " + proyecto.getCodigo() + " no tiene integrantes en su equipo.";
        }

        String id = JOptionPane.showInputDialog("Número de identificación del integrante a eliminar:");
        if (id == null || id.isBlank()) {
            return "Eliminación cancelada.";
        }

        // eliminarIntegrante (clase Proyecto) devuelve el empleado eliminado, o null si no estaba en el equipo
        Empleado eliminado = proyecto.eliminarIntegrante(id);
        if (eliminado == null) {
            return "El empleado " + id + " no hace parte del equipo del proyecto " + proyecto.getCodigo() + ".";
        }
        return "Integrante " + eliminado.getNombreCompleto() + " eliminado del proyecto " + proyecto.getCodigo()
                + ".\nEl equipo ahora tiene " + proyecto.getSuEquipo().size() + " integrante(s).";
    }

    /** Genera el código del proyecto: año-consecutivo (ej. 2026-001). El consecutivo reinicia cada año. */
    /** Actualiza el estado del proyecto indicado (según el diagrama). */
    public String actualizarEstadoProyectoDadoCodigo(ArrayList<Proyecto> losProyectos, String codigoProyecto) {
        String resultado = "ACTUALIZACION DEL ESTADO DEL PROYECTO\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            if (objProyecto.getCodigo().equals(codigoProyecto)) {
                String estadoAnterior = objProyecto.getEstado();
                String[] estados = {"INICIADO", "EN CURSO", "CANCELADO", "FINALIZADO", "SUSPENDIDO", "APLAZADO"};
                String nuevoEstado = (String) JOptionPane.showInputDialog(null, "Nuevo estado del proyecto:",
                        "Actualizar estado", JOptionPane.QUESTION_MESSAGE, null, estados, estadoAnterior);
                if (nuevoEstado == null) {
                    resultado += "Operación cancelada.";
                    return resultado;
                }
                objProyecto.setEstado(nuevoEstado);
                resultado += "Proyecto: " + objProyecto.getTitulo();
                resultado += "\nEstado anterior: " + estadoAnterior;
                resultado += "\nEstado nuevo: " + nuevoEstado;
                return resultado;
            }
        }
        resultado += "No existe un proyecto con el código " + codigoProyecto;
        return resultado;
    }

    public String generarCodigoProyecto(ArrayList<Proyecto> datos) {
        int anioActual = LocalDate.now().getYear();
        int consecutivo = 0;
        for (Proyecto objProyecto : datos) {
            if (objProyecto.getCodigo().startsWith(anioActual + "-")) {
                consecutivo++;
            }
        }
        return anioActual + "-" + String.format("%03d", consecutivo + 1);
    }
    /** 8. Consultar los proyectos dados el número de identificación del gerente. */
    public String consultarProyectosDadoNumIDEGerente(ArrayList<Proyecto> losProyectos, String numIDEGerente) {
        String resultado = "CONSULTA DE LOS PROYECTOS DADO EL NUMERO DE IDENTIFICACION DEL GERENTE\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            if (objProyecto.getSuGerente().getNumeroIdentificacion().equals(numIDEGerente)) {
                resultado += objProyecto.toString();
                resultado += "\nFecha de finalizacion: " + objProyecto.determinarFechaFinalizacion();
                resultado += "\n";
            }
        }
        return resultado;
    }

    /** 9. Consultar los proyectos dados el número de identificación del cliente. */
    public String consultarProyectosDadoNumIDECliente(ArrayList<Proyecto> losProyectos, String numIDECliente) {
        String resultado = "CONSULTA DE LOS PROYECTOS DADO EL NUMERO DE IDENTIFICACION DEL CLIENTE\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            if (objProyecto.getSuCliente().getNumeroIdentificacion().equals(numIDECliente)) {
                resultado += objProyecto.toString();
                resultado += "\nFecha de finalizacion: " + objProyecto.determinarFechaFinalizacion();
                resultado += "\n";
            }
        }
        return resultado;
    }

    public String consultarTodosProyectos(ArrayList<Proyecto> losProyectos) {
        String resultado = "CONSULTA DE TODOS LOS PROYECTOS\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            resultado += objProyecto.toString();
            resultado += "\nFecha de finalizacion: " + objProyecto.determinarFechaFinalizacion();
            resultado += "\n";
        }
        return resultado;
    }

    public String consultarProyectosEnCurso(ArrayList<Proyecto> losProyectos) {
        String resultado = "CONSULTA DE LOS PROYECTOS EN CURSO\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            if (objProyecto.getEstado().equals("EN CURSO")) {
                resultado += objProyecto.toString();
                resultado += "\nFecha de finalizacion: " + objProyecto.determinarFechaFinalizacion();
                resultado += "\n";
            }
        }
        return resultado;
    }

    public String consultarPrimerProyecto(ArrayList<Proyecto> datos) {
        if (datos.isEmpty()) return "No hay proyectos registrados.";
        Proyecto p = datos.get(0);
        return "--- PRIMER PROYECTO ---\n\n" + p.toString() + "\nFecha fin: " + p.determinarFechaFinalizacion();
    }

    public String consultarUltimoProyecto(ArrayList<Proyecto> datos) {
        if (datos.isEmpty()) return "No hay proyectos registrados.";
        Proyecto p = datos.get(datos.size() - 1);
        return "--- ÚLTIMO PROYECTO ---\n\n" + p.toString() + "\nFecha fin: " + p.determinarFechaFinalizacion();
    }

    public String consultarGerenteProyectoDadoCodigo(ArrayList<Proyecto> losProyectos, String codigoProyecto) {
        String resultado = "CONSULTA DEL GERENTE DEL PROYECTO DADO EL CODIGO\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            if (objProyecto.getCodigo().equals(codigoProyecto)) {
                resultado += "Proyecto: " + objProyecto.getTitulo();
                resultado += "\n" + objProyecto.getSuGerente().toString();
                resultado += "\n";
            }
        }
        return resultado;
    }

    public String consultarEquipoProyectoDadoCodigo(ArrayList<Proyecto> losProyectos, String codigoProyecto) {
        String resultado = "CONSULTA DEL EQUIPO DEL PROYECTO DADO EL CODIGO\n";
        if (losProyectos.isEmpty()) {
            resultado += "No hay proyectos registrados.";
            return resultado;
        }
        for (Proyecto objProyecto : losProyectos) {
            if (objProyecto.getCodigo().equals(codigoProyecto)) {
                resultado += "Proyecto: " + objProyecto.getTitulo();
                resultado += "\n";
                for (Empleado objEmpleado : objProyecto.getSuEquipo()) {
                    resultado += objEmpleado.toString();
                    resultado += "\n";
                }
            }
        }
        return resultado;
    }
    
        // ===================== GENERAR Y LEER =====================

    /** Guarda cada proyecto de la lista en el archivo "datosProyectos". */
    public void generarArchivoDeObjetosProyectos(ArrayList<Proyecto> datos){
        String nombreArchivo = "datosProyectos";
        ObjectOutputStream salida = null;
        
        try{
            salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo));
            salida.writeObject(datos);
            JOptionPane.showMessageDialog(null, "¡Operación exitosa!\nArchivo generado: " + nombreArchivo,
                    "GENERACIÓN DE ARCHIVO", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError almacenando datos en archivo " + nombreArchivo
                    + "\nRevisar serialización de clases.", "GENERACIÓN DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
        } finally{
            try{
                if(salida != null){ salida.close(); }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError cerrando archivo " + nombreArchivo,
                        "GENERACIÓN DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Lee todos los proyectos del archivo "datosProyectos" y devuelve la lista. */
    public ArrayList<Proyecto> leerArchivoDeObjetosProyectos(){
        String nombreArchivo = "datosProyectos";
        ObjectInputStream entrada = null;
        ArrayList<Proyecto> listaNueva = new ArrayList<>();
        try{
            entrada = new ObjectInputStream(new FileInputStream(nombreArchivo));
            listaNueva = (ArrayList<Proyecto>) entrada.readObject();
            JOptionPane.showMessageDialog(null, "¡Operación exitosa!\nArchivo leído: " + nombreArchivo,
                    "LECTURA DE ARCHIVO", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError recuperando datos desde archivo " + nombreArchivo,
                    "LECTURA DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
        } finally{
            try{
                if(entrada != null){ entrada.close(); }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError cerrando archivo " + nombreArchivo,
                        "LECTURA DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
            }
        }
        return listaNueva;
    }

    /** Guarda el cliente de cada proyecto en el archivo "datosClientes". */
    public void generarArchivoDeObjetosClientes(ArrayList<Proyecto> datos){
        String nombreArchivo = "datosClientes";
        ObjectOutputStream salida = null;
        ArrayList<Cliente> losClientesDelArchivo = new ArrayList<>();
        for(Proyecto objProyecto : datos){
            boolean repetido = false;
            for(Cliente objCliente : losClientesDelArchivo){
                if(objCliente.getNumeroIdentificacion().equals(objProyecto.getSuCliente().getNumeroIdentificacion())){
                    repetido = true;
                }
            }
            if(!repetido){ losClientesDelArchivo.add(objProyecto.getSuCliente()); }
        }

        try{
            salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo));
            salida.writeObject(losClientesDelArchivo);
            JOptionPane.showMessageDialog(null, "¡Operación exitosa!\nArchivo generado: " + nombreArchivo,
                    "GENERACIÓN DE ARCHIVO", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError almacenando datos en archivo " + nombreArchivo
                    + "\nRevisar serialización de clases.", "GENERACIÓN DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
        } finally{
            try{
                if(salida != null){ salida.close(); }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError cerrando archivo " + nombreArchivo,
                        "GENERACIÓN DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Lee los clientes de "datosClientes" sin repetir ninguno (por número de identificación). */
    public ArrayList<Cliente> leerArchivoDeObjetosClientes(){
        String nombreArchivo = "datosClientes";
        ObjectInputStream entrada = null;
        ArrayList<Cliente> listaNueva = new ArrayList<>();
        try{
            entrada = new ObjectInputStream(new FileInputStream(nombreArchivo));
            listaNueva = (ArrayList<Cliente>) entrada.readObject();
            JOptionPane.showMessageDialog(null, "¡Operación exitosa!\nArchivo leído: " + nombreArchivo,
                    "LECTURA DE ARCHIVO", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError recuperando datos desde archivo " + nombreArchivo,
                    "LECTURA DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
        } finally{
            try{
                if(entrada != null){ entrada.close(); }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null, "¡Operación fallida!\nError cerrando archivo " + nombreArchivo,
                        "LECTURA DE ARCHIVO", JOptionPane.ERROR_MESSAGE);
            }
        }
        return listaNueva;
    }

    /**
     * Creates new form UsaProyecto
     */
    public UsaProyecto() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButtonRegistrarProyecto = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldParaCodigoProyectoGestion = new javax.swing.JTextField();
        jButtonRegistrarEquipo = new javax.swing.JButton();
        jButtonEliminarIntegrantes = new javax.swing.JButton();
        jButtonRegistrarIntegrantes = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButtonConsultarTodos = new javax.swing.JButton();
        jButtonConsultarEnCurso = new javax.swing.JButton();
        jButtonConsultarPrimero = new javax.swing.JButton();
        jButtonConsultarUltimo = new javax.swing.JButton();
        jButtonConsultarGerente = new javax.swing.JButton();
        jButtonConsultarEquipo = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldNumIDGerente = new javax.swing.JTextField();
        jTextFieldNumIDCliente = new javax.swing.JTextField();
        jButtonConsultarPorGerente = new javax.swing.JButton();
        jButtonConsultarPorCliente = new javax.swing.JButton();
        jTextFieldCodigoProyecto = new javax.swing.JTextField();
        jButtonActualizarEstadoSegunCodigo = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaResultadoConsultas = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jButtonGenerarArchivoProyectos = new javax.swing.JButton();
        jButtonGenerarArchivoClientes = new javax.swing.JButton();
        jButtonLeerArchivoProyectos = new javax.swing.JButton();
        jButtonLeerArchivoClientes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Gestion de proyectos");

        jButtonRegistrarProyecto.setText("Registrar proyecto");
        jButtonRegistrarProyecto.addActionListener(this::jButtonRegistrarProyectoActionPerformed);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Codigo Proyecto");

        jTextFieldParaCodigoProyectoGestion.addActionListener(this::jTextFieldParaCodigoProyectoGestionActionPerformed);

        jButtonRegistrarEquipo.setText("Registrar equipo");
        jButtonRegistrarEquipo.addActionListener(this::jButtonRegistrarEquipoActionPerformed);

        jButtonEliminarIntegrantes.setText("Eliminar integrantes");
        jButtonEliminarIntegrantes.addActionListener(this::jButtonEliminarIntegrantesActionPerformed);

        jButtonRegistrarIntegrantes.setText("Registrar integrantes");
        jButtonRegistrarIntegrantes.addActionListener(this::jButtonRegistrarIntegrantesActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButtonRegistrarProyecto)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldParaCodigoProyectoGestion, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonRegistrarIntegrantes)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonRegistrarEquipo)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEliminarIntegrantes)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRegistrarProyecto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldParaCodigoProyectoGestion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRegistrarEquipo)
                    .addComponent(jButtonRegistrarIntegrantes)
                    .addComponent(jButtonEliminarIntegrantes))
                .addGap(0, 15, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Consultas");

        jButtonConsultarTodos.setText("Consultar todos");
        jButtonConsultarTodos.addActionListener(this::jButtonConsultarTodosActionPerformed);

        jButtonConsultarEnCurso.setText("Consultar en curso");
        jButtonConsultarEnCurso.addActionListener(this::jButtonConsultarEnCursoActionPerformed);

        jButtonConsultarPrimero.setText(" Consultar primero");
        jButtonConsultarPrimero.addActionListener(this::jButtonConsultarPrimeroActionPerformed);

        jButtonConsultarUltimo.setText("Consultar ultimo");
        jButtonConsultarUltimo.addActionListener(this::jButtonConsultarUltimoActionPerformed);

        jButtonConsultarGerente.setText("Consultar gerente");
        jButtonConsultarGerente.addActionListener(this::jButtonConsultarGerenteActionPerformed);

        jButtonConsultarEquipo.setText("Consultar equipo");
        jButtonConsultarEquipo.addActionListener(this::jButtonConsultarEquipoActionPerformed);

        jLabel4.setText("Num. ID gerente");

        jLabel5.setText("Num. ID cliente");

        jLabel6.setText("Codigo proyecto");

        jTextFieldNumIDGerente.addActionListener(this::jTextFieldNumIDGerenteActionPerformed);

        jTextFieldNumIDCliente.addActionListener(this::jTextFieldNumIDClienteActionPerformed);

        jButtonConsultarPorGerente.setText("Consultar por gerente");
        jButtonConsultarPorGerente.addActionListener(this::jButtonConsultarPorGerenteActionPerformed);

        jButtonConsultarPorCliente.setText("Consultar por cliente");
        jButtonConsultarPorCliente.addActionListener(this::jButtonConsultarPorClienteActionPerformed);

        jTextFieldCodigoProyecto.addActionListener(this::jTextFieldCodigoProyectoActionPerformed);

        jButtonActualizarEstadoSegunCodigo.setText("Actualizar estado según codigo");
        jButtonActualizarEstadoSegunCodigo.addActionListener(this::jButtonActualizarEstadoSegunCodigoActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTextFieldCodigoProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonConsultarGerente)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonConsultarEquipo)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonActualizarEstadoSegunCodigo))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jTextFieldNumIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButtonConsultarPorCliente))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jTextFieldNumIDGerente, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButtonConsultarPorGerente)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonConsultarTodos)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonConsultarEnCurso)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonConsultarPrimero)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonConsultarUltimo)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConsultarTodos)
                    .addComponent(jButtonConsultarEnCurso)
                    .addComponent(jButtonConsultarPrimero)
                    .addComponent(jButtonConsultarUltimo))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldNumIDGerente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonConsultarPorGerente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldNumIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonConsultarPorCliente))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonConsultarGerente)
                        .addComponent(jButtonConsultarEquipo)
                        .addComponent(jTextFieldCodigoProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonActualizarEstadoSegunCodigo))
                    .addComponent(jLabel6))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jLabel7.setText("Resultado consultas");

        jTextAreaResultadoConsultas.setEditable(false);
        jTextAreaResultadoConsultas.setColumns(20);
        jTextAreaResultadoConsultas.setRows(5);
        jScrollPane1.setViewportView(jTextAreaResultadoConsultas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel8.setText("Archivos");

        jButtonGenerarArchivoProyectos.setText("Generar Archivo proyectos");
        jButtonGenerarArchivoProyectos.addActionListener(this::jButtonGenerarArchivoProyectosActionPerformed);

        jButtonGenerarArchivoClientes.setText("Generar archivo clientes");
        jButtonGenerarArchivoClientes.addActionListener(this::jButtonGenerarArchivoClientesActionPerformed);

        jButtonLeerArchivoProyectos.setText("Leer archivo proyectos");
        jButtonLeerArchivoProyectos.addActionListener(this::jButtonLeerArchivoProyectosActionPerformed);

        jButtonLeerArchivoClientes.setText("Leer archivo clientes");
        jButtonLeerArchivoClientes.setToolTipText("");
        jButtonLeerArchivoClientes.addActionListener(this::jButtonLeerArchivoClientesActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonGenerarArchivoProyectos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonGenerarArchivoClientes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonLeerArchivoProyectos)
                        .addGap(12, 12, 12)
                        .addComponent(jButtonLeerArchivoClientes)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGenerarArchivoProyectos)
                    .addComponent(jButtonGenerarArchivoClientes)
                    .addComponent(jButtonLeerArchivoProyectos)
                    .addComponent(jButtonLeerArchivoClientes))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldParaCodigoProyectoGestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldParaCodigoProyectoGestionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldParaCodigoProyectoGestionActionPerformed

    private void jButtonConsultarGerenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarGerenteActionPerformed
        jTextAreaResultadoConsultas.setText(consultarGerenteProyectoDadoCodigo(losProyectos, jTextFieldCodigoProyecto.getText()));
    }//GEN-LAST:event_jButtonConsultarGerenteActionPerformed

    private void jTextFieldNumIDGerenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNumIDGerenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNumIDGerenteActionPerformed

    private void jButtonGenerarArchivoProyectosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerarArchivoProyectosActionPerformed
        generarArchivoDeObjetosProyectos(losProyectos);
    }//GEN-LAST:event_jButtonGenerarArchivoProyectosActionPerformed

    private void jButtonLeerArchivoClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeerArchivoClientesActionPerformed
        ArrayList<Cliente> clientesLeidos = leerArchivoDeObjetosClientes();
        if (!clientesLeidos.isEmpty()) {
            losClientes.clear();
            losClientes.addAll(clientesLeidos);
        }
    }//GEN-LAST:event_jButtonLeerArchivoClientesActionPerformed

    private void jTextFieldNumIDClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNumIDClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNumIDClienteActionPerformed

    private void jTextFieldCodigoProyectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCodigoProyectoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCodigoProyectoActionPerformed

    private void jButtonRegistrarIntegrantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarIntegrantesActionPerformed
        jTextAreaResultadoConsultas.setText(registrarIntegranteProyectoDadoCodigo(losProyectos, jTextFieldParaCodigoProyectoGestion.getText()));
    }//GEN-LAST:event_jButtonRegistrarIntegrantesActionPerformed

    private void jButtonRegistrarEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarEquipoActionPerformed
        jTextAreaResultadoConsultas.setText(registrarEquipoProyectoDadoCodigo(losProyectos, jTextFieldParaCodigoProyectoGestion.getText()));
    }//GEN-LAST:event_jButtonRegistrarEquipoActionPerformed

    private void jButtonEliminarIntegrantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarIntegrantesActionPerformed
        jTextAreaResultadoConsultas.setText(eliminarIntegranteProyectoDadoCodigo(losProyectos, jTextFieldParaCodigoProyectoGestion.getText()));
    }//GEN-LAST:event_jButtonEliminarIntegrantesActionPerformed

    private void jButtonRegistrarProyectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarProyectoActionPerformed
        losProyectos = registrarProyecto(losProyectos);
        jTextFieldParaCodigoProyectoGestion.setText("");
    }//GEN-LAST:event_jButtonRegistrarProyectoActionPerformed

    private void jButtonConsultarEnCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarEnCursoActionPerformed
        jTextAreaResultadoConsultas.setText(consultarProyectosEnCurso(losProyectos));
    }//GEN-LAST:event_jButtonConsultarEnCursoActionPerformed

    private void jButtonConsultarPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPrimeroActionPerformed
        jTextAreaResultadoConsultas.setText(consultarPrimerProyecto(losProyectos));
    }//GEN-LAST:event_jButtonConsultarPrimeroActionPerformed

    private void jButtonConsultarUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarUltimoActionPerformed
        jTextAreaResultadoConsultas.setText(consultarUltimoProyecto(losProyectos));
    }//GEN-LAST:event_jButtonConsultarUltimoActionPerformed

    private void jButtonActualizarEstadoSegunCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualizarEstadoSegunCodigoActionPerformed
        jTextAreaResultadoConsultas.setText(actualizarEstadoProyectoDadoCodigo(losProyectos, jTextFieldParaCodigoProyectoGestion.getText()));
    }//GEN-LAST:event_jButtonActualizarEstadoSegunCodigoActionPerformed

    private void jButtonConsultarPorGerenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPorGerenteActionPerformed
        jTextAreaResultadoConsultas.setText(consultarProyectosDadoNumIDEGerente(losProyectos, jTextFieldNumIDGerente.getText()));
    }//GEN-LAST:event_jButtonConsultarPorGerenteActionPerformed

    private void jButtonConsultarPorClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPorClienteActionPerformed
        jTextAreaResultadoConsultas.setText(consultarProyectosDadoNumIDECliente(losProyectos, jTextFieldNumIDCliente.getText()));
    }//GEN-LAST:event_jButtonConsultarPorClienteActionPerformed

    private void jButtonConsultarEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarEquipoActionPerformed
        jTextAreaResultadoConsultas.setText(consultarEquipoProyectoDadoCodigo(losProyectos, jTextFieldCodigoProyecto.getText()));
    }//GEN-LAST:event_jButtonConsultarEquipoActionPerformed

    private void jButtonGenerarArchivoClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerarArchivoClientesActionPerformed
        generarArchivoDeObjetosClientes(losProyectos);
    }//GEN-LAST:event_jButtonGenerarArchivoClientesActionPerformed

    private void jButtonConsultarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarTodosActionPerformed
        jTextAreaResultadoConsultas.setText(consultarTodosProyectos(losProyectos));
    }//GEN-LAST:event_jButtonConsultarTodosActionPerformed

    private void jButtonLeerArchivoProyectosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeerArchivoProyectosActionPerformed
        ArrayList<Proyecto> proyectosLeidos = leerArchivoDeObjetosProyectos();
        if (!proyectosLeidos.isEmpty()) {
            losProyectos.clear();
            losProyectos.addAll(proyectosLeidos);
        }
        jTextAreaResultadoConsultas.setText(consultarTodosProyectos(losProyectos));
    }//GEN-LAST:event_jButtonLeerArchivoProyectosActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new UsaProyecto().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActualizarEstadoSegunCodigo;
    private javax.swing.JButton jButtonConsultarEnCurso;
    private javax.swing.JButton jButtonConsultarEquipo;
    private javax.swing.JButton jButtonConsultarGerente;
    private javax.swing.JButton jButtonConsultarPorCliente;
    private javax.swing.JButton jButtonConsultarPorGerente;
    private javax.swing.JButton jButtonConsultarPrimero;
    private javax.swing.JButton jButtonConsultarTodos;
    private javax.swing.JButton jButtonConsultarUltimo;
    private javax.swing.JButton jButtonEliminarIntegrantes;
    private javax.swing.JButton jButtonGenerarArchivoClientes;
    private javax.swing.JButton jButtonGenerarArchivoProyectos;
    private javax.swing.JButton jButtonLeerArchivoClientes;
    private javax.swing.JButton jButtonLeerArchivoProyectos;
    private javax.swing.JButton jButtonRegistrarEquipo;
    private javax.swing.JButton jButtonRegistrarIntegrantes;
    private javax.swing.JButton jButtonRegistrarProyecto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaResultadoConsultas;
    private javax.swing.JTextField jTextFieldCodigoProyecto;
    private javax.swing.JTextField jTextFieldNumIDCliente;
    private javax.swing.JTextField jTextFieldNumIDGerente;
    private javax.swing.JTextField jTextFieldParaCodigoProyectoGestion;
    // End of variables declaration//GEN-END:variables
}
