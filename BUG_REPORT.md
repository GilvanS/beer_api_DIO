# Relatório de Bug: Funcionalidade de Decremento de Estoque Inexistente

**ID do Bug:** BUG-001
**Data:** 2024-10-27
**Severidade:** Alta
**Projeto:** beer_api_DIO

---

### **Resumo**

A API `beer_api` não possui um endpoint para decrementar a quantidade de estoque de uma cerveja. Testes de integração escritos para validar esta funcionalidade falham com o erro `404 Not Found`, indicando que a rota da API não existe.

---

### **Descrição Detalhada**

Durante a criação de uma suíte de testes de integração completa para a `Beer API`, foi seguida a premissa de que um sistema de controle de estoque deve permitir, no mínimo, as operações de **incremento** e **decremento** de itens.

Os testes de integração (realizados com RestAssured na classe `BeerTestController.java`) foram escritos para validar o ciclo de vida completo de uma cerveja, incluindo:
1.  Criação (POST)
2.  Leitura (GET)
3.  Incremento de estoque (PATCH .../increment)
4.  **Decremento de estoque (PATCH .../decrement)**
5.  Deleção (DELETE)

Os testes para o endpoint `PATCH /api/v1/beers/{id}/decrement` falham sistematicamente com o status **`404 Not Found`**. Isso significa que o servidor da API não reconhece essa URL, pois não há um método no `BeerController` mapeado para tratar requisições para este caminho.

---

### **Análise da Causa Raiz e Justificativa para a Correção na API**

O erro **não está nos testes**. Pelo contrário, os testes estão funcionando corretamente ao revelarem uma falha na especificação da API.

1.  **O Papel do Teste de Integração:** O teste de integração atua como um "cliente" da API, validando se ela cumpre os requisitos funcionais esperados. Um desses requisitos, para uma API de estoque, é a capacidade de decrementar a quantidade de um produto.

2.  **O Problema Real:** A falha (`404 Not Found`) não significa que a *cerveja* não foi encontrada, mas sim que a **própria funcionalidade (a "rua" `/decrement`) não existe na API**. A classe `BeerController` possui o endpoint para `/increment`, mas o endpoint para `/decrement` nunca foi implementado.

3.  **Por que a API Deve ser Alterada:** A correção **deve** ser feita no código-fonte da API (`BeerController` e `BeerService`) porque a funcionalidade está ausente. Alterar os testes para que eles "passem" (por exemplo, removendo a validação de decremento) seria o mesmo que ignorar o problema e mascarar o bug. Isso resultaria na entrega de um software incompleto, que não atende a um requisito de negócio básico para o qual foi projetado.

O teste é a ferramenta que aponta a doença; a cura deve ser aplicada no paciente (a API).

---

### **Solução Proposta**

Para corrigir o bug, é necessário implementar a funcionalidade de decremento na API:

1.  **Adicionar a lógica de negócio no `BeerService.java`:**
    ```java
    public BeerDTO decrement(Long id, int quantityToDecrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToDecrementStock = verifyIfExists(id);
        int newQuantity = beerToDecrementStock.getQuantity() - quantityToDecrement;
        if (newQuantity >= 0) {
            beerToDecrementStock.setQuantity(newQuantity);
            Beer decrementedBeerStock = beerRepository.save(beerToDecrementStock);
            return beerMapper.toDTO(decrementedBeerStock);
        }
        throw new BeerStockExceededException(id, quantityToDecrement);
    }
    ```

2.  **Expor a funcionalidade via um novo endpoint no `BeerController.java`:**
    ```java
    @PatchMapping("/{id}/decrement")
    public BeerDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockExceededException {
        return beerService.decrement(id, quantityDTO.getQuantity());
    }
    ```

Após a implementação dessas alterações na API, os testes de integração para o decremento (Ordens 6 e 7) passarão com sucesso, confirmando que o bug foi corrigido.
