package osGuri;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private Compacter comp;
    private Decompacter decomp;

    Tela tela;
    @Override
    public void start(Stage primaryStage) throws Exception {
        comp = new Compacter();
        decomp = new Decompacter();
        // comp.compactaArquivo("/home/conseg/alest/compDecomp/src/main/java/osGuri/", "king_james.txt");
        // decomp.descompacta("/home/conseg/alest/compDecomp/src/main/java/osGuri/", "Compactadoking_james.txt.zip");
        primaryStage.setTitle("Huffman Alest 2");
        tela = new Tela(primaryStage, comp, decomp); 
        primaryStage.setScene(tela.getTela());
        primaryStage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}