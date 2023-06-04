
package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static pack.GerenciadorArquivo.buscarPalavra;


public class GerenciadorArquivo {
    //Palavras desconsideradas
    Set<String> palavrasDesconsideradas = new HashSet<>(Arrays.asList(",", ".", "!", "?", ""));
    public List<String> lerArquivoConjunto(String arquivoConjunto) {
        List<String> nomesArquivos = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoConjunto))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                nomesArquivos.add(linha);
            }
        } catch (IOException e) {
           
        }

        return nomesArquivos;
    }
    
    public static void main(String[] args) {
        
        GerenciadorArquivo gerenciando = new GerenciadorArquivo();
        String diretorioRaiz = System.getProperty("user.dir");
        String arquivoConjunto = diretorioRaiz + "/src/pack/" + "conjunto.txt";
        
        List<String> nomesArquivos = gerenciando.lerArquivoConjunto(arquivoConjunto);
        
        //Camimnhos do arquivos de dados raiz
        String arquivo1 = diretorioRaiz + "/src/pack/" + nomesArquivos.get(0);
        String arquivo2 = diretorioRaiz + "/src/pack/" + nomesArquivos.get(1);
        String arquivo3 = diretorioRaiz + "/src/pack/" + nomesArquivos.get(2);
         
        //Caminhos das palvras a ser descosideradas.
        String arquivo4 = diretorioRaiz + "/src/pack/" + "desconsideradas.txt";
        
        //listando todas as palavrasDesonsideradas em set
        gerenciando.ListaParaDesconsiderar(arquivo4);
    
        //Filtrar as palavras em um array list
        List<String> a = filtrarPalavras(arquivo1, gerenciando.palavrasDesconsideradas);
        List<String> b = filtrarPalavras(arquivo2, gerenciando.palavrasDesconsideradas);
        List<String> c = filtrarPalavras(arquivo3, gerenciando.palavrasDesconsideradas);
                
       
        //remove todas as palvras repetidas usando o Set<String>
        Set<String> palavrasSet = new HashSet<>();
        palavrasSet.addAll(a);
        palavrasSet.addAll(b);
        palavrasSet.addAll(c);
       
        //Converter para usar o metodo de ordenação
        List<String> Dicionario = new ArrayList<>(palavrasSet);
        Collections.sort(Dicionario);
        
        //Gerando indice invertido
        List<Palavra> dicionario=  buscarPalavra(a, b, c, Dicionario);
        listarResultados(dicionario);
        
       
        gerarArquivoIndice( "indice.txt", dicionario);
      
        String consulta = diretorioRaiz + "/src/pack/" + "consulta.txt";
        
        List<String> consultas = lerArquivo(consulta);
        String nomes = consultas.get(0);

        Pattern pattern = Pattern.compile("[,;]");
        Matcher matcher = pattern.matcher(nomes);

        String separador = "";
        if (matcher.find()) {
            separador = matcher.group();
        }

        String[] partes = nomes.split("[,;]");
        String nome1 = partes[0].trim();
        String nome2 = partes[1].trim();

        System.out.println("Nome 1: " + nome1);
        System.out.println("Nome 2: " + nome2);
        System.out.println("seperador: " + separador);
        
        
        List<Palavra> palavras = lerArquivoIndice("indice.txt");
         
        //buscando os arquivos onde as palavras se encontram;
        String arquivoNovo = diretorioRaiz + "/src/pack/" +  "resposta.txt";
        if(separador.equals(",")){
            try {
                buscarCaminhosComuns(palavras, nome1, nome2, arquivoNovo);
            } catch (IOException ex) {
                 System.out.println("Ocorreu um erro ao gravar a saída no arquivo.");
            }
        
        }else{
            try {
                buscarCaminhos(palavras, nome1, nome2, arquivoNovo);
            } catch (IOException ex) {
                Logger.getLogger(GerenciadorArquivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private static void buscarCaminhos(List<Palavra> palavras, String nome1, String nome2, String arquivoNovo) throws IOException {
        Set<String> caminhosEncontrados = new HashSet<>();

        // Encontrar os caminhos da primeira palavra
        for (Palavra palavra : palavras) {
            if (palavra.getNome().equals(nome1)) {
                caminhosEncontrados.addAll(palavra.getCaminhos());
                break;
            }
        }

        // Encontrar os caminhos da segunda palavra
        for (Palavra palavra : palavras) {
            if (palavra.getNome().equals(nome2)) {
                caminhosEncontrados.addAll(palavra.getCaminhos());
                break;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoNovo));

        writer.write(caminhosEncontrados.size() + "\n");
        for (String caminho : caminhosEncontrados) {
            if (caminho.equals("1")) {
                writer.write("a.txt\n");
            } else if (caminho.equals("2")) {
                writer.write("b.txt\n");
            } else if (caminho.equals("3")) {
                writer.write("c.txt\n");
            }
        }
        writer.close();
    }

    
     private static void buscarCaminhosComuns(List<Palavra> palavras, String nome1, String nome2, String arquivoNovo) throws IOException {
        List<String> caminhosPalavra1 = null;
        List<String> caminhosPalavra2 = null;

        // Encontrar os caminhos da primeira palavra
        for (Palavra palavra : palavras) {
            if (palavra.getNome().equals(nome1)) {
                caminhosPalavra1 = palavra.getCaminhos();
                break;
            }
        }

        // Encontrar os caminhos da segunda palavra
        for (Palavra palavra : palavras) {
            if (palavra.getNome().equals(nome2)) {
                caminhosPalavra2 = palavra.getCaminhos();
                break;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoNovo));

        // Verificar a interseção dos caminhos
        if (caminhosPalavra1 != null && caminhosPalavra2 != null) {
            Set<String> arquivosComuns = new HashSet<>(caminhosPalavra1);
            arquivosComuns.retainAll(caminhosPalavra2);

            writer.write(arquivosComuns.size() + "\n");
            for (String caminho : arquivosComuns) {
                if (caminho.equals("1")) {
                    writer.write("a.txt\n");
                } else if (caminho.equals("2")) {
                    writer.write("b.txt\n");
                } else if (caminho.equals("3")) {
                    writer.write("c.txt\n");
                }
            }
        } else {
            writer.write("Uma ou ambas as palavras não foram encontradas.");
        }

        writer.close();
    }
     
     public static List<Palavra> lerArquivoIndice(String nomeArquivo) {
        List<Palavra> palavras = new ArrayList<>();
        String diretorioRaiz = System.getProperty("user.dir");
        String caminhoCompleto = diretorioRaiz + "/src/pack/" + nomeArquivo;

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoCompleto))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] partes = line.split(":");
                String nome = partes[0];
                String[] ocorrencias = partes[1].trim().split(" ");
                List<String> caminhos = new ArrayList<>();
                List<Integer> repetidas = new ArrayList<>();

                for (String ocorrencia : ocorrencias) {
                    String[] dados = ocorrencia.trim().split(",");
                    caminhos.add(dados[0]);
                    repetidas.add(Integer.parseInt(dados[1]));
                }

                Palavra palavra = new Palavra(nome,repetidas, caminhos);
                palavras.add(palavra);
            }
        } catch (Exception e) {
            
        }

        return palavras;
    }

    public static void gerarArquivoIndice(String nomeArquivo, List<Palavra> resultados) {
        String diretorioRaiz = System.getProperty("user.dir");
        String caminhoCompleto = diretorioRaiz + "/src/pack/" + nomeArquivo;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoCompleto))) {
            for (Palavra resultado : resultados) {
                List<String> caminhos = resultado.getCaminhos();
                writer.write(resultado.getNome() + ":");
                List<Integer> quantidade = resultado.getRepetidas();
                int x = 0;
                for (String arquivo : caminhos) {
                    if (arquivo.equals("a.txt")) {
                        writer.write(" 1," + quantidade.get(x));
                    }
                    if (arquivo.equals("b.txt")) {
                        writer.write(" 2," + quantidade.get(x));
                    }
                    if (arquivo.equals("c.txt")) {
                        writer.write(" 3," + quantidade.get(x));
                    }
                    x++;
                }
                writer.newLine();
            }
        } catch (IOException e) {
           
        }
    }
   
      // Função para ler o conteúdo de um arquivo
    public static List<String> lerArquivo(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado: " + e.getMessage());
        }
        return lines;
    }
    
    // Função para gerar o índice invertido
    private static Map<String, List<String>> gerarIndice() throws IOException {
        Map<String, List<String>> indice = new HashMap<>();
        BufferedReader leitor = new BufferedReader(new FileReader("base.txt"));
        String linha;
        int documentoId = 0;
        while ((linha = leitor.readLine()) != null) {
            documentoId++;
            String[] palavras = linha.split(",");
            for (String palavra : palavras) {
                palavra = palavra.trim();
                List<String> documentos = indice.getOrDefault(palavra, new ArrayList<>());
                documentos.add("a" + documentoId + ".txt"); // Supõe-se que o arquivo de base tenha o formato a1.txt, a2.txt, ...
                indice.put(palavra, documentos);
            }
        }
        leitor.close();
        return indice;
    }
    // Função para processar a consulta
    private static List<String> processarConsulta(Map<String, List<String>> indice, List<String> consulta) {
        List<String> resultado = new ArrayList<>();
        for (String termo : consulta) {
            String[] termos = termo.split(",");
            List<String> documentos = new ArrayList<>();
            boolean operadorAnd = true;

            for (String t : termos) {
                t = t.trim();
                if (t.equals(";")) {
                    operadorAnd = false;
                } else {
                    List<String> docs = indice.getOrDefault(t, new ArrayList<>());
                    if (operadorAnd) {
                        if (documentos.isEmpty()) {
                            documentos.addAll(docs);
                        } else {
                            documentos.retainAll(docs);
                        }
                    } else {
                        documentos.addAll(docs);
                    }
                }
            }
            resultado.addAll(documentos);
        }
        return resultado;
    }
      // Função para escrever a resposta no arquivo
    private static void escreverResposta(String nomeArquivo, List<String> resultado) throws IOException {
        BufferedWriter escritor = new BufferedWriter(new FileWriter(nomeArquivo));
        escritor.write(Integer.toString(resultado.size()));
        escritor.newLine();
        for (String documento : resultado) {
            escritor.write(documento);
            escritor.newLine();
        }
        escritor.close();
    }
   
    public void imprimirConjunto(List<Integer> numerosArquivos){
        int tamanho = numerosArquivos.size();
        System.out.println(tamanho);
        for(int arquivo: numerosArquivos){
            if(arquivo == 1){
                System.out.println("a.txt");
            }
            if(arquivo == 2){
                System.out.println("b.txt");
            }
            if(arquivo == 3){
                System.out.println("c.txt");
            }
        }
    }
    //procurar as palavras nos arquivos pares
    
    
    public static void listarResultados(List<Palavra> resultados) {
        
        for (Palavra resultado : resultados) {
            List<String> caminhos = resultado.getCaminhos();
            System.out.print(resultado.getNome()+ ":");
            List<Integer> quantidade = resultado.getRepetidas();
            int x = 0;
            for(String arquivo: caminhos){
                if(arquivo =="a.txt"){
                    System.out.print(" 1," + quantidade.get(x));
                }
                if(arquivo =="b.txt"){
                     System.out.print(" 2,"+ quantidade.get(x));
                }
                if(arquivo == "c.txt"){
                     System.out.print(" 3," + quantidade.get(x));
                }
                x++;
            }
            System.out.println();
  
        }
        
    }
    
    public static List<Palavra> buscarPalavra(List<String> listaA, List<String> listaB, List<String> listaC, List<String> ListaPalavras) {
        List<Palavra> resultados = new ArrayList<>();
       
        for(String palavra:ListaPalavras ){
            List<String> caminhos =new ArrayList<>();
            List<Integer> quantidade =new ArrayList<>();
          
            int quantidadeRepeticoesListaA = contarRepeticoes(listaA, palavra);
            int quantidadeRepeticoesListaB = contarRepeticoes(listaB, palavra);
            int quantidadeRepeticoesListaC = contarRepeticoes(listaC, palavra);
         
            if(quantidadeRepeticoesListaA > 0){
                caminhos.add("a.txt");
                quantidade.add(quantidadeRepeticoesListaA);
                
            }
            if(quantidadeRepeticoesListaB > 0){
                caminhos.add("b.txt");
                quantidade.add(quantidadeRepeticoesListaB);
            }
            if(quantidadeRepeticoesListaC > 0){
                caminhos.add("c.txt");
                quantidade.add(quantidadeRepeticoesListaC);
            }
            resultados.add(new Palavra(palavra, quantidade, caminhos));
            
           
               
        }   

        return resultados;
    }
    
    public static int contarRepeticoes(List<String> lista, String palavra) {
        int contador = 0;
        for (String elemento : lista) {
            if (elemento.equals(palavra)) {
                contador++;
            }
        }
        return contador;
    }
   
    public void ListaParaDesconsiderar(String arquivo){
        
       
        try {
            //Lendo o arquivo com as palavras desconsideradas
            Scanner scannerArquivo = new Scanner(new File(arquivo));
            String palavra;
            
            //Enquanto houver palavras disponíveis
            while(scannerArquivo.hasNext()){
                palavra = scannerArquivo.next().replaceAll("[,;!\\.\\?' ']", " ");
                palavrasDesconsideradas.add(palavra.toLowerCase());//toLowerCase trona as palavras menusculas
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GerenciadorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
           //Não precisa de retorno pois temos uma variavel global
           
    }

    public static List<String> filtrarPalavras(String arquivo, Set<String> palavrasDesconsideradas) {
    List<String> palavrasFiltradas = new ArrayList<>();//Array list para pegar as palavras repetidas

    try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
        String linha;

        while ((linha = reader.readLine()) != null) {
            String[] palavras = linha.split(" ");

            for (String palavra : palavras) {
                palavra = palavra.trim().toLowerCase().replaceAll("[.,;!]", ""); // Remove pontuações do início e do final da palavra
                palavra = palavra.replaceAll("[^a-zA-Z0-9]", ""); // Remove pontuações internas na palavra

                if (!palavrasDesconsideradas.contains(palavra)) {
                    palavrasFiltradas.add(palavra);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

        return palavrasFiltradas;
    }
    
}

   

    


