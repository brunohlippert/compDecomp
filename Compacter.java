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

  public void montaPrioryQ() {
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

    
    tabelaCodigos.forEach( (letra,codigo) -> { 
      if(letra.equals('\n')){
        System.out.println("Char: '\\n', codigo: "+codigo);
      } else if(((int)letra == 13)){
        System.out.println("Char: 'CR', codigo: "+codigo);
      } else {
        System.out.println("Char: '"+letra+"', codigo: "+codigo);
      }
    });
     
  }

  public void montaTabelaCodigos(TreeNodeChar tree, String codigo){
    if(tree.left == null && tree.right == null){
      tabelaCodigos.put(tree.carac, codigo);
    } else {
      montaTabelaCodigos(tree.left, codigo+"0");
      montaTabelaCodigos(tree.right, codigo+"1");
    } 
  }

  public static void main(String args[]) {
    Compacter comp = new Compacter();
    comp.leTXT();
    comp.montaPrioryQ();
  }
}