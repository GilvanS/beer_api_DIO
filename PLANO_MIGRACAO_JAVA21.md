# Plano de Atualização: Java 21 & Spring Boot 3.x

Este plano descreve os passos para modernizar o projeto `beer_api` atualizando para Java 21 e Spring Boot 3.x. Isso garante suporte a longo prazo, melhorias de performance e atualizações de segurança.

## Revisão do Usuário Necessária
> [!IMPORTANT]
> **Mudanças Quebrantes (Breaking Changes)**:
> *   **Mudança de Namespace**: O Spring Boot 3 exige a migração de `javax.*` para `jakarta.*` (Jakarta EE 10). Isso afeta JPA, Validação e Servlets.
> *   **Swagger/OpenAPI**: O `springfox-swagger2` é incompatível com Spring Boot 3. Ele será substituído pelo `springdoc-openapi`.
> *   **Ambiente**: O usuário confirmou que já possui o JDK 21 instalado.

## Mudanças Propostas

### Configuração (pom.xml)
*   **Versão do Java**: Atualizar `<java.version>` para `21`.
*   **Spring Boot**: Atualizar parent para `3.2.5` (ou a versão estável 3.x mais recente).
*   **Dependências**:
    *   Atualizar `lombok` para a versão mais recente.
    *   Atualizar `mapstruct` para `1.5.5.Final` (ou mais recente).
    *   **Remover**: `springfox-swagger2` e `springfox-swagger-ui`.
    *   **Adicionar**: `springdoc-openapi-starter-webmvc-ui`.
    *   Atualizar banco de dados `h2` se necessário.

### Migração do Código
#### [MODIFICAR] Código Fonte
*   **Busca e Substituição Global**:
    *   `javax.persistence` -> `jakarta.persistence`
    *   `javax.validation` -> `jakarta.validation`
    *   `javax.servlet` -> `jakarta.servlet`
*   **Swagger para OpenAPI**:
    *   Substituir `@Api`, `@ApiOperation` por `@Tag`, `@Operation`.
    *   Atualizar classes de configuração para usar o bean `OpenAPI` em vez de `Docket`.

### Testes
*   Garantir que os testes rodem com JUnit 5.
*   Verificar o uso de `MockMvc` com o novo contexto de teste do Spring Boot.

## Plano de Verificação
### Testes Automatizados
*   Rodar `mvn clean test` para garantir que todos os testes unitários e de integração passem.

### Verificação Manual
*   Iniciar a aplicação: `mvn spring-boot:run`.
*   Acessar o Swagger UI (nova URL: `/swagger-ui/index.html` ou redirecionamento `/swagger-ui.html`) para verificar a documentação da API.
*   Testar endpoints básicos (Criar, Listar) para garantir conectividade com banco de dados e lógica de validação.
