package br.com.sebodigital.api.config;

import br.com.sebodigital.api.dto.livro.LivroCopiaRequest;
import br.com.sebodigital.api.dto.livro.LivroRequest;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.enums.EstadoConservacao;
import br.com.sebodigital.api.model.enums.TipoCopia;
import br.com.sebodigital.api.model.enums.UsuarioRole;
import br.com.sebodigital.api.repository.LivroRepository;
import br.com.sebodigital.api.repository.UsuarioRepository;
import br.com.sebodigital.api.service.LivroService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            UsuarioRepository usuarioRepository,
            LivroRepository livroRepository,
            PasswordEncoder passwordEncoder,
            LivroService livroService) {
        return args -> {
            criarUsuarioSeNaoExistir(
                    usuarioRepository,
                    passwordEncoder,
                    "Administrador Sebo Digital",
                    "admin@sebodigital.com",
                    "admin123",
                    UsuarioRole.ADMIN);
            criarUsuarioSeNaoExistir(
                    usuarioRepository,
                    passwordEncoder,
                    "Cliente Demo",
                    "cliente@sebodigital.com",
                    "cliente123",
                    UsuarioRole.USER);

            if (livroRepository.count() == 0) {
                popularLivros(livroService);
            }
        };
    }

    private void popularLivros(LivroService livroService) {
        livroService.cadastrar(new LivroRequest(
                "Dom Casmurro",
                "Machado de Assis",
                "Atica",
                "Sebo Aurora",
                "Sao Paulo, SP",
                bd("4.85"),
                "9788508040845",
                "Portugues",
                1998,
                "Literatura Brasileira",
                "Classico brasileiro em edicoes novas e usadas para leitura escolar e colecao.",
                imagem("Dom+Casmurro", "3f3f46", "fafafa"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Sebo Aurora", "Sao Paulo, SP", "4.85", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "24.90", 3, true, true),
                        copia("Livraria Central", "Campinas, SP", "4.70", TipoCopia.NOVO, EstadoConservacao.NOVO, "39.90", 5, false, false))));

        livroService.cadastrar(new LivroRequest(
                "O Cortico",
                "Aluisio Azevedo",
                "Principis",
                "Estante da Praca",
                "Curitiba, PR",
                bd("4.60"),
                "9788594318603",
                "Portugues",
                2019,
                "Literatura Brasileira",
                "Romance naturalista com ofertas economicas para vestibular e clubes de leitura.",
                imagem("O+Cortico", "7c2d12", "fff7ed"),
                false,
                true,
                false,
                false,
                List.of(
                        copia("Estante da Praca", "Curitiba, PR", "4.60", TipoCopia.USADO, EstadoConservacao.BOM, "18.50", 2, false, true),
                        copia("Sebo Riachuelo", "Rio de Janeiro, RJ", "4.78", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "21.90", 1, true, false))));

        livroService.cadastrar(new LivroRequest(
                "Capitaes da Areia",
                "Jorge Amado",
                "Companhia das Letras",
                "Livros da Ladeira",
                "Salvador, BA",
                bd("4.92"),
                "9788535914061",
                "Portugues",
                2009,
                "Romance",
                "Edicao de bolso com boas opcoes de compra para leitores de literatura nacional.",
                imagem("Capitaes+da+Areia", "92400e", "fffbeb"),
                true,
                false,
                true,
                false,
                List.of(
                        copia("Livros da Ladeira", "Salvador, BA", "4.92", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "32.00", 4, true, false),
                        copia("Banca do Centro", "Recife, PE", "4.55", TipoCopia.USADO, EstadoConservacao.BOM, "27.90", 2, false, false))));

        livroService.cadastrar(new LivroRequest(
                "Torto Arado",
                "Itamar Vieira Junior",
                "Todavia",
                "Sebo Aurora",
                "Sao Paulo, SP",
                bd("4.85"),
                "9786580309313",
                "Portugues",
                2019,
                "Ficcao Contemporanea",
                "Livro recente com alta procura no catalogo e exemplares conservados.",
                imagem("Torto+Arado", "365314", "ecfccb"),
                true,
                false,
                true,
                true,
                List.of(
                        copia("Sebo Aurora", "Sao Paulo, SP", "4.85", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "44.90", 2, true, false),
                        copia("Livraria Central", "Campinas, SP", "4.70", TipoCopia.NOVO, EstadoConservacao.NOVO, "59.90", 6, true, false))));

        livroService.cadastrar(new LivroRequest(
                "Quarto de Despejo",
                "Carolina Maria de Jesus",
                "Atica",
                "Papel e Memoria",
                "Belo Horizonte, MG",
                bd("4.88"),
                "9788508196559",
                "Portugues",
                2014,
                "Memorias",
                "Obra essencial em diferentes estados de conservacao para estudantes e colecionadores.",
                imagem("Quarto+de+Despejo", "1e3a8a", "eff6ff"),
                false,
                true,
                false,
                false,
                List.of(
                        copia("Papel e Memoria", "Belo Horizonte, MG", "4.88", TipoCopia.USADO, EstadoConservacao.BOM, "29.90", 2, false, true),
                        copia("Estante da Praca", "Curitiba, PR", "4.60", TipoCopia.NOVO, EstadoConservacao.NOVO, "49.90", 3, false, false))));

        livroService.cadastrar(new LivroRequest(
                "O Hobbit",
                "J. R. R. Tolkien",
                "HarperCollins",
                "Banca do Centro",
                "Recife, PE",
                bd("4.55"),
                "9788595084742",
                "Portugues",
                2019,
                "Fantasia",
                "Edicoes novas e usadas para leitores de aventura e colecionadores.",
                imagem("O+Hobbit", "064e3b", "ecfdf5"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Banca do Centro", "Recife, PE", "4.55", TipoCopia.USADO, EstadoConservacao.ACEITAVEL, "22.90", 1, true, true),
                        copia("Livraria Central", "Campinas, SP", "4.70", TipoCopia.NOVO, EstadoConservacao.NOVO, "54.90", 4, true, false))));

        livroService.cadastrar(new LivroRequest(
                "Clean Code",
                "Robert C. Martin",
                "Alta Books",
                "Tecnica Livros",
                "Florianopolis, SC",
                bd("4.95"),
                "9788576082675",
                "Portugues",
                2009,
                "Tecnologia",
                "Referencia para desenvolvimento de software com estoque voltado a estudantes e profissionais.",
                imagem("Clean+Code", "111827", "f9fafb"),
                false,
                false,
                true,
                false,
                List.of(
                        copia("Tecnica Livros", "Florianopolis, SC", "4.95", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "86.00", 2, false, false),
                        copia("Livraria Central", "Campinas, SP", "4.70", TipoCopia.NOVO, EstadoConservacao.NOVO, "119.90", 5, false, false))));

        livroService.cadastrar(new LivroRequest(
                "Memorias Postumas de Bras Cubas",
                "Machado de Assis",
                "Penguin Companhia",
                "Sebo Riachuelo",
                "Rio de Janeiro, RJ",
                bd("4.78"),
                "9788582850017",
                "Portugues",
                2014,
                "Literatura Brasileira",
                "Edicao de catalogo com preco competitivo e boas opcoes para compra corporativa.",
                imagem("Memorias+Postumas", "581c87", "faf5ff"),
                true,
                true,
                false,
                false,
                List.of(
                        copia("Sebo Riachuelo", "Rio de Janeiro, RJ", "4.78", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "34.90", 3, true, true),
                        copia("Papel e Memoria", "Belo Horizonte, MG", "4.88", TipoCopia.NOVO, EstadoConservacao.NOVO, "47.90", 8, false, false))));
    }

    private LivroCopiaRequest copia(
            String vendedor,
            String cidade,
            String avaliacao,
            TipoCopia tipo,
            EstadoConservacao estado,
            String preco,
            int estoque,
            boolean freteGratis,
            boolean promocao) {
        return new LivroCopiaRequest(
                vendedor,
                cidade,
                bd(avaliacao),
                tipo,
                estado,
                bd(preco),
                estoque,
                cidade,
                freteGratis,
                promocao,
                estoque >= 5,
                true);
    }

    private void criarUsuarioSeNaoExistir(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String nome,
            String email,
            String senha,
            UsuarioRole role) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole(role);
        usuarioRepository.save(usuario);
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private String imagem(String titulo, String fundo, String texto) {
        return "https://placehold.co/600x900/" + fundo + "/" + texto + "?text=" + titulo;
    }
}
