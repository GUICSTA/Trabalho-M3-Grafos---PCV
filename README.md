Aplique sobre o grafo abaixo a técnica de Algoritmos Genéticos para resolver o Problema do
Caixeiro Viajante (PCV) – visitar todas as cidades, passando por cada uma apenas uma vez e
retornando à cidade de origem ao final, gastando o mínimo possível, ou seja, utilizando o caminho de menor custo.
Deve-se permitir a configuração de alguns parâmetros – veja as dicas:
Tamanho da População – mínimo de 100 indivíduos. Lembre-se que R(n)=(n-1)! – logo no grafo
acima, com 10 cidades, tem-se R(10) = 9! = 362880 possibilidades de rotas. A população inicial
será gerada de forma aleatória, a partir da escolha da cidade de partida. Cada indivíduo da
população deve representar uma rota entre as cidades (p.ex. rota1 F,G,H,E,K,N,C,L).
Taxa de Cruzamento – faixa de 60%-80%.
Taxa de Mutação – faixa de 0.5%-1%.
Itens obrigatórios:
Cruzamento em 2 pontos – utilize o operador OX.
Tipo de seleção de indivíduos para população da nova geração – utilize a estratégia elitista.
Critério de parada – utilize número máximo de gerações, sendo no mínimo de 50 gerações.
Os demais itens relacionados ao funcionamento do AG serão escolhidos pelo aluno: pontos de
cruzamento fixos ou aleatórios, intervalo de geração (porcentagem da população que será
substituída durante a próxima geração), quais genes sofrerão mutação, etc. Caso queira, pode
também deixar algum destes itens configuráveis.
No início de cada geração deve-se perguntar ao usuário se quer ver os indivíduos (e seus custos)
que compõem a população – interessante para acompanhamento da convergência do método, pode
também mostrar apenas os 10 ou 20 melhores.
Rotas impossíveis devem ser validadas como indivíduos da população (basta que ligações
inexistentes tenham custos muito altos, tendendo ao infinito), assim o AG deverá logo excluí-las da
população.
Ao final, mostrar a melhor rota graficamente destacando seu custo também.
Deve-se tentar incluir esta solução na ferramenta que vem sendo desenvolvida ao longo do semestre,
caso não seja possível realizar uma implementação separada.

AVALIAÇÃO
Serão considerados para efeitos de avaliação: a corretude e a otimização do programa; a adequação
da interface; e as respostas aos questionamentos na defesa.
O trabalho será desenvolvido em dupla preferencialmente. O código fonte deve ser postado no link
da atividade até 19h de 03/julho/2026, com defesa nesta data a partir das 19h. Sem defesa, a
postagem não terá validade. Trazer o código impresso.
