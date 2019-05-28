package osGuri;

import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;

public class Decompacter {
    
    private HashMap<String,Character> tabelaCodigos;
    private String headerPath;
    private String compPath;
    private String decompPath;
    private String textoBinario;
    private String textoDescomp;
    private int skipBits;

    public Decompacter(){
        tabelaCodigos = new HashMap<>();
    }

    public boolean descompacta(String pasta, String nomeArq){
        String nomeOriginal = nomeArq.split("Compactado")[1].split(".zip")[0];
        compPath = pasta+nomeArq;
        headerPath = pasta+"Header"+nomeOriginal;
        decompPath = pasta+nomeOriginal;

        if(!montaTabela())
            return false;
        if(!leBIN())
            return false;
        descompactaBinario();
        salvaArquivoDescompactado();
        return true;
    }

    public boolean montaTabela(){
        try {
            Scanner scan = new Scanner(new File(headerPath));
            skipBits = Integer.valueOf(scan.next());
            scan.nextLine();
            while (scan.hasNextLine()) {
                String linha[] = scan.nextLine().split("");
                String carac = linha[0];
                String binCode = "";
                if(linha[1].equals(":")){
                    for(int i=2;i<linha.length;i++){
                        binCode+= linha[i];
                    }   
                }else{
                    carac+= linha[1];
                    for(int i=3;i<linha.length;i++){
                        binCode+= linha[i];
                    } 
                }
                if(carac.equals("\\n")){
                    tabelaCodigos.put(binCode, '\n');
                }else if(carac.equals("CR")){
                    tabelaCodigos.put(binCode, (char)13);
                }else{
                    tabelaCodigos.put(binCode, carac.charAt(0));
                }
            }
            // tabelaCodigos.forEach((k,v) -> System.out.println(k+":"+v));
            scan.close();
        }catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean leBIN(){
        StringBuilder strBuilder = new StringBuilder();
        try{
            byte[] arq = java.nio.file.Files.readAllBytes(new File(compPath).toPath());
            for(int i = 0; i < arq.length; i++){
                strBuilder.append(String.format("%8s", Integer.toBinaryString(arq[i] & 0xFF)).replace(' ', '0'));
            }
            textoBinario = strBuilder.substring(0, strBuilder.length() - skipBits);   
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public void descompactaBinario(){
        Scanner sc = new Scanner(textoBinario);
        sc.useDelimiter("");
        StringBuilder strAux = new StringBuilder();
        StringBuilder textoFinal = new StringBuilder();
        while(sc.hasNext()){
            strAux.append(sc.next());
            if(tabelaCodigos.get(strAux.toString()) != null){
                textoFinal.append(tabelaCodigos.get(strAux.toString()));
                strAux = new StringBuilder();
            }
        }
        textoDescomp = textoFinal.toString();
    }

    public void salvaArquivoDescompactado(){
        try{
            FileWriter arq = new FileWriter(decompPath);
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.print(textoDescomp);
            arq.close();
        }catch(Exception e){
            
        }
    }
}