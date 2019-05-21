import java.io.IOException;

public class App{
    public static void main (String args[]){
        Compacter comp = new Compacter();
        Decompacter decomp = new Decompacter();
        comp.leTXT();
        comp.montaArvore();
        comp.compactaTXT();
        try{
            comp.escreveHeader();
            
        } catch(IOException e){
            System.out.println(e);
        }

        decomp.montaTabela();
        decomp.leBIN();
        decomp.descompactaBinario();
        decomp.salvaArquivoDescompactado();
    }
}