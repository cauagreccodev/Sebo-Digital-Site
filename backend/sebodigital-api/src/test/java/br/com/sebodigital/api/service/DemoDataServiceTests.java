package br.com.sebodigital.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.sebodigital.api.model.entity.Livro;
import br.com.sebodigital.api.model.entity.LivroCopia;
import br.com.sebodigital.api.model.entity.Pedido;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.entity.Vendedor;
import br.com.sebodigital.api.repository.LivroCopiaRepository;
import br.com.sebodigital.api.repository.PedidoRepository;
import br.com.sebodigital.api.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class DemoDataServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroCopiaRepository livroCopiaRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DemoDataService demoDataService;

    @BeforeEach
    void setUp() {
        demoDataService = new DemoDataService(
                usuarioRepository,
                livroCopiaRepository,
                pedidoRepository,
                passwordEncoder);
    }

    @Test
    void criaContaDemoCompletaComHistoricoDePedidos() {
        when(usuarioRepository.findByEmailIgnoreCase(DemoDataService.DEMO_EMAIL))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(DemoDataService.DEMO_PASSWORD))
                .thenReturn("senha-codificada");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(livroCopiaRepository.findOfertasAtivasParaDemo())
                .thenReturn(List.of(oferta(1L, "Livro A"), oferta(2L, "Livro B")));
        when(pedidoRepository.existsByCodigo(anyString())).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        demoDataService.prepararContaDemo();

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuario = usuarioCaptor.getValue();
        assertThat(usuario.getEmail()).isEqualTo("guest@exemplo.com");
        assertThat(usuario.getEnderecoPrincipal()).isEqualTo("Avenida Paulista, 1578");
        assertThat(usuario.getCep()).isEqualTo("01310-200");

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository, times(7)).save(pedidoCaptor.capture());
        assertThat(pedidoCaptor.getAllValues())
                .extracting(Pedido::getStatus)
                .containsExactlyInAnyOrder(
                        br.com.sebodigital.api.model.enums.StatusPedido.ENTREGUE,
                        br.com.sebodigital.api.model.enums.StatusPedido.ENTREGUE,
                        br.com.sebodigital.api.model.enums.StatusPedido.EM_TRANSPORTE,
                        br.com.sebodigital.api.model.enums.StatusPedido.ENVIADO,
                        br.com.sebodigital.api.model.enums.StatusPedido.EM_SEPARACAO,
                        br.com.sebodigital.api.model.enums.StatusPedido.PAGAMENTO_APROVADO,
                        br.com.sebodigital.api.model.enums.StatusPedido.PEDIDO_REALIZADO);
        assertThat(pedidoCaptor.getAllValues())
                .allSatisfy(pedido -> {
                    assertThat(pedido.getItens()).isNotEmpty();
                    assertThat(pedido.getCodigoRastreio()).startsWith("SD-DEMO-");
                });
    }

    private LivroCopia oferta(Long id, String titulo) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setAutor("Autor Demo");
        livro.setImagemUrl("https://example.com/" + id + ".jpg");

        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setNome("Sebo Demo");
        vendedor.setCidade("Sao Paulo");

        LivroCopia copia = new LivroCopia();
        copia.setId(id);
        copia.setLivro(livro);
        copia.setVendedor(vendedor);
        copia.setPreco(new BigDecimal("39.90"));
        copia.setEstoque(10);
        copia.setAtivo(true);
        return copia;
    }
}
