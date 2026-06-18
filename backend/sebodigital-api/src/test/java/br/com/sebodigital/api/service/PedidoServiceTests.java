package br.com.sebodigital.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.sebodigital.api.dto.pedido.CheckoutItemRequest;
import br.com.sebodigital.api.dto.pedido.CriarPedidoRequest;
import br.com.sebodigital.api.dto.pedido.PedidoResponse;
import br.com.sebodigital.api.model.entity.Livro;
import br.com.sebodigital.api.model.entity.LivroCopia;
import br.com.sebodigital.api.model.entity.Pedido;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.entity.Vendedor;
import br.com.sebodigital.api.model.enums.StatusPedido;
import br.com.sebodigital.api.repository.LivroCopiaRepository;
import br.com.sebodigital.api.repository.PedidoRepository;
import br.com.sebodigital.api.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private LivroCopiaRepository livroCopiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoService(pedidoRepository, livroCopiaRepository, usuarioRepository);
    }

    @Test
    void criaPedidoBaixaEstoqueECalculaTotal() {
        Usuario usuario = usuario();
        LivroCopia copia = copia(10, false);
        CriarPedidoRequest request = request(2);

        when(usuarioRepository.findByEmailIgnoreCase(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(livroCopiaRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(copia));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponse response = pedidoService.criar(usuario.getEmail(), request);

        assertThat(response.status()).isEqualTo(StatusPedido.PAGAMENTO_APROVADO);
        assertThat(response.subtotal()).isEqualByComparingTo("59.80");
        assertThat(response.frete()).isEqualByComparingTo("14.90");
        assertThat(response.total()).isEqualByComparingTo("74.70");
        assertThat(response.itens()).hasSize(1);
        assertThat(copia.getEstoque()).isEqualTo(8);
    }

    @Test
    void impedePedidoQuandoEstoqueForInsuficiente() {
        Usuario usuario = usuario();
        LivroCopia copia = copia(1, true);

        when(usuarioRepository.findByEmailIgnoreCase(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(livroCopiaRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(copia));

        assertThatThrownBy(() -> pedidoService.criar(usuario.getEmail(), request(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estoque insuficiente");

        verify(pedidoRepository, never()).save(any(Pedido.class));
        assertThat(copia.getEstoque()).isEqualTo(1);
    }

    private CriarPedidoRequest request(int quantidade) {
        return new CriarPedidoRequest(
                List.of(new CheckoutItemRequest(20L, quantidade)),
                "Rua dos Livros, 100",
                "Sao Paulo",
                "SP",
                "01001-000",
                "PIX");
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Leitor");
        usuario.setEmail("leitor@example.com");
        usuario.setSenha("senha");
        return usuario;
    }

    private LivroCopia copia(int estoque, boolean freteGratis) {
        Livro livro = new Livro();
        livro.setId(10L);
        livro.setTitulo("Livro de teste");
        livro.setAutor("Autor");
        livro.setImagemUrl("https://example.com/livro.jpg");

        Vendedor vendedor = new Vendedor();
        vendedor.setId(30L);
        vendedor.setNome("Sebo Teste");
        vendedor.setCidade("Sao Paulo");

        LivroCopia copia = new LivroCopia();
        copia.setId(20L);
        copia.setLivro(livro);
        copia.setVendedor(vendedor);
        copia.setPreco(new BigDecimal("29.90"));
        copia.setEstoque(estoque);
        copia.setFreteGratis(freteGratis);
        copia.setAtivo(true);
        return copia;
    }
}
