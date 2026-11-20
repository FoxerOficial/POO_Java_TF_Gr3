/*
 * Pruebas unitarias de la clase Proyecto (paso 7 de la tarea final).
 */
package Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juan David Bravo
 */
public class ProyectoTest {

    private Cliente elCliente;
    private Empleado elGerente;
    private Proyecto elProyecto;

    public ProyectoTest() {
    }

    /*
     * Antes de cada prueba se arma un proyecto de ejemplo, para no repetir
     * los mismos datos en todas las pruebas.
     */
    @Before
    public void setUp() {
        elCliente = new ClientePersona("3155551122", "ana@correo.com", "1144", "Ana Ruiz", "Cali");
        elGerente = new Empleado("900", "Luis Gomez", "Ingeniero de sistemas", (byte) 10);
        elProyecto = new Proyecto("2026-001", "Sistema de inventario para bodega",
                LocalDate.of(2026, 2, 15), "Meses", (byte) 6, elCliente, elGerente, "INICIADO");
    }

    /*
     * ---------- Pruebas del metodo determinarFechaFinalizacion
     */
    @Test
    public void laDuracionEnSemanasSeSumaBien() {
        Proyecto objProyecto = new Proyecto("2026-002", "Portal web de atencion al cliente",
                LocalDate.of(2026, 2, 15), "Semanas", (byte) 3, elCliente, elGerente, "INICIADO");
        assertEquals(LocalDate.of(2026, 3, 8), objProyecto.determinarFechaFinalizacion());
    }

    @Test
    public void laDuracionEnMesesSeSumaBien() {
        assertEquals(LocalDate.of(2026, 8, 15), elProyecto.determinarFechaFinalizacion());
    }

    @Test
    public void laDuracionEnAniosSeSumaBien() {
        Proyecto objProyecto = new Proyecto("2026-003", "Migracion de base de datos central",
                LocalDate.of(2026, 2, 15), "Anios", (byte) 3, elCliente, elGerente, "INICIADO");
        assertEquals(LocalDate.of(2029, 2, 15), objProyecto.determinarFechaFinalizacion());
    }

    /*
     * Caso borde: el 31 de enero mas un mes no existe como 31 de febrero.
     */
    @Test
    public void unProyectoQueArrancaAFinDeMesNoSeSaleDelCalendario() {
        Proyecto objProyecto = new Proyecto("2026-004", "Actualizacion del sistema contable",
                LocalDate.of(2026, 1, 31), "Meses", (byte) 1, elCliente, elGerente, "INICIADO");
        assertEquals(LocalDate.of(2026, 2, 28), objProyecto.determinarFechaFinalizacion());
    }

    /*
     * Caso borde: el 29 de febrero de un anio bisiesto mas un anio.
     */
    @Test
    public void unProyectoQueArrancaEnAnioBisiestoNoSeSaleDelCalendario() {
        Proyecto objProyecto = new Proyecto("2024-001", "Rediseno del portal institucional",
                LocalDate.of(2024, 2, 29), "Anios", (byte) 1, elCliente, elGerente, "INICIADO");
        assertEquals(LocalDate.of(2025, 2, 28), objProyecto.determinarFechaFinalizacion());
    }

    /*
     * ---------- Pruebas del equipo del proyecto
     */
    @Test
    public void elProyectoRegistradoSinEquipoArrancaVacio() {
        assertTrue(elProyecto.getSuEquipo().isEmpty());
    }

    @Test
    public void agregarIntegranteAumentaElEquipo() {
        elProyecto.agregarIntegrante(new Empleado("111", "Carlos Diaz", "Analista", (byte) 3));
        elProyecto.agregarIntegrante(new Empleado("222", "Sara Mora", "Disenadora", (byte) 5));
        assertEquals(2, elProyecto.getSuEquipo().size());
    }

    @Test
    public void eliminarIntegranteDevuelveElQueSeElimino() {
        elProyecto.agregarIntegrante(new Empleado("111", "Carlos Diaz", "Analista", (byte) 3));
        elProyecto.agregarIntegrante(new Empleado("222", "Sara Mora", "Disenadora", (byte) 5));

        Empleado eliminado = elProyecto.eliminarIntegrante("111");

        assertNotNull(eliminado);
        assertEquals("111", eliminado.getNumeroIdentificacion());
        assertEquals(1, elProyecto.getSuEquipo().size());
    }

    @Test
    public void eliminarUnIntegranteQueNoEstaDevuelveNuloYNoBorraNada() {
        elProyecto.agregarIntegrante(new Empleado("111", "Carlos Diaz", "Analista", (byte) 3));

        Empleado eliminado = elProyecto.eliminarIntegrante("999");

        assertNull(eliminado);
        assertEquals(1, elProyecto.getSuEquipo().size());
    }

    /*
     * ---------- Pruebas del estado
     */
    @Test
    public void elEstadoSePuedeActualizar() {
        elProyecto.setEstado("EN CURSO");
        assertEquals("EN CURSO", elProyecto.getEstado());
    }

    /*
     * ---------- Pruebas del metodo toString
     */
    @Test
    public void elToStringMuestraElCodigoYElTitulo() {
        String informacion = elProyecto.toString();
        assertTrue(informacion.contains("2026-001"));
        assertTrue(informacion.contains("Sistema de inventario para bodega"));
    }

    /*
     * El cliente debe mostrar tambien los datos que hereda de la clase Cliente,
     * no solo los suyos propios.
     */
    @Test
    public void elToStringMuestraLosDatosHeredadosDelCliente() {
        String informacion = elProyecto.toString();
        assertTrue(informacion.contains("1144"));
        assertTrue(informacion.contains("Ana Ruiz"));
        assertTrue(informacion.contains("Cali"));
    }

    /*
     * ---------- Prueba del constructor con equipo
     */
    @Test
    public void elConstructorConEquipoGuardaLosIntegrantes() {
        ArrayList<Empleado> elEquipo = new ArrayList<>();
        elEquipo.add(new Empleado("111", "Carlos Diaz", "Analista", (byte) 3));
        elEquipo.add(new Empleado("222", "Sara Mora", "Disenadora", (byte) 5));

        Proyecto objProyecto = new Proyecto("2026-005", "Automatizacion de reportes mensuales",
                LocalDate.of(2026, 5, 10), "Meses", (byte) 4, elCliente, elGerente, elEquipo, "INICIADO");

        assertEquals(2, objProyecto.getSuEquipo().size());
    }
}
