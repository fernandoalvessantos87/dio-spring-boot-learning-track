# Budgeting API - Desafio Spring AI (DIO Bootcamp Santander 2026)

## Sobre o Projeto

Esta é a minha evolução do projeto final do módulo Spring AI da trilha de aprendizado
Spring Boot da DIO. O projeto original é uma API de controle financeiro (orçamento)
que usa Inteligência Artificial para interpretar comandos de voz, transformá-los em
transações financeiras e responder em áudio.

A base do código (arquitetura, camadas, integração com IA) foi construída seguindo
as aulas e o repositório oficial do desafio. A partir dessa base, implementei melhorias
próprias, descritas abaixo.

## O Que o Projeto Faz

Fluxo principal da API:
1. Recebe uma requisição (via REST ou, no fluxo completo com IA, via áudio).
2. Processa a intenção (criar uma transação ou consultar transações por categoria).
3. Valida os dados de entrada antes de persistir.
4. Salva ou consulta as transações em um banco de dados.
5. Retorna a resposta ao usuário.

## Minha Evolução: Validação de Transações + Testes Automatizados

Escolhi evoluir o projeto adicionando **regras de validação de negócio** que não
existiam na versão original, além de **testes automatizados** para comprovar que
essas regras funcionam.

### O que foi implementado:

- **Validação no domínio (`Transaction.java`)**: uma transação não pode ser criada
  com valor zero ou negativo, nem com descrição vazia/nula. Essa validação fica na
  camada de domínio, garantindo que a regra vale independente de a transação vir
  da API REST tradicional ou do fluxo de IA por voz.
- **Tratamento de erro amigável (`GlobalExceptionHandler.java`)**: antes, uma
  transação inválida geraria um erro genérico (HTTP 500). Agora, a API retorna um
  erro claro (HTTP 400) com a mensagem explicando o motivo, por exemplo:
```json
  { "erro": "O valor da transação deve ser maior que zero" }
```
- **Testes automatizados (`TransactionTest.java`)**: 5 testes unitários cobrindo
  criação válida, valor zero, valor negativo, descrição vazia e descrição nula.

### Por que essa escolha:

Ao estudar o código original, percebi que a entidade `Transaction` aceitava
qualquer valor, incluindo negativos — o que não faz sentido para um sistema
financeiro real. Corrigir isso me pareceu uma melhoria pequena, mas que demonstra
entendimento de regras de negócio e do porquê de colocá-las na camada de domínio
(e não espalhadas pela API).

## Tecnologias Usadas

- Java 21
- Spring Boot 4
- Spring AI (ChatClient, Tool Calling, Transcription e Text-to-Speech)
- Spring Data JPA
- H2 Database (banco em memória, usado para rodar o projeto localmente sem Docker)
- Gradle
- JUnit 5

## Uma Decisão Importante: MySQL → H2

O projeto original usa MySQL via Docker Compose. Como não tinha o Docker
disponível no meu ambiente, troquei o banco para o **H2**, que roda em memória
dentro da própria aplicação. Isso não muda nenhuma regra de negócio — só a forma
como os dados são persistidos — e permite rodar o projeto em qualquer máquina com
Java instalado, sem dependências externas.

## Sobre a Integração com IA (OpenAI)

O fluxo de IA (transcrição de áudio, Tool Calling e geração de voz) está
implementado e configurado conforme a base do curso, usando `spring.ai.openai.*`.
Essa parte depende de uma chave de API paga da OpenAI (`OPENAI_API_KEY`), que não
utilizei neste momento por restrição de custo. Por isso, testei e validei
manualmente todo o fluxo de negócio (criação e consulta de transações, validações)
através dos endpoints REST tradicionais, que não dependem de nenhum serviço externo.

## Como Executar a Aplicação

Pré-requisitos: Java 21+ instalado.

```bash
# Clonar o repositório
git clone https://github.com/fernandoalvessantos87/dio-spring-boot-learning-track.git
cd dio-spring-boot-learning-track/05-spring-ai

# Rodar a aplicação (usa o banco H2 em memória, nenhuma configuração extra necessária)
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`.

## Como Testar o Fluxo Principal

**Criar uma transação válida:**
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d "{\"description\":\"Compra no mercado\",\"category\":\"GROCERIES\",\"amount\":5000}"
```

**Tentar criar uma transação inválida (deve retornar erro 400):**
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d "{\"description\":\"Teste invalido\",\"category\":\"GROCERIES\",\"amount\":-100}"
```

**Consultar transações por categoria:**
```bash
curl http://localhost:8080/transactions/GROCERIES
```

**Rodar os testes automatizados:**
```bash
./gradlew test --tests "dio.budgeting.domain.TransactionTest"
```

## O Que Aprendi

Esse desafio foi minha primeira experiência prática integrando IA a uma aplicação
Java real, e também minha primeira vez configurando um projeto Spring Boot do zero
em uma máquina nova (Git, Java, Gradle Wrapper). Entendi na prática:

- Como separar responsabilidades entre `domain`, `application` e `infrastructure`,
  e por que colocar as regras de negócio no domínio evita que elas fiquem
  espalhadas ou dependentes de um framework específico.
- Como o Spring AI usa `@Tool` para permitir que um modelo de linguagem execute
  funções reais da aplicação (Tool Calling).
- Como tratar erros de forma centralizada em uma API REST com
  `@RestControllerAdvice`.
- A importância de testes automatizados para comprovar que uma regra de negócio
  realmente funciona, em vez de depender só de testes manuais.

## Créditos

Projeto base desenvolvido nas aulas do bootcamp DIO + Santander (2026), trilha
Java Back-end com IA, com a orientação do expert Poiani. Repositório original:
[digitalinnovationone/dio-spring-boot-learning-track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track)