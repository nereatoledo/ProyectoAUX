package colectivo.interfaz;

import java.time.LocalTime;
import java.util.List;
// Quitamos import de Application, Stage, Scene
import colectivo.aplicacion.Coordinador;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import javafx.collections.FXCollections; // Importante
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent; // Importamos Parent
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.HBox; // Para el selector de hora
import javafx.scene.layout.Priority; // Para agrandar el área de resultados
import javafx.scene.layout.Region; 

// --- YA NO ES "extends Application" ---
public class InterfazJavaFX {

	private Coordinador coordinador;
	
	private ComboBox<Parada> comboOrigen;
	private ComboBox<Parada> comboDestino;
	private ComboBox<Integer> comboDia;
	
	private ComboBox<String> comboHora;
	private ComboBox<String> comboMinuto;
	
	private TextArea resultadoArea;
	
	private VBox rootLayout; 
	
	/**
	 * Constructor que construye la interfaz.
	 * Reemplaza al método start().
	 * @param coordinador La instancia del coordinador.
	 * @param paradasDisponibles La lista de paradas cargadas por AplicacionConsultas.
	 */
	public InterfazJavaFX(Coordinador coordinador, List<Parada> paradasDisponibles) {
		this.coordinador = coordinador;
		
		// --- CREAR COMPONENTES ---
		comboOrigen = new ComboBox<>();
		comboDestino = new ComboBox<>();
		comboDia = new ComboBox<>();
		
		comboHora = new ComboBox<>();
		comboMinuto = new ComboBox<>();
		
	    resultadoArea = new TextArea();
	    resultadoArea.setEditable(false);
	    
	    configurarEstilosComponentes();
	    
	    // --- POBLAR LOS COMBOBOX ---
	    comboOrigen.setItems(FXCollections.observableArrayList(paradasDisponibles));
	    comboDestino.setItems(FXCollections.observableArrayList(paradasDisponibles));
	    
	    for(int i = 1; i <= 7; i++)
	    	comboDia.getItems().add(i);
	    comboDia.setValue(1); // Valor por defecto
	    
	    for (int i = 0; i <= 23; i++) {
	    	comboHora.getItems().add(String.format("%02d", i));
	    }
	    for (int i = 0; i <= 59; i++) {
	    	comboMinuto.getItems().add(String.format("%02d", i));
	    }
	    comboHora.setValue("10"); // Valor por defecto
	    comboMinuto.setValue("00"); // Valor por defecto
	    
	    
	    // --- CONFIGURAR INTERFAZ ---
	    Label titulo = new Label("Consulta de Recorridos");
	    titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
	    titulo.setStyle("-fx-text-fill: #2c3e50;");
	    
	    Label lblOrigen = crearLabel("Parada de Origen:");
	    Label lblDestino = crearLabel("Parada de Destino:");
	    Label lblDia = crearLabel("Día de la Semana:");
	    Label lblHora = crearLabel("Hora de Llegada:"); 
	    Label lblResultados = crearLabel("Resultados:");
	    
	    Button calcularButton = new Button("🔍 Calcular Recorrido");
	    calcularButton.setOnAction(e -> calcularRecorrido());
	    calcularButton.setMaxWidth(Double.MAX_VALUE);
	    
	     calcularButton.setStyle(
	        "-fx-background-color: #3498db;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-size: 16px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 12px;" +
	        "-fx-background-radius: 8px;" +
	        "-fx-cursor: hand;"
	    );
	    
	    calcularButton.setOnMouseEntered(e -> 
	        calcularButton.setStyle(
	            "-fx-background-color: #2980b9;" +
	            "-fx-text-fill: white;" +
	            "-fx-font-size: 16px;" +
	            "-fx-font-weight: bold;" +
	            "-fx-padding: 12px;" +
	            "-fx-background-radius: 8px;" +
	            "-fx-cursor: hand;"
	        )
	    );
	    calcularButton.setOnMouseExited(e -> 
	        calcularButton.setStyle(
	            "-fx-background-color: #3498db;" +
	            "-fx-text-fill: white;" +
	            "-fx-font-size: 16px;" +
	            "-fx-font-weight: bold;" +
	            "-fx-padding: 12px;" +
	            "-fx-background-radius: 8px;" +
	            "-fx-cursor: hand;"
	        )
	    );
	    
	    
	    // --- CONSTRUIR EL LAYOUT ---
	    rootLayout = new VBox(15);
	    rootLayout.setPadding(new Insets(25));
	    rootLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");
	    
	    VBox formPanel = new VBox(12);
	    formPanel.setPadding(new Insets(20));
	    formPanel.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"
	    );
	    // --- LIMITAMOS EL CRECIMIENTO DEL PANEL DE FORMULARIO ---
	    // Hacemos que el panel de formulario use su tamaño preferido y NO crezca cuando la ventana se expanda.
	    formPanel.setMaxHeight(Region.USE_PREF_SIZE); 
	    
	    Label lblDosPuntos = new Label(":");
	    lblDosPuntos.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	    HBox horaPanel = new HBox(8, comboHora, lblDosPuntos, comboMinuto);
	    horaPanel.setAlignment(Pos.CENTER_LEFT);
	    
	    formPanel.getChildren().addAll(
	        lblOrigen, comboOrigen,
	        lblDestino, comboDestino,
	        lblDia, comboDia,
	        lblHora, horaPanel, 
	        calcularButton
	    );
	    
	    VBox resultadosPanel = new VBox(10);
	    resultadosPanel.setPadding(new Insets(20));
	    resultadosPanel.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"
	    );
	    
	    // Aumentamos el tamaño mínimo del panel de resultados para que ocupe más espacio visual.
	    resultadosPanel.setMinHeight(300);
	    
	    resultadoArea.setWrapText(true);
	    // Aumentamos la altura preferida del área de resultados y el tamaño de fuente para mejor legibilidad.
	    resultadoArea.setPrefHeight(420);
	    resultadoArea.setStyle(
	        "-fx-control-inner-background: #f8f9fa;" +
	        "-fx-font-family: 'Consolas', 'Courier New';" +
	        "-fx-font-size: 14px;" +    // fuente un poco más grande
	        "-fx-border-color: #dcdde1;" +
	        "-fx-border-width: 1px;" +
	        "-fx-border-radius: 5px;"
	    );
	    
	    VBox.setVgrow(resultadoArea, Priority.ALWAYS); 
	    
	    resultadosPanel.getChildren().addAll(lblResultados, resultadoArea);
	    
	    titulo.setAlignment(Pos.CENTER);
	    titulo.setMaxWidth(Double.MAX_VALUE); 
	    
	    rootLayout.getChildren().addAll(titulo, formPanel, resultadosPanel);
	    
	    // --- AJUSTE DE CRECIMIENTO VERTICAL ---
	    // 1. Evitamos que el panel del formulario crezca (queda con su tamaño preferido).
	    VBox.setVgrow(formPanel, Priority.NEVER);
	    
	    // 2. Hacemos que el panel de resultados SIEMPRE crezca y ocupe el espacio restante.
	    VBox.setVgrow(resultadosPanel, Priority.ALWAYS);
	}
	
	/**
	 * Método para que AplicacionConsultas obtenga la UI
	 */
	public Parent getRoot() {
		return rootLayout;
	}
	
	private void configurarEstilosComponentes() {
		String comboStyle = 
	        "-fx-background-color: white;" +
	        "-fx-border-color: #bdc3c7;" +
	        "-fx-border-width: 2px;" +
	        "-fx-border-radius: 5px;" +
	        "-fx-background-radius: 5px;" +
	        "-fx-font-size: 13px;" +
	        "-fx-padding: 8px;";
	    
	    comboOrigen.setStyle(comboStyle);
	    comboDestino.setStyle(comboStyle);
	    comboDia.setStyle(comboStyle);
	    
	    comboHora.setStyle(comboStyle);
	    comboMinuto.setStyle(comboStyle);
	    comboHora.setPrefWidth(90); 
	    comboMinuto.setPrefWidth(90);
	    
	    comboOrigen.setMaxWidth(Double.MAX_VALUE);
	    comboDestino.setMaxWidth(Double.MAX_VALUE);
	    comboDia.setMaxWidth(Double.MAX_VALUE);
	    
	}
	
	private Label crearLabel(String texto) {
	    Label label = new Label(texto);
	    label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
	    label.setStyle("-fx-text-fill: #34495e;");
	    return label;
	}
	
	public void calcularRecorrido() {
		try {
			Parada origen = comboOrigen.getValue();
			Parada destino = comboDestino.getValue();
			Integer dia = comboDia.getValue();
			
			String horaStr = comboHora.getValue();
			String minStr = comboMinuto.getValue();
			
			if(origen == null || destino == null || dia == null || horaStr == null || minStr == null) {
				resultadoArea.setText("⚠️ Por favor complete todos los campos (incluyendo hora y minutos)."); 
				resultadoArea.setStyle(
					"-fx-control-inner-background: #fff3cd;" +
					"-fx-text-fill: #856404;" +
					"-fx-font-family: 'Arial';" +
					"-fx-font-size: 13px;" +
					"-fx-border-color: #ffc107;" +
					"-fx-border-width: 2px;" +
					"-fx-border-radius: 5px;"
				);
				return;
			}
			
            if (origen.equals(destino)) {
                resultadoArea.setText("⚠️ La parada de origen y destino no pueden ser la misma.");
                resultadoArea.setStyle(
					"-fx-control-inner-background: #fff3cd;" +
					"-fx-text-fill: #856404;" +
					"-fx-font-family: 'Arial';" +
					"-fx-font-size: 13px;" +
					"-fx-border-color: #ffc107;" +
					"-fx-border-width: 2px;" +
					"-fx-border-radius: 5px;"
				);
                return;
            }
			
			LocalTime hora = LocalTime.of(Integer.parseInt(horaStr), Integer.parseInt(minStr));

			
			List<List<Recorrido>> recorridos = coordinador.calcularRecorrido(origen, destino, dia, hora);
			mostrarResultados(recorridos);
			
		} catch (Exception e) {
			resultadoArea.setText("❌ Error inesperado al calcular el recorrido: " + e.getMessage());
			resultadoArea.setStyle(
				"-fx-control-inner-background: #f8d7da;" +
				"-fx-text-fill: #721c24;" +
				"-fx-font-family: 'Arial';" +
				"-fx-font-size: 13px;" +
				"-fx-border-color: #dc3545;" +
				"-fx-border-width: 2px;" +
				"-fx-border-radius: 5px;"
			);
			e.printStackTrace(); 
		}
	}
	
	
	public void mostrarResultados(List<List<Recorrido>> listaRecorridos) {
		if(listaRecorridos == null || listaRecorridos.isEmpty()) {
			resultadoArea.setText("ℹ️ No hay recorridos disponibles para la búsqueda realizada.");
			resultadoArea.setStyle(
				"-fx-control-inner-background: #d1ecf1;" +
				"-fx-text-fill: #0c5460;" +
				"-fx-font-family: 'Arial';" +
				"-fx-font-size: 13px;" +
				"-fx-border-color: #17a2b8;" +
				"-fx-border-width: 2px;" +
				"-fx-border-radius: 5px;"
			);
			return;
		}
		
		resultadoArea.setStyle(
			"-fx-control-inner-background: #f8f9fa;" +
			"-fx-font-family: 'Consolas', 'Courier New';" +
			"-fx-font-size: 14px;" +
			"-fx-border-color: #28a745;" +
			"-fx-border-width: 2px;" +
			"-fx-border-radius: 5px;"
		);
		
		StringBuilder sb = new StringBuilder();
		sb.append("═══════════════════════════════════════════\n");
		sb.append("          RECORRIDOS DISPONIBLES\n"); 
		sb.append("═══════════════════════════════════════════\n\n");
		
		int contador = 1;
		for(List<Recorrido> r : listaRecorridos) {
			sb.append("🚏 Opción ").append(contador).append(":\n");
			sb.append("───────────────────────────────────────────\n");
			
			for(Recorrido recorrido : r) {
				
				if (recorrido.getLinea() != null) {
					sb.append("  🚍 Línea: ").append(recorrido.getLinea().getNombre())
					  .append(" (").append(recorrido.getLinea().getCodigo()).append(")\n");
				} else {
					sb.append("  🚶 Tramo Caminando\n"); 
				}

				List<Parada> paradasDelRecorrido = recorrido.getParadas(); 
				if (!paradasDelRecorrido.isEmpty()) {
					sb.append("     Desde: ").append(paradasDelRecorrido.get(0).getDireccion()).append("\n");
					sb.append("     Hasta: ").append(paradasDelRecorrido.get(paradasDelRecorrido.size() - 1).getDireccion()).append("\n");
				}

				sb.append("     Sale:  ").append(recorrido.getHoraSalida()).append("\n");
				
				int duracionTotalSegundos = recorrido.getDuracion();
				int minutos = duracionTotalSegundos / 60;
				int segundos = duracionTotalSegundos % 60;
				sb.append("     Duración: ").append(minutos).append(" min ").append(segundos).append(" seg\n\n");
			}
			contador++;
		}
		resultadoArea.setText(sb.toString());
	}
}