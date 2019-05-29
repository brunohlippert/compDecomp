package osGuri;

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

/** 
 *  Classe responsavel por realizar todos os passos do algoritmo de Huffman para
 *  realizar a compressao de um arquivo.
*/
public class Compacter {

  /** 
   *  Classe auxiliar para relizar o mapeamento de caracteres e sua respectiva frequencia, assim como 
   *  para montar a arvore binaria.
  */
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
  private String path;
  private String headerPath;
  private String compactadoPath;
  private HashMap<Character,String> tabelaCodigos;
  private int skipChars;

  /** 
   *  Metodo responsavel por receber o arquivo e o diretorio deste arquivo para ser comprimido
   *  e chamar na ordem correta os metodos responsaveis por cada parte do processo de compressao.
   *  Monta os paths do arquivo Header e do arquivo final compactado.
   * 
   * @param pasta caminho ate o diretorio do arquivo.
   * @param nomeArquivo nome do arquivo a ser comprimido.
  */
  public void compactaArquivo(String pasta, String nomeArquivo){
    path = pasta+nomeArquivo;
    headerPath = pasta+"Header"+nomeArquivo;
    compactadoPath = pasta+"Compactado"+nomeArquivo+".zip";
    
    leTXT();
    montaArvore();
    compactaTXT();
    try{
      escreveHeader();
    } catch(IOException e){
      System.out.println(e);
    }
  }

  public Compacter() {
    charMap = new HashMap<>();
    tabelaCodigos = new HashMap<>();
    minPQ = new PriorityQueue<>();
  }

  /** 
   *  Metodo responsavel por abrir o arquivo txt que deseja-se comprimir, ler caracter a caracter
   * do arquivo e inseri-lo em um mapa, onde o caracter eh a key e o valor armazenado eh a 
   * frequencia que este caracter aparece no texto. A frequencia eh calculada na hora de sua adicao
   * no map, sendo a primeira vez que eh adicionado frequencia 1, e as seguintes este valor eh incrementado.
  */
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
      }
      scan.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /** 
   *  Metodo responsavel por percorrer o map que contem os caracteres e suas frequencias adicionando-os
   * em uma priority queue como um objeto TreeNodeChar, que sera utilizado para montar a arvore dos caracteres.
   * Depois, remove dois a dois os caracteres com menor frequencia, adicionando-os a um novo objeto TreeNodeChar
   * que sera um ramo intermediario da arvore com a soma das frequencias dos dois caracteres, em seguida, este ramo
   * eh adicionado novamente a prioty queue e o processo eh repetido ate que haja apenas um TreeNodeChar na priority queue
   * seno este a raiz. por fim, chama o metodo montaTabelaCodigos com a raiz.
   * 
  */
  public void montaArvore() {
    charMap.forEach((letra, freq) -> {
      TreeNodeChar node = new TreeNodeChar(letra, freq);
      minPQ.add(node);
    });

    while (minPQ.size() != 1){
      TreeNodeChar nodeAuxLeft = minPQ.remove();
      TreeNodeChar nodeAuxRight = minPQ.remove();
      TreeNodeChar nodeAuxRoot = new TreeNodeChar(nodeAuxLeft.freq+nodeAuxRight.freq, nodeAuxLeft, nodeAuxRight);
      
      minPQ.add(nodeAuxRoot);
    }

    montaTabelaCodigos(minPQ.remove(), "");
  }

  /** 
   *  Metodo responsavel por salvar o arquivo header contendo a tabela de codigos no mesmo diretorio
   *  onde o arquivo original foi selecionado.
   * 
  */
  public void escreveHeader() throws IOException{
    FileWriter arq = new FileWriter(headerPath);
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

  /** 
   *  Metodo responsavel por receber um objeto TreeNodeChar e o codigo binario passado recursivamente
   *  para montar a tabela de codigos. Quando acha um folha, adiciona o caracter e o codigo binario construido
   *  recursivamente a um hashmap tabelaCodigos, onde o caracter eh a key e o codigo eh o valor.
   * 
   * @param tree objeto TreeNodeChar para ser percorrido.
   * @param codigo codigo binario do caracter montado recursivamente.
  */
  public void montaTabelaCodigos(TreeNodeChar tree, String codigo){
    if(tree.left == null && tree.right == null){
      tabelaCodigos.put(tree.carac, codigo);
    } else {
      montaTabelaCodigos(tree.left, codigo+"0");
      montaTabelaCodigos(tree.right, codigo+"1");
    } 
  }

  /** 
   *  Metodo responsavel por receber uma string de de binarios  ex: "10100101", e transforma-la
   * em um byte array, para ser escrito no arquivo final.
   * 
   * @param s string com o codigo binario.
  */
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

  /** 
   *  Metodo responsavel por ler o arquivo txt que deseja-se compacter, caracter a caracter
   *  buscando na tabela de codigos seu respectivo codigo binario, transaformando este codigo
   * em um array de bytes e em seguida escrevendo todo esste array de bytes para o arquivo de saida
   *  .zip compactado.
   * 
   * Observacao: como java nao reconhece bits, apenas Bytes, eh necessario adicionar a quantidade
   * faltante de bits para que o codigo binario feche um byte no ultimo caracter, sendo assim,
   * esses bits sao adicionados como zeros no final do arquivo, e escritos no header, para assim
   * o descompactador saber quantos bits ele precisa deixar de ler no fim do arquivo.
  */
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
      skipChars = 8 - strBuild.toString().length() % 8;
      for(int i = 0; i < skipChars; i++){
        strBuild.append("0");
      }
      byte[] data = decodeBinary(strBuild.toString());
      java.nio.file.Files.write(new File(compactadoPath).toPath(), data);
      scan.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}