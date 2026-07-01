import java.util.*;

class Aresta {
    public String id;
    public String destino;
    public double peso;

    public Aresta(String id, String destino, double peso) {
        this.id = id;
        this.destino = destino;
        this.peso = peso;
    }
}

public class Grafo {
    private boolean dirigido;
    private Map<String, LinkedList<Aresta>> adjacencia;

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
        this.adjacencia = new LinkedHashMap<>();
    }

    public void inserirVertice(String v) {
        adjacencia.putIfAbsent(v, new LinkedList<>());
    }

    public String inserirAresta(String id, String origem, String destino, double peso) {
        if (peso < 0) {
            return "Erro: Aresta " + id + " possui peso negativo.";
        }

        if (adjacencia.containsKey(origem) && adjacencia.containsKey(destino)) {
            adjacencia.get(origem).add(new Aresta(id, destino, peso));
            if (!dirigido) {
                adjacencia.get(destino).add(new Aresta(id, origem, peso));
            }
            return "Aresta '" + id + "' inserida: [" + origem + " -> " + destino + "] Peso: " + peso;
        } else {
            return "Erro: Vértice de origem ou destino não existe.";
        }
    }

    public Set<String> getVertices() {
        return adjacencia.keySet();
    }

    public Map<String, LinkedList<Aresta>> getAdjacencia() {
        return adjacencia;
    }

    public boolean isDirigido() {
        return dirigido;
    }

    // Método essencial para o cálculo de custo no Algoritmo Genético
    public double getPeso(String origem, String destino) {
        if (adjacencia.containsKey(origem)) {
            for (Aresta a : adjacencia.get(origem)) {
                if (a.destino.equals(destino)) {
                    return a.peso;
                }
            }
        }
        return -1; // -1 indica que não há ligação direta entre as cidades
    }
}