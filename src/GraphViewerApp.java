import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphViewerApp extends JFrame {

    private Grafo grafoAtivo;
    private JTextArea consoleOutput;

    // Variável para armazenar a melhor rota do AG e desenhar em destaque no painel
    private List<String> melhorCaminhoAG = new ArrayList<>();

    public GraphViewerApp() {
        grafoAtivo = new Grafo(false);

        setTitle("Visualizador de Grafos - Trabalho 3 (AG)");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        consoleOutput.setBackground(new Color(43, 43, 43));
        consoleOutput.setForeground(new Color(169, 183, 198));

        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Console de Saída do Grafo"));
        add(scrollPane, BorderLayout.SOUTH);

        setJMenuBar(criarMenuBar());
        add(new GraphPanel(), BorderLayout.CENTER);

        log("Crie o grafo e insira vértices e arestas usando o menu acima.\n");
    }

    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- MENU ARQUIVO ---
        JMenu menuArquivo = new JMenu("Grafo");
        JMenuItem itemNovoGrafo = new JMenuItem("Novo Grafo");
        itemNovoGrafo.addActionListener(e -> {
            boolean dirigido = (JOptionPane.showConfirmDialog(this, "O grafo será Dirigido?", "Novo Grafo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
            grafoAtivo = new Grafo(dirigido);
            limparVisuais();
            log("Grafo " + (dirigido ? "dirigido" : "não dirigido") + " criado.\n");
            repaint();
        });

        JMenuItem itemCarregarT3 = new JMenuItem("Carregar Mapa Trabalho 3 (PDF)");
        itemCarregarT3.addActionListener(e -> carregarMapaTrabalho3());

        menuArquivo.add(itemNovoGrafo);
        menuArquivo.add(itemCarregarT3);
        menuArquivo.addSeparator();
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);

        // --- MENU OPERAÇÕES ---
        JMenu menuEstrutura = new JMenu("Operações");
        JMenuItem itemAddVertice = new JMenuItem("Inserir Vértice");
        itemAddVertice.addActionListener(e -> {
            String v = JOptionPane.showInputDialog(this, "Nome do Vértice:");
            if (v != null && !v.trim().isEmpty()) {
                grafoAtivo.inserirVertice(v.toUpperCase());
                log("Vértice '" + v.toUpperCase() + "' inserido.");
                repaint();
            }
        });

        JMenuItem itemAddAresta = new JMenuItem("Inserir Aresta");
        itemAddAresta.addActionListener(e -> {
            try {
                String origem = JOptionPane.showInputDialog(this, "Vértice de Origem:").toUpperCase();
                String destino = JOptionPane.showInputDialog(this, "Vértice de Destino:").toUpperCase();
                double peso = Double.parseDouble(JOptionPane.showInputDialog(this, "Peso da Aresta:"));
                grafoAtivo.inserirAresta("E" + System.currentTimeMillis(), origem, destino, peso);
                log("Aresta " + origem + " -> " + destino + " inserida.");
                repaint();
            } catch (Exception ex) {
                log("Erro na inserção da aresta. Verifique os dados inseridos.");
            }
        });
        menuEstrutura.add(itemAddVertice);
        menuEstrutura.add(itemAddAresta);

        // --- MENU ALGORITMOS ---
        JMenu menuAlgoritmos = new JMenu("Algoritmos");
        JMenuItem itemAG = new JMenuItem("Executar Algoritmo Genético (PCV)");
        itemAG.addActionListener(e -> executarAlgoritmoGenetico());

        menuAlgoritmos.add(itemAG);

        menuBar.add(menuArquivo);
        menuBar.add(menuEstrutura);
        menuBar.add(menuAlgoritmos);

        return menuBar;
    }

    private void limparVisuais() {
        melhorCaminhoAG.clear();
        consoleOutput.setText("");
    }

    private void carregarMapaTrabalho3() {
        grafoAtivo = new Grafo(false);
        limparVisuais();

        String[] cidades = {"F", "N", "K", "G", "C", "E", "H", "L"};
        for (String c : cidades) grafoAtivo.inserirVertice(c);

        grafoAtivo.inserirAresta("e1", "F", "N", 30);
        grafoAtivo.inserirAresta("e2", "F", "C", 20);
        grafoAtivo.inserirAresta("e3", "F", "L", 10);
        grafoAtivo.inserirAresta("e4", "F", "G", 55);
        grafoAtivo.inserirAresta("e5", "N", "K", 60);
        grafoAtivo.inserirAresta("e6", "N", "C", 47);
        grafoAtivo.inserirAresta("e7", "K", "C", 70);
        grafoAtivo.inserirAresta("e8", "K", "E", 10);
        grafoAtivo.inserirAresta("e9", "K", "H", 73);
        grafoAtivo.inserirAresta("e10", "K", "G", 90);
        grafoAtivo.inserirAresta("e11", "C", "E", 10);
        grafoAtivo.inserirAresta("e12", "C", "H", 30);
        grafoAtivo.inserirAresta("e13", "C", "L", 10);
        grafoAtivo.inserirAresta("e14", "E", "G", 40);
        grafoAtivo.inserirAresta("e15", "E", "H", 60);
        grafoAtivo.inserirAresta("e16", "E", "L", 5);
        grafoAtivo.inserirAresta("e17", "H", "L", 40);
        grafoAtivo.inserirAresta("e18", "H", "G", 80);

        log("Mapa do Trabalho 3 carregado com sucesso!\n");
        repaint();
    }

    // =========================================================================
    // LÓGICA DO ALGORITMO GENÉTICO (TRABALHO 3)
    // =========================================================================
    private void executarAlgoritmoGenetico() {
        if (grafoAtivo.getVertices().isEmpty()) {
            log("Erro: O grafo está vazio.");
            return;
        }

        String inicio = JOptionPane.showInputDialog(this, "Escolha a cidade de partida para o Caixeiro Viajante:\n" + grafoAtivo.getVertices());
        if (inicio == null || inicio.trim().isEmpty()) return;

        inicio = inicio.trim().toUpperCase();

        if (!grafoAtivo.getVertices().contains(inicio)) {
            log("Cidade inválida ou operação cancelada.");
            return;
        }

        // Parâmetros do AG
        int tamPopulacao = 100;
        int geracoes = 50;
        double taxaCruzamento = 0.70;
        double taxaMutacao = 0.01;
        double custoInfinito = 999999;
        Random rand = new Random();

        List<String> todasCidades = new ArrayList<>(grafoAtivo.getVertices());
        todasCidades.remove(inicio);

        // 1. População Inicial
        List<List<String>> populacao = new ArrayList<>();
        for (int i = 0; i < tamPopulacao; i++) {
            List<String> individuo = new ArrayList<>(todasCidades);
            Collections.shuffle(individuo);
            individuo.add(0, inicio); // A origem é fixa na posição 0
            populacao.add(individuo);
        }

        log("\n=== INICIANDO ALGORITMO GENÉTICO ===");
        log("População: " + tamPopulacao + " | Gerações: " + geracoes);

        List<String> melhorAbsoluto = null;
        double melhorCustoAbsoluto = Double.MAX_VALUE;

        // 2. Loop de Gerações
        for (int g = 1; g <= geracoes; g++) {

            // Avaliação da População
            List<Individuo> avaliados = new ArrayList<>();
            for (List<String> rota : populacao) {
                double custo = calcularCustoRota(rota, custoInfinito);
                avaliados.add(new Individuo(rota, custo));
            }
            avaliados.sort(Comparator.comparingDouble(ind -> ind.custo));

            // Salva o melhor da história
            if (avaliados.get(0).custo < melhorCustoAbsoluto) {
                melhorCustoAbsoluto = avaliados.get(0).custo;
                melhorAbsoluto = new ArrayList<>(avaliados.get(0).rota);
            }

            // Exibição interativa via JOptionPane
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Geração " + g + " concluída.\nMelhor custo atual: " + avaliados.get(0).custo +
                            "\nDeseja exibir os 10 melhores indivíduos no console?",
                    "Acompanhamento AG - Geração " + g, JOptionPane.YES_NO_CANCEL_OPTION);

            if (resposta == JOptionPane.CANCEL_OPTION) {
                log("Algoritmo interrompido pelo usuário na geração " + g + ".");
                break; // Para o loop e exibe o que achou até o momento
            } else if (resposta == JOptionPane.YES_OPTION) {
                log("\n--- Top 10 da Geração " + g + " ---");
                for (int i = 0; i < Math.min(10, avaliados.size()); i++) {
                    log((i+1) + "º: " + String.join("-", avaliados.get(i).rota) + " | Custo: " + avaliados.get(i).custo);
                }
            }

            // 3. Seleção Elitista (Mantém os 20% melhores)
            List<List<String>> novaPopulacao = new ArrayList<>();
            int numElitismo = (int) (tamPopulacao * 0.2);
            for (int i = 0; i < numElitismo; i++) {
                novaPopulacao.add(avaliados.get(i).rota);
            }

            // 4. Cruzamento OX (Order Crossover em 2 pontos)
            while (novaPopulacao.size() < tamPopulacao) {
                List<String> pai1 = selecaoTorneio(avaliados, rand);
                List<String> pai2 = selecaoTorneio(avaliados, rand);
                List<String> filho;

                if (rand.nextDouble() < taxaCruzamento) {
                    filho = cruzamentoOX(pai1, pai2, rand);
                } else {
                    filho = new ArrayList<>(pai1);
                }

                // 5. Mutação (Swap)
                if (rand.nextDouble() < taxaMutacao) {
                    int idx1 = 1 + rand.nextInt(filho.size() - 1);
                    int idx2 = 1 + rand.nextInt(filho.size() - 1);
                    String temp = filho.get(idx1);
                    filho.set(idx1, filho.get(idx2));
                    filho.set(idx2, temp);
                }

                novaPopulacao.add(filho);
            }
            populacao = novaPopulacao;
        }

        log("\n=== FIM DO ALGORITMO GENÉTICO ===");
        if (melhorAbsoluto != null) {
            log("Melhor Rota Encontrada: " + String.join(" -> ", melhorAbsoluto) + " -> " + melhorAbsoluto.get(0));
            log("Custo Total Mínimo: " + melhorCustoAbsoluto);
            this.melhorCaminhoAG = melhorAbsoluto;
        }
        repaint(); // Atualiza a tela para desenhar o caminho em vermelho
    }

    private double calcularCustoRota(List<String> rota, double custoInfinito) {
        double custoTotal = 0;
        for (int i = 0; i < rota.size(); i++) {
            String atual = rota.get(i);
            String prox = rota.get((i + 1) % rota.size()); // Liga o último ao primeiro de volta
            double peso = grafoAtivo.getPeso(atual, prox);

            if (peso == -1) custoTotal += custoInfinito;
            else custoTotal += peso;
        }
        return custoTotal;
    }

    private List<String> selecaoTorneio(List<Individuo> avaliados, Random rand) {
        Individuo melhor = avaliados.get(rand.nextInt(avaliados.size()));
        for (int i = 1; i < 3; i++) { // Torneio de 3
            Individuo competidor = avaliados.get(rand.nextInt(avaliados.size()));
            if (competidor.custo < melhor.custo) {
                melhor = competidor;
            }
        }
        return melhor.rota;
    }

    private List<String> cruzamentoOX(List<String> pai1, List<String> pai2, Random rand) {
        int n = pai1.size();
        List<String> filho = new ArrayList<>(Collections.nCopies(n, null));
        filho.set(0, pai1.get(0)); // Mantém a cidade de origem intacta

        int p1 = 1 + rand.nextInt(n - 1);
        int p2 = 1 + rand.nextInt(n - 1);
        if (p1 > p2) { int temp = p1; p1 = p2; p2 = temp; }

        for (int i = p1; i <= p2; i++) {
            filho.set(i, pai1.get(i));
        }

        int posInserir = (p2 + 1) % n;
        if (posInserir == 0) posInserir = 1;

        for (String gene : pai2) {
            if (!filho.contains(gene)) {
                while (filho.get(posInserir) != null) {
                    posInserir = (posInserir + 1) % n;
                    if (posInserir == 0) posInserir = 1;
                }
                filho.set(posInserir, gene);
            }
        }
        return filho;
    }

    private static class Individuo {
        List<String> rota;
        double custo;
        Individuo(List<String> rota, double custo) {
            this.rota = rota;
            this.custo = custo;
        }
    }
    // =========================================================================

    private void log(String message) {
        consoleOutput.append(message + "\n");
        consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphViewerApp().setVisible(true));
    }

    // CLASSE DE DESENHO DO GRAFO
    class GraphPanel extends JPanel {
        private final int RAIO_VERTICE = 25;

        public GraphPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<String> vertices = new ArrayList<>(grafoAtivo.getVertices());
            if (vertices.isEmpty()) return;

            // Distribuição circular dos vértices
            Map<String, Point> posicoes = new HashMap<>();
            int width = getWidth();
            int height = getHeight();
            int cx = width / 2, cy = height / 2;
            int raio = Math.min(width, height) / 2 - 60;

            for (int i = 0; i < vertices.size(); i++) {
                double angulo = 2 * Math.PI * i / vertices.size();
                posicoes.put(vertices.get(i), new Point((int)(cx + raio * Math.cos(angulo)), (int)(cy + raio * Math.sin(angulo))));
            }

            // DESENHAR AS ARESTAS
            Set<String> desenhadas = new HashSet<>();
            for (String origem : vertices) {
                Point p1 = posicoes.get(origem);
                if (grafoAtivo.getAdjacencia().get(origem) == null) continue;

                for (Object aObj : grafoAtivo.getAdjacencia().get(origem)) {
                    String destino = null;
                    double peso = 0;
                    try {
                        destino = (String) aObj.getClass().getField("destino").get(aObj);
                        peso = (double) aObj.getClass().getField("peso").get(aObj);
                    } catch (Exception e) {}

                    Point p2 = posicoes.get(destino);
                    if (p2 == null) continue;

                    String chaveUnica = origem.compareTo(destino) < 0 ? origem + "-" + destino : destino + "-" + origem;
                    if (!grafoAtivo.isDirigido() && desenhadas.contains(chaveUnica)) continue;
                    desenhadas.add(chaveUnica);

                    // Verifica se a aresta atual faz parte da melhor rota encontrada pelo AG
                    boolean noCaminhoAG = false;
                    if (!melhorCaminhoAG.isEmpty()) {
                        for (int i = 0; i < melhorCaminhoAG.size(); i++) {
                            String atual = melhorCaminhoAG.get(i);
                            String prox = melhorCaminhoAG.get((i + 1) % melhorCaminhoAG.size());
                            if ((atual.equals(origem) && prox.equals(destino)) || (!grafoAtivo.isDirigido() && atual.equals(destino) && prox.equals(origem))) {
                                noCaminhoAG = true; break;
                            }
                        }
                    }

                    if (noCaminhoAG) {
                        g2d.setColor(Color.RED);
                        g2d.setStroke(new BasicStroke(4));
                    } else {
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.setStroke(new BasicStroke(1));
                    }

                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString(String.valueOf(peso), (p1.x + p2.x) / 2, (p1.y + p2.y) / 2 - 5);
                }
            }

            // DESENHAR OS VÉRTICES
            for (String v : vertices) {
                Point p = posicoes.get(v);

                g2d.setColor(new Color(70, 130, 180));
                g2d.fillOval(p.x - RAIO_VERTICE, p.y - RAIO_VERTICE, RAIO_VERTICE * 2, RAIO_VERTICE * 2);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(p.x - RAIO_VERTICE, p.y - RAIO_VERTICE, RAIO_VERTICE * 2, RAIO_VERTICE * 2);

                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(v, p.x - fm.stringWidth(v) / 2, p.y + fm.getAscent() / 2 - 2);
            }
        }
    }
}