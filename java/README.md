# BuildPipeline Refactoring Kata (Java)

## Projeto original

Este projeto simula uma pipeline de entrega com três responsabilidades principais:

- executar testes do projeto;
- realizar deploy;
- enviar resumo por e-mail.

No código original, quase toda a regra estava concentrada em `Pipeline.run`, com múltiplas responsabilidades, condicionais aninhadas e uso repetitivo de literais de status (`"success"`/`"failure"`). Além disso, a suíte de testes não cobria o comportamento da pipeline.

## Configuração e execução

Ambiente usado:

- Java 17;
- Maven 3.9+.

Comandos:

```bash
mvn test
```

## Melhorias realizadas

### 1) Verificação inicial e testes automatizados

- Ajuste de build no `pom.xml` para `maven.compiler.release=17`, compatível com o ambiente.
- Criação de testes de comportamento em `PipelineTest` cobrindo:
  - fluxo de sucesso completo;
  - falha no deploy;
  - falha nos testes;
  - ausência de testes;
  - e-mail desabilitado;
  - garantia de que deploy não é chamado quando os testes falham.

Justificativa:

- protege o comportamento atual antes de refatorar;
- reduz risco de regressão durante mudanças estruturais.

### 2) Reestruturação de método complexo

- Refatoração de `Pipeline.run` com extração de métodos para etapas explícitas.
- Separação lógica das fases de teste, deploy e envio de resumo.

Justificativa:

- reduz complexidade ciclomática local;
- melhora legibilidade do fluxo principal.

### 3) Expressividade com variáveis e eliminação de repetição

- Substituição de comparações repetidas com literais por funções/variáveis nomeadas.
- Centralização da decisão de mensagem de resumo.

Justificativa:

- código mais semântico e previsível;
- menos duplicação e menor chance de inconsistência.

### 4) Melhorias de assinatura e encapsulamento

- Introdução de `PipelineExecutionResult` para encapsular estado da execução.
- Métodos deixam de trocar pares de booleanos soltos e passam a operar com um objeto de domínio da própria pipeline.

Justificativa:

- redução de acoplamento entre métodos;
- assinatura mais clara e orientada à intenção.

### 5) Reorganização de classes e processo

- Criação de classes coesas:
  - `PipelineProcess`: executa testes e deploy;
  - `PipelineNotificationService`: envia resumo e trata configuração de envio.
- `Pipeline` passou a atuar como orquestradora simples.

Justificativa:

- separação de responsabilidades;
- maior modularidade e facilidade de extensão/reuso.
