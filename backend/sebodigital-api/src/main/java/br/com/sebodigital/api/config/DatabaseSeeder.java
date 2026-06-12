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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
            atualizarImagensLivros(livroRepository);
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
                "Harry Potter e a Pedra Filosofal",
                "J. K. Rowling",
                "Rocco",
                "Sebo Coruja",
                "Sao Paulo, SP",
                bd("4.91"),
                "9788532511010",
                "Portugues",
                2000,
                "Fantasia",
                "Primeiro volume da saga Harry Potter, com edicoes para colecionadores e leitura infantojuvenil.",
                imagem("Harry+Potter+Pedra+Filosofal", "1e1b4b", "fef3c7"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Sebo Coruja", "Sao Paulo, SP", "4.91", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "42.90", 4, true, true),
                        copia("Livraria Travessa do Livro", "Rio de Janeiro, RJ", "4.74", TipoCopia.NOVO, EstadoConservacao.NOVO, "69.90", 5, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Harry Potter e a Camara Secreta",
                "J. K. Rowling",
                "Rocco",
                "Sebo Coruja",
                "Sao Paulo, SP",
                bd("4.88"),
                "9788532511669",
                "Portugues",
                2000,
                "Fantasia",
                "Segundo volume da saga Harry Potter, bastante procurado para completar colecoes.",
                imagem("Harry+Potter+Camara+Secreta", "0f766e", "ecfeff"),
                false,
                true,
                true,
                false,
                List.of(
                        copia("Sebo Coruja", "Sao Paulo, SP", "4.91", TipoCopia.USADO, EstadoConservacao.BOM, "39.90", 3, false, true),
                        copia("Mundo dos Livros", "Curitiba, PR", "4.79", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "48.90", 2, true, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "Harry Potter e o Prisioneiro de Azkaban",
                "J. K. Rowling",
                "Rocco",
                "Mundo dos Livros",
                "Curitiba, PR",
                bd("4.79"),
                "9788532512062",
                "Portugues",
                2000,
                "Fantasia",
                "Terceiro livro da saga, com exemplares usados em bom estado para leitores jovens.",
                imagem("Harry+Potter+Prisioneiro+Azkaban", "581c87", "faf5ff"),
                true,
                false,
                true,
                false,
                List.of(
                        copia("Mundo dos Livros", "Curitiba, PR", "4.79", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "46.90", 4, true, false),
                        copia("Banca Aurora", "Curitiba, PR", "4.73", TipoCopia.USADO, EstadoConservacao.BOM, "34.90", 2, false, true))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "O Senhor dos Aneis: A Sociedade do Anel",
                "J. R. R. Tolkien",
                "HarperCollins",
                "Toca dos Livros",
                "Porto Alegre, RS",
                bd("4.86"),
                "9788595084759",
                "Portugues",
                2019,
                "Fantasia",
                "Primeiro volume de O Senhor dos Aneis em edicao moderna para colecoes de fantasia.",
                imagem("Sociedade+do+Anel", "14532d", "f0fdf4"),
                true,
                false,
                true,
                false,
                List.of(
                        copia("Toca dos Livros", "Porto Alegre, RS", "4.86", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "58.90", 2, true, false),
                        copia("Livros da Ladeira", "Salvador, BA", "4.92", TipoCopia.NOVO, EstadoConservacao.NOVO, "79.90", 4, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "O Senhor dos Aneis: As Duas Torres",
                "J. R. R. Tolkien",
                "HarperCollins",
                "Toca dos Livros",
                "Porto Alegre, RS",
                bd("4.86"),
                "9788595084766",
                "Portugues",
                2019,
                "Fantasia",
                "Segundo volume da trilogia, indicado para montar boxes e colecoes de fantasia classica.",
                imagem("As+Duas+Torres", "166534", "fff7ed"),
                false,
                true,
                true,
                false,
                List.of(
                        copia("Toca dos Livros", "Porto Alegre, RS", "4.86", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "56.90", 2, false, true),
                        copia("Sebo Republica", "Porto Alegre, RS", "4.63", TipoCopia.USADO, EstadoConservacao.BOM, "44.90", 1, false, false))));

        cadastrarSeNaoExistir(livroRepository, livroService, new LivroRequest(
                "O Senhor dos Aneis: O Retorno do Rei",
                "J. R. R. Tolkien",
                "HarperCollins",
                "Livros da Ladeira",
                "Salvador, BA",
                bd("4.92"),
                "9788595084773",
                "Portugues",
                2019,
                "Fantasia",
                "Fechamento da trilogia de Tolkien com boa procura em ofertas de colecao.",
                imagem("Retorno+do+Rei", "422006", "fefce8"),
                true,
                true,
                true,
                false,
                List.of(
                        copia("Livros da Ladeira", "Salvador, BA", "4.92", TipoCopia.USADO, EstadoConservacao.MUITO_BOM, "59.90", 3, true, true),
                        copia("Toca dos Livros", "Porto Alegre, RS", "4.86", TipoCopia.NOVO, EstadoConservacao.NOVO, "82.90", 4, false, false))));

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

    private void atualizarImagensLivros(LivroRepository livroRepository) {
        dadosImagensLivros().forEach((isbn, imagens) ->
                livroRepository.findByIsbnIgnoreCase(isbn).ifPresent(livro -> {
                    livro.setImagemUrl(imagens.capaUrl());
                    livro.setAutorImagemUrl(imagens.autorImagemUrl());
                    livroRepository.save(livro);
                }));
    }

    private Map<String, LivroImagens> dadosImagensLivros() {
        return Map.ofEntries(
                imagens("9788508040845", 647501, "Machado de Assis"),
                imagens("9788594318603", 8176059, "Aluisio Azevedo"),
                imagens("9788535914061", 4178919, "Jorge Amado"),
                imagens("9786580309313", 12369648, "Itamar Vieira Junior"),
                imagens("9788508196559", 295782, "Carolina Maria de Jesus"),
                imagens("9788595084742", 14627509, "J. R. R. Tolkien"),
                imagens("9788576082675", 8065615, "Robert C. Martin"),
                imagens("9788582850017", 123152, "Machado de Assis"),
                imagens("9788532508126", 650866, "Clarice Lispector"),
                imagens("9788501067340", 12369687, "Graciliano Ramos"),
                imagens("9788594318788", 647504, "Machado de Assis"),
                imagens("9788535914849", 9267242, "George Orwell"),
                imagens("9788525052247", 12993656, "Ray Bradbury"),
                imagens("9788535909555", 15200524, "George Orwell"),
                imagens("9788598078175", 8153054, "Markus Zusak"),
                imagens("9788522005230", 10708272, "Antoine de Saint-Exupery"),
                imagens("9788589020109", 13470796, "Lygia Bojunga"),
                imagens("9788532511010", 15155833, "J. K. Rowling"),
                imagens("9788532511669", 15158664, "J. K. Rowling"),
                imagens("9788532512062", 10580435, "J. K. Rowling"),
                imagens("9788595084759", 14627060, "J. R. R. Tolkien"),
                imagens("9788595084766", 14627564, "J. R. R. Tolkien"),
                imagens("9788595084773", 14627062, "J. R. R. Tolkien"),
                imagens("9788582603361", 62098, "Herbert Schildt"),
                imagens("9788550800653", 5548424, "Eric Evans"),
                imagens("9788575227244", 7087623, "Martin Fowler"),
                imagens("9788531402401", 8171450, "Boris Fausto"),
                imagens("9788532520838", 10007224, "Donald Norman"),
                imagens("9788535904260", 12370709, "Giulio Carlo Argan"));
    }

    private Map.Entry<String, LivroImagens> imagens(String isbn, int capaId, String autor) {
        return Map.entry(isbn, new LivroImagens(capaPngOpenLibrary(capaId), retratoAutor(autor)));
    }

    private String capaPngOpenLibrary(int capaId) {
        String origem = "https://covers.openlibrary.org/b/id/" + capaId + "-L.jpg?default=false";
        return imagemPng(origem, 800, 1200, "cover");
    }

    private String imagemPng(String origem, int largura, int altura, String fit) {
        return "https://images.weserv.nl/?url="
                + URLEncoder.encode(origem, StandardCharsets.UTF_8)
                + "&w=" + largura
                + "&h=" + altura
                + "&fit=" + fit
                + "&output=png";
    }

    private String retratoAutor(String autor) {
        String origem = switch (autor) {
            case "Machado de Assis" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Machado_de_Assis_by_Marc_Ferrez.jpg/500px-Machado_de_Assis_by_Marc_Ferrez.jpg";
            case "Aluisio Azevedo" -> "https://upload.wikimedia.org/wikipedia/commons/4/4e/Aluisio_Azevedo.jpg";
            case "Jorge Amado" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Jorge_Amado%2C_Headshot_2%2C_1988_%28cropped%29.png/500px-Jorge_Amado%2C_Headshot_2%2C_1988_%28cropped%29.png";
            case "Itamar Vieira Junior" -> "https://upload.wikimedia.org/wikipedia/pt/1/1d/Fotografia_de_Itamar_Vieira_Junior.webp";
            case "Carolina Maria de Jesus" -> "https://upload.wikimedia.org/wikipedia/commons/5/53/Carolina_Maria_de_Jesus%2C_1960_cr.jpg";
            case "J. R. R. Tolkien" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/J._R._R._Tolkien%2C_ca._1925.jpg/500px-J._R._R._Tolkien%2C_ca._1925.jpg";
            case "J. K. Rowling" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/J._K._Rowling_2010.jpg/500px-J._K._Rowling_2010.jpg";
            case "Robert C. Martin" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Robert_C._Martin_surrounded_by_computers_%28cropped%29.jpg/500px-Robert_C._Martin_surrounded_by_computers_%28cropped%29.jpg";
            case "Clarice Lispector" -> "https://upload.wikimedia.org/wikipedia/commons/7/7c/%281920-1977%29_Clarice_Lispector_6zxkp_please_credit%28palette.fm%29_%28cropped%29.png";
            case "Graciliano Ramos" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Graciliano_Ramos%2C_1940.jpg/500px-Graciliano_Ramos%2C_1940.jpg";
            case "George Orwell" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/George_Orwell_press_photo.jpg/500px-George_Orwell_press_photo.jpg";
            case "Ray Bradbury" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Ray_Bradbury_%281975%29_-cropped-.jpg/500px-Ray_Bradbury_%281975%29_-cropped-.jpg";
            case "Markus Zusak" -> "https://upload.wikimedia.org/wikipedia/commons/8/8c/Markus_Zusak_at_the_Book_Thief_Interview_%28cropped%29.jpg";
            case "Antoine de Saint-Exupery" -> "https://upload.wikimedia.org/wikipedia/commons/6/68/Antoine_de_Saint-Exup%C3%A9ry.jpg";
            case "Lygia Bojunga" -> "https://upload.wikimedia.org/wikipedia/commons/0/04/Lygia_bojunga.jpg";
            case "Martin Fowler" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e2/Webysther_20150414193208_-_Martin_Fowler.jpg/500px-Webysther_20150414193208_-_Martin_Fowler.jpg";
            case "Boris Fausto" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Boris_Fausto_2015.jpg/500px-Boris_Fausto_2015.jpg";
            case "Donald Norman" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Donald_Norman_at_AWF05.jpg/500px-Donald_Norman_at_AWF05.jpg";
            case "Giulio Carlo Argan" -> "https://upload.wikimedia.org/wikipedia/commons/7/71/Argan_politico.jpg";
            default -> avatarAutor(autor);
        };

        return origem.contains("ui-avatars.com") ? origem : imagemPng(origem, 500, 500, "cover");
    }

    private String avatarAutor(String autor) {
        return "https://ui-avatars.com/api/?name="
                + autor.replace(" ", "+")
                + "&size=256&background=286c67&color=ffffff&bold=true&format=png";
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

    private record LivroImagens(String capaUrl, String autorImagemUrl) {
    }
}
