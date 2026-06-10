const books = [
  {
    id: 1,
    title: "Dom Casmurro",
    author: "Machado de Assis",
    category: "Literatura",
    price: 32.9,
    condition: "Muito bom",
    year: "1998",
    publisher: "Atica",
    pages: 256,
    stock: 1,
    seller: "Banca Aurora",
    city: "Curitiba, PR",
    type: "Usado",
    freeShipping: true,
    promotion: true,
    corporatePurchase: false,
    rating: 4.8,
    language: "Portugues",
    sales: 42,
    cover: "cover-burgundy",
    short: "Classico brasileiro em edicao escolar bem preservada.",
    description: "Exemplar de segunda mao com capa comum, miolo firme e paginas preservadas. Ideal para leitura, estudo e colecao inicial de literatura brasileira.",
    notes: ["Sem grifos aparentes", "Corte levemente amarelado", "Capa com pequenos sinais de prateleira"]
  },
  {
    id: 2,
    title: "Quarto de Despejo",
    author: "Carolina Maria de Jesus",
    category: "Literatura",
    price: 41.5,
    condition: "Bom",
    year: "2014",
    publisher: "Atica",
    pages: 200,
    stock: 2,
    seller: "Estante do Centro",
    city: "Sao Paulo, SP",
    type: "Usado",
    freeShipping: false,
    promotion: false,
    corporatePurchase: true,
    rating: 4.9,
    language: "Portugues",
    sales: 38,
    cover: "cover-teal",
    short: "Relato marcante, com sinais leves de leitura.",
    description: "Livro usado em bom estado geral, com marcas discretas de manuseio. Uma obra essencial para quem busca literatura brasileira e memoria social.",
    notes: ["Miolo integro", "Nome pequeno na primeira pagina", "Sem paginas soltas"]
  },
  {
    id: 3,
    title: "Java para Iniciantes",
    author: "Herbert Schildt",
    category: "Tecnologia",
    price: 68.0,
    condition: "Muito bom",
    year: "2019",
    publisher: "Bookman",
    pages: 720,
    stock: 1,
    seller: "Livros & Codigo",
    city: "Belo Horizonte, MG",
    type: "Usado",
    freeShipping: true,
    promotion: false,
    corporatePurchase: true,
    rating: 4.7,
    language: "Portugues",
    sales: 35,
    cover: "cover-blue",
    short: "Livro tecnico para estudo, sem anotacoes relevantes.",
    description: "Exemplar robusto para estudos de programacao Java. Bom candidato para acompanhar a futura etapa backend deste projeto.",
    notes: ["Sem grifos importantes", "Lombada firme", "Capa com desgaste minimo"]
  },
  {
    id: 4,
    title: "Historia do Brasil",
    author: "Boris Fausto",
    category: "Historia",
    price: 54.9,
    condition: "Bom",
    year: "2012",
    publisher: "Edusp",
    pages: 688,
    stock: 1,
    seller: "Sebo Republica",
    city: "Porto Alegre, RS",
    type: "Usado",
    freeShipping: false,
    promotion: true,
    corporatePurchase: true,
    rating: 4.5,
    language: "Portugues",
    sales: 26,
    cover: "cover-gold",
    short: "Panorama historico em exemplar usado e completo.",
    description: "Edicao com sinais normais de leitura, indicada para estudos, consulta e formacao de biblioteca pessoal.",
    notes: ["Corte com pontos de oxidacao", "Sem rasgos", "Algumas marcas a lapis"]
  },
  {
    id: 5,
    title: "O Pequeno Principe",
    author: "Antoine de Saint-Exupery",
    category: "Infantojuvenil",
    price: 24.0,
    condition: "Muito bom",
    year: "2017",
    publisher: "Agir",
    pages: 96,
    stock: 3,
    seller: "Casa das Letras",
    city: "Florianopolis, SC",
    type: "Usado",
    freeShipping: true,
    promotion: true,
    corporatePurchase: false,
    rating: 4.9,
    language: "Portugues",
    sales: 51,
    cover: "cover-sage",
    short: "Edicao compacta, otima para presente ou releitura.",
    description: "Livro em excelente estado, com capa preservada e paginas limpas. Um classico afetivo para diferentes idades.",
    notes: ["Paginas limpas", "Sem dedicacao", "Capa preservada"]
  },
  {
    id: 6,
    title: "Design do Dia a Dia",
    author: "Donald Norman",
    category: "Arte",
    price: 59.9,
    condition: "Muito bom",
    year: "2018",
    publisher: "Rocco",
    pages: 272,
    stock: 1,
    seller: "Prateleira Criativa",
    city: "Rio de Janeiro, RJ",
    type: "Usado",
    freeShipping: true,
    promotion: false,
    corporatePurchase: true,
    rating: 4.6,
    language: "Portugues",
    sales: 22,
    cover: "cover-clay",
    short: "Livro de design e usabilidade em otimo estado.",
    description: "Obra recomendada para quem estuda experiencia do usuario, produto digital e design centrado em pessoas.",
    notes: ["Sem grifos", "Capa com marca minima", "Miolo firme"]
  },
  {
    id: 7,
    title: "Grande Sertao: Veredas",
    author: "Joao Guimaraes Rosa",
    category: "Literatura",
    price: 47.0,
    condition: "Bom",
    year: "2006",
    publisher: "Nova Fronteira",
    pages: 624,
    stock: 1,
    seller: "Ponto do Livro",
    city: "Goiania, GO",
    type: "Usado",
    freeShipping: false,
    promotion: false,
    corporatePurchase: false,
    rating: 4.4,
    language: "Portugues",
    sales: 19,
    cover: "cover-ink",
    short: "Classico extenso, completo, com marcas de leitura.",
    description: "Exemplar para leitor que quer mergulhar em um dos grandes romances brasileiros. Usado, completo e com boa estrutura.",
    notes: ["Corte amarelado", "Pequenas dobras na capa", "Sem paginas ausentes"]
  },
  {
    id: 8,
    title: "A Bolsa Amarela",
    author: "Lygia Bojunga",
    category: "Infantojuvenil",
    price: 28.5,
    condition: "Novo",
    year: "2021",
    publisher: "Casa Lygia Bojunga",
    pages: 136,
    stock: 2,
    seller: "Leitura de Bolso",
    city: "Recife, PE",
    type: "Novo",
    freeShipping: true,
    promotion: true,
    corporatePurchase: true,
    rating: 5.0,
    language: "Portugues",
    sales: 33,
    cover: "cover-violet",
    short: "Exemplar novo de um classico da literatura juvenil.",
    description: "Livro sem sinais de uso relevante. Boa opcao para escola, presente ou primeira biblioteca de jovens leitores.",
    notes: ["Sem marcas", "Capa e miolo preservados", "Estoque demonstrativo"]
  },
  {
    id: 9,
    title: "Clean Code",
    author: "Robert C. Martin",
    category: "Tecnologia",
    price: 92.0,
    condition: "Bom",
    year: "2009",
    publisher: "Prentice Hall",
    pages: 464,
    stock: 1,
    seller: "Livros & Codigo",
    city: "Belo Horizonte, MG",
    type: "Usado",
    freeShipping: false,
    promotion: false,
    corporatePurchase: true,
    rating: 4.7,
    language: "Ingles",
    sales: 29,
    cover: "cover-teal",
    short: "Referencia de engenharia de software com uso moderado.",
    description: "Exemplar importado em bom estado, indicado para estudos de boas praticas e arquitetura de codigo.",
    notes: ["Alguns grifos", "Capa com sinais de manuseio", "Lombada firme"]
  },
  {
    id: 10,
    title: "Arte Moderna",
    author: "Giulio Carlo Argan",
    category: "Arte",
    price: 76.4,
    condition: "Muito bom",
    year: "1992",
    publisher: "Companhia das Letras",
    pages: 709,
    stock: 1,
    seller: "Prateleira Criativa",
    city: "Rio de Janeiro, RJ",
    type: "Usado",
    freeShipping: true,
    promotion: false,
    corporatePurchase: false,
    rating: 4.6,
    language: "Portugues",
    sales: 17,
    cover: "cover-blue",
    short: "Volume de referencia para arte, cultura e historia.",
    description: "Livro usado, amplo e bem preservado, com conteudo indicado para pesquisa e formacao cultural.",
    notes: ["Capa preservada", "Sem rasgos", "Corte levemente escurecido"]
  }
];

const literaryUniverses = [
  { name: "Machado de Assis", theme: "universe-wine", link: "livros.html?q=Machado" },
  { name: "Literatura brasileira", theme: "universe-gold", link: "livros.html?categoria=Literatura" },
  { name: "Programacao", theme: "universe-teal", link: "livros.html?categoria=Tecnologia" },
  { name: "Arte moderna", theme: "universe-blue", link: "livros.html?categoria=Arte" },
  { name: "Historia do Brasil", theme: "universe-sage", link: "livros.html?categoria=Historia" },
  { name: "Infantojuvenil", theme: "universe-clay", link: "livros.html?categoria=Infantojuvenil" }
];

const boxSets = [
  {
    title: "Box Literatura Brasileira",
    description: "Classicos nacionais para montar uma primeira estante.",
    price: 119.9,
    theme: "box-wine",
    link: "livros.html?categoria=Literatura"
  },
  {
    title: "Box Estudos em Java",
    description: "Livros tecnicos para acompanhar projetos full-stack.",
    price: 148.0,
    theme: "box-teal",
    link: "livros.html?categoria=Tecnologia"
  },
  {
    title: "Box Historia e Sociedade",
    description: "Obras para leitura, pesquisa e repertorio cultural.",
    price: 132.4,
    theme: "box-gold",
    link: "livros.html?categoria=Historia"
  },
  {
    title: "Box Arte e Design",
    description: "Referencias visuais e leituras de design.",
    price: 156.3,
    theme: "box-blue",
    link: "livros.html?categoria=Arte"
  }
];

const formatter = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL"
});

const cartKey = "seboDigitalCart";
const page = document.body.dataset.page;

document.addEventListener("DOMContentLoaded", () => {
  setupNavigation();
  setupSearchForms();
  setupCartEvents();
  setupAuthPage();
  updateCartBadge();

  if (page === "home") renderHome();
  if (page === "catalog") renderCatalog();
  if (page === "detail") renderDetail();
  if (page === "cart") renderCart();
});

function setupNavigation() {
  const toggle = document.querySelector("[data-menu-toggle]");
  const nav = document.querySelector("[data-site-nav]");
  const catalogMenu = document.querySelector("[data-catalog-menu]");
  const catalogTrigger = document.querySelector("[data-catalog-trigger]");
  const accountMenu = document.querySelector("[data-account-menu]");
  const accountTrigger = document.querySelector("[data-account-trigger]");
  if (!toggle || !nav) return;

  toggle.addEventListener("click", () => {
    const isOpen = nav.classList.toggle("is-open");
    document.body.classList.toggle("nav-open", isOpen);
    toggle.setAttribute("aria-expanded", String(isOpen));
    if (!isOpen && catalogMenu) setCatalogState(false);
  });

  setupAccountMenu(accountMenu, accountTrigger);

  if (!catalogMenu || !catalogTrigger) return;

  const isMobileNav = () => window.matchMedia("(max-width: 980px)").matches;
  const setCatalogState = (isOpen) => {
    catalogMenu.classList.toggle("is-catalog-open", isOpen);
    catalogTrigger.setAttribute("aria-expanded", String(isOpen));
  };

  catalogMenu.addEventListener("mouseenter", () => {
    if (!isMobileNav()) setCatalogState(true);
  });
  catalogMenu.addEventListener("mouseleave", () => {
    if (!isMobileNav()) setCatalogState(false);
  });
  catalogMenu.addEventListener("focusin", () => {
    if (!isMobileNav()) setCatalogState(true);
  });
  catalogMenu.addEventListener("focusout", (event) => {
    if (!isMobileNav() && !catalogMenu.contains(event.relatedTarget)) setCatalogState(false);
  });

  catalogTrigger.addEventListener("click", (event) => {
    if (!isMobileNav()) return;
    event.preventDefault();
    setCatalogState(!catalogMenu.classList.contains("is-catalog-open"));
  });
}

function setupAccountMenu(accountMenu, accountTrigger) {
  if (!accountMenu || !accountTrigger) return;

  const isCompactHeader = () => window.matchMedia("(max-width: 700px)").matches;
  const setAccountState = (isOpen) => {
    accountMenu.classList.toggle("is-account-open", isOpen);
    accountTrigger.setAttribute("aria-expanded", String(isOpen));
  };

  accountMenu.addEventListener("mouseenter", () => {
    if (!isCompactHeader()) setAccountState(true);
  });
  accountMenu.addEventListener("mouseleave", () => {
    if (!isCompactHeader()) setAccountState(false);
  });
  accountMenu.addEventListener("focusin", () => setAccountState(true));
  accountMenu.addEventListener("focusout", (event) => {
    if (!accountMenu.contains(event.relatedTarget)) setAccountState(false);
  });

  accountTrigger.addEventListener("click", (event) => {
    if (!isCompactHeader()) return;
    event.preventDefault();
    setAccountState(!accountMenu.classList.contains("is-account-open"));
  });
}

function setupAuthPage() {
  document.querySelectorAll("[data-password-toggle]").forEach((button) => {
    button.addEventListener("click", () => {
      const input = button.parentElement.querySelector("input");
      const isPassword = input.type === "password";
      input.type = isPassword ? "text" : "password";
      button.textContent = isPassword ? "Ocultar" : "Mostrar";
    });
  });

  const loginForm = document.querySelector("#login-form");
  const signupForm = document.querySelector("#signup-form");

  if (loginForm) {
    loginForm.addEventListener("submit", (event) => {
      event.preventDefault();
      showToast("Login demonstrativo: autenticacao sera conectada ao backend.");
    });
  }

  if (signupForm) {
    signupForm.addEventListener("submit", (event) => {
      event.preventDefault();
      showToast("Cadastro demonstrativo: criacao de conta entra na etapa backend.");
    });
  }
}

function setupSearchForms() {
  document.querySelectorAll("[data-search-form]").forEach((form) => {
    form.addEventListener("submit", (event) => {
      event.preventDefault();
      const input = form.querySelector("input[type='search']");
      const query = input ? input.value.trim() : "";
      const target = query ? `livros.html?q=${encodeURIComponent(query)}` : "livros.html";
      window.location.href = target;
    });
  });
}

function setupCartEvents() {
  document.addEventListener("click", (event) => {
    const addButton = event.target.closest("[data-add-cart]");
    const removeButton = event.target.closest("[data-remove-cart]");
    const quantityButton = event.target.closest("[data-cart-quantity]");

    if (addButton) {
      addToCart(Number(addButton.dataset.addCart));
    }

    if (removeButton) {
      removeFromCart(Number(removeButton.dataset.removeCart));
    }

    if (quantityButton) {
      changeQuantity(Number(quantityButton.dataset.cartQuantity), Number(quantityButton.dataset.delta));
    }
  });
}

function renderHome() {
  const bestSellers = sortBooks(books, "mais-vendidos");
  renderBooks(document.querySelector("#best-seller-shelf"), bestSellers);
  renderAuthors();
  renderBooks(document.querySelector("#classic-books"), books.filter((book) => book.category === "Literatura").slice(0, 4));
  renderUniverses();
  renderBoxSets();
  setupShelfControls();
}

function setupShelfControls() {
  const shelf = document.querySelector("#best-seller-shelf");
  const previousButton = document.querySelector("[data-shelf-prev]");
  const nextButton = document.querySelector("[data-shelf-next]");
  if (!shelf || !previousButton || !nextButton) return;

  const scrollShelf = (direction) => {
    const firstCard = shelf.querySelector(".book-card");
    const distance = firstCard ? firstCard.getBoundingClientRect().width + 18 : 280;
    shelf.scrollBy({ left: direction * distance, behavior: "smooth" });
  };

  previousButton.addEventListener("click", () => scrollShelf(-1));
  nextButton.addEventListener("click", () => scrollShelf(1));
}

function renderAuthors() {
  const container = document.querySelector("#author-strip");
  if (!container) return;

  const authors = [...new Map(books.map((book) => [book.author, book])).values()].slice(0, 8);
  container.innerHTML = authors.map((book) => `
    <a class="author-chip" href="livros.html?autor=${encodeURIComponent(book.author)}">
      <span>${getInitials(book.author)}</span>
      <strong>${escapeHtml(book.author)}</strong>
      <small>${escapeHtml(book.category)}</small>
    </a>
  `).join("");
}

function renderUniverses() {
  const container = document.querySelector("#literary-universes");
  if (!container) return;

  container.innerHTML = literaryUniverses.map((universe) => `
    <a class="universe-card ${universe.theme}" href="${universe.link}">
      <span aria-hidden="true">${getInitials(universe.name)}</span>
      <strong>${escapeHtml(universe.name)}</strong>
    </a>
  `).join("");
}

function renderBoxSets() {
  const container = document.querySelector("#box-sets");
  if (!container) return;

  container.innerHTML = boxSets.map((box) => `
    <a class="box-card" href="${box.link}">
      <span class="box-visual ${box.theme}" aria-hidden="true">
        <i></i><i></i><i></i>
      </span>
      <strong>${escapeHtml(box.title)}</strong>
      <small>${escapeHtml(box.description)}</small>
      <b>A partir de ${formatter.format(box.price)}</b>
    </a>
  `).join("");
}

function renderCatalog() {
  const searchInput = document.querySelector("#catalog-search");
  const categoryFilter = document.querySelector("#category-filter");
  const typeFilter = document.querySelector("#book-type-filter");
  const authorFilter = document.querySelector("#author-filter");
  const publisherFilter = document.querySelector("#publisher-filter");
  const yearFilter = document.querySelector("#year-filter");
  const cityFilter = document.querySelector("#city-filter");
  const sellerFilter = document.querySelector("#seller-filter");
  const languageFilter = document.querySelector("#language-filter");
  const ratingFilter = document.querySelector("#rating-filter");
  const minPriceFilter = document.querySelector("#min-price-filter");
  const maxPriceFilter = document.querySelector("#max-price-filter");
  const freeShippingFilter = document.querySelector("#free-shipping-filter");
  const promotionFilter = document.querySelector("#promotion-filter");
  const corporateFilter = document.querySelector("#corporate-filter");
  const sortFilter = document.querySelector("#sort-filter");
  const clearButton = document.querySelector("#clear-filters");
  const params = new URLSearchParams(window.location.search);

  fillSelect(categoryFilter, uniqueValues("category"));
  fillSelect(typeFilter, uniqueValues("type"));
  fillSelect(authorFilter, uniqueValues("author"));
  fillSelect(publisherFilter, uniqueValues("publisher"));
  fillSelect(yearFilter, uniqueValues("year").sort((a, b) => Number(b) - Number(a)));
  fillSelect(cityFilter, uniqueValues("city"));
  fillSelect(sellerFilter, uniqueValues("seller"));
  fillSelect(languageFilter, uniqueValues("language"));

  searchInput.value = params.get("q") || "";
  categoryFilter.value = params.get("categoria") || "";
  typeFilter.value = params.get("tipo") || "";
  authorFilter.value = params.get("autor") || "";
  publisherFilter.value = params.get("editora") || "";
  yearFilter.value = params.get("ano") || "";
  cityFilter.value = params.get("cidade") || "";
  sellerFilter.value = params.get("sebo") || "";
  languageFilter.value = params.get("idioma") || "";
  ratingFilter.value = params.get("avaliacao") || "";
  minPriceFilter.value = params.get("preco_min") || "";
  maxPriceFilter.value = params.get("preco_max") || "";
  freeShippingFilter.checked = params.get("frete") === "gratis";
  promotionFilter.checked = params.get("promo") === "true";
  corporateFilter.checked = params.get("corporativo") === "true";
  sortFilter.value = params.get("ordenar") || "relevance";

  const update = () => {
    const filters = {
      query: searchInput.value.trim().toLowerCase(),
      category: categoryFilter.value,
      type: typeFilter.value,
      author: authorFilter.value,
      publisher: publisherFilter.value,
      year: yearFilter.value,
      city: cityFilter.value,
      seller: sellerFilter.value,
      language: languageFilter.value,
      rating: Number(ratingFilter.value || 0),
      minPrice: Number(minPriceFilter.value || 0),
      maxPrice: Number(maxPriceFilter.value || 0),
      freeShipping: freeShippingFilter.checked,
      promotion: promotionFilter.checked,
      corporate: corporateFilter.checked,
      sort: sortFilter.value
    };

    let filtered = books.filter((book) => {
      const searchable = `${book.title} ${book.author} ${book.category} ${book.publisher} ${book.seller} ${book.city} ${book.language}`.toLowerCase();
      const matchesQuery = !filters.query || searchable.includes(filters.query);
      const matchesCategory = !filters.category || book.category === filters.category;
      const matchesType = !filters.type || book.type === filters.type;
      const matchesAuthor = !filters.author || book.author === filters.author;
      const matchesPublisher = !filters.publisher || book.publisher === filters.publisher;
      const matchesYear = !filters.year || book.year === filters.year;
      const matchesCity = !filters.city || book.city === filters.city;
      const matchesSeller = !filters.seller || book.seller === filters.seller;
      const matchesLanguage = !filters.language || book.language === filters.language;
      const matchesRating = !filters.rating || book.rating >= filters.rating;
      const matchesMinPrice = !filters.minPrice || book.price >= filters.minPrice;
      const matchesMaxPrice = !filters.maxPrice || book.price <= filters.maxPrice;
      const matchesFreeShipping = !filters.freeShipping || book.freeShipping;
      const matchesPromotion = !filters.promotion || book.promotion;
      const matchesCorporate = !filters.corporate || book.corporatePurchase;

      return matchesQuery
        && matchesCategory
        && matchesType
        && matchesAuthor
        && matchesPublisher
        && matchesYear
        && matchesCity
        && matchesSeller
        && matchesLanguage
        && matchesRating
        && matchesMinPrice
        && matchesMaxPrice
        && matchesFreeShipping
        && matchesPromotion
        && matchesCorporate;
    });

    filtered = sortBooks(filtered, filters.sort);
    renderBooks(document.querySelector("#catalog-books"), filtered);
    updateResultsCount(filtered.length);
  };

  [
    searchInput,
    categoryFilter,
    typeFilter,
    authorFilter,
    publisherFilter,
    yearFilter,
    cityFilter,
    sellerFilter,
    languageFilter,
    ratingFilter,
    minPriceFilter,
    maxPriceFilter,
    freeShippingFilter,
    promotionFilter,
    corporateFilter,
    sortFilter
  ].forEach((control) => {
    control.addEventListener("input", update);
    control.addEventListener("change", update);
  });

  clearButton.addEventListener("click", () => {
    searchInput.value = "";
    categoryFilter.value = "";
    typeFilter.value = "";
    authorFilter.value = "";
    publisherFilter.value = "";
    yearFilter.value = "";
    cityFilter.value = "";
    sellerFilter.value = "";
    languageFilter.value = "";
    ratingFilter.value = "";
    minPriceFilter.value = "";
    maxPriceFilter.value = "";
    freeShippingFilter.checked = false;
    promotionFilter.checked = false;
    corporateFilter.checked = false;
    sortFilter.value = "relevance";
    update();
  });

  update();
}

function renderDetail() {
  const params = new URLSearchParams(window.location.search);
  const selectedBook = books.find((book) => book.id === Number(params.get("id"))) || books[0];
  const detail = document.querySelector("#book-detail");
  const breadcrumb = document.querySelector("#breadcrumb-title");

  document.title = `${selectedBook.title} | Sebo Digital`;
  breadcrumb.textContent = selectedBook.title;

  detail.innerHTML = `
    <div class="detail-cover-wrap">
      ${renderCover(selectedBook)}
    </div>
    <div class="detail-panel">
      <article class="detail-main">
        <span class="condition-badge">${escapeHtml(selectedBook.condition)}</span>
        <h1>${escapeHtml(selectedBook.title)}</h1>
        <p class="detail-author">por ${escapeHtml(selectedBook.author)}</p>
        <p class="detail-description">${escapeHtml(selectedBook.description)}</p>
        <div class="detail-buy">
          <div>
            <span class="price">${formatter.format(selectedBook.price)}</span>
            <p class="summary-note">Estoque: ${selectedBook.stock} ${selectedBook.stock === 1 ? "exemplar" : "exemplares"}</p>
          </div>
          <button class="primary-button" type="button" data-add-cart="${selectedBook.id}">Adicionar ao carrinho</button>
        </div>
      </article>
      <div class="info-grid">
        ${renderInfo("Categoria", selectedBook.category)}
        ${renderInfo("Tipo", selectedBook.type)}
        ${renderInfo("Editora", selectedBook.publisher)}
        ${renderInfo("Ano", selectedBook.year)}
        ${renderInfo("Idioma", selectedBook.language)}
        ${renderInfo("Avaliacao", `${selectedBook.rating.toFixed(1)} estrelas`)}
        ${renderInfo("Frete", selectedBook.freeShipping ? "Gratis" : "Calculado")}
        ${renderInfo("Paginas", `${selectedBook.pages}`)}
        ${renderInfo("Vendedor", selectedBook.seller)}
        ${renderInfo("Localizacao", selectedBook.city)}
      </div>
      <article class="detail-main">
        <h2>Conservacao</h2>
        <ul class="detail-list">
          ${selectedBook.notes.map((note) => `<li>${escapeHtml(note)}</li>`).join("")}
        </ul>
      </article>
    </div>
  `;

  const related = books
    .filter((book) => book.id !== selectedBook.id && book.category === selectedBook.category)
    .concat(books.filter((book) => book.id !== selectedBook.id && book.category !== selectedBook.category))
    .slice(0, 4);

  renderBooks(document.querySelector("#related-books"), related);
}

function renderCart() {
  const cart = getCart();
  const itemsElement = document.querySelector("#cart-items");
  const summaryElement = document.querySelector("#cart-summary");

  if (!cart.length) {
    itemsElement.innerHTML = `
      <div class="empty-state">
        <h2>Seu carrinho esta vazio</h2>
        <p>Escolha alguns livros no catalogo para simular uma intencao de compra.</p>
      </div>
    `;
    summaryElement.innerHTML = `
      <h2>Resumo</h2>
      <p class="summary-note">Nenhum item selecionado ainda.</p>
      <a class="primary-button" href="livros.html">Ver catalogo</a>
    `;
    return;
  }

  const detailedItems = cart
    .map((item) => ({
      ...item,
      book: books.find((book) => book.id === item.id)
    }))
    .filter((item) => item.book);

  itemsElement.innerHTML = detailedItems.map(renderCartItem).join("");

  const subtotal = detailedItems.reduce((sum, item) => sum + item.book.price * item.quantity, 0);
  const shippingPreview = subtotal > 0 ? 12.9 : 0;
  const total = subtotal + shippingPreview;

  summaryElement.innerHTML = `
    <h2>Resumo</h2>
    <div class="summary-line">
      <span>Subtotal</span>
      <strong>${formatter.format(subtotal)}</strong>
    </div>
    <div class="summary-line">
      <span>Frete estimado</span>
      <strong>${formatter.format(shippingPreview)}</strong>
    </div>
    <div class="summary-total">
      <span>Total</span>
      <strong>${formatter.format(total)}</strong>
    </div>
    <p class="summary-note">Checkout, login, endereco e pagamento entram na etapa backend.</p>
    <button class="primary-button" type="button" disabled>Finalizar depois</button>
  `;
}

function renderBooks(container, collection) {
  if (!container) return;

  if (!collection.length) {
    container.innerHTML = `
      <div class="empty-state">
        <h3>Nenhum livro encontrado</h3>
        <p>Tente ajustar a busca ou limpar os filtros.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = collection.map(renderBookCard).join("");
}

function renderBookCard(book) {
  return `
    <article class="book-card">
      <a href="detalhes.html?id=${book.id}" aria-label="Ver detalhes de ${escapeHtml(book.title)}">
        ${renderCover(book)}
      </a>
      <div class="book-card-body">
        <div class="book-meta">
          <h3>${escapeHtml(book.title)}</h3>
          <p>${escapeHtml(book.author)}</p>
          <p>${escapeHtml(book.short)}</p>
        </div>
        <div class="book-price-row">
          <span class="price">${formatter.format(book.price)}</span>
          <span class="condition-badge">${escapeHtml(book.condition)}</span>
        </div>
        <div class="book-tags" aria-label="Informacoes comerciais">
          <span>${escapeHtml(book.type)}</span>
          <span>${book.freeShipping ? "Frete gratis" : "Frete calculado"}</span>
          <span>${escapeHtml(book.language)}</span>
          <span>${book.rating.toFixed(1)} estrelas</span>
        </div>
        <div class="book-actions">
          <a class="details-link" href="detalhes.html?id=${book.id}">Detalhes</a>
          <button class="mini-button" type="button" data-add-cart="${book.id}">Adicionar</button>
        </div>
      </div>
    </article>
  `;
}

function renderCover(book) {
  return `
    <div class="book-cover ${book.cover}" aria-hidden="true">
      <span class="cover-category">${escapeHtml(book.category)}</span>
      <strong>${escapeHtml(book.title)}</strong>
      <small>${escapeHtml(book.author)}</small>
    </div>
  `;
}

function renderCartItem(item) {
  const { book, quantity } = item;
  return `
    <article class="cart-item">
      ${renderCover(book)}
      <div class="cart-item-info">
        <h2>${escapeHtml(book.title)}</h2>
        <p>${escapeHtml(book.author)} · ${escapeHtml(book.type)} · ${escapeHtml(book.condition)}</p>
        <div class="quantity-control" aria-label="Quantidade de ${escapeHtml(book.title)}">
          <button type="button" data-cart-quantity="${book.id}" data-delta="-1" aria-label="Diminuir quantidade">-</button>
          <span>${quantity}</span>
          <button type="button" data-cart-quantity="${book.id}" data-delta="1" aria-label="Aumentar quantidade">+</button>
        </div>
      </div>
      <div class="cart-item-total">
        <strong>${formatter.format(book.price * quantity)}</strong>
        <button class="danger-button" type="button" data-remove-cart="${book.id}">Remover</button>
      </div>
    </article>
  `;
}

function renderInfo(label, value) {
  return `
    <div class="info-card">
      <span>${escapeHtml(label)}</span>
      <strong>${escapeHtml(value)}</strong>
    </div>
  `;
}

function fillSelect(select, values) {
  values.forEach((value) => {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = value;
    select.appendChild(option);
  });
}

function uniqueValues(key) {
  return [...new Set(books.map((book) => book[key]))].sort((a, b) => a.localeCompare(b, "pt-BR"));
}

function sortBooks(collection, sort) {
  const sorted = [...collection];

  if (sort === "price-asc") return sorted.sort((a, b) => a.price - b.price);
  if (sort === "price-desc") return sorted.sort((a, b) => b.price - a.price);
  if (sort === "title") return sorted.sort((a, b) => a.title.localeCompare(b.title, "pt-BR"));
  if (sort === "mais-vendidos") return sorted.sort((a, b) => b.sales - a.sales || b.rating - a.rating);

  return sorted;
}

function updateResultsCount(total) {
  const count = document.querySelector("#results-count");
  if (!count) return;
  count.textContent = `${total} ${total === 1 ? "livro encontrado" : "livros encontrados"}`;
}

function getCart() {
  try {
    return JSON.parse(localStorage.getItem(cartKey)) || [];
  } catch (error) {
    return [];
  }
}

function saveCart(cart) {
  localStorage.setItem(cartKey, JSON.stringify(cart));
  updateCartBadge();
}

function addToCart(id) {
  const book = books.find((item) => item.id === id);
  if (!book) return;

  const cart = getCart();
  const existing = cart.find((item) => item.id === id);

  if (existing) {
    existing.quantity = Math.min(existing.quantity + 1, book.stock);
  } else {
    cart.push({ id, quantity: 1 });
  }

  saveCart(cart);
  showToast(`${book.title} foi adicionado ao carrinho.`);

  if (page === "cart") renderCart();
}

function removeFromCart(id) {
  const cart = getCart().filter((item) => item.id !== id);
  saveCart(cart);
  renderCart();
}

function changeQuantity(id, delta) {
  const cart = getCart();
  const item = cart.find((entry) => entry.id === id);
  const book = books.find((entry) => entry.id === id);
  if (!item || !book) return;

  item.quantity += delta;

  if (item.quantity <= 0) {
    removeFromCart(id);
    return;
  }

  item.quantity = Math.min(item.quantity, book.stock);
  saveCart(cart);
  renderCart();
}

function updateCartBadge() {
  const total = getCart().reduce((sum, item) => sum + item.quantity, 0);
  document.querySelectorAll("[data-cart-count]").forEach((element) => {
    element.textContent = total;
  });
}

function showToast(message) {
  let toast = document.querySelector(".toast");

  if (!toast) {
    toast = document.createElement("div");
    toast.className = "toast";
    toast.setAttribute("role", "status");
    document.body.appendChild(toast);
  }

  toast.textContent = message;
  toast.classList.add("is-visible");
  window.clearTimeout(showToast.timeout);
  showToast.timeout = window.setTimeout(() => {
    toast.classList.remove("is-visible");
  }, 2600);
}

function getInitials(value) {
  return String(value)
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((word) => word[0])
    .join("")
    .toUpperCase();
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
