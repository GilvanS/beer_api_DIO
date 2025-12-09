<h2>Digital Innovation: Expert class - Desenvolvimento de testes unitários para validar uma API REST de gerenciamento de estoques de cerveja.</h2>

Nesta live coding, vamos aprender a testar, unitariamente, uma API REST para o gerenciamento de estoques de cerveja. Vamos desenvolver testes unitários para validar o nosso sistema de gerenciamento de estoques de cerveja, e também apresentar os principais conceitos e vantagens de criar testes unitários com JUnit e Mockito. Além disso, vamos também mostrar como desenvolver funcionalidades da nossa API através da prática do TDD.

Durante a sessão, serão abordados os seguintes tópicos:

* Baixar um projeto através do Git para desenolver nossos testes unitários. 
* Apresentação conceitual sobre testes: a pirâmide dos tipos de testes, e também a importância de cada tipo de teste durante o ciclo de desenvolvimento.
* Foco nos testes unitários: mostrar o porque é importante o desenvolvimento destes tipos de testes como parte do ciclo de desenvolvimento de software.
* Principais frameworks para testes unitários em Java: JUnit, Mockito e Hamcrest. 
* Desenvolvimento de testes unitários para validação de funcionalides básicas: criação, listagem, consulta por nome e exclusão de cervejas.
* TDD: apresentação e exemplo prático em 2 funcionaliades importantes: incremento e decremento do número de cervejas no estoque.

---

## Evolução do Projeto: Testes de Integração com Java 21 e RestAssured

O projeto original foi aprimorado com a criação de uma suíte de **Testes de Integração**, que valida o comportamento da API de ponta a ponta, simulando requisições HTTP reais e garantindo que todas as camadas (Controller, Service, Repository) funcionem corretamente em conjunto.

### Principais Melhorias e Tecnologias Utilizadas:

*   **Java 21:** O projeto foi atualizado para rodar com a versão 21 do Java, aproveitando as melhorias de performance e sintaxe da LTS mais recente.

*   **RestAssured:** Em vez de usar apenas `MockMvc` para testes de unidade do Controller, foi adotado o **RestAssured** para criar testes de integração robustos e legíveis. Isso permite validar o comportamento real da API, incluindo status codes HTTP, headers e o corpo das respostas JSON.

*   **JUnit 5 (`@TestInstance` e `@Order`):** A suíte de testes de integração foi estruturada para testar o ciclo de vida completo de um recurso (`Beer`). As anotações `@TestInstance(Lifecycle.PER_CLASS)` e `@Order` foram utilizadas para garantir uma execução sequencial e lógica, facilitando a depuração e o entendimento do fluxo da API.

*   **`BeerManager` com `ThreadLocal`:** Para gerenciar o estado entre os testes ordenados (como o ID da cerveja criada), foi implementada a classe `BeerManager`. Ela utiliza `ThreadLocal` para garantir que o estado de um teste não interfira em outro, uma abordagem segura para paralelismo.

### Correções de Bugs Implementadas:

Durante a criação dos testes de integração, foram identificados e corrigidos dois bugs críticos na API original:

1.  **Funcionalidade de Decremento Inexistente (BUG-001):**
    *   **Problema:** A API possuía um endpoint para incrementar (`/increment`), mas não havia um endpoint correspondente para decrementar o estoque. Os testes de integração falharam com o erro `404 Not Found` ao tentar acessar a rota `/decrement`.
    *   **Solução:** Foi implementado o método `decrement` no `BeerService` e exposto um novo endpoint `PATCH /api/v1/beers/{id}/decrement` no `BeerController`, completando a funcionalidade de gerenciamento de estoque.

2.  **Validação Incorreta no Incremento de Estoque:**
    *   **Problema:** A lógica original no `BeerService` para o incremento de estoque não validava corretamente se a soma da quantidade atual com o valor a ser incrementado excederia a capacidade máxima.
    *   **Solução:** A validação foi corrigida para garantir que a exceção `BeerStockExceededException` seja lançada antes de tentar salvar um valor inválido no banco de dados.

---

### Como Executar o Projeto

Para executar o projeto no terminal, digite o seguinte comando:

```shell script
mvn spring-boot:run 
```

Para executar a suíte de **testes de integração** desenvolvida, utilize o comando:

```shell script
# Executa todos os testes do projeto
mvn clean test

# Para rodar apenas a suíte de integração
mvn clean test -Dtest=BeerTestController
```

Após executar o comando acima, basta apenas abrir o seguinte endereço e visualizar a execução do projeto:

```
http://localhost:8080/api/v1/beers
```

### Pré-requisitos Atualizados:

*   **Java 21 ou versões superiores.**
*   Maven 3.6.3 ou versões superiores.
*   Intellj IDEA Community Edition ou sua IDE favorita.
*   Controle de versão GIT instalado na sua máquina.
*   Muita vontade de aprender e compartilhar conhecimento :)

### Links e Referências Adicionais:

*   [SDKMan! para gerenciamento e instalação do Java e Maven](https://sdkman.io/)
*   [Referência do Intellij IDEA Community, para download](https://www.jetbrains.com/idea/download)
*   [Palheta de atalhos de comandos do Intellij](https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf)
*   [Site oficial do Spring](https://spring.io/)
*   [Site oficial do RestAssured](https://rest-assured.io/)
*   [Site oficial JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
*   [Site oficial Mockito](https://site.mockito.org/)
*   [Site oficial Hamcrest](http://hamcrest.org/JavaHamcrest/)
*   [Referências - testes em geral com o Spring Boot](https://www.baeldung.com/spring-boot-testing)
*   [Referência para o padrão arquitetural REST](https://restfulapi.net/)
*   [Referência pirâmide de testes - Martin Fowler](https://martinfowler.com/articles/practical-test-pyramid.html#TheImportanceOftestAutomation)

[Neste link](https://drive.google.com/file/d/1KPh19mvyKirorOI-UsEYHKkmZpet3Ks6/view?usp=sharing), seguem os slides apresentados como o roteiro utilizado para o desenvolvimento do projeto da nossa sessão.
