package br.com.sebodigital.api.service;

import br.com.sebodigital.api.dto.livro.CopiasLivroResponse;
import br.com.sebodigital.api.dto.livro.DestaquesLivroResponse;
import br.com.sebodigital.api.dto.livro.LivroCopiaRequest;
import br.com.sebodigital.api.dto.livro.LivroCopiaResponse;
import br.com.sebodigital.api.dto.livro.LivroRequest;
import br.com.sebodigital.api.dto.livro.LivroResponse;
import br.com.sebodigital.api.model.entity.Editora;
import br.com.sebodigital.api.model.entity.Livro;
import br.com.sebodigital.api.model.entity.LivroCopia;
import br.com.sebodigital.api.model.entity.Vendedor;
import br.com.sebodigital.api.model.enums.EstadoConservacao;
import br.com.sebodigital.api.model.enums.TipoCopia;
import br.com.sebodigital.api.repository.EditoraRepository;
import br.com.sebodigital.api.repository.LivroRepository;
import br.com.sebodigital.api.repository.VendedorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LivroService {

    private static final String CIDADE_PADRAO = "Brasil";
    private static final BigDecimal AVALIACAO_PADRAO = new BigDecimal("4.70");

    private final LivroRepository livroRepository;
    private final EditoraRepository editoraRepository;
    private final VendedorRepository vendedorRepository;

    public LivroService(
            LivroRepository livroRepository,
            EditoraRepository editoraRepository,
            VendedorRepository vendedorRepository) {
        this.livroRepository = livroRepository;
        this.editoraRepository = editoraRepository;
        this.vendedorRepository = vendedorRepository;
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listar(
            String busca,
            String categoria,
            Boolean freteGratis,
            Boolean oferta,
            Boolean maisVendido,
            Boolean lancamento) {
        return livroRepository.findAll(
                        filtrosLivros(
                                normalizarFiltro(busca),
                                normalizarFiltro(categoria),
                                freteGratis,
                                oferta,
                                maisVendido,
                                lancamento),
                        Sort.by("titulo").ascending())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro nao encontrado"));
        return toResponse(livro);
    }

    @Transactional
    public LivroResponse cadastrar(LivroRequest request) {
        Livro livro = new Livro();
        preencherLivro(livro, request);
        return toResponse(livroRepository.save(livro));
    }

    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest request) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro nao encontrado"));
        preencherLivro(livro, request);
        return toResponse(livroRepository.save(livro));
    }

    @Transactional
    public void excluir(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new EntityNotFoundException("Livro nao encontrado");
        }
        livroRepository.deleteById(id);
    }

    private void preencherLivro(Livro livro, LivroRequest request) {
        livro.setTitulo(request.titulo().trim());
        livro.setAutor(request.autor().trim());
        livro.setEditora(obterEditora(request.editora()));
        livro.setVendedora(obterVendedoraPrincipal(request));
        livro.setIsbn(trimToNull(request.isbn()));
        livro.setIdioma(request.idioma().trim());
        livro.setAnoPublicacao(request.anoPublicacao());
        livro.setCategoria(request.categoria().trim());
        livro.setDescricao(trimToNull(request.descricao()));
        livro.setImagemUrl(request.imagemUrl().trim());
        livro.setDestaqueFreteGratis(request.destaqueFreteGratis());
        livro.setDestaqueOferta(request.destaqueOferta());
        livro.setDestaqueMaisVendido(request.destaqueMaisVendido());
        livro.setDestaqueLancamento(request.destaqueLancamento());

        livro.limparCopias();
        request.copias().forEach(copiaRequest -> livro.adicionarCopia(toEntity(copiaRequest)));
    }

    private LivroCopia toEntity(LivroCopiaRequest request) {
        LivroCopia copia = new LivroCopia();
        copia.setVendedor(obterVendedor(
                request.vendedor(),
                valorOuPadrao(request.cidadeVendedor(), request.cidade()),
                request.avaliacaoVendedor()));
        copia.setTipo(request.tipo());
        copia.setEstadoConservacao(estadoConservacao(request));
        copia.setPreco(request.preco());
        copia.setEstoque(request.estoque());
        copia.setCidade(request.cidade().trim());
        copia.setFreteGratis(Boolean.TRUE.equals(request.freteGratis()));
        copia.setPromocao(Boolean.TRUE.equals(request.promocao()));
        copia.setCompraCorporativa(Boolean.TRUE.equals(request.compraCorporativa()));
        copia.setAtivo(request.ativo() == null || request.ativo());
        return copia;
    }

    private EstadoConservacao estadoConservacao(LivroCopiaRequest request) {
        if (request.estadoConservacao() != null) {
            return request.estadoConservacao();
        }
        return request.tipo() == TipoCopia.NOVO ? EstadoConservacao.NOVO : EstadoConservacao.BOM;
    }

    private Editora obterEditora(String nome) {
        String nomeNormalizado = nome.trim();
        return editoraRepository.findByNomeIgnoreCase(nomeNormalizado)
                .orElseGet(() -> {
                    Editora editora = new Editora();
                    editora.setNome(nomeNormalizado);
                    return editoraRepository.save(editora);
                });
    }

    private Vendedor obterVendedoraPrincipal(LivroRequest request) {
        if (hasText(request.vendedora())) {
            return obterVendedor(
                    request.vendedora(),
                    valorOuPadrao(request.cidadeVendedora(), CIDADE_PADRAO),
                    request.avaliacaoVendedora());
        }

        LivroCopiaRequest primeiraCopia = request.copias().get(0);
        return obterVendedor(
                primeiraCopia.vendedor(),
                valorOuPadrao(primeiraCopia.cidadeVendedor(), primeiraCopia.cidade()),
                primeiraCopia.avaliacaoVendedor());
    }

    private Vendedor obterVendedor(String nome, String cidade, BigDecimal avaliacao) {
        String nomeNormalizado = nome.trim();
        String cidadeNormalizada = valorOuPadrao(cidade, CIDADE_PADRAO);
        return vendedorRepository.findByNomeIgnoreCaseAndCidadeIgnoreCase(nomeNormalizado, cidadeNormalizada)
                .orElseGet(() -> {
                    Vendedor vendedor = new Vendedor();
                    vendedor.setNome(nomeNormalizado);
                    vendedor.setCidade(cidadeNormalizada);
                    vendedor.setAvaliacao(avaliacao == null ? AVALIACAO_PADRAO : avaliacao);
                    return vendedorRepository.save(vendedor);
                });
    }

    private LivroResponse toResponse(Livro livro) {
        List<LivroCopiaResponse> novas = livro.getCopias().stream()
                .filter(copia -> copia.getTipo() == TipoCopia.NOVO)
                .sorted(Comparator.comparing(LivroCopia::getPreco))
                .map(this::toResponse)
                .toList();

        List<LivroCopiaResponse> usadas = livro.getCopias().stream()
                .filter(copia -> copia.getTipo() == TipoCopia.USADO)
                .sorted(Comparator.comparing(LivroCopia::getPreco))
                .map(this::toResponse)
                .toList();

        BigDecimal menorPreco = livro.getCopias().stream()
                .filter(LivroCopia::isAtivo)
                .map(LivroCopia::getPreco)
                .min(BigDecimal::compareTo)
                .orElse(null);

        Integer estoqueTotal = livro.getCopias().stream()
                .filter(LivroCopia::isAtivo)
                .mapToInt(LivroCopia::getEstoque)
                .sum();

        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getEditora().getNome(),
                livro.getVendedora() == null ? null : livro.getVendedora().getNome(),
                livro.getIsbn(),
                livro.getIdioma(),
                livro.getAnoPublicacao(),
                livro.getCategoria(),
                livro.getDescricao(),
                livro.getImagemUrl(),
                new DestaquesLivroResponse(
                        livro.isDestaqueFreteGratis(),
                        livro.isDestaqueOferta(),
                        livro.isDestaqueMaisVendido(),
                        livro.isDestaqueLancamento()),
                new CopiasLivroResponse(novas, usadas),
                menorPreco,
                estoqueTotal);
    }

    private LivroCopiaResponse toResponse(LivroCopia copia) {
        return new LivroCopiaResponse(
                copia.getId(),
                copia.getVendedor().getNome(),
                copia.getVendedor().getCidade(),
                copia.getVendedor().getAvaliacao(),
                copia.getTipo(),
                copia.getEstadoConservacao(),
                copia.getPreco(),
                copia.getEstoque(),
                copia.getCidade(),
                copia.isFreteGratis(),
                copia.isPromocao(),
                copia.isCompraCorporativa(),
                copia.isAtivo());
    }

    private Specification<Livro> filtrosLivros(
            String busca,
            String categoria,
            Boolean freteGratis,
            Boolean oferta,
            Boolean maisVendido,
            Boolean lancamento) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(busca)) {
                String termo = "%" + busca.toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), termo),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("autor")), termo),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), termo)));
            }

            if (hasText(categoria)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("categoria")),
                        categoria.toLowerCase(Locale.ROOT)));
            }

            if (freteGratis != null) {
                predicates.add(criteriaBuilder.equal(root.get("destaqueFreteGratis"), freteGratis));
            }

            if (oferta != null) {
                predicates.add(criteriaBuilder.equal(root.get("destaqueOferta"), oferta));
            }

            if (maisVendido != null) {
                predicates.add(criteriaBuilder.equal(root.get("destaqueMaisVendido"), maisVendido));
            }

            if (lancamento != null) {
                predicates.add(criteriaBuilder.equal(root.get("destaqueLancamento"), lancamento));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private String normalizarFiltro(String valor) {
        return hasText(valor) ? valor.trim() : null;
    }

    private String trimToNull(String valor) {
        return hasText(valor) ? valor.trim() : null;
    }

    private String valorOuPadrao(String valor, String padrao) {
        return hasText(valor) ? valor.trim() : padrao;
    }

    private boolean hasText(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
