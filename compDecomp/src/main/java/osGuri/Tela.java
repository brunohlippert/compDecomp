package osGuri;

import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Tela {
    private Stage mainStage;
    private Scene cenaOperacoes;

    private FileChooser fileComp;
    private FileChooser fileDecomp;

    private File comFile;
    private File decompFile;
    private Compacter comp;
    private Decompacter decomp;

    public Tela(Stage mainStage, Compacter comp, Decompacter decomp) { // conta
        this.mainStage = mainStage;
        fileComp = new FileChooser();
        fileDecomp = new FileChooser();

        this.comp = comp;
        this.decomp = decomp;
    }

    public Scene getTela() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // COMPACTER
        grid.add(new Label("Compactador"), 0, 0);

        Button escolherTxtComp = new Button("Escolher txt");
        grid.add(escolherTxtComp, 0, 1);

        Button btnComp = new Button("Compactar");

        grid.add(btnComp, 0, 2);

        // DECOMPACTER
        grid.add(new Label("Descompactador"), 2, 0);

        Button escolherTxtDecomp = new Button("Escolher zip");
        grid.add(escolherTxtDecomp, 2, 1);

        Button btnDecomp = new Button("Descompactar");

        btnComp.setOnAction(e -> {
            if (comFile != null) {
                String nomrArq = comFile.getName();
                String pasta = comFile.getParent();
                comp.compactaArquivo(pasta+"/", nomrArq);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Compactação concluida");
                alert.setHeaderText("Arquivo compactado");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("404");
                alert.setHeaderText("Arquivo não selecionado");
                alert.setContentText("Selecione um arquivo!");
                alert.showAndWait();
            }
		});

		btnDecomp.setOnAction(e -> {
            if(decompFile != null){
                String nomrArq = decompFile.getName();
                String pasta = decompFile.getParent();
                if(!decomp.descompacta(pasta+"/", nomrArq)){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Arquivo inválido");
                    alert.setContentText("Selecione um arquivo válido para descompactar!");
                    alert.showAndWait();
                }else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Descompactação concluida");
                    alert.setHeaderText("Arquivo descompactado");
                    alert.showAndWait();
                }
            }else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("404");
                alert.setHeaderText("Arquivo não selecionado");
                alert.setContentText("Selecione um arquivo!");
                alert.showAndWait();
            }
        });

        escolherTxtComp.setOnAction(e -> {
            comFile = fileComp.showOpenDialog(mainStage);
		});

		escolherTxtDecomp.setOnAction(e -> {
            decompFile = fileDecomp.showOpenDialog(mainStage);
        });
        
        grid.add(btnDecomp, 2, 2);

		cenaOperacoes = new Scene(grid);
		return cenaOperacoes;
	}

}
