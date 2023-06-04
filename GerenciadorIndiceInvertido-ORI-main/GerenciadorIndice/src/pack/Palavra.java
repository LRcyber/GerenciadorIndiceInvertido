
package pack;

import java.util.List;

public class Palavra {
    String nome;
    List<Integer> repetidas;
    List<String> caminhos;
    
    // Construtor
    public Palavra(String nome,List<Integer> repetidas, List<String> caminhos) {
        this.nome = nome;
        this.repetidas = repetidas;
        this.caminhos = caminhos;
    }
    
    // Métodos getter e setter
    public String getNome() {
        return nome;
    }
    
    
    public List<Integer> getRepetidas() {
        return repetidas;
    }
    
    
    public List<String> getCaminhos() {
        return caminhos;
    }
    
}
