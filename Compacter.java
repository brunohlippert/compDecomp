import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;

public class Compacter {

  private class TreeNodeChar implements Comparable<TreeNodeChar>{
    public char carac;
    public int freq;
    public TreeNodeChar left;
    public TreeNodeChar right;

    public TreeNodeChar(char carac) {
      this.carac = carac;
      this.freq = 1;
      this.left = null;
      this.right = null;
    }

    public TreeNodeChar(char carac, int freq) {
      this.carac = carac;
      this.freq = freq;
      this.left = null;
      this.right = null;
    }

    public TreeNodeChar(int freq, TreeNodeChar left, TreeNodeChar right) {
      this.carac = ' ';
      this.freq = freq;
      this.left = left;
      this.right = right;
    }

    public void incFreq() {
      this.freq++;
    }

    public int compareTo(TreeNodeChar node){
      return this.freq - node.freq;
    }

    public String toString() {
      if (carac == '\n') {
        return "Char: '\\n', frequencia: "+freq;
      } else if(((int)carac == 13)){
        return "Char: 'CR', frequencia: "+freq;
      }
       else {
        return "Char: '"+String.valueOf(carac)+"' frequencia: "+freq;
      }
    }
  }

  private HashMap<Character, Integer> charMap;
  private PriorityQueue<TreeNodeChar> minPQ;
  private final String path = "king_james.txt";
  private HashMap<Character,String> tabelaCodigos;
  private int skipChars;

  public Compacter() {
    charMap = new HashMap<>();
    tabelaCodigos = new HashMap<>();
    minPQ = new PriorityQueue<>();
  }

  public void leTXT() {
    try {
      Scanner scan = new Scanner(new File(path));
      scan.useDelimiter("");
      while (scan.hasNext()) {
        char letra = scan.next().charAt(0);
        if (charMap.containsKey(letra)) {
          charMap.put(letra, charMap.get(letra) + 1);
        } else {
          charMap.put(letra, 1);
        }
        /*
         * OR WE CAN DO... charMap.put(letra, charMap.getOrDefault(letra, 0) +1); sem os
         * 'ifs'. que coloca no mapa a chave 'letra' e associa ao valor do get!, se a
         * chave ja tiver valor associado entao ele pega o valor do get no mapa, caso
         * ela nao esteja associada ele retorna o valor default, no caso '0'.
         */
      }
      scan.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    // IMPRESSAO DOS DADOS DO MAPA!
    /*
     * charMap.forEach( (letra,freq) -> { if(letra.equals('\n')){
     * //System.out.println("Letra :'"+String.valueOf(letra.hashCode())
     * +"', Frequencia :"+freq); //String s = String.valueOf("\r"+letra);
     * //System.out.printf("Letra : '\\%c', Frequencia : %d\n",letra,freq );
     * System.out.println("Letra :'\\n', Frequencia :"+freq); //Gambiarra se nao
     * essa jossa cria uma nova linha na impressao }else{
     * System.out.println("Letra :'"+String.valueOf(letra)+"', Frequencia :"+freq);
     * } });
     */
  }

  public void montaArvore() {
    charMap.forEach((letra, freq) -> {
      TreeNodeChar node = new TreeNodeChar(letra, freq);
      minPQ.add(node);
    });
    // IMPRESSAO REMOVENDO NA ORDEM QUE A PQ ORDENA A PRIORIDADE
    // MENOR FREQUENCIA PRIMEIRO
    while (minPQ.size() != 1){
      TreeNodeChar nodeAuxLeft = minPQ.remove();
      TreeNodeChar nodeAuxRight = minPQ.remove();
      TreeNodeChar nodeAuxRoot = new TreeNodeChar(nodeAuxLeft.freq+nodeAuxRight.freq, nodeAuxLeft, nodeAuxRight);
      
      minPQ.add(nodeAuxRoot);
    }

    montaTabelaCodigos(minPQ.remove(), "");
  }

  public void escreveHeader() throws IOException{
    FileWriter arq = new FileWriter("header.txt");
    PrintWriter gravarArq = new PrintWriter(arq);
    gravarArq.printf(String.valueOf(skipChars)+"\n");
    tabelaCodigos.forEach( (letra,codigo) -> { 
      if(letra.equals('\n')){
        gravarArq.printf("\\n:"+codigo+"\n");
      } else if(((int)letra == 13)){
        gravarArq.printf("CR:"+codigo+"\n");
      } else {
        gravarArq.printf("%c:"+codigo+"\n", letra);
      }
    });

    arq.close();
  }

  public void montaTabelaCodigos(TreeNodeChar tree, String codigo){
    if(tree.left == null && tree.right == null){
      tabelaCodigos.put(tree.carac, codigo);
    } else {
      montaTabelaCodigos(tree.left, codigo+"0");
      montaTabelaCodigos(tree.right, codigo+"1");
    } 
  }

  static byte[] decodeBinary(String s) {
    if (s.length() % 8 != 0) throw new IllegalArgumentException(
        "Binary data length must be multiple of 8");
    byte[] data = new byte[s.length() / 8];
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c == '1') {
            data[i >> 3] |= 0x80 >> (i & 0x7);
        } else if (c != '0') {
            throw new IllegalArgumentException("Invalid char in binary string");
        }
    }
    return data;
  }

  public void compactaTXT(){
    try {
      Scanner scan = new Scanner(new File(path));
      scan.useDelimiter("");
      StringBuilder strBuild = new StringBuilder();
      while (scan.hasNext()) { 
        char letra = scan.next().charAt(0);
        String codBin = tabelaCodigos.get(letra);
        strBuild.append(codBin);
      }
      skipChars = strBuild.toString().length() % 8;
      for(int i = 0; i < skipChars; i++){
        strBuild.append("0");
      }
      byte[] data = decodeBinary(strBuild.toString());
      java.nio.file.Files.write(new File("compactado.zip").toPath(), data);
      scan.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*public static void main(String args[]) {
    Compacter comp = new Compacter();
    comp.leTXT();
    comp.montaArvore();
    comp.compactaTXT();
    try{
      comp.escreveHeader();
    } catch(IOException e){
      System.out.println(e);
    }
  }*/
}