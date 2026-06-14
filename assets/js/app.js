let books = [];
let catalogLoadError = null;

const pngImage = (sourceUrl, width, height, fit = "cover") =>
  `https://images.weserv.nl/?url=${encodeURIComponent(sourceUrl)}&w=${width}&h=${height}&fit=${fit}&output=png`;

const openLibraryPng = (coverId, width = 800, height = 1200, fit = "cover") =>
  pngImage(`https://covers.openlibrary.org/b/id/${coverId}-L.jpg?default=false`, width, height, fit);

const literaryUniverses = [
  { name: "Harry Potter", theme: "universe-wine", link: "livros.html?q=Harry%20Potter", imageUrl: pngImage("https://i.pinimg.com/736x/09/cf/fd/09cffdfd899a2b71f90248249e27ed11.jpg", 520, 520) },
  { name: "Senhor dos Aneis", theme: "universe-sage", link: "livros.html?q=Senhor%20dos%20Aneis", imageUrl: pngImage("https://upload.wikimedia.org/wikipedia/commons/4/45/The_Argonath_And_The_Falls_Of_Rauros_(199869205).jpeg", 520, 520) },
  { name: "Machado de Assis", theme: "universe-wine", link: "livros.html?q=Machado", imageUrl: pngImage("https://static.todamateria.com.br/upload/ma/ch/machadodeassis-cke.jpg", 520, 520) },
  { name: "Literatura brasileira", theme: "universe-gold", link: "livros.html?categoria=Literatura", imageUrl: pngImage("https://upload.wikimedia.org/wikipedia/commons/4/43/Coleção_Literatura_Brasileira_Contemporânea._2020..jpg", 520, 520) },
  { name: "Programacao", theme: "universe-teal", link: "livros.html?categoria=Tecnologia", imageUrl: "https://upload.wikimedia.org/wikipedia/commons/5/51/Abstract_monitor_with_IDE.png" },
  { name: "Arte moderna", theme: "universe-blue", link: "livros.html?categoria=Arte", imageUrl: pngImage("https://upload.wikimedia.org/wikipedia/commons/e/e5/'Abstract_sky',_1993_-_small_acrylic_painting_by_Dutch_artist_Fons_Heijnsbroek;_free_download_abstract_art_image,_CCO.jpg", 520, 520) },
  { name: "Historia do Brasil", theme: "universe-sage", link: "livros.html?categoria=Historia", imageUrl: pngImage("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Pedro_Américo_-_Independência_ou_Morte_-_Google_Art_Project.jpg/960px-Pedro_Américo_-_Independência_ou_Morte_-_Google_Art_Project.jpg", 520, 520) },
  { name: "Infantojuvenil", theme: "universe-clay", link: "livros.html?categoria=Infantojuvenil", imageUrl: pngImage("https://upload.wikimedia.org/wikipedia/commons/3/38/Dulcibella-the-coming-of-the-fairies-crop.jpg", 520, 520) }
];

const boxSets = [
  {
    title: "Box Harry Potter",
    description: "Saga de fantasia para colecionar ou presentear.",
    price: 249.9,
    theme: "box-wine",
    imageUrl: openLibraryPng(8457523, 760, 760, "contain"),
    link: "livros.html?q=Harry%20Potter"
  },
  {
    title: "Box Senhor dos Aneis",
    description: "Fantasia classica em trilogia para colecionadores.",
    price: 189.9,
    theme: "box-sage",
    imageUrl: openLibraryPng(14625765, 760, 760, "contain"),
    link: "livros.html?q=Senhor%20dos%20Aneis"
  },
  {
    title: "Box Fantasia Classica",
    description: "Selecao para quem busca sagas e mundos literarios.",
    price: 169.9,
    theme: "box-blue",
    imageUrl: openLibraryPng(9321656, 760, 760, "contain"),
    link: "livros.html?categoria=Fantasia"
  },
  {
    title: "Box Literatura Brasileira",
    description: "Classicos nacionais para montar uma primeira estante.",
    price: 119.9,
    theme: "box-gold",
    imageUrl: openLibraryPng(647501, 760, 760, "contain"),
    link: "livros.html?categoria=Literatura"
  },
  {
    title: "Box Programacao",
    description: "Livros tecnicos para acompanhar projetos full-stack.",
    price: 148.0,
    theme: "box-teal",
    imageUrl: openLibraryPng(8065615, 760, 760, "contain"),
    link: "livros.html?categoria=Tecnologia"
  },
  {
    title: "Box Arte e Design",
    description: "Referencias visuais e leituras de design.",
    price: 156.3,
    theme: "box-blue",
    imageUrl: openLibraryPng(12370709, 760, 760, "contain"),
    link: "livros.html?categoria=Arte"
  }
];

const formatter = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL"
});

const cartKey = "seboDigitalCart";
const authTokenKey = "seboDigitalAuth";
const authMessageKey = "seboDigitalAuthMessage";
const apiBaseUrl = window.SEBO_API_URL || "http://localhost:8080";
const page = document.body.dataset.page;

document.addEventListener("DOMContentLoaded", async () => {
  setupThemeToggle();
  setupNavigation();
  setupSearchForms();
  setupCartEvents();
  setupAuthPage();
  showPendingAuthMessage();
  updateCartBadge();
  await loadBooksFromApi();

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

  applyLoggedInAccountState(accountMenu, accountTrigger);

  let closeTimer;
  const isCompactHeader = () => window.matchMedia("(max-width: 700px)").matches;
  const setAccountState = (isOpen) => {
    accountMenu.classList.toggle("is-account-open", isOpen);
    accountTrigger.setAttribute("aria-expanded", String(isOpen));
  };
  const openAccountMenu = () => {
    window.clearTimeout(closeTimer);
    setAccountState(true);
  };
  const closeAccountMenu = () => {
    window.clearTimeout(closeTimer);
    closeTimer = window.setTimeout(() => setAccountState(false), 220);
  };

  accountMenu.addEventListener("mouseenter", () => {
    if (!isCompactHeader()) openAccountMenu();
  });
  accountMenu.addEventListener("mouseleave", () => {
    if (!isCompactHeader()) closeAccountMenu();
  });
  accountMenu.addEventListener("focusin", () => openAccountMenu());
  accountMenu.addEventListener("focusout", (event) => {
    if (!accountMenu.contains(event.relatedTarget)) closeAccountMenu();
  });

  accountTrigger.addEventListener("click", (event) => {
    if (!isCompactHeader()) return;
    event.preventDefault();
    setAccountState(!accountMenu.classList.contains("is-account-open"));
  });

  accountMenu.addEventListener("click", (event) => {
    const logoutButton = event.target.closest("[data-logout]");
    if (!logoutButton) return;
    localStorage.removeItem(authTokenKey);
    showToast("Voce saiu da sua conta.");
    window.setTimeout(() => window.location.assign("index.html"), 500);
  });
}

function setupAuthPage() {
  handleOAuthRedirect();
  setupSocialLoginButtons();

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
    loginForm.addEventListener("submit", async (event) => {
      event.preventDefault();
      const formData = new FormData(loginForm);
      try {
        const auth = await apiRequest("/api/auth/login", {
          method: "POST",
          body: JSON.stringify({
            email: formData.get("email"),
            senha: formData.get("password")
          })
        });
        completeAuthentication(auth, `Bem-vindo, ${auth.usuario.nome}.`);
      } catch (error) {
        showToast(error.message || "Nao foi possivel entrar agora.");
      }
    });
  }

  if (signupForm) {
    signupForm.addEventListener("submit", async (event) => {
      event.preventDefault();
      const formData = new FormData(signupForm);
      try {
        const auth = await apiRequest("/api/auth/cadastro", {
          method: "POST",
          body: JSON.stringify({
            nome: formData.get("nome"),
            email: formData.get("email"),
            senha: formData.get("password")
          })
        });
        completeAuthentication(auth, `Conta criada para ${auth.usuario.nome}.`);
      } catch (error) {
        showToast(error.message || "Nao foi possivel criar a conta agora.");
      }
    });
  }
}

function setupSocialLoginButtons() {
  document.querySelectorAll("[data-oauth-provider]").forEach((button) => {
    button.addEventListener("click", () => {
      const provider = button.dataset.oauthProvider;
      const redirectUri = getOAuthRedirectUri();
      const loginUrl = new URL(`${apiBaseUrl}/api/auth/oauth2/${provider}`);
      loginUrl.searchParams.set("redirect_uri", redirectUri);
      window.location.href = loginUrl.toString();
    });
  });
}

function handleOAuthRedirect() {
  const params = new URLSearchParams(window.location.search);
  const oauthStatus = params.get("oauth");
  if (!oauthStatus) return;

  if (oauthStatus === "success" && params.get("token")) {
    const usuarioId = params.get("usuarioId");
    const auth = {
      token: params.get("token"),
      tipo: params.get("tipo") || "Bearer",
      expiraEm: params.get("expiraEm"),
      usuario: {
        id: usuarioId ? Number(usuarioId) : null,
        nome: params.get("nome") || "Usuario",
        email: params.get("email") || "",
        role: params.get("role") || "USER",
        authProvider: params.get("authProvider") || "LOCAL",
        fotoUrl: params.get("fotoUrl") || ""
      }
    };

    completeAuthentication(auth, `Bem-vindo, ${auth.usuario.nome}.`);
    return;
  }

  if (oauthStatus === "erro") {
    showToast(params.get("mensagem") || "Nao foi possivel entrar com a conta social.");
    cleanOAuthUrl();
  }
}

function completeAuthentication(auth, message) {
  const normalizedAuth = normalizeAuth(auth);
  localStorage.setItem(authTokenKey, JSON.stringify(normalizedAuth));
  sessionStorage.setItem(authMessageKey, message);
  window.location.assign("index.html");
}

function normalizeAuth(auth) {
  return {
    ...auth,
    usuario: {
      ...(auth.usuario || {}),
      authProvider: auth.usuario?.authProvider || "LOCAL",
      fotoUrl: auth.usuario?.fotoUrl || ""
    }
  };
}

function showPendingAuthMessage() {
  const message = sessionStorage.getItem(authMessageKey);
  if (!message) return;
  sessionStorage.removeItem(authMessageKey);
  showToast(message);
}

function applyLoggedInAccountState(accountMenu, accountTrigger) {
  const auth = getStoredAuth();
  if (!auth?.token || !auth.usuario) return;

  const firstName = String(auth.usuario.nome || "Conta").trim().split(" ")[0];
  accountTrigger.textContent = `Ola, ${firstName}`;
  accountTrigger.href = "login.html?next=conta";

  const dropdown = accountMenu.querySelector(".account-dropdown");
  if (!dropdown) return;

  dropdown.innerHTML = `
    <a href="login.html?next=conta">Minha conta</a>
    <a href="login.html?next=pedidos">Meus pedidos</a>
    <a href="login.html?next=listas">Minhas listas</a>
    <button type="button" data-logout>Sair</button>
  `;
}

function getStoredAuth() {
  try {
    return JSON.parse(localStorage.getItem(authTokenKey));
  } catch (error) {
    return null;
  }
}

function getOAuthRedirectUri() {
  if (window.SEBO_OAUTH_REDIRECT_URL) return window.SEBO_OAUTH_REDIRECT_URL;

  if (window.location.protocol === "file:") {
    return "http://localhost:5500/login.html";
  }

  const url = new URL(window.location.href);
  url.search = "";
  url.hash = "";
  return url.toString();
}

function cleanOAuthUrl() {
  const cleanUrl = `${window.location.origin}${window.location.pathname}${window.location.hash}`;
  window.history.replaceState({}, document.title, cleanUrl);
}

async function loadBooksFromApi() {
  catalogLoadError = null;
  books = [];

  try {
    const apiBooks = await apiRequest("/api/livros");
    if (Array.isArray(apiBooks)) {
      books = apiBooks.map(mapApiBook);
    }
  } catch (error) {
    catalogLoadError = error;
    console.warn("Catalogo indisponivel; verifique a API e a conexao com PostgreSQL.", error);
  }
}

async function apiRequest(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };

  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    let message = "Nao foi possivel concluir a operacao.";
    try {
      const errorBody = await response.json();
      message = errorBody.erro || message;
    } catch (error) {
      message = response.statusText || message;
    }
    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
}

function mapApiBook(apiBook) {
  const copies = flattenCopies(apiBook.copias);
  const bestCopy = selectBestCopy(copies);
  const highlights = apiBook.destaques || {};

  return {
    id: apiBook.id,
    title: apiBook.titulo,
    author: apiBook.autor,
    authorImageUrl: apiBook.autorImagemUrl,
    category: normalizeCategory(apiBook.categoria),
    price: Number(apiBook.menorPreco ?? bestCopy.preco ?? 0),
    condition: formatEnum(bestCopy.estadoConservacao || "BOM"),
    year: String(apiBook.anoPublicacao || ""),
    publisher: apiBook.editora,
    pages: "Nao informado",
    stock: Number(apiBook.estoqueTotal ?? bestCopy.estoque ?? 0),
    seller: bestCopy.vendedor || apiBook.vendedora || "Sebo Digital",
    city: bestCopy.cidade || bestCopy.cidadeVendedor || "Brasil",
    type: formatEnum(bestCopy.tipo || "USADO"),
    freeShipping: Boolean(highlights.freteGratis || copies.some((copy) => copy.freteGratis)),
    promotion: Boolean(highlights.oferta || copies.some((copy) => copy.promocao)),
    corporatePurchase: copies.some((copy) => copy.compraCorporativa),
    rating: Number(bestCopy.avaliacaoVendedor || 4.7),
    language: apiBook.idioma || "Portugues",
    sales: highlights.maisVendido ? 100 : highlights.lancamento ? 70 : 25,
    cover: coverClassFor(apiBook.categoria, apiBook.id),
    imageUrl: apiBook.imagemUrl,
    short: apiBook.descricao || "Livro disponivel no catalogo do Sebo Digital.",
    description: apiBook.descricao || "Obra cadastrada no marketplace com ofertas novas e usadas.",
    notes: buildApiNotes(copies)
  };
}

function flattenCopies(copies) {
  if (!copies) return [];
  return [...(copies.novas || []), ...(copies.usadas || [])].filter((copy) => copy.ativo !== false);
}

function selectBestCopy(copies) {
  if (!copies.length) return {};
  return [...copies].sort((a, b) => Number(a.preco || 0) - Number(b.preco || 0))[0];
}

function normalizeCategory(category) {
  if (!category) return "Geral";
  if (category.includes("Literatura")) return "Literatura";
  if (category.includes("Ficcao") || category.includes("Romance")) return "Literatura";
  return category;
}

function formatEnum(value) {
  return String(value)
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

function coverClassFor(category, id) {
  const byCategory = {
    Literatura: "cover-burgundy",
    Romance: "cover-burgundy",
    Tecnologia: "cover-blue",
    Fantasia: "cover-sage",
    Memorias: "cover-teal",
    Arte: "cover-clay",
    Historia: "cover-gold"
  };
  const fallback = ["cover-burgundy", "cover-teal", "cover-blue", "cover-gold", "cover-sage", "cover-clay", "cover-ink"];
  return byCategory[category] || fallback[id % fallback.length];
}

function buildApiNotes(copies) {
  if (!copies.length) return ["Oferta cadastrada no backend", "Consulte disponibilidade antes da compra"];
  const freeShipping = copies.some((copy) => copy.freteGratis);
  const usedCopies = copies.filter((copy) => copy.tipo === "USADO").length;
  const newCopies = copies.filter((copy) => copy.tipo === "NOVO").length;
  return [
    `${newCopies} oferta(s) nova(s) e ${usedCopies} usada(s)`,
    freeShipping ? "Possui opcao com frete gratis" : "Frete calculado por oferta",
    "Dados carregados da API Spring Boot"
  ];
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
  renderBooks(document.querySelector("#best-seller-shelf"), [...bestSellers, ...bestSellers]);
  renderAuthors();
  renderBooks(document.querySelector("#classic-books"), books.filter((book) => book.category.includes("Literatura")).slice(0, 4));
  renderUniverses();
  renderBoxSets();
  setupShelfControls();
  setupAuthorCarousel();
}

function setupShelfControls() {
  const shelf = document.querySelector("#best-seller-shelf");
  const previousButton = document.querySelector("[data-shelf-prev]");
  const nextButton = document.querySelector("[data-shelf-next]");
  if (!shelf || !previousButton || !nextButton) return;

  let isPaused = false;
  let pauseTimeout;
  const autoStep = 1;
  const autoInterval = 36;

  const normalizeShelfPosition = () => {
    const loopPoint = shelf.scrollWidth / 2;

    if (loopPoint > shelf.clientWidth && shelf.scrollLeft >= loopPoint) {
      shelf.scrollLeft -= loopPoint;
    }
  };

  const pauseTemporarily = () => {
    isPaused = true;
    window.clearTimeout(pauseTimeout);
    pauseTimeout = window.setTimeout(() => {
      isPaused = false;
    }, 1600);
  };

  const scrollShelf = (direction) => {
    const firstCard = shelf.querySelector(".book-card");
    const distance = firstCard ? firstCard.getBoundingClientRect().width + 18 : 280;
    const loopPoint = shelf.scrollWidth / 2;
    pauseTemporarily();

    if (loopPoint > shelf.clientWidth && direction < 0 && shelf.scrollLeft <= 2) {
      shelf.scrollLeft = loopPoint;
    }

    shelf.scrollBy({ left: direction * distance, behavior: "smooth" });

    window.setTimeout(() => {
      normalizeShelfPosition();
    }, 420);
  };

  previousButton.addEventListener("click", () => scrollShelf(-1));
  nextButton.addEventListener("click", () => scrollShelf(1));

  window.setInterval(() => {
    if (isPaused || shelf.scrollWidth <= shelf.clientWidth) return;
    shelf.scrollLeft += autoStep;
    normalizeShelfPosition();
  }, autoInterval);
}

function setupAuthorCarousel() {
  const strip = document.querySelector("#author-strip");
  const previousButton = document.querySelector("[data-author-prev]");
  const nextButton = document.querySelector("[data-author-next]");
  if (!strip || !previousButton || !nextButton) return;

  let isPaused = false;
  let pauseTimeout;
  const autoStep = 1;
  const autoInterval = 34;

  const normalizeStripPosition = () => {
    const loopPoint = strip.scrollWidth / 2;

    if (loopPoint > strip.clientWidth && strip.scrollLeft >= loopPoint) {
      strip.scrollLeft -= loopPoint;
    }
  };

  const pauseTemporarily = () => {
    isPaused = true;
    window.clearTimeout(pauseTimeout);
    pauseTimeout = window.setTimeout(() => {
      isPaused = false;
    }, 1600);
  };

  const scrollAuthors = (direction) => {
    const firstAuthor = strip.querySelector(".author-chip");
    const distance = firstAuthor ? firstAuthor.getBoundingClientRect().width + 18 : 160;
    const loopPoint = strip.scrollWidth / 2;
    pauseTemporarily();

    if (loopPoint > strip.clientWidth && direction < 0 && strip.scrollLeft <= 2) {
      strip.scrollLeft = loopPoint;
    }

    strip.scrollBy({ left: direction * distance, behavior: "smooth" });

    window.setTimeout(() => {
      normalizeStripPosition();
    }, 420);
  };

  previousButton.addEventListener("click", () => scrollAuthors(-1));
  nextButton.addEventListener("click", () => scrollAuthors(1));

  window.setInterval(() => {
    if (isPaused || strip.scrollWidth <= strip.clientWidth) return;
    strip.scrollLeft += autoStep;
    normalizeStripPosition();
  }, autoInterval);
}

function renderAuthors() {
  const container = document.querySelector("#author-strip");
  if (!container) return;

  const authors = [...new Map(books.map((book) => [book.author, book])).values()];
  const loopedAuthors = authors.length > 1 ? [...authors, ...authors] : authors;

  container.innerHTML = loopedAuthors.map((book) => `
    <a class="author-chip" href="livros.html?autor=${encodeURIComponent(book.author)}">
      ${renderAuthorAvatar(book.author, book.authorImageUrl)}
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
      <span aria-hidden="true">
        <b>${getInitials(universe.name)}</b>
        ${universe.imageUrl ? `<img src="${escapeAttribute(universe.imageUrl)}" alt="" loading="lazy" onerror="this.remove()">` : ""}
      </span>
      <strong>${escapeHtml(universe.name)}</strong>
    </a>
  `).join("");
}

function renderBoxSets() {
  const container = document.querySelector("#box-sets");
  if (!container) return;

  container.innerHTML = boxSets.map((box) => `
    <a class="box-card" href="${box.link}">
      <span class="box-visual ${box.theme}${box.imageUrl ? " box-visual-photo" : ""}" aria-hidden="true">
        ${box.imageUrl ? `<img src="${escapeAttribute(box.imageUrl)}" alt="" loading="lazy" onerror="this.parentElement.classList.remove('box-visual-photo'); this.remove()">` : ""}
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
      const matchesCategory = !filters.category || book.category === filters.category || book.category.includes(filters.category);
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

  if (!selectedBook) {
    document.title = "Livro indisponivel | Sebo Digital";
    if (breadcrumb) breadcrumb.textContent = "Livro indisponivel";
    if (detail) {
      detail.innerHTML = `
        <div class="empty-state">
          <h2>Livro indisponivel</h2>
          <p>${catalogLoadError ? "Nao foi possivel carregar o catalogo pela API conectada ao PostgreSQL." : "Nenhum livro foi encontrado no banco PostgreSQL."}</p>
        </div>
      `;
    }
    renderBooks(document.querySelector("#related-books"), []);
    return;
  }

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
        <p class="detail-author">
          ${renderAuthorAvatar(selectedBook.author, selectedBook.authorImageUrl)}
          <span>por ${escapeHtml(selectedBook.author)}</span>
        </p>
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
        <p>Escolha alguns livros no catalogo para montar seu carrinho.</p>
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

  if (!detailedItems.length) {
    itemsElement.innerHTML = `
      <div class="empty-state">
        <h2>Itens indisponiveis</h2>
        <p>${catalogLoadError ? "Nao foi possivel consultar os livros pela API conectada ao PostgreSQL." : "Os itens salvos no carrinho nao existem mais no banco."}</p>
      </div>
    `;
    summaryElement.innerHTML = `
      <h2>Resumo</h2>
      <p class="summary-note">Nenhum item disponivel para compra.</p>
      <a class="primary-button" href="livros.html">Ver catalogo</a>
    `;
    return;
  }

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
    const message = catalogLoadError
      ? "Nao foi possivel carregar os livros pela API conectada ao PostgreSQL."
      : books.length
        ? "Tente ajustar a busca ou limpar os filtros."
        : "Nenhum livro foi encontrado no banco PostgreSQL.";

    container.innerHTML = `
      <div class="empty-state">
        <h3>Nenhum livro encontrado</h3>
        <p>${message}</p>
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
          <p class="book-author-line">
            ${renderAuthorAvatar(book.author, book.authorImageUrl)}
            <span>${escapeHtml(book.author)}</span>
          </p>
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
    <div class="book-cover ${book.cover}${book.imageUrl ? " book-cover-photo" : ""}" aria-hidden="true">
      ${book.imageUrl ? `<img src="${escapeAttribute(book.imageUrl)}" alt="" loading="lazy" onerror="this.parentElement.classList.remove('book-cover-photo'); this.remove()">` : ""}
      <span class="cover-category">${escapeHtml(book.category)}</span>
      <strong>${escapeHtml(book.title)}</strong>
      <small>${escapeHtml(book.author)}</small>
    </div>
  `;
}

function renderAuthorAvatar(author, imageUrl) {
  const image = imageUrl
    ? `<img src="${escapeAttribute(imageUrl)}" alt="" loading="lazy" onerror="this.remove()">`
    : "";

  return `<span class="author-avatar" aria-hidden="true"><span>${getInitials(author)}</span>${image}</span>`;
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
  return [...new Set(books.map((book) => book[key]).filter(Boolean))].sort((a, b) => a.localeCompare(b, "pt-BR"));
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

function escapeAttribute(value) {
  return escapeHtml(value);
}

function setupThemeToggle() {
  const menus = document.querySelectorAll("[data-theme-menu]");
  if (!menus.length) return;

  const updateActive = (theme) => {
    document.querySelectorAll("[data-set-theme]").forEach(btn => {
      btn.classList.toggle("is-active", btn.dataset.setTheme === theme);
    });
  };

  const initialTheme = document.documentElement.getAttribute("data-theme") || "light";
  updateActive(initialTheme);

  document.addEventListener("click", (event) => {
    const btn = event.target.closest("[data-set-theme]");
    if (!btn) return;
    const newTheme = btn.dataset.setTheme;
    document.documentElement.setAttribute("data-theme", newTheme);
    localStorage.setItem("seboDigitalTheme", newTheme);
    updateActive(newTheme);
  });
}
