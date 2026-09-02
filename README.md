    # Escape Escape

Projeto desenvolvido para a disciplina de Projeto de Inteligência Artificial.

## Navegação com A Reacalcular *

esta etapa do projeto, foi implementado um agente autônomo capaz de navegar pelo tilemap utilizando o algoritmo de busca A* para encontrar uma rota até um destino.

Além da navegação, o agente foi configurado para recalcular automaticamente sua rota quando o destino é alterado durante a execução.

A implementação foi integrada ao projeto Escape Escape, mantendo a estrutura e as tecnologias utilizadas anteriormente.

Requisitos implementados

O agente:

Inicia em uma posição válida do tilemap;
Possui um ponto de destino;
Utiliza o tilemap como estrutura navegável;
Diferencia tiles transitáveis de paredes e obstáculos;
Utiliza o algoritmo A* para calcular a rota;
Utiliza a distância Manhattan como função heurística;
Permite movimentação em quatro direções: cima, baixo, esquerda e direita;
Percorre os pontos da rota na ordem correta;
Para ao alcançar o destino;
Não atravessa tiles bloqueados;
Não ultrapassa os limites do mapa;
Exibe visualmente o caminho calculado;
Recalcula a rota quando o destino é alterado.
Funcionamento do recálculo

Durante a execução, o destino do agente pode ser alterado através das teclas:

1 – altera para o destino 1;
2 – altera para o destino 2;
3 – altera para o destino 3.

Quando o destino é alterado, o agente identifica a mudança e executa novamente o algoritmo A* a partir de sua posição atual, gerando uma nova rota até o novo destino.

Estrutura da implementação

A solução foi dividida em:

TileMap.java – representa o mapa e identifica os tiles transitáveis e bloqueados;
GridNode.java – representa cada posição do tilemap utilizada pelo algoritmo;
AStarPathfinder.java – realiza o cálculo da rota utilizando o algoritmo A*;
Agent.java – controla o agente, seu destino, movimento e recálculo da rota;
Renderer.java – realiza a representação visual do mapa e do caminho;
HelloApplication.java – integra as funcionalidades e controla a execução do jogo.
Função heurística

Foi utilizada a distância Manhattan, adequada para o tipo de movimentação utilizado no projeto.

A fórmula considera:

H = |linha atual - linha destino| + |coluna atual - coluna destino|

O agente pode se movimentar apenas em quatro direções, sem movimentação diagonal.

Representação visual

Durante a execução, o caminho calculado pelo A* é destacado em amarelo, permitindo visualizar a rota escolhida pelo algoritmo.

O agente é representado visualmente e percorre os tiles da rota calculada até chegar ao destino.

Execução e testes

Para executar o projeto, é utilizado o Maven através do comando:

.\mvnw.cmd javafx:run

Durante a execução, podem ser realizados os seguintes testes:

Movimentar o jogador utilizando W, A, S, D ou as setas;
Observar o caminho calculado pelo A*;
Pressionar 1, 2 ou 3 para alterar o destino;
Verificar se o agente calcula uma nova rota;
Verificar se o agente evita paredes e obstáculos;
Verificar se o agente chega ao novo destino.
Resultado

A implementação atende ao objetivo da atividade, pois o agente consegue calcular uma rota utilizando o algoritmo A* em um tilemap e recalcular essa rota quando o destino é alterado durante a execução.
## Como executar

1. Abrir o projeto no IntelliJ IDEA.
2. Utilizar JDK 21.
3. Executar a classe `Launcher`.
4. Observar o agente percorrer a rota calculada pelo A*.

## Integrantes

- Paulo Henrique
- Douglas Leite
- Rodrigo Lamartine
