# Estrategia para imagens

Para este projeto demonstrativo, a melhor abordagem e armazenar os arquivos de imagem localmente no projeto/servidor e salvar no banco apenas o caminho, nome, tipo, tamanho e metadados.

## Banco com BYTEA/LONGBLOB

Guardar imagens diretamente no banco pode simplificar backup transacional, mas costuma deixar o banco mais pesado, aumentar trafego nas consultas e dificultar cache/entrega estatica. Para um sebo virtual de estudo, isso adiciona complexidade cedo demais.

## Arquivos locais + metadados no banco

E a opcao mais simples e didatica para este projeto:

- imagens em uma pasta local, como `uploads/livros/`;
- tabela `livros` salva `imagem_capa`, `imagem_alt`, `mime_type` e tamanho;
- Spring Boot pode servir os arquivos de forma controlada ou expor URLs publicas;
- no futuro, a mesma interface pode migrar para S3, Cloudinary ou outro storage.

Recomendacao inicial: usar arquivos locais e salvar no banco apenas caminho/metadados.
