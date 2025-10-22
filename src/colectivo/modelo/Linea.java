package colectivo.modelo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Linea {

	private String codigo;
	private String nombre;
	private List<Parada> paradas;
	private List<Frecuencia> frecuencias;

	public Linea() {
		this.paradas = new ArrayList<Parada>();
		this.frecuencias = new ArrayList<Frecuencia>();
	}

	public Linea(String codigo, String nombre) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
		this.paradas = new ArrayList<Parada>();
		this.frecuencias = new ArrayList<Frecuencia>();
	}

	public void agregarParada(Parada parada) {
		paradas.add(parada);
		parada.agregarLinea(this);
	}

	public void agregarFrecuencia(int diaSemana, LocalTime hora) {
		frecuencias.add(new Frecuencia(diaSemana, hora));
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Parada> getParadas() {
		return paradas;
	}

	public List<Frecuencia> getFrecuencias() {
		return frecuencias;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Linea other = (Linea) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Linea [codigo=" + codigo + ", nombre=" + nombre + "]";
	}

	public class Frecuencia {

		private int diaSemana;
		private LocalTime hora;

		public Frecuencia(int diaSemana, LocalTime hora) {
			super();
			this.diaSemana = diaSemana;
			this.hora = hora;
		}

		public int getDiaSemana() {
			return diaSemana;
		}

		public void setDiaSemana(int diaSemana) {
			this.diaSemana = diaSemana;
		}

		public LocalTime getHora() {
			return hora;
		}

		public void setHora(LocalTime hora) {
			this.hora = hora;
		}

		@Override
		public String toString() {
			return "Frecuencia [diaSemana=" + diaSemana + ", hora=" + hora + "]";
		}
	}
}
