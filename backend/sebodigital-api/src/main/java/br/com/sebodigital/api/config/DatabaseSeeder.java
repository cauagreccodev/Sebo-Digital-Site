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
            popularLivrosComplementares(livroRepository, livroService);
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

    private void popularLivrosComplementares(LivroRepository livroRepository, LivroService livroService) {
        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "A Hora da Estrela",
                "Clarice Lispector",
                "Rocco",
                "Sebo Riachuelo",
                "Rio de Janeiro, RJ",
                bd("4.78"),
                "9788532508126",
                "Portugues",
                1998,
                "Literatura Brasileira",
                "Romance breve e essencial em edicoes de bolso para leitores e estudantes.",
                imagem("A+Hora+da+Estrela", "7f1d1d", "fef2f2"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Sebo Riachuelo", "Rio de Janeiro, RJ", "4.78", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "28.90", 4, true, true),
                        copia("Livraria Central", "Campinas, SP", "4.70", TipoCopia.NOVO, EstadoConservacao.NOVO, "44.90", 7, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Vidas Secas",
                "Graciliano Ramos",
                "Record",
                "Estante da Praca",
                "Curitiba, PR",
                bd("4.60"),
                "9788501067340",
                "Portugues",
                2013,
                "Literatura Brasileira",
                "Classico do modernismo brasileiro com opcoes economicas para leitura escolar.",
                imagem("Vidas+Secas", "78350f", "fff7ed"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Estante da Praca", "Curitiba, PR", "4.60", TipoCopia.USADO, EstadoConservacao.BOM, "19.90", 5, true, true),
                        copia("Papel e Memoria", "Belo Horizonte, MG", "4.88", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "26.90", 2, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "O Alienista",
                "Machado de Assis",
                "Principis",
                "Banca Aurora",
                "Curitiba, PR",
                bd("4.73"),
                "9788594318788",
                "Portugues",
                2020,
                "Literatura Brasileira",
                "Novela satirica em edicoes acessiveis, boa para colecoes escolares.",
                imagem("O+Alienista", "4c1d95", "f5f3ff"),
                false,
                true,
                false,
                false,
                List.of(
                        copia("Banca Aurora", "Curitiba, PR", "4.73", TipoCopia.NOVO, EstadoConservacao.NOVO, "16.90", 10, false, true),
                        copia("Sebo Aurora", "Sao Paulo, SP", "4.85", TipoCopia.USADO, EstadoConservacao.BOM, "11.90", 3, false, true))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "1984",
                "George Orwell",
                "Companhia das Letras",
                "Livros da Ladeira",
                "Salvador, BA",
                bd("4.92"),
                "9788535914849",
                "Portugues",
                2009,
                "Ficcao",
                "Distopia moderna com boa saida entre leitores de ficcao politica.",
                imagem("1984", "111827", "f9fafb"),
                true,
                false,
                true,
                false,
                List.of(
                        copia("Livros da Ladeira", "Salvador, BA", "4.92", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "36.90", 2, true, false),
                        copia("Livraria Central", "Campinas, SP", "4.70", TipoCopia.NOVO, EstadoConservacao.NOVO, "57.90", 6, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Fahrenheit 451",
                "Ray Bradbury",
                "Biblioteca Azul",
                "Ponto do Livro",
                "Goiania, GO",
                bd("4.66"),
                "9788525052247",
                "Portugues",
                2012,
                "Ficcao Cientifica",
                "Classico de ficcao cientifica em exemplares novos e usados.",
                imagem("Fahrenheit+451", "991b1b", "fff1f2"),
                false,
                true,
                false,
                false,
                List.of(
                        copia("Ponto do Livro", "Goiania, GO", "4.66", TipoCopia.USADO, EstadoConservacao.BOM, "31.90", 2, false, true),
                        copia("Tecnica Livros", "Florianopolis, SC", "4.95", TipoCopia.NOVO, EstadoConservacao.NOVO, "52.90", 3, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "A Revolucao dos Bichos",
                "George Orwell",
                "Companhia das Letras",
                "Sebo Aurora",
                "Sao Paulo, SP",
                bd("4.85"),
                "9788535909555",
                "Portugues",
                2007,
                "Ficcao",
                "Fabula politica muito procurada, com ofertas de baixo preco.",
                imagem("A+Revolucao+dos+Bichos", "14532d", "f0fdf4"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Sebo Aurora", "Sao Paulo, SP", "4.85", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "24.50", 4, true, true),
                        copia("Banca do Centro", "Recife, PE", "4.55", TipoCopia.USADO, EstadoConservacao.BOM, "18.90", 2, false, true))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "A Menina que Roubava Livros",
                "Markus Zusak",
                "Intrinseca",
                "Casa das Letras",
                "Florianopolis, SC",
                bd("4.82"),
                "9788598078175",
                "Portugues",
                2007,
                "Romance",
                "Romance historico popular com estoque variado para presente e leitura.",
                imagem("A+Menina+que+Roubava+Livros", "312e81", "eef2ff"),
                true,
                false,
                true,
                false,
                List.of(
                        copia("Casa das Letras", "Florianopolis, SC", "4.82", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "39.90", 3, true, false),
                        copia("Estante da Praca", "Curitiba, PR", "4.60", TipoCopia.USADO, EstadoConservacao.BOM, "32.90", 2, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "O Pequeno Principe",
                "Antoine de Saint-Exupery",
                "Agir",
                "Leitura de Bolso",
                "Recife, PE",
                bd("4.80"),
                "9788522005230",
                "Portugues",
                2015,
                "Infantojuvenil",
                "Classico afetivo com edicoes novas e usadas para diferentes idades.",
                imagem("O+Pequeno+Principe", "854d0e", "fffbeb"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Leitura de Bolso", "Recife, PE", "4.80", TipoCopia.NOVO, EstadoConservacao.NOVO, "24.90", 12, true, true),
                        copia("Banca Aurora", "Curitiba, PR", "4.73", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "18.90", 3, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "A Bolsa Amarela",
                "Lygia Bojunga",
                "Casa Lygia Bojunga",
                "Leitura de Bolso",
                "Recife, PE",
                bd("4.80"),
                "9788589020109",
                "Portugues",
                2021,
                "Infantojuvenil",
                "Literatura juvenil brasileira com boa disponibilidade para escolas.",
                imagem("A+Bolsa+Amarela", "6d28d9", "faf5ff"),
                false,
                false,
                false,
                true,
                List.of(
                        copia("Leitura de Bolso", "Recife, PE", "4.80", TipoCopia.NOVO, EstadoConservacao.NOVO, "35.90", 8, false, false),
                        copia("Ponto do Livro", "Goiania, GO", "4.66", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "27.90", 2, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Java para Iniciantes",
                "Herbert Schildt",
                "Bookman",
                "Livros e Codigo",
                "Belo Horizonte, MG",
                bd("4.71"),
                "9788582603361",
                "Portugues",
                2015,
                "Tecnologia",
                "Livro tecnico para introducao a Java, ideal para estudantes de programacao.",
                imagem("Java+para+Iniciantes", "1e3a8a", "eff6ff"),
                false,
                true,
                false,
                false,
                List.of(
                        copia("Livros e Codigo", "Belo Horizonte, MG", "4.71", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "68.00", 2, false, true),
                        copia("Tecnica Livros", "Florianopolis, SC", "4.95", TipoCopia.NOVO, EstadoConservacao.NOVO, "99.90", 4, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Domain-Driven Design",
                "Eric Evans",
                "Alta Books",
                "Tecnica Livros",
                "Florianopolis, SC",
                bd("4.95"),
                "9788550800653",
                "Portugues",
                2016,
                "Tecnologia",
                "Referencia de arquitetura de software para acervos tecnicos e profissionais.",
                imagem("Domain-Driven+Design", "0f172a", "f8fafc"),
                false,
                false,
                true,
                false,
                List.of(
                        copia("Tecnica Livros", "Florianopolis, SC", "4.95", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "139.90", 1, false, false),
                        copia("Livros e Codigo", "Belo Horizonte, MG", "4.71", TipoCopia.NOVO, EstadoConservacao.NOVO, "189.90", 3, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Refactoring",
                "Martin Fowler",
                "Novatec",
                "Tecnica Livros",
                "Florianopolis, SC",
                bd("4.95"),
                "9788575227244",
                "Portugues",
                2020,
                "Tecnologia",
                "Obra de melhoria de codigo para compor uma prateleira tecnica atual.",
                imagem("Refactoring", "164e63", "ecfeff"),
                true,
                false,
                false,
                true,
                List.of(
                        copia("Tecnica Livros", "Florianopolis, SC", "4.95", TipoCopia.NOVO, EstadoConservacao.NOVO, "149.90", 5, true, false),
                        copia("Livros e Codigo", "Belo Horizonte, MG", "4.71", TipoCopia.USADO, EstadoConservacao.BOM, "98.00", 1, false, true))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Historia do Brasil",
                "Boris Fausto",
                "Edusp",
                "Sebo Republica",
                "Porto Alegre, RS",
                bd("4.63"),
                "9788531402401",
                "Portugues",
                2012,
                "Historia",
                "Panorama historico em exemplar completo para consulta e estudos.",
                imagem("Historia+do+Brasil", "a16207", "fefce8"),
                false,
                true,
                false,
                false,
                List.of(
                        copia("Sebo Republica", "Porto Alegre, RS", "4.63", TipoCopia.USADO, EstadoConservacao.BOM, "54.90", 2, false, true),
                        copia("Papel e Memoria", "Belo Horizonte, MG", "4.88", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "61.90", 1, true, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Design do Dia a Dia",
                "Donald Norman",
                "Rocco",
                "Prateleira Criativa",
                "Rio de Janeiro, RJ",
                bd("4.68"),
                "9788532520838",
                "Portugues",
                2018,
                "Arte e Design",
                "Livro de design e usabilidade com procura por estudantes de produto digital.",
                imagem("Design+do+Dia+a+Dia", "9a3412", "fff7ed"),
                true,
                false,
                false,
                false,
                List.of(
                        copia("Prateleira Criativa", "Rio de Janeiro, RJ", "4.68", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "59.90", 2, true, false),
                        copia("Casa das Letras", "Florianopolis, SC", "4.82", TipoCopia.NOVO, EstadoConservacao.NOVO, "84.90", 4, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Arte Moderna",
                "Giulio Carlo Argan",
                "Companhia das Letras",
                "Prateleira Criativa",
                "Rio de Janeiro, RJ",
                bd("4.68"),
                "9788535904260",
                "Portugues",
                1992,
                "Arte e Design",
                "Volume de referencia para arte, cultura e historia em acervo de sebo.",
                imagem("Arte+Moderna", "1d4ed8", "eff6ff"),
                false,
                false,
                false,
                false,
                List.of(
                        copia("Prateleira Criativa", "Rio de Janeiro, RJ", "4.68", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "76.40", 1, false, false),
                        copia("Sebo Republica", "Porto Alegre, RS", "4.63", TipoCopia.USADO, EstadoConservacao.BOM, "69.90", 1, false, true))));
    }

    private void cadastrarSeNaoExistir(
            LivroRepository livroRepository,
            LivroService livroService,
            LivroRequest request) {
        if (livroRepository.existsByIsbnIgnoreCase(request.isbn())) {
            return;
        }

        livroService.cadastrar(request);
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
