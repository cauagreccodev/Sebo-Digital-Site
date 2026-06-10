# Sebo Digital

Frontend estatico inicial para uma loja virtual de livros usados, feito com HTML, CSS e JavaScript puro.

## Como abrir

Abra `index.html` no navegador. As paginas principais ficam em:

- `index.html` - pagina inicial
- `livros.html` - catalogo/listagem
- `detalhes.html?id=1` - detalhes de um livro
- `carrinho.html` - intencao de compra/carrinho demonstrativo

## Estrutura

```text
assets/
  css/styles.css
  img/hero-sebo.png
  js/app.js
docs/
  estrategia-imagens.md
index.html
livros.html
detalhes.html
carrinho.html
```

## Integracao futura

Os dados dos livros estao hoje mockados em `assets/js/app.js`. Em uma etapa com Spring Boot, esse mesmo formato pode ser servido por endpoints REST, por exemplo:

- `GET /api/livros`
- `GET /api/livros/{id}`
- `POST /api/carrinho`
- `GET /api/categorias`

As capas estao representadas por componentes visuais no frontend e podem ser trocadas por URLs de imagens locais retornadas pela API.
