package colectivo.datos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Map;
import java.util.LinkedHashMap;

import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;

public class CargarDatos {

	private static Map<String, Linea> lineasCargadas;
	private static Map<Integer, Parada> paradasCargadas;

	public static Map<String, Linea> getLineasCargadas() {
		return lineasCargadas;
	}
	
	public static Map<Integer, Parada> getParadasCargadas() { 
        return paradasCargadas;
    }

	public static Map<Integer, Parada> cargarParadas(String nombreArchivo) throws IOException {
		Map<Integer, Parada> paradas = new LinkedHashMap<Integer, Parada>();
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				if (linea.trim().isEmpty())
					continue;
				String partes[] = linea.split(";");
				int codigo = Integer.parseInt(partes[0].trim());
				String direccion = partes[1].trim();
				double latitud = Double.parseDouble(partes[2].trim());
				double longitud = Double.parseDouble(partes[3].trim());
				Parada parada = new Parada(codigo, direccion, latitud, longitud);
				paradas.put(codigo, parada);
			}
		}
		paradasCargadas = paradas;
		return paradas;
	}

	public static Map<String, Tramo> cargarTramos(String nombreArchivo, Map<Integer, Parada> paradas)
			throws FileNotFoundException {
		Map<String, Tramo> tramos = new LinkedHashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
			String lineaTexto;
			while ((lineaTexto = br.readLine()) != null) {
				if (lineaTexto.trim().isEmpty())
					continue;
				String partes[] = lineaTexto.split(";");
				int codigoInicio = Integer.parseInt(partes[0].trim());
				int codigoFin = Integer.parseInt(partes[1].trim());
				int tiempo = Integer.parseInt(partes[2].trim());
				int tipo = Integer.parseInt(partes[3].trim());

				Parada inicio = paradas.get(codigoInicio);
				Parada fin = paradas.get(codigoFin);

				if (inicio != null && fin != null) {
					Tramo tramo = new Tramo(inicio, fin, tiempo, tipo);
					String key = codigoInicio + "-" + codigoFin;
					tramos.put(key, tramo);

					// Agregamos también el tramo inverso
					Tramo tramoInverso = new Tramo(fin, inicio, tiempo, tipo);
					String keyInverso = codigoFin + "-" + codigoInicio;
					tramos.put(keyInverso, tramoInverso);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tramos;
	}

	public static Map<String, Linea> cargarLineas(String nombreArchivo, String nombreArchivoFrecuencia,
			Map<Integer, Parada> paradas) throws FileNotFoundException {
		Map<String, Linea> lineas = new LinkedHashMap<>();

		// Cargar líneas y sus paradas
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
			String lineaTexto;
			while ((lineaTexto = br.readLine()) != null) {
				if (lineaTexto.trim().isEmpty())
					continue;
				String[] partes = lineaTexto.split(";");
				String codigo = partes[0].trim();
				String nombreLinea = partes[1].trim();

				Linea linea = new Linea(codigo, nombreLinea);

				for (int i = 2; i < partes.length; i++) {
					String paradaStr = partes[i].trim();
					if (!paradaStr.isEmpty()) {
						int codigoParada = Integer.parseInt(paradaStr);
						Parada parada = paradas.get(codigoParada);
						if (parada != null) {
							linea.agregarParada(parada);
						}
					}
				}

				lineas.put(linea.getCodigo(), linea);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Cargar frecuencias
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivoFrecuencia))) {
			String lineaTexto;
			int contadorFrecuencias = 0;
			while ((lineaTexto = br.readLine()) != null) {
				if (lineaTexto.trim().isEmpty())
					continue;

				String[] partes = lineaTexto.split(";");

				// Validar que hay al menos 3 partes
				if (partes.length < 3) {
					System.out.println("Línea inválida en frecuencias: " + lineaTexto);
					continue;
				}

				try {
					String codigoLinea = partes[0].trim();
					int diaSemana = Integer.parseInt(partes[1].trim());
					String horaStr = partes[2].trim();

					if (horaStr.isEmpty()) {
						System.out.println("Hora vacía para línea: " + codigoLinea);
						continue;
					}

					LocalTime hora = LocalTime.parse(horaStr);

					Linea linea = lineas.get(codigoLinea);
					if (linea != null) {
						linea.agregarFrecuencia(diaSemana, hora);
						contadorFrecuencias++;
					} else {
						System.out.println("Línea no encontrada: " + codigoLinea);
					}
				} catch (Exception e) {
					System.out.println("Error procesando línea: " + lineaTexto);
					e.printStackTrace();
				}
			}
			// System.out.println("Total frecuencias cargadas: " + contadorFrecuencias);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lineasCargadas = lineas;

		return lineas;
	}
	
	// -----------------------------------------------------
	// MÉTODOS AÑADIDOS (EL PARCHE)
	// -----------------------------------------------------

	/**
	 * Permite al Coordinador "publicar" las paradas cargadas
	 * para que los DAOs (LineaDAO, TramoDAO) puedan usarlas.
	 */
	public static void setParadasCargadas(Map<Integer, Parada> paradas) {
		paradasCargadas = paradas;
	}

	/**
	 * Permite al Coordinador "publicar" las líneas cargadas
	 * para que la clase estática Calculo pueda usarlas.
	 */
	public static void setLineasCargadas(Map<String, Linea> lineas) {
		lineasCargadas = lineas;
	}
}