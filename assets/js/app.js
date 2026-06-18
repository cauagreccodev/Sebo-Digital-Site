let books = [];
let catalogLoadError = null;

const universeThemes = ["universe-wine", "universe-sage", "universe-gold", "universe-teal", "universe-blue", "universe-clay"];

const formatter = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL"
});

const cartKey = "seboDigitalCart";
const authTokenKey = "seboDigitalAuth";
const authMessageKey = "seboDigitalAuthMessage";
const orderStatuses = [
  "PEDIDO_REALIZADO",
  "PAGAMENTO_APROVADO",
  "EM_SEPARACAO",
  "ENVIADO",
  "EM_TRANSPORTE",
  "ENTREGUE"
];
const orderStatusLabels = {
  PEDIDO_REALIZADO: "Pedido realizado",
  PAGAMENTO_APROVADO: "Pagamento aprovado",
  EM_SEPARACAO: "Em separacao",
  ENVIADO: "Enviado",
  EM_TRANSPORTE: "Em transporte",
  ENTREGUE: "Entregue"
};
let apiBaseUrl = normalizeApiBaseUrl(window.SEBO_API_URL || "http://localhost:8080");
const page = document.body.dataset.page;

document.addEventListener("DOMContentLoaded", async () => {
  setupThemeToggle();
  setupNavigation();
  setupSearchForms();
  setupCartEvents();
  await loadRuntimeConfig();
  await setupAuthPage();
  showPendingAuthMessage();
  updateCartBadge();
  if (["home", "catalog", "detail", "cart"].includes(page)) {
    await loadBooksFromApi();
  }

  if (page === "home") renderHome();
  if (page === "catalog") renderCatalog();
  if (page === "detail") renderDetail();
  if (page === "cart") renderCart();
  if (page === "purchases") await renderPurchases();
  if (page === "account") await renderAccount();
});

async function loadRuntimeConfig() {
  if (window.SEBO_API_URL || window.location.protocol === "file:") return;

  try {
    const response = await fetch("/api/config", {
      cache: "no-store",
      headers: { Accept: "application/json" }
    });
    if (!response.ok) return;

    const config = await response.json();
    if (config.apiBaseUrl) {
      apiBaseUrl = normalizeApiBaseUrl(config.apiBaseUrl);
    }
    if (config.oauthRedirectUrl) {
      window.SEBO_OAUTH_REDIRECT_URL = config.oauthRedirectUrl;
    }
  } catch (error) {
    console.warn("Configuracao de deploy indisponivel; usando API local.", error);
  }
}

function normalizeApiBaseUrl(value) {
  return String(value || "").replace(/\/+$/, "") || "http://localhost:8080";
}

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
    toggle.setAttribute("aria-label", isOpen ? "Fechar menu" : "Abrir menu");
    if (!isOpen && catalogMenu) setCatalogState(false);
  });

  setupAccountMenu(accountMenu, accountTrigger);

  if (!catalogMenu || !catalogTrigger) return;

  const isMobileNav = () => window.matchMedia("(max-width: 980px)").matches;
  const setCatalogState = (isOpen) => {
    catalogMenu.classList.toggle("is-catalog-open", isOpen);
    catalogTrigger.setAttribute("aria-expanded", String(isOpen));
  };
  const closeNavigation = () => {
    nav.classList.remove("is-open");
    document.body.classList.remove("nav-open");
    toggle.setAttribute("aria-expanded", "false");
    toggle.setAttribute("aria-label", "Abrir menu");
    setCatalogState(false);
  };

  nav.addEventListener("click", (event) => {
    const link = event.target.closest("a");
    if (link && link !== catalogTrigger) closeNavigation();
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && nav.classList.contains("is-open")) {
      closeNavigation();
      toggle.focus();
    }
  });

  window.matchMedia("(min-width: 981px)").addEventListener("change", (event) => {
    if (event.matches) closeNavigation();
  });

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
    const isLoggedIn = accountTrigger.dataset.authenticated === "true";
    if (!isLoggedIn && !isCompactHeader()) return;
    event.preventDefault();
    setAccountState(!accountMenu.classList.contains("is-account-open"));
  });

  accountMenu.addEventListener("click", (event) => {
    const logoutButton = event.target.closest("[data-logout]");
    if (!logoutButton) return;
    logout();
  });
}

async function setupAuthPage() {
  handleOAuthRedirect();

  const auth = getStoredAuth();
  if (page === "login" && auth) {
    window.location.replace(getPostLoginDestination("compras.html"));
    return;
  }

  await setupSocialLoginButtons();

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
  const demoLoginButton = document.querySelector("[data-demo-login]");

  if (demoLoginButton) {
    demoLoginButton.addEventListener("click", async () => {
      demoLoginButton.disabled = true;
      demoLoginButton.textContent = "Entrando...";
      try {
        const auth = await apiRequest("/api/auth/login", {
          method: "POST",
          body: JSON.stringify({
            email: "guest@exemplo.com",
            senha: "guest123"
          })
        });
        await ensureDemoCart();
        completeAuthentication(auth, "Conta demonstrativa carregada.", "conta.html");
      } catch (error) {
        showToast(error.message || "Nao foi possivel acessar a conta demo.");
        demoLoginButton.disabled = false;
        demoLoginButton.textContent = "Entrar com a conta demo";
      }
    });
  }

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

async function ensureDemoCart() {
  if (getCart().length) return;

  try {
    const apiBooks = await apiRequest("/api/livros");
    const availableBooks = (Array.isArray(apiBooks) ? apiBooks : [])
      .map(mapApiBook)
      .filter(canAddToCart)
      .slice(0, 2);

    if (availableBooks.length) {
      saveCart(availableBooks.map((book) => ({ id: book.id, quantity: 1 })));
    }
  } catch (error) {
    console.warn("Nao foi possivel preparar o carrinho demonstrativo.", error);
  }
}

function logout() {
  localStorage.removeItem(authTokenKey);
  sessionStorage.removeItem(authMessageKey);
  showToast("Voce saiu da sua conta.");
  window.setTimeout(() => window.location.assign("index.html"), 500);
}

async function setupSocialLoginButtons() {
  const buttons = [...document.querySelectorAll("[data-oauth-provider]")];
  if (!buttons.length) return;

  buttons.forEach((button) => {
    button.disabled = true;
    button.setAttribute("aria-busy", "true");
  });

  let providers;
  try {
    providers = await apiRequest("/api/auth/oauth2/providers");
  } catch (error) {
    buttons.forEach((button) => {
      button.title = "Login social indisponivel no momento";
      button.removeAttribute("aria-busy");
    });
    return;
  }

  buttons.forEach((button) => {
    const provider = button.dataset.oauthProvider;
    const available = providers?.[provider] === true;
    button.disabled = !available;
    button.removeAttribute("aria-busy");
    button.title = available
      ? ""
      : `Login com ${provider === "google" ? "Google" : "Facebook"} ainda nao configurado`;

    button.addEventListener("click", async () => {
      const redirectUri = getOAuthRedirectUri();
      const loginUrl = new URL(`${apiBaseUrl}/api/auth/oauth2/${provider}`);
      loginUrl.searchParams.set("redirect_uri", redirectUri);

      button.disabled = true;
      window.location.assign(loginUrl.toString());
    });
  });
}

function handleOAuthRedirect() {
  const params = oauthRedirectParams();
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

function oauthRedirectParams() {
  const searchParams = new URLSearchParams(window.location.search);
  if (searchParams.has("oauth")) return searchParams;

  const fragment = window.location.hash.startsWith("#")
    ? window.location.hash.slice(1)
    : window.location.hash;
  return new URLSearchParams(fragment);
}

function completeAuthentication(auth, message, fallback = "index.html") {
  const normalizedAuth = normalizeAuth(auth);
  localStorage.setItem(authTokenKey, JSON.stringify(normalizedAuth));
  sessionStorage.setItem(authMessageKey, message);
  window.location.assign(getPostLoginDestination(fallback));
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
  accountTrigger.href = "conta.html";
  accountTrigger.dataset.authenticated = "true";

  const dropdown = accountMenu.querySelector(".account-dropdown");
  if (!dropdown) return;

  dropdown.innerHTML = `
    <a href="conta.html">Minha conta</a>
    <a href="compras.html">Minhas compras</a>
    <a href="carrinho.html">Meu carrinho</a>
    <a href="livros.html">Continuar comprando</a>
    <button type="button" data-logout>Sair</button>
  `;
}

function getPostLoginDestination(fallback = "index.html") {
  const next = new URLSearchParams(window.location.search).get("next");
  const destinations = {
    conta: "conta.html",
    compras: "compras.html",
    checkout: "carrinho.html"
  };
  return destinations[next] || fallback;
}

function getStoredAuth() {
  try {
    const auth = JSON.parse(localStorage.getItem(authTokenKey));
    if (!auth?.token || !auth.usuario) return null;

    const expiration = Date.parse(auth.expiraEm);
    if (Number.isFinite(expiration) && expiration <= Date.now()) {
      localStorage.removeItem(authTokenKey);
      return null;
    }

    return auth;
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
  const next = url.searchParams.get("next");
  url.search = "";
  url.hash = "";
  if (next === "compras" || next === "checkout") {
    url.searchParams.set("next", next);
  }
  return url.toString();
}

function cleanOAuthUrl() {
  const url = new URL(window.location.href);
  [
    "oauth",
    "token",
    "tipo",
    "expiraEm",
    "usuarioId",
    "nome",
    "email",
    "role",
    "authProvider",
    "fotoUrl",
    "mensagem"
  ].forEach((parameter) => url.searchParams.delete(parameter));
  url.hash = "";
  window.history.replaceState({}, document.title, `${url.pathname}${url.search}`);
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
    console.warn("Catalogo indisponivel.", error);
  }
}

async function apiRequest(path, options = {}) {
  const auth = getStoredAuth();
  const headers = {
    "Content-Type": "application/json",
    ...(auth?.token ? { Authorization: `Bearer ${auth.token}` } : {}),
    ...(options.headers || {})
  };

  let response;
  try {
    response = await fetch(`${apiBaseUrl}${path}`, {
      ...options,
      headers
    });
  } catch (error) {
    throw new Error(apiUnavailableMessage());
  }

  if (!response.ok) {
    let message = "Nao foi possivel concluir a operacao.";
    try {
      const errorBody = await response.json();
      message = errorBody.erro || message;
    } catch (error) {
      message = response.statusText || message;
    }
    if (response.status === 401 && !path.startsWith("/api/auth/")) {
      localStorage.removeItem(authTokenKey);
      message = "Sua sessao expirou. Entre novamente para continuar.";
    }
    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
}

function apiUnavailableMessage() {
  return "Nao foi possivel acessar o catalogo no momento.";
}

function numberOrNull(value) {
  if (value === undefined || value === null || value === "") return null;
  const number = Number(value);
  return Number.isFinite(number) ? number : null;
}

function highlightScore(highlights) {
  return [
    highlights.maisVendido,
    highlights.oferta,
    highlights.lancamento,
    highlights.freteGratis
  ].filter(Boolean).length;
}

function mapApiBook(apiBook) {
  const copies = flattenCopies(apiBook.copias);
  const bestCopy = selectBestCopy(copies);
  const highlights = apiBook.destaques || {};
  const price = numberOrNull(bestCopy.preco ?? apiBook.menorPreco);
  const rating = numberOrNull(bestCopy.avaliacaoVendedor);

  return {
    id: apiBook.id,
    copyId: bestCopy.id ?? null,
    title: apiBook.titulo || "",
    author: apiBook.autor || "",
    authorImageUrl: apiBook.autorImagemUrl,
    category: normalizeCategory(apiBook.categoria),
    price,
    condition: bestCopy.estadoConservacao ? formatEnum(bestCopy.estadoConservacao) : "",
    year: String(apiBook.anoPublicacao || ""),
    publisher: apiBook.editora || "",
    pages: "",
    stock: Number(bestCopy.estoque ?? 0),
    seller: bestCopy.vendedor || apiBook.vendedora || "",
    city: bestCopy.cidade || bestCopy.cidadeVendedor || "",
    type: bestCopy.tipo ? formatEnum(bestCopy.tipo) : "",
    freeShipping: Boolean(bestCopy.freteGratis),
    promotion: Boolean(highlights.oferta || bestCopy.promocao),
    corporatePurchase: Boolean(bestCopy.compraCorporativa),
    rating,
    language: apiBook.idioma || "",
    bestSeller: Boolean(highlights.maisVendido),
    newRelease: Boolean(highlights.lancamento),
    highlightScore: highlightScore(highlights),
    cover: coverClassFor(apiBook.categoria, apiBook.id),
    imageUrl: apiBook.imagemUrl,
    short: apiBook.descricao || "",
    description: apiBook.descricao || "",
    notes: buildApiNotes(copies)
  };
}

function flattenCopies(copies) {
  if (!copies) return [];
  return [...(copies.novas || []), ...(copies.usadas || [])].filter((copy) => copy.ativo !== false);
}

function selectBestCopy(copies) {
  if (!copies.length) return {};
  return [...copies].sort((a, b) =>
    Number(Number(b.estoque || 0) > 0) - Number(Number(a.estoque || 0) > 0)
    || Number(a.preco || 0) - Number(b.preco || 0))[0];
}

function normalizeCategory(category) {
  return category || "Geral";
}

function formatEnum(value) {
  return String(value)
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

function coverClassFor(category, id) {
  const fallback = ["cover-burgundy", "cover-teal", "cover-blue", "cover-gold", "cover-sage", "cover-clay", "cover-ink"];
  const key = String(category || id || "catalogo");
  return fallback[Math.abs(hashString(key)) % fallback.length];
}

function hashString(value) {
  return [...value].reduce((hash, char) => ((hash << 5) - hash + char.charCodeAt(0)) | 0, 0);
}

function buildApiNotes(copies) {
  if (!copies.length) return [];
  const freeShipping = copies.some((copy) => copy.freteGratis);
  const usedCopies = copies.filter((copy) => copy.tipo === "USADO").length;
  const newCopies = copies.filter((copy) => copy.tipo === "NOVO").length;
  const notes = [`${newCopies} oferta(s) nova(s) e ${usedCopies} usada(s)`];

  if (freeShipping) {
    notes.push("Possui opcao com frete gratis");
  }

  if (copies.some((copy) => copy.compraCorporativa)) {
    notes.push("Possui compra corporativa");
  }

  return notes;
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
  const bestSellers = sortBooks(books.filter((book) => book.bestSeller), "mais-vendidos");
  const shelfBooks = shouldAutoScrollCarousels() && bestSellers.length > 1
    ? [...bestSellers, ...bestSellers]
    : bestSellers;

  renderBooks(document.querySelector("#best-seller-shelf"), shelfBooks);
  renderAuthors();
  renderBooks(document.querySelector("#classic-books"), books.slice(0, 4));
  renderUniverses();
  renderOfferCards();
  setupShelfControls();
  setupAuthorCarousel();
}

function shouldAutoScrollCarousels() {
  return window.matchMedia("(hover: hover) and (pointer: fine) and (min-width: 701px)").matches
    && !window.matchMedia("(prefers-reduced-motion: reduce)").matches;
}

function setupShelfControls() {
  const shelf = document.querySelector("#best-seller-shelf");
  const previousButton = document.querySelector("[data-shelf-prev]");
  const nextButton = document.querySelector("[data-shelf-next]");
  if (!shelf || !previousButton || !nextButton) return;
  if (!shouldAutoScrollCarousels()) return;

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
  if (!shouldAutoScrollCarousels()) return;

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

  const authors = [...new Map(books.filter((book) => book.author).map((book) => [book.author, book])).values()];
  const loopedAuthors = shouldAutoScrollCarousels() && authors.length > 1
    ? [...authors, ...authors]
    : authors;

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

  const universes = buildUniverseCards();

  if (!universes.length) {
    renderEmptyState(container, "Nenhuma categoria encontrada", "As categorias aparecem conforme os livros cadastrados.");
    return;
  }

  container.innerHTML = universes.map((universe) => `
    <a class="universe-card ${universe.theme}" href="${universe.link}">
      <span aria-hidden="true">
        <b>${getInitials(universe.name)}</b>
        ${universe.imageUrl ? `<img src="${escapeAttribute(universe.imageUrl)}" alt="" loading="lazy" onerror="this.remove()">` : ""}
      </span>
      <strong>${escapeHtml(universe.name)}</strong>
    </a>
  `).join("");
}

function buildUniverseCards() {
  const byCategory = new Map();

  books.forEach((book) => {
    if (!book.category) return;
    const current = byCategory.get(book.category) || {
      name: book.category,
      count: 0,
      imageUrl: "",
      link: catalogFilterUrl({ categoria: book.category })
    };

    current.count += 1;
    if (!current.imageUrl && book.imageUrl) {
      current.imageUrl = book.imageUrl;
    }

    byCategory.set(book.category, current);
  });

  return [...byCategory.values()]
    .sort((a, b) => b.count - a.count || a.name.localeCompare(b.name, "pt-BR"))
    .slice(0, 8)
    .map((universe, index) => ({
      ...universe,
      theme: universeThemes[index % universeThemes.length]
    }));
}

function renderOfferCards() {
  const container = document.querySelector("#offer-cards");
  if (!container) return;

  const offers = books
    .filter((book) => book.promotion || book.freeShipping || book.bestSeller || book.newRelease)
    .sort((a, b) => b.highlightScore - a.highlightScore || (a.price ?? Number.MAX_VALUE) - (b.price ?? Number.MAX_VALUE))
    .slice(0, 6);

  if (!offers.length) {
    renderEmptyState(container, "Nenhuma oferta cadastrada", "As ofertas aparecem conforme os livros cadastrados.");
    return;
  }

  container.innerHTML = offers.map((book) => `
    <a class="offer-card" href="detalhes.html?id=${book.id}">
      ${renderCover(book)}
      <strong>${escapeHtml(book.title)}</strong>
      <small>${escapeHtml([book.author, book.category].filter(Boolean).join(" - "))}</small>
      <b>${formatBookPrice(book)}</b>
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
      const matchesMinPrice = !filters.minPrice || (book.price !== null && book.price >= filters.minPrice);
      const matchesMaxPrice = !filters.maxPrice || (book.price !== null && book.price <= filters.maxPrice);
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
          <p>${catalogLoadError ? "Nao foi possivel carregar o catalogo." : "Nenhum livro foi encontrado no catalogo."}</p>
        </div>
      `;
    }
    renderBooks(document.querySelector("#related-books"), []);
    return;
  }

  document.title = `${selectedBook.title} | Sebo Digital`;
  breadcrumb.textContent = selectedBook.title;
  const addButtonAttributes = canAddToCart(selectedBook) ? `data-add-cart="${selectedBook.id}"` : "disabled";

  detail.innerHTML = `
    <div class="detail-cover-wrap">
      ${renderCover(selectedBook)}
    </div>
    <div class="detail-panel">
      <article class="detail-main">
        ${selectedBook.condition ? `<span class="condition-badge">${escapeHtml(selectedBook.condition)}</span>` : ""}
        <h1>${escapeHtml(selectedBook.title)}</h1>
        <p class="detail-author">
          ${renderAuthorAvatar(selectedBook.author, selectedBook.authorImageUrl)}
          <span>por ${escapeHtml(displayText(selectedBook.author))}</span>
        </p>
        <p class="detail-description">${escapeHtml(selectedBook.description || "Sem descricao cadastrada.")}</p>
        <div class="detail-buy">
          <div>
            <span class="price">${formatBookPrice(selectedBook)}</span>
            <p class="summary-note">Estoque: ${selectedBook.stock} ${selectedBook.stock === 1 ? "exemplar" : "exemplares"}</p>
          </div>
          <button class="primary-button" type="button" ${addButtonAttributes}>${canAddToCart(selectedBook) ? "Adicionar ao carrinho" : "Indisponivel"}</button>
        </div>
      </article>
      <div class="info-grid">
        ${renderInfo("Categoria", selectedBook.category)}
        ${renderInfo("Tipo", selectedBook.type)}
        ${renderInfo("Editora", selectedBook.publisher)}
        ${renderInfo("Ano", selectedBook.year)}
        ${renderInfo("Idioma", selectedBook.language)}
        ${renderInfo("Avaliacao", formatRating(selectedBook.rating))}
        ${renderInfo("Frete", selectedBook.freeShipping ? "Gratis" : "Calculado")}
        ${renderInfo("Paginas", `${selectedBook.pages}`)}
        ${renderInfo("Vendedor", selectedBook.seller)}
        ${renderInfo("Localizacao", selectedBook.city)}
      </div>
      <article class="detail-main">
        <h2>Conservacao</h2>
        <ul class="detail-list">
          ${(selectedBook.notes.length ? selectedBook.notes : ["Nenhuma observacao cadastrada."]).map((note) => `<li>${escapeHtml(note)}</li>`).join("")}
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
  const cart = normalizeCartAgainstCatalog(getCart());
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
    .filter((item) => item.book && item.book.copyId && item.book.stock > 0);

  if (!detailedItems.length) {
    itemsElement.innerHTML = `
      <div class="empty-state">
        <h2>Itens indisponiveis</h2>
        <p>${catalogLoadError ? "Nao foi possivel consultar os livros." : "Os itens salvos no carrinho nao estao mais disponiveis."}</p>
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

  const subtotal = detailedItems.reduce((sum, item) => sum + (item.book.price ?? 0) * item.quantity, 0);
  const shipping = detailedItems.every((item) => item.book.freeShipping) ? 0 : 14.9;
  const total = subtotal + shipping;
  const auth = getStoredAuth();

  summaryElement.innerHTML = `
    <h2>Resumo da compra</h2>
    <div class="summary-line">
      <span>Subtotal</span>
      <strong>${formatter.format(subtotal)}</strong>
    </div>
    <div class="summary-line">
      <span>Frete</span>
      <strong>${shipping === 0 ? "Gratis" : formatter.format(shipping)}</strong>
    </div>
    <div class="summary-total">
      <span>Total</span>
      <strong>${formatter.format(total)}</strong>
    </div>
    ${auth ? renderCheckoutForm(auth.usuario) : `
      <p class="summary-note">Entre na sua conta para informar a entrega e finalizar o pedido.</p>
      <a class="primary-button" href="login.html?next=checkout">Entrar para continuar</a>
    `}
  `;

  const checkoutForm = summaryElement.querySelector("[data-checkout-form]");
  if (checkoutForm) {
    checkoutForm.addEventListener("submit", (event) => submitCheckout(event, detailedItems));
  }
}

function normalizeCartAgainstCatalog(cart) {
  if (catalogLoadError || !books.length) return cart;

  const normalized = cart
    .map((item) => {
      const book = books.find((entry) => entry.id === item.id);
      if (!book || !book.copyId || book.stock <= 0) return null;
      return {
        id: item.id,
        quantity: Math.max(1, Math.min(Number(item.quantity) || 1, book.stock))
      };
    })
    .filter(Boolean);

  if (JSON.stringify(normalized) !== JSON.stringify(cart)) {
    saveCart(normalized);
  }
  return normalized;
}

function renderCheckoutForm(usuario = {}) {
  const endereco = [usuario.enderecoPrincipal, usuario.complemento].filter(Boolean).join(" - ");
  return `
    <form class="checkout-form" data-checkout-form>
      <h3>Entrega e pagamento</h3>
      <div class="filter-group">
        <label for="checkout-address">Endereco e numero</label>
        <input id="checkout-address" name="endereco" type="text" maxlength="240" autocomplete="street-address" value="${escapeAttribute(endereco)}" required>
      </div>
      <div class="checkout-location-grid">
        <div class="filter-group">
          <label for="checkout-city">Cidade</label>
          <input id="checkout-city" name="cidade" type="text" maxlength="120" autocomplete="address-level2" value="${escapeAttribute(usuario.cidade || "")}" required>
        </div>
        <div class="filter-group">
          <label for="checkout-state">UF</label>
          <input id="checkout-state" name="estado" type="text" maxlength="2" pattern="[A-Za-z]{2}" autocomplete="address-level1" value="${escapeAttribute(usuario.estado || "")}" required>
        </div>
      </div>
      <div class="filter-group">
        <label for="checkout-cep">CEP</label>
        <input id="checkout-cep" name="cep" type="text" inputmode="numeric" pattern="\\d{5}-?\\d{3}" placeholder="00000-000" autocomplete="postal-code" value="${escapeAttribute(usuario.cep || "")}" required>
      </div>
      <div class="filter-group">
        <label for="checkout-payment">Forma de pagamento</label>
        <select id="checkout-payment" name="pagamento" required>
          <option value="PIX">PIX</option>
          <option value="Cartao de credito">Cartao de credito</option>
          <option value="Boleto">Boleto</option>
        </select>
      </div>
      <p class="checkout-disclaimer">Ambiente demonstrativo: nenhuma cobranca real sera realizada.</p>
      <button class="primary-button" type="submit">Finalizar pedido</button>
    </form>
  `;
}

async function submitCheckout(event, detailedItems) {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button[type='submit']");
  const formData = new FormData(form);

  button.disabled = true;
  button.textContent = "Finalizando...";

  try {
    const order = await apiRequest("/api/pedidos", {
      method: "POST",
      body: JSON.stringify({
        itens: detailedItems.map((item) => ({
          livroCopiaId: item.book.copyId,
          quantidade: item.quantity
        })),
        enderecoEntrega: formData.get("endereco"),
        cidadeEntrega: formData.get("cidade"),
        estadoEntrega: formData.get("estado"),
        cepEntrega: formData.get("cep"),
        formaPagamento: formData.get("pagamento")
      })
    });

    saveCart([]);
    sessionStorage.setItem(authMessageKey, `Pedido ${order.codigo} realizado com sucesso.`);
    window.location.assign(`compras.html?pedido=${order.id}&novo=true`);
  } catch (error) {
    showToast(error.message || "Nao foi possivel finalizar o pedido.");
    button.disabled = false;
    button.textContent = "Finalizar pedido";
  }
}

async function renderAccount() {
  const container = document.querySelector("#account-content");
  if (!container) return;

  const auth = getStoredAuth();
  if (!auth) {
    container.innerHTML = `
      <div class="empty-state account-login">
        <h2>Entre para acessar sua conta</h2>
        <p>Consulte seu endereco principal, compras e entregas em andamento.</p>
        <a class="primary-button" href="login.html?next=conta">Entrar ou criar conta</a>
      </div>
    `;
    return;
  }

  try {
    const [usuario, orders] = await Promise.all([
      apiRequest("/api/auth/me"),
      apiRequest("/api/pedidos")
    ]);
    const updatedAuth = { ...auth, usuario };
    localStorage.setItem(authTokenKey, JSON.stringify(updatedAuth));

    const activeOrders = orders.filter((order) => order.status !== "ENTREGUE");
    const completedOrders = orders.filter((order) => order.status === "ENTREGUE");
    const trackingOrder = activeOrders[0];
    const fullAddress = [
      usuario.enderecoPrincipal,
      usuario.complemento,
      usuario.bairro,
      [usuario.cidade, usuario.estado].filter(Boolean).join(" - "),
      usuario.cep
    ].filter(Boolean);

    container.innerHTML = `
      <section class="account-profile-card">
        <div class="account-profile-main">
          <span class="account-profile-avatar" aria-hidden="true">${escapeHtml(getInitials(usuario.nome))}</span>
          <div>
            <p class="eyebrow">Sua conta</p>
            <h2>${escapeHtml(usuario.nome)}</h2>
            <p>${escapeHtml(usuario.email)}</p>
          </div>
        </div>
        <div class="account-stats">
          <div><strong>${orders.length}</strong><span>compras</span></div>
          <div><strong>${activeOrders.length}</strong><span>em andamento</span></div>
          <div><strong>${completedOrders.length}</strong><span>entregues</span></div>
        </div>
      </section>

      <div class="account-grid">
        <section class="account-panel">
          <div class="account-panel-heading">
            <div>
              <p class="eyebrow">Entrega</p>
              <h2>Endereco principal</h2>
            </div>
            <span class="account-verified">Cadastrado</span>
          </div>
          ${fullAddress.length ? `
            <address>${fullAddress.map((line) => `<span>${escapeHtml(line)}</span>`).join("")}</address>
            <p class="account-contact">Telefone: ${escapeHtml(usuario.telefone || "Nao informado")}</p>
          ` : `
            <p class="summary-note">Nenhum endereco principal cadastrado.</p>
          `}
        </section>

        <section class="account-panel">
          <p class="eyebrow">Acesso</p>
          <h2>Dados da conta</h2>
          <dl class="account-data-list">
            <div><dt>Nome</dt><dd>${escapeHtml(usuario.nome)}</dd></div>
            <div><dt>E-mail</dt><dd>${escapeHtml(usuario.email)}</dd></div>
            <div><dt>Forma de acesso</dt><dd>${escapeHtml(authProviderLabel(usuario.authProvider))}</dd></div>
          </dl>
        </section>
      </div>

      <section class="account-shortcuts" aria-label="Atalhos da conta">
        <a href="compras.html">
          <span>Compras</span>
          <strong>Ver historico de pedidos</strong>
        </a>
        <a href="${trackingOrder ? `compras.html?pedido=${trackingOrder.id}` : "compras.html"}">
          <span>Rastreamento</span>
          <strong>${trackingOrder ? orderStatusLabels[trackingOrder.status] : "Nenhuma entrega em andamento"}</strong>
        </a>
        <a href="carrinho.html">
          <span>Carrinho</span>
          <strong>Continuar sua compra</strong>
        </a>
      </section>

      <button class="danger-button account-logout" type="button" data-account-logout>Sair da conta</button>
    `;

    container.querySelector("[data-account-logout]")?.addEventListener("click", logout);
  } catch (error) {
    container.innerHTML = `
      <div class="empty-state">
        <h2>Nao foi possivel carregar sua conta</h2>
        <p>${escapeHtml(error.message)}</p>
        <a class="primary-button" href="login.html?next=conta">Entrar novamente</a>
      </div>
    `;
  }
}

function authProviderLabel(provider) {
  const normalizedProvider = String(provider || "LOCAL").toUpperCase();
  if (normalizedProvider === "GOOGLE") return "Google";
  if (normalizedProvider === "FACEBOOK") return "Facebook";
  return "E-mail e senha";
}

async function renderPurchases() {
  const container = document.querySelector("#purchases-content");
  if (!container) return;

  const auth = getStoredAuth();
  if (!auth) {
    container.innerHTML = `
      <div class="empty-state purchases-login">
        <h2>Entre para ver suas compras</h2>
        <p>Seu historico e o rastreamento ficam vinculados a sua conta.</p>
        <a class="primary-button" href="login.html?next=compras">Entrar ou criar conta</a>
      </div>
    `;
    return;
  }

  try {
    const orders = await apiRequest("/api/pedidos");
    if (!orders.length) {
      container.innerHTML = `
        <div class="empty-state purchases-empty">
          <h2>Voce ainda nao fez nenhuma compra</h2>
          <p>Quando finalizar um pedido, o acompanhamento aparecera aqui.</p>
          <a class="primary-button" href="livros.html">Explorar livros</a>
        </div>
      `;
      return;
    }

    const selectedId = Number(new URLSearchParams(window.location.search).get("pedido"));
    const selectedOrder = orders.find((order) => order.id === selectedId);
    const newOrder = new URLSearchParams(window.location.search).get("novo") === "true";

    container.innerHTML = `
      ${selectedOrder ? renderOrderDetail(selectedOrder, newOrder) : ""}
      <div class="purchases-heading">
        <div>
          <p class="eyebrow">Historico</p>
          <h2>${selectedOrder ? "Outras compras" : "Seus pedidos"}</h2>
        </div>
        <a class="text-link" href="livros.html">Comprar mais livros</a>
      </div>
      <div class="order-list">
        ${orders.map((order) => renderOrderCard(order, selectedOrder?.id === order.id)).join("")}
      </div>
    `;
  } catch (error) {
    const sessionExpired = !getStoredAuth();
    container.innerHTML = `
      <div class="empty-state">
        <h2>${sessionExpired ? "Sua sessao expirou" : "Nao foi possivel carregar suas compras"}</h2>
        <p>${escapeHtml(error.message)}</p>
        <a class="primary-button" href="${sessionExpired ? "login.html?next=compras" : "compras.html"}">
          ${sessionExpired ? "Entrar novamente" : "Tentar de novo"}
        </a>
      </div>
    `;
  }
}

function renderOrderDetail(order, isNewOrder) {
  return `
    <article class="order-detail">
      ${isNewOrder ? `
        <div class="order-success">
          <strong>Compra concluida</strong>
          <span>O pedido foi salvo e ja aparece no seu historico.</span>
        </div>
      ` : ""}
      <div class="order-detail-header">
        <div>
          <p class="eyebrow">Pedido ${escapeHtml(order.codigo)}</p>
          <h2>${escapeHtml(orderStatusLabels[order.status] || formatEnum(order.status))}</h2>
          <p>Realizado em ${formatOrderDate(order.criadoEm)}</p>
        </div>
        <span class="order-status ${statusClassName(order.status)}">${escapeHtml(orderStatusLabels[order.status] || order.status)}</span>
      </div>
      ${renderTracking(order)}
      <div class="order-detail-grid">
        <section class="order-info-panel">
          <h3>Entrega</h3>
          <p>${escapeHtml(order.enderecoEntrega)}</p>
          <p>${escapeHtml(order.cidadeEntrega)} - ${escapeHtml(order.estadoEntrega)}, ${escapeHtml(order.cepEntrega)}</p>
          <dl>
            <div><dt>Codigo de rastreio</dt><dd>${escapeHtml(order.codigoRastreio)}</dd></div>
            <div><dt>Previsao</dt><dd>${formatOrderDay(order.previsaoEntrega)}</dd></div>
          </dl>
        </section>
        <section class="order-info-panel">
          <h3>Pagamento</h3>
          <p>${escapeHtml(order.formaPagamento)}</p>
          <dl>
            <div><dt>Produtos</dt><dd>${formatter.format(Number(order.subtotal))}</dd></div>
            <div><dt>Frete</dt><dd>${Number(order.frete) === 0 ? "Gratis" : formatter.format(Number(order.frete))}</dd></div>
            <div class="order-total-line"><dt>Total</dt><dd>${formatter.format(Number(order.total))}</dd></div>
          </dl>
        </section>
      </div>
      <div class="order-products">
        <h3>Livros deste pedido</h3>
        ${order.itens.map(renderOrderItem).join("")}
      </div>
    </article>
  `;
}

function renderTracking(order) {
  const currentIndex = Math.max(0, orderStatuses.indexOf(order.status));
  return `
    <section class="tracking-panel" aria-label="Rastreamento do pedido">
      <div class="tracking-heading">
        <div>
          <span>Rastreamento</span>
          <strong>${escapeHtml(orderStatusLabels[order.status] || formatEnum(order.status))}</strong>
        </div>
        <small>Atualizado em ${formatOrderDate(order.atualizadoEm)}</small>
      </div>
      <ol class="tracking-steps">
        ${orderStatuses.map((status, index) => {
          const state = index < currentIndex ? "is-complete" : index === currentIndex ? "is-current" : "";
          return `
            <li class="${state}">
              <span class="tracking-dot" aria-hidden="true"></span>
              <strong>${escapeHtml(orderStatusLabels[status])}</strong>
            </li>
          `;
        }).join("")}
      </ol>
    </section>
  `;
}

function renderOrderCard(order, isSelected) {
  const itemCount = order.itens.reduce((sum, item) => sum + item.quantidade, 0);
  const firstItems = order.itens.slice(0, 3);
  return `
    <article class="order-card${isSelected ? " is-selected" : ""}">
      <div class="order-card-top">
        <div>
          <span>Pedido ${escapeHtml(order.codigo)}</span>
          <strong>${formatOrderDate(order.criadoEm)}</strong>
        </div>
        <span class="order-status ${statusClassName(order.status)}">${escapeHtml(orderStatusLabels[order.status] || order.status)}</span>
      </div>
      <div class="order-card-body">
        <div class="order-card-items">
          ${firstItems.map((item) => `
            <span class="order-mini-cover">
              ${item.imagemUrl ? `<img src="${escapeAttribute(item.imagemUrl)}" alt="" loading="lazy" onerror="this.remove()">` : escapeHtml(getInitials(item.titulo))}
            </span>
          `).join("")}
          <p>${itemCount} ${itemCount === 1 ? "livro" : "livros"} neste pedido</p>
        </div>
        <div class="order-card-summary">
          <strong>${formatter.format(Number(order.total))}</strong>
          <a class="primary-button" href="compras.html?pedido=${order.id}">Acompanhar entrega</a>
        </div>
      </div>
    </article>
  `;
}

function renderOrderItem(item) {
  return `
    <article class="order-product">
      <span class="order-product-cover">
        ${item.imagemUrl ? `<img src="${escapeAttribute(item.imagemUrl)}" alt="" loading="lazy" onerror="this.remove()">` : escapeHtml(getInitials(item.titulo))}
      </span>
      <div>
        <strong>${escapeHtml(item.titulo)}</strong>
        <span>${escapeHtml(item.autor)} · ${escapeHtml(item.vendedor)}</span>
        <small>Quantidade: ${item.quantidade}</small>
      </div>
      <strong>${formatter.format(Number(item.subtotal))}</strong>
    </article>
  `;
}

function formatOrderDate(value) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Data indisponivel";
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(date);
}

function statusClassName(status) {
  return `status-${String(status || "").toLowerCase().replaceAll("_", "-")}`;
}

function formatOrderDay(value) {
  const date = new Date(`${value}T12:00:00`);
  if (Number.isNaN(date.getTime())) return "A confirmar";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "long" }).format(date);
}

function renderBooks(container, collection) {
  if (!container) return;

  if (!collection.length) {
    const message = catalogLoadError
      ? "Nao foi possivel carregar os livros."
      : books.length
        ? "Tente ajustar a busca ou limpar os filtros."
        : "Nenhum livro foi encontrado no catalogo.";

    renderEmptyState(container, "Nenhum livro encontrado", message);
    return;
  }

  container.innerHTML = collection.map(renderBookCard).join("");
}

function renderEmptyState(container, title, message) {
  container.innerHTML = `
    <div class="empty-state">
      <h3>${escapeHtml(title)}</h3>
      <p>${escapeHtml(message)}</p>
    </div>
  `;
}

function renderBookCard(book) {
  const titleFilterUrl = catalogFilterUrl({ q: book.title });
  const shippingFilterUrl = book.freeShipping ? catalogFilterUrl({ frete: "gratis" }) : catalogFilterUrl({ q: book.title });
  const tags = buildBookTags(book, shippingFilterUrl);

  return `
    <article class="book-card">
      <a href="detalhes.html?id=${book.id}" aria-label="Ver detalhes de ${escapeHtml(book.title)}">
        ${renderCover(book)}
      </a>
      <div class="book-card-body">
        <a class="book-card-filter-link" href="${titleFilterUrl}" aria-label="Filtrar catalogo por ${escapeHtml(book.title)}">
          <div class="book-meta">
            <h3>${escapeHtml(book.title)}</h3>
            <p class="book-author-line">
              ${renderAuthorAvatar(book.author, book.authorImageUrl)}
              <span>${escapeHtml(displayText(book.author))}</span>
            </p>
            <p>${escapeHtml(book.short || "Sem descricao cadastrada.")}</p>
          </div>
        </a>
        <div class="book-price-row">
          <span class="price">${formatBookPrice(book)}</span>
          ${book.condition ? `<span class="condition-badge">${escapeHtml(book.condition)}</span>` : ""}
        </div>
        ${tags.length ? `<div class="book-tags" aria-label="Informacoes comerciais">${tags.join("")}</div>` : ""}
        <div class="book-actions">
          <a class="details-link" href="detalhes.html?id=${book.id}">Detalhes</a>
          <button class="mini-button" type="button" ${canAddToCart(book) ? `data-add-cart="${book.id}"` : "disabled"}>${canAddToCart(book) ? "Adicionar" : "Indisponivel"}</button>
        </div>
      </div>
    </article>
  `;
}

function buildBookTags(book, shippingFilterUrl) {
  const tags = [];

  if (book.type) {
    tags.push(`<a href="${catalogFilterUrl({ tipo: book.type })}" aria-label="Filtrar por tipo ${escapeHtml(book.type)}">${escapeHtml(book.type)}</a>`);
  }

  tags.push(`<a href="${shippingFilterUrl}" aria-label="${book.freeShipping ? "Filtrar por frete gratis" : `Filtrar catalogo por ${escapeHtml(book.title)}`}">${book.freeShipping ? "Frete gratis" : "Frete calculado"}</a>`);

  if (book.language) {
    tags.push(`<a href="${catalogFilterUrl({ idioma: book.language })}" aria-label="Filtrar por idioma ${escapeHtml(book.language)}">${escapeHtml(book.language)}</a>`);
  }

  if (book.rating !== null) {
    tags.push(`<a href="${catalogFilterUrl({ avaliacao: ratingFilterValue(book.rating) })}" aria-label="Filtrar por avaliacao minima">${formatRating(book.rating)}</a>`);
  }

  return tags;
}

function catalogFilterUrl(filters) {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && String(value).trim()) {
      params.set(key, value);
    }
  });

  const query = params.toString();
  return query ? `livros.html?${query}` : "livros.html";
}

function ratingFilterValue(rating) {
  if (rating >= 5) return "5";
  if (rating >= 4.5) return "4.5";
  return "4";
}

function displayText(value, fallback = "Nao informado") {
  const text = String(value ?? "").trim();
  return text || fallback;
}

function formatBookPrice(book) {
  return book.price === null ? "Preco indisponivel" : formatter.format(book.price);
}

function formatRating(rating) {
  return rating === null ? "" : `${rating.toFixed(1)} estrelas`;
}

function canAddToCart(book) {
  return book.copyId !== null && book.price !== null && book.stock > 0;
}

function renderCover(book) {
  return `
    <div class="book-cover ${book.cover}${book.imageUrl ? " book-cover-photo" : ""}" aria-hidden="true">
      ${book.imageUrl ? `<img src="${escapeAttribute(book.imageUrl)}" alt="" loading="lazy" onerror="this.parentElement.classList.remove('book-cover-photo'); this.remove()">` : ""}
      <span class="cover-category">${escapeHtml(book.category)}</span>
      <strong>${escapeHtml(displayText(book.title, "Livro"))}</strong>
      <small>${escapeHtml(displayText(book.author))}</small>
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
  const total = book.price === null ? "Preco indisponivel" : formatter.format(book.price * quantity);
  return `
    <article class="cart-item">
      <a href="detalhes.html?id=${book.id}" aria-label="Ver ${escapeHtml(book.title)}">${renderCover(book)}</a>
      <div class="cart-item-info">
        <h2><a href="detalhes.html?id=${book.id}">${escapeHtml(book.title)}</a></h2>
        <p>${escapeHtml(book.author)} · ${escapeHtml(book.type)} · ${escapeHtml(book.condition)}</p>
        <small>Vendido por ${escapeHtml(displayText(book.seller))}</small>
        <div class="quantity-control" aria-label="Quantidade de ${escapeHtml(book.title)}">
          <button type="button" data-cart-quantity="${book.id}" data-delta="-1" aria-label="Diminuir quantidade">-</button>
          <span>${quantity}</span>
          <button type="button" data-cart-quantity="${book.id}" data-delta="1" aria-label="Aumentar quantidade" ${quantity >= book.stock ? "disabled" : ""}>+</button>
        </div>
      </div>
      <div class="cart-item-total">
        <strong>${total}</strong>
        <button class="danger-button" type="button" data-remove-cart="${book.id}">Remover</button>
      </div>
    </article>
  `;
}

function renderInfo(label, value) {
  return `
    <div class="info-card">
      <span>${escapeHtml(label)}</span>
      <strong>${escapeHtml(displayText(value))}</strong>
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

  if (sort === "price-asc") return sorted.sort((a, b) => comparePrice(a, b, "asc"));
  if (sort === "price-desc") return sorted.sort((a, b) => comparePrice(a, b, "desc"));
  if (sort === "title") return sorted.sort((a, b) => a.title.localeCompare(b.title, "pt-BR"));
  if (sort === "mais-vendidos") {
    return sorted.sort((a, b) =>
      Number(b.bestSeller) - Number(a.bestSeller)
      || b.highlightScore - a.highlightScore
      || (b.rating ?? 0) - (a.rating ?? 0));
  }

  return sorted;
}

function comparePrice(a, b, direction) {
  if (a.price === null && b.price === null) return 0;
  if (a.price === null) return 1;
  if (b.price === null) return -1;
  return direction === "asc" ? a.price - b.price : b.price - a.price;
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
  if (!book || !canAddToCart(book) || !book.copyId) return;

  const cart = getCart();
  const existing = cart.find((item) => item.id === id);

  if (existing) {
    if (existing.quantity >= book.stock) {
      showToast(`O estoque disponivel de ${book.title} ja esta no carrinho.`);
      return;
    }
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

  if (delta > 0 && item.quantity >= book.stock) {
    showToast(`Quantidade maxima disponivel: ${book.stock}.`);
    return;
  }

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
