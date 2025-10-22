package colectivo.test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import colectivo.aplicacion.Constantes;
import colectivo.datos.CargarDatos;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;

/**
 * Clase de test unitario para la carga de datos desde archivos.
 * Se usarán siempre los datos de prueba proporcionados por la cátedra.
 * 
 * Verifica:
 * - Carga correcta de paradas desde archivo
 * - Carga correcta de tramos desde archivo
 * - Carga correcta de líneas y frecuencias desde archivos
 * - Manejo de casos límite y archivos no existentes
 */
public class TestCargarDatos {

	private static final String ARCHIVO_PARADAS = "parada.txt";
	private static final String ARCHIVO_TRAMOS = "tramo.txt";
	private static final String ARCHIVO_LINEAS = "linea.txt";
	private static final String ARCHIVO_FRECUENCIAS = "frecuencia.txt";

	private Map<Integer, Parada> paradas;
	private Map<String, Tramo> tramos;
	private Map<String, Linea> lineas;

	/**
	 * Configuración inicial antes de cada test.
	 * Inicializa las estructuras de datos a null.
	 */
	@BeforeEach
	public void setUp() {
		paradas = null;
		tramos = null;
		lineas = null;
	}

	/**
	 * Test: Verifica que se carguen correctamente las paradas desde el archivo.
	 * Condición: El archivo existe y tiene formato válido
	 * Resultado esperado: Mapa no nulo con paradas cargadas
	 */
	@Test
	public void testCargarParadas_ArchivoValido_CargaExitosa() throws IOException {
		// Arrange & Act
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		// Assert
		assertNotNull(paradas, "El mapa de paradas no debe ser nulo");
		assertFalse(paradas.isEmpty(), "El mapa de paradas no debe estar vacío");
		
		// Verificar que se cargaron paradas específicas
		assertTrue(paradas.containsKey(1), "Debe existir la parada con código 1");
		assertTrue(paradas.containsKey(5), "Debe existir la parada con código 5");
		
		// Verificar datos de una parada específica
		Parada parada1 = paradas.get(1);
		assertNotNull(parada1, "La parada 1 debe existir");
		assertEquals(1, parada1.getCodigo(), "El código debe ser 1");
		assertEquals("1 De Marzo, 405", parada1.getDireccion(), "La dirección debe coincidir");
		assertEquals(-42.766285, parada1.getLatitud(), 0.000001, "La latitud debe coincidir");
		assertEquals(-65.040768, parada1.getLongitud(), 0.000001, "La longitud debe coincidir");
	}

	/**
	 * Test: Verifica que las paradas tengan coordenadas geográficas válidas.
	 * Condición: Paradas cargadas correctamente
	 * Resultado esperado: Todas las paradas tienen latitud y longitud
	 */
	@Test
	public void testCargarParadas_CoordenadasValidas_TodasLasParadas() throws IOException {
		// Arrange & Act
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		// Assert
		for (Parada parada : paradas.values()) {
			assertNotEquals(0.0, parada.getLatitud(), "La latitud no debe ser 0");
			assertNotEquals(0.0, parada.getLongitud(), "La longitud no debe ser 0");
			assertNotNull(parada.getDireccion(), "La dirección no debe ser nula");
			assertFalse(parada.getDireccion().trim().isEmpty(), "La dirección no debe estar vacía");
		}
	}

	/**
	 * Test: Verifica que se lance excepción cuando el archivo no existe.
	 * Condición: Archivo inexistente
	 * Resultado esperado: IOException lanzada
	 */
	@Test
	public void testCargarParadas_ArchivoInexistente_LanzaExcepcion() {
		// Arrange
		String archivoInexistente = "archivo_que_no_existe.txt";

		// Act & Assert
		assertThrows(IOException.class, () -> {
			CargarDatos.cargarParadas(archivoInexistente);
		}, "Debe lanzar IOException cuando el archivo no existe");
	}

	/**
	 * Test: Verifica que se carguen correctamente los tramos desde el archivo.
	 * Condición: Archivos de paradas y tramos válidos
	 * Resultado esperado: Mapa no nulo con tramos cargados
	 */
	@Test
	public void testCargarTramos_ArchivosValidos_CargaExitosa() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		// Act
		tramos = CargarDatos.cargarTramos(ARCHIVO_TRAMOS, paradas);

		// Assert
		assertNotNull(tramos, "El mapa de tramos no debe ser nulo");
		assertFalse(tramos.isEmpty(), "El mapa de tramos no debe estar vacío");
		
		// Verificar que existe un tramo específico (88-97 según el archivo)
		assertTrue(tramos.containsKey("88-97"), "Debe existir el tramo 88-97");
		
		Tramo tramo = tramos.get("88-97");
		assertNotNull(tramo, "El tramo 88-97 debe existir");
		assertNotNull(tramo.getInicio(), "El tramo debe tener parada de inicio");
		assertNotNull(tramo.getFin(), "El tramo debe tener parada de fin");
		assertEquals(88, tramo.getInicio().getCodigo(), "La parada de inicio debe ser 88");
		assertEquals(97, tramo.getFin().getCodigo(), "La parada de fin debe ser 97");
		assertEquals(60, tramo.getTiempo(), "El tiempo del tramo debe ser 60");
	}

	/**
	 * Test: Verifica que los tramos tienen paradas válidas.
	 * Condición: Tramos cargados correctamente
	 * Resultado esperado: Todos los tramos tienen paradas de inicio y fin válidas
	 */
	@Test
	public void testCargarTramos_ParadasValidas_TodosLosTramos() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		// Act
		tramos = CargarDatos.cargarTramos(ARCHIVO_TRAMOS, paradas);

		// Assert
		for (Tramo tramo : tramos.values()) {
			assertNotNull(tramo.getInicio(), "Todo tramo debe tener parada de inicio");
			assertNotNull(tramo.getFin(), "Todo tramo debe tener parada de fin");
			assertTrue(tramo.getTiempo() > 0, "El tiempo del tramo debe ser positivo");
		}
	}

	/**
	 * Test: Verifica que se lance excepción cuando el archivo de tramos no existe.
	 * Condición: Archivo de tramos inexistente
	 * Resultado esperado: Exception lanzada
	 */
	@Test
	public void testCargarTramos_ArchivoInexistente_LanzaExcepcion() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);
		String archivoInexistente = "archivo_que_no_existe.txt";

		// Act & Assert
		assertThrows(Exception.class, () -> {
			CargarDatos.cargarTramos(archivoInexistente, paradas);
		}, "Debe lanzar excepción cuando el archivo no existe");
	}

	/**
	 * Test: Verifica que se carguen correctamente las líneas y frecuencias.
	 * Condición: Archivos válidos
	 * Resultado esperado: Mapa no nulo con líneas cargadas
	 */
	@Test
	public void testCargarLineas_ArchivosValidos_CargaExitosa() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		// Act
		lineas = CargarDatos.cargarLineas(ARCHIVO_LINEAS, ARCHIVO_FRECUENCIAS, paradas);

		// Assert
		assertNotNull(lineas, "El mapa de líneas no debe ser nulo");
		assertFalse(lineas.isEmpty(), "El mapa de líneas no debe estar vacío");
		
		// Verificar que existe una línea específica (L1I según el archivo)
		assertTrue(lineas.containsKey("L1I"), "Debe existir la línea L1I");
		
		Linea linea = lineas.get("L1I");
		assertNotNull(linea, "La línea L1I debe existir");
		assertEquals("L1I", linea.getCodigo(), "El código debe ser L1I");
		assertEquals("Lï¿½nea 1 Ida", linea.getNombre(), "El nombre debe coincidir");
		assertNotNull(linea.getParadas(), "La línea debe tener lista de paradas");
		assertFalse(linea.getParadas().isEmpty(), "La línea debe tener paradas asignadas");
	}

	/**
	 * Test: Verifica que las líneas tengan paradas en el orden correcto.
	 * Condición: Líneas cargadas correctamente
	 * Resultado esperado: Las paradas están en el orden del recorrido
	 */
	@Test
	public void testCargarLineas_OrdenParadas_Correcto() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		// Act
		lineas = CargarDatos.cargarLineas(ARCHIVO_LINEAS, ARCHIVO_FRECUENCIAS, paradas);

		// Assert
		Linea lineaL1I = lineas.get("L1I");
		assertNotNull(lineaL1I, "La línea L1I debe existir");
		
		// Verificar que las primeras paradas están en orden según el archivo
		// L1I;Línea 1 Ida;88;97;44;43;47;58;37;74;77;25;24;5;52;14;61;35;34;89;
		assertEquals(88, lineaL1I.getParadas().get(0).getCodigo(), "Primera parada debe ser 88");
		assertEquals(97, lineaL1I.getParadas().get(1).getCodigo(), "Segunda parada debe ser 97");
		assertEquals(44, lineaL1I.getParadas().get(2).getCodigo(), "Tercera parada debe ser 44");
	}

	/**
	 * Test: Verifica que se lance excepción cuando el archivo de líneas no existe.
	 * Condición: Archivo de líneas inexistente
	 * Resultado esperado: Exception lanzada
	 */
	@Test
	public void testCargarLineas_ArchivoLineasInexistente_LanzaExcepcion() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);
		String archivoInexistente = "archivo_que_no_existe.txt";

		// Act & Assert
		assertThrows(Exception.class, () -> {
			CargarDatos.cargarLineas(archivoInexistente, ARCHIVO_FRECUENCIAS, paradas);
		}, "Debe lanzar excepción cuando el archivo de líneas no existe");
	}

	/**
	 * Test: Verifica que se lance excepción cuando el archivo de frecuencias no existe.
	 * Condición: Archivo de frecuencias inexistente
	 * Resultado esperado: Exception lanzada
	 */
	@Test
	public void testCargarLineas_ArchivoFrecuenciasInexistente_LanzaExcepcion() throws IOException {
		// Arrange
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);
		String archivoInexistente = "archivo_que_no_existe.txt";

		// Act & Assert
		assertThrows(Exception.class, () -> {
			CargarDatos.cargarLineas(ARCHIVO_LINEAS, archivoInexistente, paradas);
		}, "Debe lanzar excepción cuando el archivo de frecuencias no existe");
	}

	/**
	 * Test de integración: Verifica la carga completa de todos los datos.
	 * Condición: Todos los archivos válidos
	 * Resultado esperado: Todos los datos se cargan correctamente y están relacionados
	 */
	@Test
	public void testCargaCompleta_TodosLosArchivos_IntegracionExitosa() throws IOException {
		// Act
		paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);
		tramos = CargarDatos.cargarTramos(ARCHIVO_TRAMOS, paradas);
		lineas = CargarDatos.cargarLineas(ARCHIVO_LINEAS, ARCHIVO_FRECUENCIAS, paradas);

		// Assert
		assertNotNull(paradas, "Las paradas deben cargarse");
		assertNotNull(tramos, "Los tramos deben cargarse");
		assertNotNull(lineas, "Las líneas deben cargarse");
		
		assertFalse(paradas.isEmpty(), "Debe haber paradas cargadas");
		assertFalse(tramos.isEmpty(), "Debe haber tramos cargados");
		assertFalse(lineas.isEmpty(), "Debe haber líneas cargadas");
		
		// Verificar integridad referencial: las paradas de las líneas existen en el mapa
		for (Linea linea : lineas.values()) {
			for (Parada parada : linea.getParadas()) {
				assertTrue(paradas.containsValue(parada), 
					"Cada parada de una línea debe existir en el mapa de paradas");
			}
		}
	}
	// --- New tests to verify the two requested bidirectional behaviors ---

		@Test
		public void testCargarLineas_ParadaTieneLinea_Bidireccional() throws Exception {
			paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);
			lineas = CargarDatos.cargarLineas(ARCHIVO_LINEAS, ARCHIVO_FRECUENCIAS, paradas);

			Linea lineaL1I = lineas.get("L1I");
			assertNotNull(lineaL1I, "La línea L1I debe existir");

			Parada primera = lineaL1I.getParadas().get(0);
			assertNotNull(primera, "La primera parada de L1I no debe ser nula");

			// Verificar que la parada referencia a la línea (parada.agregarLinea(lineaObj) fue invocado)
			assertNotNull(primera.getLineas(), "La lista de líneas de la parada no debe ser nula");
			assertTrue(primera.getLineas().contains(lineaL1I),
				"La parada debe contener la referencia a la línea (bidireccional)");
		}

		@Test
		public void testCargarTramos_Caminando_BidireccionalYTramoInverso() throws IOException {
		    // Arrange
		    paradas = CargarDatos.cargarParadas(ARCHIVO_PARADAS);

		    // Act
		    tramos = CargarDatos.cargarTramos(ARCHIVO_TRAMOS, paradas);

		    // Assert
		    boolean foundCaminando = false;
		    for (Tramo t : tramos.values()) {
		        if (t.getTipo() == Constantes.CAMINANDO) {
		            foundCaminando = true;
		            String claveInversa = t.getFin().getCodigo() + "-" + t.getInicio().getCodigo();
		            assertTrue(tramos.containsKey(claveInversa), "Debe existir el tramo inverso para caminando: " + claveInversa);
		            Tramo inverso = tramos.get(claveInversa);
		            assertNotNull(inverso, "El tramo inverso no debe ser nulo");
		            assertEquals(t.getFin(), inverso.getInicio(), "El inicio del inverso debe ser el fin del original");
		            assertEquals(t.getInicio(), inverso.getFin(), "El fin del inverso debe ser el inicio del original");
		            assertEquals(t.getTiempo(), inverso.getTiempo(), "El tiempo debe ser el mismo");
		            assertEquals(t.getTipo(), inverso.getTipo(), "El tipo debe ser el mismo");
		            // Verificar bidireccionalidad en paradas
		            assertTrue(t.getInicio().getParadaCaminando().contains(t.getFin()), "Inicio debe contener fin en paradaCaminando");
		            assertTrue(t.getFin().getParadaCaminando().contains(t.getInicio()), "Fin debe contener inicio en paradaCaminando");
		        }
		    }
		    assertTrue(foundCaminando, "Debe haber al menos un tramo de tipo CAMINANDO");
		}

}
