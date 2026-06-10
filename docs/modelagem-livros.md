# Modelagem de livros e copias

Este projeto deve tratar "livro" e "copia/oferta" como conceitos diferentes.

Um livro representa a obra/catalogo bibliografico: titulo, autor, editora, ISBN, idioma, ano de publicacao, categoria, descricao e imagem principal.

Uma copia representa um exemplar vendavel dessa obra: preco, estado, tipo, vendedor/sebo, cidade, estoque, frete, promocao, avaliacao do vendedor e disponibilidade.

## Regra principal

Nao classificar cada livro individualmente como novo ou usado no cadastro principal da obra.

O correto e cadastrar a obra uma unica vez e manter uma lista de copias/ofertas associadas a ela. Dentro dessa lista, as copias podem ser separadas entre:

- copias novas;
- copias usadas.

Assim, se o mesmo titulo existir em versao nova e usada, ele continua sendo um unico livro no catalogo, mas com varias opcoes de compra.

## Exemplo conceitual

```text
Livro
  id
  titulo
  autor
  editora
  isbn
  idioma
  ano_publicacao
  categoria
  imagem_capa

  copias
    novas
      - copia_id
      - vendedor_id
      - preco
      - estoque
      - cidade
      - frete_gratis
      - promocao

    usadas
      - copia_id
      - vendedor_id
      - preco
      - estado_conservacao
      - estoque
      - cidade
      - frete_gratis
      - promocao
```

## Modelo relacional recomendado

Em banco relacional, a estrutura pode ser normalizada assim:

```text
livros
  id
  titulo
  autor_id
  editora_id
  isbn
  idioma
  ano_publicacao
  categoria_id
  descricao
  imagem_capa

livro_copias
  id
  livro_id
  vendedor_id
  tipo
  estado_conservacao
  preco
  estoque
  cidade
  frete_gratis
  promocao
  compra_corporativa
  ativo

vendedores
  id
  nome
  cidade
  avaliacao
```

O campo `tipo` em `livro_copias` deve indicar se a copia e `NOVO` ou `USADO`.

O campo `estado_conservacao` deve ser usado principalmente para copias usadas, com valores como `MUITO_BOM`, `BOM` ou `ACEITAVEL`. Para copias novas, esse campo pode ser nulo ou receber um valor padrao controlado pela aplicacao.

## Exemplo de resposta da API

Uma API futura pode retornar um livro com suas copias agrupadas:

```json
{
  "id": 1,
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "editora": "Atica",
  "idioma": "Portugues",
  "anoPublicacao": 1998,
  "categoria": "Literatura",
  "copias": {
    "novas": [
      {
        "id": 101,
        "vendedor": "Livraria Central",
        "preco": 49.9,
        "estoque": 3,
        "cidade": "Sao Paulo, SP",
        "freteGratis": true
      }
    ],
    "usadas": [
      {
        "id": 201,
        "vendedor": "Banca Aurora",
        "preco": 32.9,
        "estadoConservacao": "MUITO_BOM",
        "estoque": 1,
        "cidade": "Curitiba, PR",
        "freteGratis": true
      }
    ]
  }
}
```

## Impacto no frontend

No frontend, o card do catalogo deve representar a obra e destacar o menor preco disponivel ou a melhor oferta.

A pagina de detalhes do livro deve exibir as opcoes de compra agrupadas por:

- novas;
- usadas.

Filtros como preco, frete gratis, vendedor, cidade, promocao e compra corporativa devem atuar sobre as copias/ofertas, nao somente sobre a obra.

Filtros como autor, editora, categoria, idioma e ano de publicacao pertencem ao livro/obra.

## Beneficios

- evita duplicidade de cadastros para o mesmo titulo;
- permite comparar exemplares novos e usados na mesma pagina;
- facilita estoque por vendedor/sebo;
- melhora filtros de marketplace;
- prepara o projeto para pedidos reais, carrinho e checkout;
- deixa a modelagem mais proxima de um marketplace de livros usados.
