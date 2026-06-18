package br.com.sebodigital.api.service;

import br.com.sebodigital.api.model.entity.ItemPedido;
import br.com.sebodigital.api.model.entity.LivroCopia;
import br.com.sebodigital.api.model.entity.Pedido;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.enums.AuthProvider;
import br.com.sebodigital.api.model.enums.StatusPedido;
import br.com.sebodigital.api.model.enums.UsuarioRole;
import br.com.sebodigital.api.repository.LivroCopiaRepository;
import br.com.sebodigital.api.repository.PedidoRepository;
import br.com.sebodigital.api.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoDataService {

    public static final String DEMO_EMAIL = "guest@exemplo.com";
    public static final String DEMO_PASSWORD = "guest123";

    private static final Logger log = LoggerFactory.getLogger(DemoDataService.class);
    private static final BigDecimal FRETE_PADRAO = new BigDecimal("14.90");

    private final UsuarioRepository usuarioRepository;
    private final LivroCopiaRepository livroCopiaRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataService(
            UsuarioRepository usuarioRepository,
            LivroCopiaRepository livroCopiaRepository,
            PedidoRepository pedidoRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.livroCopiaRepository = livroCopiaRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void prepararContaDemo() {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(DEMO_EMAIL)
                .orElseGet(Usuario::new);

        usuario.setNome("Leitor Demo");
        usuario.setEmail(DEMO_EMAIL);
        usuario.setSenha(passwordEncoder.encode(DEMO_PASSWORD));
        usuario.setRole(UsuarioRole.USER);
        usuario.setAuthProvider(AuthProvider.LOCAL);
        usuario.setProviderId(null);
        usuario.setFotoUrl(null);
        usuario.setTelefone("(11) 99999-1234");
        usuario.setEnderecoPrincipal("Avenida Paulista, 1578");
        usuario.setComplemento("Apartamento 42");
        usuario.setBairro("Bela Vista");
        usuario.setCidade("Sao Paulo");
        usuario.setEstado("SP");
        usuario.setCep("01310-200");
        usuario = usuarioRepository.save(usuario);

        List<LivroCopia> ofertas = livroCopiaRepository.findOfertasAtivasParaDemo();
        if (ofertas.isEmpty()) {
            log.warn("Conta demo criada sem pedidos porque o catalogo nao possui ofertas ativas");
            return;
        }

        List<PedidoDemo> pedidos = List.of(
                new PedidoDemo(
                        "DEMO-PED-001",
                        "SD-DEMO-0001",
                        StatusPedido.ENTREGUE,
                        75,
                        -63,
                        "Cartao de credito",
                        2),
                new PedidoDemo(
                        "DEMO-PED-002",
                        "SD-DEMO-0002",
                        StatusPedido.ENTREGUE,
                        38,
                        -29,
                        "PIX",
                        1),
                new PedidoDemo(
                        "DEMO-PED-003",
                        "SD-DEMO-0003",
                        StatusPedido.EM_TRANSPORTE,
                        6,
                        2,
                        "PIX",
                        2),
                new PedidoDemo(
                        "DEMO-PED-004",
                        "SD-DEMO-0004",
                        StatusPedido.ENVIADO,
                        3,
                        5,
                        "Cartao de credito",
                        1),
                new PedidoDemo(
                        "DEMO-PED-005",
                        "SD-DEMO-0005",
                        StatusPedido.EM_SEPARACAO,
                        1,
                        7,
                        "Boleto",
                        2),
                new PedidoDemo(
                        "DEMO-PED-006",
                        "SD-DEMO-0006",
                        StatusPedido.PAGAMENTO_APROVADO,
                        0,
                        8,
                        "PIX",
                        1),
                new PedidoDemo(
                        "DEMO-PED-007",
                        "SD-DEMO-0007",
                        StatusPedido.PEDIDO_REALIZADO,
                        0,
                        9,
                        "Cartao de credito",
                        1));

        for (int index = 0; index < pedidos.size(); index++) {
            PedidoDemo dados = pedidos.get(index);
            if (!pedidoRepository.existsByCodigo(dados.codigo())) {
                pedidoRepository.save(criarPedido(usuario, ofertas, dados, index));
            }
        }

        log.info("Conta demo pronta para uso: {}", DEMO_EMAIL);
    }

    private Pedido criarPedido(
            Usuario usuario,
            List<LivroCopia> ofertas,
            PedidoDemo dados,
            int indicePedido) {
        Instant criadoEm = Instant.now().minus(dados.diasAtras(), ChronoUnit.DAYS);
        Instant atualizadoEm = switch (dados.status()) {
            case ENTREGUE -> criadoEm.plus(10, ChronoUnit.DAYS);
            case EM_TRANSPORTE -> criadoEm.plus(5, ChronoUnit.DAYS);
            case ENVIADO -> criadoEm.plus(2, ChronoUnit.DAYS);
            case EM_SEPARACAO -> criadoEm.plus(18, ChronoUnit.HOURS);
            default -> criadoEm.plus(2, ChronoUnit.HOURS);
        };

        Pedido pedido = new Pedido();
        pedido.setCodigo(dados.codigo());
        pedido.setCodigoRastreio(dados.codigoRastreio());
        pedido.setUsuario(usuario);
        pedido.setStatus(dados.status());
        pedido.setEnderecoEntrega(usuario.getEnderecoPrincipal() + " - " + usuario.getComplemento());
        pedido.setCidadeEntrega(usuario.getCidade());
        pedido.setEstadoEntrega(usuario.getEstado());
        pedido.setCepEntrega(usuario.getCep());
        pedido.setFormaPagamento(dados.formaPagamento());
        pedido.setPrevisaoEntrega(LocalDate.now().plusDays(dados.diasParaPrevisao()));
        pedido.setCriadoEm(criadoEm);
        pedido.setAtualizadoEm(atualizadoEm);

        BigDecimal subtotal = BigDecimal.ZERO;
        boolean freteGratis = true;
        int quantidadeItens = Math.min(dados.quantidadeItens(), ofertas.size());

        for (int itemIndex = 0; itemIndex < quantidadeItens; itemIndex++) {
            LivroCopia copia = ofertas.get((indicePedido + itemIndex) % ofertas.size());
            BigDecimal subtotalItem = copia.getPreco();
            subtotal = subtotal.add(subtotalItem);
            freteGratis = freteGratis && copia.isFreteGratis();

            ItemPedido item = new ItemPedido();
            item.setLivro(copia.getLivro());
            item.setLivroCopiaId(copia.getId());
            item.setTitulo(copia.getLivro().getTitulo());
            item.setAutor(copia.getLivro().getAutor());
            item.setImagemUrl(copia.getLivro().getImagemUrl());
            item.setVendedor(copia.getVendedor().getNome());
            item.setQuantidade(1);
            item.setPrecoUnitario(copia.getPreco());
            item.setSubtotal(subtotalItem);
            pedido.adicionarItem(item);
        }

        BigDecimal frete = freteGratis ? BigDecimal.ZERO : FRETE_PADRAO;
        pedido.setSubtotal(subtotal);
        pedido.setFrete(frete);
        pedido.setTotal(subtotal.add(frete));
        return pedido;
    }

    private record PedidoDemo(
            String codigo,
            String codigoRastreio,
            StatusPedido status,
            int diasAtras,
            int diasParaPrevisao,
            String formaPagamento,
            int quantidadeItens) {
    }
}
