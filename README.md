# Sebo Digital

Sebo Digital is a second-hand bookstore marketplace focused on book discovery, curated listings, advanced filters, and a simple purchase-intent flow.

The project presents a virtual used-book store, with a warm bookstore-inspired interface and a catalog experience designed for browsing, comparing, and finding books by different criteria.

## Live Demo

- Frontend: https://sebo-digital-site.vercel.app
- Backend API: https://sebo-digital-site-production.up.railway.app
- Public catalog endpoint: https://sebo-digital-site-production.up.railway.app/api/livros

Production currently runs with:

- Vercel for the static frontend
- Railway for the Spring Boot API
- Neon PostgreSQL for persistence

## Highlights

- Responsive storefront for a virtual second-hand bookstore
- Home page with search, featured books, categories, and reading suggestions
- Home discovery areas derived from API categories, authors, highlights, and offers
- Marketplace-style navigation with a catalog mega menu
- Account access focused on purchases, checkout, and order tracking
- Book listing page with advanced filters
- Book detail page with price, seller, location, condition notes, and metadata
- Shopping cart with quantity controls, stock validation, checkout, and persisted orders
- Purchase history with delivery status timeline and tracking code
- Visual book cards with title, author, price, condition, type, shipping, language, and rating
- Frontend catalog loaded from the backend API
- Spring Boot backend with JWT authentication
- REST API for book registration and marketplace-style offers

## Catalog Filters

The catalog currently supports filtering by:

- Free shipping
- Promotions
- Corporate purchase availability
- Category
- Book type: new or used
- Author
- Publisher
- Publication year
- Book location city
- Sellers and bookstores
- Language
- Minimum rating
- Price range
- Best-selling, price, and title sorting

## Pages

- `index.html` - storefront home page
- `livros.html` - book catalog and filters
- `detalhes.html?id=1` - book detail page
- `carrinho.html` - cart, delivery details, and checkout
- `login.html` - login and account creation screen
- `compras.html` - purchase history and order tracking

## Project Structure

```text
assets/
  css/
  img/
  js/
docs/
backend/
  sebodigital-api/
index.html
livros.html
detalhes.html
carrinho.html
```

## Image Strategy

The catalog stores cover image URLs with each book record. If the project later needs file uploads, the recommended approach is to store image files locally or in object storage and save only their paths and metadata in the database.

This keeps the project simple and lets the same structure evolve to an external storage provider if needed.

More details are available in `docs/estrategia-imagens.md`.

## Book Copy Modeling

The database model separates the book record from its sellable copies. A single book can have multiple offers, grouped as new and used copies.

More details are available in `docs/modelagem-livros.md`.

## Backend API

The backend lives in `backend/sebodigital-api` and includes:

- User registration and login with JWT
- Book CRUD endpoints
- Authenticated checkout and order history endpoints
- Stock reduction, delivery address, payment method, and tracking status persistence
- Book copies/offers grouped as new and used
- Sellers, publishers, stock, prices, highlights, and cover image URLs
- PostgreSQL-only persistence for users, books, offers, orders, and order items

The API no longer creates demo users or demo books at startup. Users must be created through the registration flow/API, and books/offers must be persisted in PostgreSQL through the backend endpoints.

### Production database

The deployed application does not use a PostgreSQL server installed on the developer machine. Production data is stored in a Neon PostgreSQL database, and the Spring Boot API running on Railway connects to it through environment variables.

Configure these variables in the Railway service:

```text
DB_URL=jdbc:postgresql://<neon-host>/<database>?sslmode=require
DB_USERNAME=<neon-user>
DB_PASSWORD=<neon-password>
JWT_SECRET=<strong-production-secret>
```

Use the JDBC connection values supplied by Neon. Do not commit database credentials or put them directly in `application.yaml`.

The default values in `application.yaml` point to `localhost` only as a development fallback when the database variables are absent.

### Local development with Neon

Local development can use the same Neon database architecture without installing PostgreSQL. Create the ignored local environment file from the provided template:

```powershell
Copy-Item scripts\local-env.example.ps1 scripts\local-env.ps1
notepad scripts\local-env.ps1
```

Fill in the Neon JDBC URL, username, password, and any OAuth credentials in `scripts/local-env.ps1`, then start the local frontend and backend:

```powershell
.\scripts\start-local-dev.ps1
```

The script loads the ignored environment file, serves the frontend on port `5500`, and runs the API with the `local` profile.

### Optional local PostgreSQL

If you prefer a completely local database, create a PostgreSQL database named `sebodigital` and set:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/sebodigital"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua_senha"
```

Start the API from the project root:

```powershell
mvn -f backend\sebodigital-api\pom.xml spring-boot:run "-Dspring-boot.run.profiles=local"
```

Or run it from the backend directory:

```powershell
cd backend\sebodigital-api
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Serve the static frontend on port `5500` while testing OAuth redirects:

```powershell
node scripts/static-server.mjs
```

Then open:

```text
http://127.0.0.1:5500/login.html
```

### Social login

The login page has Google and Facebook buttons connected to the backend OAuth2 flow. After a successful provider login, the API creates or updates the user in the PostgreSQL `usuarios` table and returns the same JWT response used by regular login.

Register these redirect URIs in the provider dashboards:

```text
Google:   http://localhost:8080/login/oauth2/code/google
Facebook: http://localhost:8080/login/oauth2/code/facebook
```

Set the OAuth2 credentials before starting the API:

```powershell
$env:GOOGLE_CLIENT_ID="seu_google_client_id"
$env:GOOGLE_CLIENT_SECRET="seu_google_client_secret"
$env:FACEBOOK_CLIENT_ID="id_numerico_do_app_facebook"
$env:FACEBOOK_CLIENT_SECRET="seu_facebook_client_secret"
```

You can configure only one provider during development. The API registers Google only when `GOOGLE_CLIENT_ID` ends with `.apps.googleusercontent.com` and `GOOGLE_CLIENT_SECRET` is set. Facebook is registered only when `FACEBOOK_CLIENT_ID` is numeric and `FACEBOOK_CLIENT_SECRET` is set. Restart the API after changing these variables.

For Google, create an OAuth Client ID of type `Web application` in Google Cloud and copy the value named `Client ID`, not an API key. The Google client id normally ends with `.apps.googleusercontent.com`. If Google returns `Erro 401: invalid_client`, the client id/secret being sent by the backend is wrong, missing, or belongs to a deleted/different OAuth client.

For Facebook, use the numeric `App ID` from Meta for Developers as `FACEBOOK_CLIENT_ID`. If the Facebook URL shows `client_id=${FACEBOOK_CLIENT_ID}`, the API was started without a real environment variable value or was not restarted after setting it.

The social registration stores the normalized e-mail, display name, provider (`GOOGLE` or `FACEBOOK`), provider id, and profile image URL when the provider returns it.

If your frontend is not running at `http://localhost:5500/login.html`, also set:

```powershell
$env:APP_FRONTEND_LOGIN_URL="http://localhost:5500/login.html"
$env:APP_FRONTEND_ALLOWED_REDIRECT_ORIGINS="http://localhost:5500,http://127.0.0.1:5500"
```

The frontend reads the API from `http://localhost:8080` by default. If the backend or PostgreSQL connection is unavailable, the catalog shows an empty/error state and no local demo catalog is used.

## Production Deployment

The production architecture is:

```text
Browser -> Vercel static frontend -> Railway Spring Boot API -> Neon PostgreSQL
```

Production frontend:

```text
https://sebo-digital-site.vercel.app
```

Production backend:

```text
https://sebo-digital-site-production.up.railway.app
```

In the Vercel project, set this environment variable:

```text
SEBO_API_URL=https://sebo-digital-site-production.up.railway.app
```

Use the public URL of the Spring Boot API without a trailing slash. The frontend reads this value from `/api/config` at runtime, so the same static files work locally and in production.

Configure the Railway backend with:

```text
DB_URL=jdbc:postgresql://<neon-host>/<database>?sslmode=require
DB_USERNAME=<neon-user>
DB_PASSWORD=<neon-password>
JWT_SECRET=<strong-production-secret>
APP_FRONTEND_LOGIN_URL=https://sebo-digital-site.vercel.app/login.html
APP_FRONTEND_ALLOWED_REDIRECT_ORIGINS=https://sebo-digital-site.vercel.app
```

For production social login, also configure:

```text
GOOGLE_CLIENT_ID=<google-web-client-id>
GOOGLE_CLIENT_SECRET=<google-client-secret>
FACEBOOK_CLIENT_ID=<numeric-meta-app-id>
FACEBOOK_CLIENT_SECRET=<facebook-client-secret>
```

The Google and Facebook callback URLs must point to the Railway backend:

```text
Google:   https://sebo-digital-site-production.up.railway.app/login/oauth2/code/google
Facebook: https://sebo-digital-site-production.up.railway.app/login/oauth2/code/facebook
```

After changing Railway variables, restart or redeploy the service. Provider availability can be checked at:

```text
https://sebo-digital-site-production.up.railway.app/api/auth/oauth2/providers
```

## Future Roadmap

- Book registration and management area
- Cover image upload
- Seller/bookstore profiles
- Integration with a real payment provider
- Carrier integration for automatic tracking updates

## Version

Current tagged version:

`v0.8` - Purchase-focused navigation, functional checkout, persisted orders, purchase history, stock control, and delivery tracking

`v0.7` - Production deployment with Vercel, Railway, and Neon, removal of hardcoded catalog data, and improved mobile navigation and touch carousels

`v0.6` - Neon PostgreSQL configuration and Vercel runtime deployment setup

`v0.5` - PostgreSQL persistence for social authentication and removal of demo seed data

`v0.4` - Social login with Google/Facebook, original provider logos, and post-login redirect to the home page

`v0.3` - Complete dark theme with a hover dropdown for switching between light and dark modes

`v0.2` - Backend authentication, frontend API integration, and books API

`v0.1` - Initial frontend prototype
