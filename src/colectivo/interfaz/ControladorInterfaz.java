package colectivo.interfaz;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import colectivo.controlador.Coordinador;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ControladorInterfaz {

    // Controles de la vista
    @FXML private ComboBox<Parada> comboOrigen;
    @FXML private ComboBox<Parada> comboDestino;
    @FXML private ComboBox<String> comboDia;

    @FXML private Label lblHoraSeleccionada;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnCalcular;

    @FXML private TextArea resultadoArea;

    // Estado
    private Coordinador coordinador;

    private Map<String, Integer> diasMap = new HashMap<>();
    private final List<LocalTime> horariosDisponibles = new ArrayList<>();
    private LocalTime horaSeleccionada;

    // Llamar desde AplicacionConsultas luego de cargar el FXML
    public void init(Coordinador coordinador, List<Parada> paradasDisponibles) {
        this.coordinador = coordinador;

        // Poblar combos
        comboOrigen.getItems().setAll(paradasDisponibles);
        comboDestino.getItems().setAll(paradasDisponibles);

        // Días
        comboDia.getItems().setAll("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo");
        diasMap.put("Lunes", 1);
        diasMap.put("Martes", 2);
        diasMap.put("Miércoles", 3);
        diasMap.put("Jueves", 4);
        diasMap.put("Viernes", 5);
        diasMap.put("Sábado", 6);
        diasMap.put("Domingo", 7);

        // Eventos
        comboOrigen.setOnAction(e -> recalcularHorariosDisponibles());
        comboDia.setOnAction(e -> recalcularHorariosDisponibles());

        // Estado inicial
        actualizarHoraSeleccionada(null);
        actualizarEstadoBotones();
    }

    @FXML
    private void onAnterior() {
        if (horariosDisponibles.isEmpty() || horaSeleccionada == null) return;
        int i = horariosDisponibles.indexOf(horaSeleccionada);
        i = (i - 1 + horariosDisponibles.size()) % horariosDisponibles.size();
        horaSeleccionada = horariosDisponibles.get(i);
        actualizarHoraSeleccionada(horaSeleccionada);
    }

    @FXML
    private void onSiguiente() {
        if (horariosDisponibles.isEmpty() || horaSeleccionada == null) return;
        int i = horariosDisponibles.indexOf(horaSeleccionada);
        i = (i + 1) % horariosDisponibles.size();
        horaSeleccionada = horariosDisponibles.get(i);
        actualizarHoraSeleccionada(horaSeleccionada);
    }

    @FXML
    private void onCalcular() {
        Parada origen = comboOrigen.getValue();
        Parada destino = comboDestino.getValue();
        String dia = comboDia.getValue();

        if (origen == null || destino == null || dia == null || horaSeleccionada == null) {
            pintarMensajeAdvertencia("⚠️ Complete origen, destino, día y seleccione un horario con los botones.");
            return;
        }
        if (origen.equals(destino)) {
            pintarMensajeAdvertencia("⚠️ La parada de origen y destino no pueden ser la misma.");
            return;
        }

        Integer numeroDia = diasMap.get(dia);
        try {
            List<List<Recorrido>> recorridos = coordinador.calcularRecorrido(origen, destino, numeroDia, horaSeleccionada);
            mostrarResultados(recorridos);
        } catch (Exception ex) {
            pintarMensajeError("❌ Error al calcular el recorrido: " + ex.getMessage());
        }
    }

    private void recalcularHorariosDisponibles() {
        horariosDisponibles.clear();
        horaSeleccionada = null;
        actualizarHoraSeleccionada(null);
        actualizarEstadoBotones();

        Parada origen = comboOrigen.getValue();
        String dia = comboDia.getValue();
        if (origen == null || dia == null) return;

        Integer numeroDia = diasMap.get(dia);
        if (numeroDia == null) return;

        List<Linea> lineas = coordinador.getLineas();
        Map<String, Tramo> tramos = coordinador.getTramos();
        if (lineas == null || tramos == null) {
            pintarMensajeAdvertencia("ℹ️ No hay datos suficientes para calcular horarios en la parada.");
            return;
        }

        for (Linea linea : lineas) {
            List<Parada> paradas = linea.getParadas();
            int idxOrigen = paradas.indexOf(origen);
            if (idxOrigen < 0) continue;

            int tiempoHastaOrigen = calcularTiempoEntreParadas(paradas, 0, idxOrigen, tramos);

            linea.getFrecuencias().stream()
                .filter(f -> f.getDiaSemana() == numeroDia)
                .map(f -> f.getHora().plusSeconds(tiempoHastaOrigen))
                .forEach(horariosDisponibles::add);
        }

        // Ordenar y quitar duplicados manteniendo orden
        Collections.sort(horariosDisponibles);
        Set<LocalTime> set = new LinkedHashSet<>(horariosDisponibles);
        horariosDisponibles.clear();
        horariosDisponibles.addAll(set);

        if (!horariosDisponibles.isEmpty()) {
            LocalTime ahora = LocalTime.now();
            horaSeleccionada = horariosDisponibles.stream()
                    .filter(h -> !h.isBefore(ahora))
                    .findFirst()
                    .orElse(horariosDisponibles.get(0));
            actualizarHoraSeleccionada(horaSeleccionada);
        }
        actualizarEstadoBotones();
    }

    private int calcularTiempoEntreParadas(List<Parada> paradas, int idxInicio, int idxFin, Map<String, Tramo> tramos) {
        int tiempo = 0;
        for (int i = idxInicio; i < idxFin; i++) {
            if (i + 1 >= paradas.size()) break;
            String clave = paradas.get(i).getCodigo() + "-" + paradas.get(i + 1).getCodigo();
            Tramo tramo = tramos.get(clave);
            if (tramo != null) {
                tiempo += tramo.getTiempo();
            }
        }
        return tiempo;
    }

    private void actualizarHoraSeleccionada(LocalTime hora) {
        lblHoraSeleccionada.setText(hora == null ? "--:--" : String.format("%02d:%02d", hora.getHour(), hora.getMinute()));
    }

    private void actualizarEstadoBotones() {
        boolean habilitar = comboOrigen.getValue() != null && comboDia.getValue() != null && !horariosDisponibles.isEmpty();
        btnAnterior.setDisable(!habilitar);
        btnSiguiente.setDisable(!habilitar);
    }

    private void mostrarResultados(List<List<Recorrido>> listaRecorridos) {
        if (listaRecorridos == null || listaRecorridos.isEmpty()) {
            resultadoArea.setText("ℹ️ No hay recorridos disponibles para la búsqueda realizada.");
            resultadoArea.setStyle("-fx-control-inner-background: #d1ecf1; -fx-text-fill: #0c5460;");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("          RECORRIDOS DISPONIBLES\n");
        sb.append("═══════════════════════════════════════════\n\n");

        int contador = 1;
        for (List<Recorrido> opcion : listaRecorridos) {
            sb.append("🚏 Opción ").append(contador++).append(":\n");
            sb.append("───────────────────────────────────────────\n");
            for (Recorrido r : opcion) {
                if (r.getLinea() != null) {
                    sb.append("  🚍 Línea: ").append(r.getLinea().getNombre())
                      .append(" (").append(r.getLinea().getCodigo()).append(")\n");
                } else {
                    sb.append("  🚶 Tramo Caminando\n");
                }
                List<Parada> ps = r.getParadas();
                if (!ps.isEmpty()) {
                    sb.append("     Desde: ").append(ps.get(0).getDireccion()).append("\n");
                    sb.append("     Hasta: ").append(ps.get(ps.size() - 1).getDireccion()).append("\n");
                }
                sb.append("     Sale:  ").append(r.getHoraSalida()).append("\n");
                int dur = r.getDuracion();
                sb.append("     Duración: ").append(dur / 60).append(" min ").append(dur % 60).append(" seg\n\n");
            }
        }
        resultadoArea.setText(sb.toString());
        resultadoArea.setStyle("-fx-control-inner-background: #f8f9fa; -fx-text-fill: black;");
    }

    private void pintarMensajeAdvertencia(String msg) {
        resultadoArea.setText(msg);
        resultadoArea.setStyle("-fx-control-inner-background: #fff3cd; -fx-text-fill: #856404;");
    }

    private void pintarMensajeError(String msg) {
        resultadoArea.setText(msg);
        resultadoArea.setStyle("-fx-control-inner-background: #f8d7da; -fx-text-fill: #721c24;");
    }
}