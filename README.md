# Gestão Project Finance

Aplicação Java com Spring Boot que apresenta um painel financeiro inspirado na planilha fornecida.

## Tecnologias
- Java 17
- Spring Boot 3.4.2
- Maven
- HTML, CSS, JavaScript e jQuery

## Executando o projeto
1. Certifique-se de ter o Java 17 instalado.
2. Na raiz do projeto execute:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Acesse o painel em [http://localhost:8080](http://localhost:8080).

## Estrutura
- `src/main/java`: código Java do backend e serviços.
- `src/main/resources/static`: arquivos estáticos (HTML, CSS e JavaScript).

O endpoint `/api/finance/summary` retorna os dados utilizados pelo dashboard.
