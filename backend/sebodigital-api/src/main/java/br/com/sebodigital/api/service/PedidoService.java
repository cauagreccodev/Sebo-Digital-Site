package br.com.sebodigital.api.service;

import br.com.sebodigital.api.dto.pedido.CheckoutItemRequest;
import br.com.sebodigital.api.dto.pedido.CriarPedidoRequest;
import br.com.sebodigital.api.dto.pedido.ItemPedidoResponse;
import br.com.sebodigital.api.dto.pedido.PedidoResponse;
import br.com.sebodigital.api.model.entity.ItemPedido;
import br.com.sebodigital.api.model.entity.LivroCopia;
import br.com.sebodigital.api.model.entity.Pedido;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.enums.StatusPedido;
import br.com.sebodigital.api.repository.LivroCopiaRepository;
import br.com.sebodigital.api.repository.PedidoRepository;
import br.com.sebodigital.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private static final BigDecimal FRETE_PADRAO = new BigDecimal("14.90");
    private static final BigDecimal FRETE_GRATIS = BigDecimal.ZERO;

    private final PedidoRepository pedidoRepository;
    private final LivroCopiaRepository livroCopiaRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            LivroCopiaRepository livroCopiaRepository,
            UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.livroCopiaRepository = livroCopiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PedidoResponse criar(String emailUsuario, CriarPedidoRequest request) {
        Usuario usuario = buscarUsuario(emailUsuario);
        Map<Long, Integer> quantidades = consolidarItens(request.itens());

        Pedido pedido = new Pedido();
        pedido.setCodigo(gerarCodigo("PED"));
        pedido.setCodigoRastreio(gerarCodigo("SD"));
        pedido.setUsuario(usuario);
        pedido.setEnderecoEntrega(request.enderecoEntrega().trim());
        pedido.setCidadeEntrega(request.cidadeEntrega().trim());
        pedido.setEstadoEntrega(request.estadoEntrega().trim().toUpperCase());
        pedido.setCepEntrega(normalizarCep(request.cepEntrega()));
        String formaPagamento = request.formaPagamento().trim();
        pedido.setFormaPagamento(formaPagamento);
        pedido.setStatus("Boleto".equalsIgnoreCase(formaPagamento)
                ? StatusPedido.PEDIDO_REALIZADO
                : StatusPedido.PAGAMENTO_APROVADO);
        pedido.setPrevisaoEntrega(LocalDate.now().plusDays(8));

        BigDecimal subtotal = BigDecimal.ZERO;
        boolean todosComFreteGratis = true;

        for (Map.Entry<Long, Integer> entrada : quantidades.entrySet()) {
            LivroCopia copia = livroCopiaRepository.findByIdForUpdate(entrada.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Oferta de livro nao encontrada"));
            int quantidade = entrada.getValue();

            if (!copia.isAtivo() || copia.getEstoque() < quantidade) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para " + copia.getLivro().getTitulo());
            }

            BigDecimal subtotalItem = copia.getPreco().multiply(BigDecimal.valueOf(quantidade));
            subtotal = subtotal.add(subtotalItem);
            todosComFreteGratis = todosComFreteGratis && copia.isFreteGratis();
            copia.setEstoque(copia.getEstoque() - quantidade);

            ItemPedido item = new ItemPedido();
            item.setLivro(copia.getLivro());
            item.setLivroCopiaId(copia.getId());
            item.setTitulo(copia.getLivro().getTitulo());
            item.setAutor(copia.getLivro().getAutor());
            item.setImagemUrl(copia.getLivro().getImagemUrl());
            item.setVendedor(copia.getVendedor().getNome());
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(copia.getPreco());
            item.setSubtotal(subtotalItem);
            pedido.adicionarItem(item);
        }

        BigDecimal frete = todosComFreteGratis ? FRETE_GRATIS : FRETE_PADRAO;
        pedido.setSubtotal(subtotal);
        pedido.setFrete(frete);
        pedido.setTotal(subtotal.add(frete));

        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(String emailUsuario) {
        return pedidoRepository.findByUsuarioEmailIgnoreCaseOrderByCriadoEmDesc(emailUsuario)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(String emailUsuario, Long id) {
        Pedido pedido = pedidoRepository.findByIdAndUsuarioEmailIgnoreCase(id, emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Pedido nao encontrado"));
        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido nao encontrado"));
        pedido.setStatus(status);
        return toResponse(pedidoRepository.save(pedido));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
    }

    private Map<Long, Integer> consolidarItens(List<CheckoutItemRequest> itens) {
        Map<Long, Integer> quantidades = new LinkedHashMap<>();
        for (CheckoutItemRequest item : itens) {
            quantidades.merge(item.livroCopiaId(), item.quantidade(), Integer::sum);
        }
        return quantidades;
    }

    private String gerarCodigo(String prefixo) {
        return prefixo + "-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private String normalizarCep(String cep) {
        String digitos = cep.replaceAll("\\D", "");
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }

    private PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm(),
                pedido.getPrevisaoEntrega(),
                pedido.getEnderecoEntrega(),
                pedido.getCidadeEntrega(),
                pedido.getEstadoEntrega(),
                pedido.getCepEntrega(),
                pedido.getFormaPagamento(),
                pedido.getCodigoRastreio(),
                pedido.getSubtotal(),
                pedido.getFrete(),
                pedido.getTotal(),
                pedido.getItens().stream().map(this::toResponse).toList());
    }

    private ItemPedidoResponse toResponse(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getLivro().getId(),
                item.getLivroCopiaId(),
                item.getTitulo(),
                item.getAutor(),
                item.getImagemUrl(),
                item.getVendedor(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal());
    }
}
