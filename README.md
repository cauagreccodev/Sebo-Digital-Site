# Sebo Digital

Sebo Digital is a second-hand bookstore marketplace prototype focused on book discovery, curated listings, advanced filters, and a simple purchase-intent flow.

The project presents the first version of a virtual used-book store, with a warm bookstore-inspired interface and a catalog experience designed for browsing, comparing, and finding books by different criteria.

## Highlights

- Responsive storefront for a virtual second-hand bookstore
- Home page with search, featured books, categories, and reading suggestions
- Marketplace-style navigation with a catalog mega menu
- Book listing page with advanced filters
- Book detail page with price, seller, location, condition notes, and metadata
- Demo shopping cart using local browser storage
- Visual book cards with title, author, price, condition, type, shipping, language, and rating
- Structure prepared for future backend integration

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
- `carrinho.html` - demo cart and purchase intent

## Project Structure

```text
assets/
  css/
  img/
  js/
docs/
index.html
livros.html
detalhes.html
carrinho.html
```

## Image Strategy

For the future full-stack version, the recommended approach is to store image files locally and save only their paths and metadata in the database.

This keeps the project simple, easier to understand, and more suitable for a demonstrative bookstore system. The same structure can later evolve to an external storage provider if needed.

More details are available in `docs/estrategia-imagens.md`.

## Future Roadmap

- Backend API for books, sellers, users, and orders
- Relational database integration
- Book registration and management area
- Cover image upload
- Persistent cart and checkout flow
- Seller/bookstore profiles
- Authentication and user accounts

## Version

Current tagged version:

`0.1` - Initial frontend prototype
