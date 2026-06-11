package br.com.sebodigital.api.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Column(nullable = false, length = 140)
    private String autor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editora_id", nullable = false)
    private Editora editora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedora_id")
    private Vendedor vendedora;

    @Column(length = 30)
    private String isbn;

    @Column(nullable = false, length = 50)
    private String idioma;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "imagem_url", nullable = false, length = 600)
    private String imagemUrl;

    @Column(nullable = false)
    private boolean destaqueFreteGratis;

    @Column(nullable = false)
    private boolean destaqueOferta;

    @Column(nullable = false)
    private boolean destaqueMaisVendido;

    @Column(nullable = false)
    private boolean destaqueLancamento;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LivroCopia> copias = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Editora getEditora() {
        return editora;
    }

    public void setEditora(Editora editora) {
        this.editora = editora;
    }

    public Vendedor getVendedora() {
        return vendedora;
    }

    public void setVendedora(Vendedor vendedora) {
        this.vendedora = vendedora;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public boolean isDestaqueFreteGratis() {
        return destaqueFreteGratis;
    }

    public void setDestaqueFreteGratis(boolean destaqueFreteGratis) {
        this.destaqueFreteGratis = destaqueFreteGratis;
    }

    public boolean isDestaqueOferta() {
        return destaqueOferta;
    }

    public void setDestaqueOferta(boolean destaqueOferta) {
        this.destaqueOferta = destaqueOferta;
    }

    public boolean isDestaqueMaisVendido() {
        return destaqueMaisVendido;
    }

    public void setDestaqueMaisVendido(boolean destaqueMaisVendido) {
        this.destaqueMaisVendido = destaqueMaisVendido;
    }

    public boolean isDestaqueLancamento() {
        return destaqueLancamento;
    }

    public void setDestaqueLancamento(boolean destaqueLancamento) {
        this.destaqueLancamento = destaqueLancamento;
    }

    public List<LivroCopia> getCopias() {
        return copias;
    }

    public void adicionarCopia(LivroCopia copia) {
        copias.add(copia);
        copia.setLivro(this);
    }

    public void limparCopias() {
        for (LivroCopia copia : copias) {
            copia.setLivro(null);
        }
        copias.clear();
    }
}
