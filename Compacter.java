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

public class Compacter{
    
    private class NodeChar {
        public char carac;
        public int freq;

        public NodeChar(char carac){
            this.carac = carac;
            this.freq = 1;
        }
        public NodeChar(char carac, int freq){
            this.carac = carac;
            this.freq = freq;
        }

        public void incFreq(){this.freq++;}
        public String toString(){
            if(carac == '\n'){
                return "Char: '\\n', frequencia: "+freq;
            }else{
                return "Char: '"+carac+"', frequencia: "+freq;
            }
        }
    }

    private HashMap<Character, Integer> charMap;
    private final String path = "texto.txt";

    public Compacter(){
        charMap = new HashMap<>();
    }

    public void leTXT(){
        try{
            Scanner scan = new Scanner(new File(path));
            scan.useDelimiter("");
            while(scan.hasNext()){  
                char letra = scan.next().charAt(0);
                if(charMap.containsKey(letra)){
                    charMap.put(letra, charMap.get(letra)+1 );
                } else {
                    charMap.put(letra, 1);
                }
                /*OR WE CAN DO...
                charMap.put(letra, charMap.getOrDefault(letra, 0) +1); sem os 'ifs'.
                que coloca no mapa a chave 'letra' e associa ao valor do get!,
                se a chave ja tiver valor associado entao ele pega o valor do get no mapa,
                caso ela nao esteja associada ele retorna o valor default, no caso '0'.
                */
            }
            scan.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        //IMPRESSAO DOS DADOS DO MAPA!
        /*charMap.forEach( (letra,freq) -> {
            if(letra.equals('\n')){
                //System.out.println("Letra :'"+String.valueOf(letra.hashCode())+"', Frequencia :"+freq);
                //String s = String.valueOf("\r"+letra);
                //System.out.printf("Letra : '\\%c', Frequencia : %d\n",letra,freq );
                System.out.println("Letra :'\\n', Frequencia :"+freq); //Gambiarra se nao essa jossa cria uma nova linha na impressao
            }else{
                System.out.println("Letra :'"+String.valueOf(letra)+"', Frequencia :"+freq);
            }
         });*/
    }

    public void montaPrioryQ(){
        //Comparador para a PQ, compara por frequencia
        Comparator<NodeChar> compNodeChar = new Comparator<>(){
            @Override
            public int compare(NodeChar n1, NodeChar n2){
              return n1.freq - n2.freq;
            }
        };
        PriorityQueue<NodeChar> pq = new PriorityQueue<>(compNodeChar);
        charMap.forEach((letra,freq) -> {
            NodeChar node = new NodeChar(letra,freq);
            pq.add(node);
        });
        //IMPRESSAO REMOVENDO NA ORDEM QUE A PQ ORDENA A PRIORIDADE
        //MENOR FREQUENCIA PRIMEIRO
        while(!pq.isEmpty()){
            System.out.println(pq.remove());
        }
    }

    public static void main(String args[]){
        Compacter comp = new Compacter();
        comp.leTXT();
        comp.montaPrioryQ();
    }
}