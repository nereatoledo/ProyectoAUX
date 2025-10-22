package colectivo.test;

import java.io.IOException;
import java.util.Map;

import colectivo.datos.CargarDatos;
import colectivo.datos.CargarParametros;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;

/**
 * Aplicación de prueba que carga todos los datos y muestra por consola
 * cuántos registros se cargaron de cada tipo.
 */
public class AplicacionCargaDatos {

    /**
     * Ejecuta la carga y muestra los conteos.
     */
    public void ejecutar() {
        try {
            // Carga parámetros y luego los datos; cualquier IOException (incluyendo FileNotFoundException)
            // se captura aquí para evitar errores de compilación por excepciones no controladas.
            CargarParametros.parametros(); // Carga los parámetros desde archivos

            Map<Integer, Parada> paradas = CargarDatos.cargarParadas(CargarParametros.getArchivoParada());

            Map<String, Linea> lineas = CargarDatos.cargarLineas(CargarParametros.getArchivoLinea(),
                    CargarParametros.getArchivoFrecuencia(), paradas);

            Map<String, Tramo> tramos = CargarDatos.cargarTramos(CargarParametros.getArchivoTramo(), paradas);

            int cantParadas = (paradas != null) ? paradas.size() : 0;
            int cantLineas = (lineas != null) ? lineas.size() : 0;
            int cantTramos = (tramos != null) ? tramos.size() : 0;

            System.out.println("=== Resultado de la carga de datos ===");
            System.out.println("Paradas cargadas : " + cantParadas);
            System.out.println("Líneas cargadas  : " + cantLineas);
            System.out.println("Tramos cargados  : " + cantTramos);

        } catch (IOException e) {
            System.err.println("Error al cargar parametros o datos: " + e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        new AplicacionCargaDatos().ejecutar();
    }
}