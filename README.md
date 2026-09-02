    # Escape Escape

Projeto desenvolvido para a disciplina de Projeto de Inteligência Artificial.

## Navegação com A*

Foi implementado um agente autônomo que utiliza o algoritmo A* para navegar pelo tilemap.

O mapa é representado por uma matriz de tiles. Cada tile informa se pode ser atravessado e qual é seu custo de movimentação.

Tipos utilizados:

- FLOOR: transitável, custo 1
- WALL: não transitável
- DEBRIS: transitável, custo 2

O A* foi implementado pela equipe e utiliza a distância de Manhattan como heurística.

O agente pode se mover apenas em quatro direções:

- cima
- baixo
- esquerda
- direita

Movimentos diagonais não são permitidos.

O caminho calculado pelo A* é exibido em amarelo no mapa. O agente é representado por um quadrado ciano e percorre automaticamente a rota até o destino, onde para.

## Como executar

1. Abrir o projeto no IntelliJ IDEA.
2. Utilizar JDK 21.
3. Executar a classe `Launcher`.
4. Observar o agente percorrer a rota calculada pelo A*.

## Integrantes

- Paulo Henrique
- Douglas Leite
- Rodrigo Lamartine
