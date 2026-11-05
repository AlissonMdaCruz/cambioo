# PRD — Câmbioo

> Tipo: PRD inicial · Data: 2026-11-04
> **Status:** Implementada
>
> <!-- Valores possíveis: "Aguardando implementação" | "Implementada". Atualize para "Implementada" quando todas as specs estiverem concluídas. -->
>
> Fontes: `docs/product-brief.md` (visão). SoT técnica: `docs/architecture.md`. Estilo: `docs/design/style-reference.md` (inspiração apenas, não redefine o produto).

## 1. Visão geral

O **Câmbioo** é um app Android de consulta de câmbio em reais para leitura rápida no celular. Mostra a cotação mais recente do dólar, do euro, da libra esterlina e do franco suíço, a variação em relação à observação anterior publicada e o histórico dessa janela de 30 dias.

Não há conta, saldo, ordem nem conversão de valor digitado. O uso é só **leitura**. A fonte da primeira versão é a taxa de referência do Banco Central Europeu, obtida pela API pública Frankfurter, e a tela nomeia essa fonte — sem se passar por PTAX.

O visual pode se inspirar no tom de `docs/design/style-reference.md` (leitura rápida, hierarquia no número, dark contido), sem importar componentes ou fluxos extras desse arquivo.

## 2. Problema que resolve

Consultar câmbio no celular costuma ser lento ou pouco honesto com o dado:

- A pessoa precisa, em poucos segundos, do valor em reais, da data daquela cotação e se subiu ou caiu.
- Dia sem publicação (fim de semana ou feriado) não pode aparecer como queda, alta ou como “hoje”.
- Sem rede, a última consulta boa ainda precisa estar disponível, com a data do que foi salvo.
- Falha de atualização não pode apagar o dado bom nem deixar a pessoa sem “tentar de novo”.
- TalkBack precisa anunciar a moeda e o valor em reais e centavos, alinhados ao número da tela.

## 3. Público-alvo

Pessoa no Brasil que consulta no celular o valor do dólar e de poucas moedas em reais, com pressa, e que pode abrir o app de novo sem conexão.

Há um segundo leitor do repositório (avaliação de portfólio Android). A interface continua sendo a da pessoa que consulta a cotação; o portfólio é consequência da construção, não um segundo produto.

## 4. Objetivo do recorte atual

Entregar a primeira versão utilizável do Câmbioo: quatro moedas em reais com data e variação honestas, histórico de 30 dias, snapshot local para leitura offline, estados de tela claros, TalkBack com valor por extenso e fonte BCE nomeada — sem conta e sem operação financeira.

## 5. Funcionalidades

**Essenciais:**

- Listar USD, EUR, GBP e CHF (nessa ordem) com valor em reais, variação e data da observação.
- Mostrar no topo a data da cotação mais recente do snapshot e o momento em que a resposta foi guardada no aparelho.
- Abrir histórico por moeda com as observações publicadas na janela de 30 dias corridos.
- Atualizar puxando a lista (início ou histórico).
- Abrir sem rede a partir do último snapshot bem-sucedido.
- Distinguir carregando, conteúdo, vazio, erro sem dado local e erro com dado local.
- Anunciar valor por extenso no TalkBack, alinhado à cotação exibida.
- Nomear a fonte: “Banco Central Europeu, via Frankfurter”.

**Desejáveis:**

- Não se aplica neste recorte. Itens como PTAX, gráfico, conversor e mais moedas ficam em **Fora do escopo**.

### 5.1 Referência de estilo (`docs/design/style-reference.md`)

`style-reference.md` **não** é especificação de produto nem checklist de componentes. Serve só como inspiração visual para a v1:

- Leitura rápida, hierarquia no número, interface limpa (sem ruído ornamental).
- Dark mode contido e contraste alto nos valores são bem-vindos.
- Cor pode reforçar alta/queda/estável, **sem** ser o único sinal (texto `+` / `−` / `—` e TalkBack continuam obrigatórios).
- Tipografia expressiva (ex.: sans para rótulos + mono para números) é opcional, desde que a legibilidade da v1 seja mantida.

Em qualquer conflito, prevalecem brief, plano de contexto e este PRD. Anatomia de “Currency Exchange Card”, sparkline, badge com setas, conversor, timeframe switcher e layouts tablet/desktop do design **não** entram como requisito da v1.

## 6. Fora do escopo

- Gráfico da série
- Conversor com valor digitado
- Moedas além de USD, EUR, GBP e CHF
- PTAX do Banco Central do Brasil
- Troca de fonte no app ou série misturada de outros provedores
- Conta, backend próprio, push, widget, favoritos e alerta de variação
- iOS, web ou outras plataformas
- Componentes extras do `style-reference.md` (sparkline, barra de conversão, seletor de timeframe, grid multi-coluna, etc.)

## 7. Regras de negócio

- **Moedas fixas e ordem fixa:** USD, EUR, GBP, CHF — nessa ordem na tela inicial; a ordem da API não define a ordem da lista.
- **Cotação do produto:** quantos reais valem 1 unidade da moeda estrangeira, a partir da taxa da API (base BRL). A cotação do app usa arredondamento comercial e **duas casas decimais**. Tela, TalkBack e variação partem dessa cotação já arredondada.
- **Janela:** 30 dias corridos até a data local de São Paulo (pontas incluídas). Uma atualização cobre as quatro moedas e o intervalo.
- **Observação anterior:** variação de um ponto usa o ponto **publicado** imediatamente anterior da mesma moeda. Dia sem publicação não entra como zero, queda, alta nem “ontem”.
- **Variação (%):** `(atual − anterior) / anterior × 100`, com duas casas. Positiva = moeda estrangeira mais cara em reais; negativa = mais barata; zero = estável; sem anterior = sem variação (“—” visual; comparação omitida no anúncio). Texto visual: `+0,32%`, `−0,32%` (menos tipográfico), `0,00%` ou “—”. Cor pode reforçar; não substitui sinal/texto nem o TalkBack.
- **Taxa inválida:** ausente, zero ou negativa não vira cotação; a moeda some da lista. Se as quatro faltarem, estado vazio.
- **Data honesta:** a data exibida é a data da observação do payload, sem inventar “hoje”. “Hoje” só se a data da observação for a data local de São Paulo.
- **Duas datas no conteúdo:** (1) data da cotação/observação; (2) momento em que o snapshot foi guardado no aparelho. Ambas visíveis sempre que há conteúdo.
- **Snapshot:** sucesso substitui por completo o anterior. Falha de rede/HTTP/parse **não** apaga o snapshot já salvo. Offline de leitura = último snapshot, não acúmulo dia a dia.
- **Fonte:** apenas BCE via Frankfurter nesta versão. A tela não apresenta o número como PTAX.
- **Idioma:** interface, datas e números em português do Brasil, mesmo se o idioma do aparelho for outro.
- **Privacidade:** sem conta, sem analytics e sem dado pessoal.

## 8. Fluxos principais

### Fluxo 1 — Abrir o app e ver as cotações

1. Pessoa abre o app.
2. Se não há snapshot e a carga começa: vê carregando (“Carregando cotações”).
3. Se a carga traz observações utilizáveis: vê as quatro moedas (ou as válidas), as duas datas e a fonte.
4. Se a carga não traz observação utilizável e não há snapshot: vê vazio e “Tentar de novo”.
5. Se a carga falha e não há snapshot: vê erro e “Tentar de novo”.
6. Se já há snapshot: vê o conteúdo guardado; uma nova carga não troca a tela por carregando de tela cheia.

### Fluxo 2 — Ver histórico de uma moeda

1. Na tela inicial, a pessoa toca uma moeda.
2. Abre o histórico daquela moeda, do mais recente ao mais antigo, só com dias publicados na janela.
3. Cada linha mostra data, valor em reais e variação contra a observação anterior da mesma moeda.
4. Voltar do sistema retorna ao início.

### Fluxo 3 — Atualizar

1. Na inicial ou no histórico, a pessoa puxa a lista (ou toca “Tentar de novo” quando aplicável).
2. Dispara a mesma carga da janela.
3. Se sucesso: conteúdo atualizado; snapshot substituído.
4. Se falha com snapshot: lista guardada permanece, com aviso de que não foi possível atualizar.
5. Se falha sem snapshot: tela de erro com “Tentar de novo”.

### Fluxo 4 — Abrir sem rede

1. Com snapshot salvo, a pessoa abre o app sem rede.
2. Vê o conteúdo guardado e as duas datas, sem indicador infinito.
3. Sem snapshot e sem rede: vê erro e “Tentar de novo”.

## 9. Critérios de aceite

- O usuário consegue ver dólar, euro, libra e franco com valor em reais (duas casas), variação e data da observação.
- O usuário consegue abrir o histórico de uma moeda e percorrer só os dias publicados na janela de 30 dias.
- O usuário consegue atualizar puxando a lista.
- Sem rede e com snapshot, o app abre no conteúdo com as duas datas visíveis.
- Sem snapshot, falha mostra a mensagem fixa de erro e “Tentar de novo”.
- Com snapshot, falha conserva a lista, mostra o aviso e a data guardada.
- Resposta utilizável vazia mostra estado vazio, distinto do erro.
- TalkBack anuncia o valor do dólar a 5,42 como “dólar, 5 reais e 42 centavos” (e a tabela de singular/plural equivalente).
- A tela nomeia a fonte como Banco Central Europeu, via Frankfurter.
- Alta/queda/estável são distinguíveis sem depender só de cor.
- O sistema não inventa taxa para dia sem publicação.
- Itens da seção 6 ausentes desta entrega.

## 10. Stack

Em alto nível (decisão confirmada no plano de contexto e no scaffold atual):

- **Plataforma:** Android nativo, Kotlin, um módulo de app
- **UI:** Jetpack Compose, Material 3, navegação entre telas Compose (estilo livremente inspirado em `docs/design/style-reference.md`, sem obrigar seus componentes)
- **Estado na UI:** ViewModel + fluxo de estado observável
- **Assíncrono:** Coroutines
- **Injeção de dependências:** Hilt
- **Rede:** cliente HTTP tipado + serialização Kotlin (API Frankfurter v2, HTTPS, sem chave)
- **Persistência local:** banco local para o snapshot da última resposta bem-sucedida
- **Testes:** testes unitários JVM para regras de domínio e ViewModel (sucesso, erro, variação, formatador acessível)
- **Permissão:** apenas internet
- **minSdk:** 26

## 11. Justificativa da stack

Android nativo com Compose atende o público (leitura rápida no celular) e o segundo objetivo do repositório (portfólio com Compose, injeção, rede, persistência, MVVM e teste unitário num módulo só). A API pública sem chave evita backend próprio. Snapshot local resolve offline de leitura sem conta nem sync complexo. O escopo pequeno (quatro moedas, uma janela) não pede paginação, multi-módulo nem infraestrutura extra.

## 12. Fases de construção

### Fase 1 — Regras de cotação e apresentação

Objetivo: garantir que o número, a variação e o texto falado/visual sejam honestos e testáveis antes das telas.
Specs:

- Spec 01 — Cotação em reais, janela e variação
- Spec 02 — Formatação visual e falada

### Fase 2 — Fonte de dados e snapshot

Objetivo: obter a série da janela, persistir o último sucesso e preservar dado bom em falha.
Specs:

- Spec 03 — Consulta da janela (BCE via Frankfurter)
- Spec 04 — Snapshot local e leitura offline

### Fase 3 — Tela inicial e estados

Objetivo: entregar a leitura principal com estados distintos e metadados de data.
Specs:

- Spec 05 — Lista das quatro moedas na tela inicial
- Spec 06 — Máquina de estados (carregando, conteúdo, vazio, erro)

### Fase 4 — Histórico e atualização

Objetivo: detalhar a série por moeda e permitir atualizar sem perder conteúdo bom.
Specs:

- Spec 07 — Histórico por moeda
- Spec 08 — Atualizar e tentar de novo

### Fase 5 — Acessibilidade e fechamento

Objetivo: fechar o recorte com anúncio TalkBack alinhado e roteiro de verificação.
Specs:

- Spec 09 — TalkBack e verificação do recorte

## 13. Specs funcionais detalhadas

> Cada spec deve ser autossuficiente: um agente de codificação vai ler SÓ esta spec (mais as dependências) para montar o plano técnico e implementar. Preencha todos os campos; se um não se aplica, escreva "Não se aplica" e o porquê.

### Spec 01 — Cotação em reais, janela e variação

- **Fase:** 1 — Regras de cotação e apresentação
- **Objetivo (o quê):** Definir como uma taxa da API vira cotação em reais, como a janela de 30 dias é delimitada e como a variação entre observações publicadas é calculada.
- **Intenção (por quê):** O valor do produto é honestidade do número: mesma escala na tela, na fala e na variação; dias sem publicação não distorcem alta/queda.
- **Contexto:** Base do app; ainda sem UI. Fonte conceitual: observações com data de calendário, base BRL, moeda de destino e taxa (unidades da moeda estrangeira por 1 real).
- **Atores:** Sistema (cálculo); pessoa só consome o resultado nas specs de UI.
- **Descrição do comportamento:** Para cada observação válida, a cotação do app é o inverso da taxa, arredondado com regra comercial a duas casas decimais. A janela vai de `to` (data local São Paulo) até `from` = `to` − 29 dias (30 dias corridos, pontas incluídas). Por moeda, a série ordenada por data usa o ponto publicado imediatamente anterior para a variação percentual com duas casas. Sem anterior, não há variação. Taxa ausente, zero ou negativa é rejeitada (não gera cotação).
- **Entradas e saídas:**
  - Entrada: taxa decimal da API; pares de cotações já na escala do app; datas de observação; data local de referência.
  - Saída: cotação em reais (2 casas); variação % (2 casas) ou ausência de variação; lista só com dias publicados.
- **Dados/entidades envolvidos (conceitual):** Observação (data, moeda, taxa); Cotação do app (moeda, data, valor em reais); Variação (percentual ou ausente).
- **Estados e transições:** Não se aplica — regras puras, sem ciclo de vida de tela.
- **Regras de negócio:** As da seção 7 relativas a cotação, janela, observação anterior, variação e taxa inválida.
- **Validações:** Rejeitar taxa ausente, zero ou negativa; não interpolar dia sem publicação.
- **Fluxo do usuário (passo a passo):** Não se aplica — comportamento de domínio consumido pelas telas.
- **Casos de borda e erros:** Sem observação anterior → sem variação; série com buracos de fim de semana → variação liga só pontos publicados; todas as taxas inválidas → nenhuma cotação utilizável.
- **Impacto no existente:** Nenhum (projeto em scaffold).
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado uma taxa válida Quando se calcula a cotação Então o resultado tem duas casas e representa reais por 1 unidade da moeda.
  - Dado duas cotações consecutivas publicadas Quando se calcula a variação Então o percentual usa a fórmula da seção 7 com duas casas e o sinal correto (alta/queda/estável).
  - Dado um dia sem publicação entre dois pontos Quando se calcula a variação do mais novo Então o anterior usado é o último publicado, não o dia em branco.
  - Dado taxa zero ou negativa Quando se tenta formar cotação Então essa observação é descartada.
- **Definição de pronto:** Regras cobertas por testes unitários de domínio (inversão, variação, ausência de anterior, rejeição de taxa inválida).
- **Dependências:** Nenhuma.
- **Fora do escopo desta spec:** UI, rede, persistência, formatação por extenso, PTAX.

### Spec 02 — Formatação visual e falada

- **Fase:** 1 — Regras de cotação e apresentação
- **Objetivo (o quê):** Definir como valor e variação aparecem na tela e como devem ser anunciados em português do Brasil.
- **Intenção (por quê):** Leitura rápida na tela e anúncio TalkBack alinhado ao mesmo número arredondado — sem divergência entre o que se vê e o que se ouve.
- **Contexto:** Consome a cotação e a variação já na escala do app (Spec 01). Estilo visual livremente inspirado na seção 5.1; regras de texto vêm do produto.
- **Atores:** Sistema (formatação); pessoa (leitura visual e auditiva).
- **Descrição do comportamento:** Valor visual: `R$` + espaço + inteiros + vírgula + duas casas. Variação visual: `+X,XX%`, `−X,XX%` (menos tipográfico), `0,00%` ou “—” sem comparação. Frase falada do valor: `{nome falado}, {quantia por extenso}` com singular/plural corretos. Variação falada: “alta de … por cento”, “queda de … por cento” (absoluto), “estável”, ou omissão se não houver comparação. Nomes falados: dólar, euro, libra esterlina, franco suíço. Nomes na tela: Dólar, Euro, Libra esterlina, Franco suíço.
- **Entradas e saídas:**
  - Entrada: cotação (2 casas), variação (ou ausência), código/nome da moeda.
  - Saída: textos visual e falado de valor e variação.
- **Dados/entidades envolvidos (conceitual):** Textos de apresentação por moeda (visual valor, falado valor, visual variação, falado variação).
- **Estados e transições:** Não se aplica.
- **Regras de negócio:** Mesma cotação arredondada para tela e fala; PT-BR fixo; tabela de quantia (0,00 → “0 reais”; 0,01 → “1 centavo”; 1,00 → “1 real”; 1,01 → “1 real e 1 centavo”; 5,42 → “5 reais e 42 centavos”; etc.).
- **Validações:** Não inventar casas além da cotação do app; não anunciar comparação quando variação ausente.
- **Fluxo do usuário (passo a passo):** Não se aplica diretamente — usado nas linhas da lista/histórico.
- **Casos de borda e erros:** Parte inteira 0 com centavos → só centavos na frase; variação zero → “estável” / `0,00%`; sem anterior → “—” e sem frase de comparação.
- **Impacto no existente:** Nenhum.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado cotação 5,42 e moeda dólar Quando se formata o falado Então a frase é “dólar, 5 reais e 42 centavos”.
  - Dado variação positiva 0,32 Quando se formata Então visual `+0,32%` e falado “alta de 0,32 por cento”.
  - Dado ausência de variação Quando se formata Então visual “—” e o falado omite a comparação.
- **Definição de pronto:** Formatador coberto por testes unitários com a tabela de singular/plural e frases de alta/queda/estável.
- **Dependências:** Spec 01 — cotação e variação na escala do app.
- **Fora do escopo desta spec:** Layout/tema Compose (Specs 05–06); integração TalkBack na UI (Spec 09).

### Spec 03 — Consulta da janela (BCE via Frankfurter)

- **Fase:** 2 — Fonte de dados e snapshot
- **Objetivo (o quê):** Buscar, numa atualização, as observações das quatro moedas na janela de 30 dias na fonte BCE via Frankfurter.
- **Intenção (por quê):** Uma fonte nomeada e uma chamada por atualização mantêm o número auditável e o app simples.
- **Contexto:** Depende das regras de janela e moedas (Spec 01). API pública sem chave; base BRL; quotes USD, EUR, GBP, CHF.
- **Atores:** Pessoa (dispara atualização indiretamente); sistema (executa a consulta).
- **Descrição do comportamento:** O sistema solicita a série no intervalo da janela para as quatro moedas. Sucesso devolve observações (data, base, moeda, taxa). A observação mais recente de cada moeda é a de maior data na série — não há chamada extra de “latest”. Erros de rede, HTTP ou parse equivalem a falha de atualização (tratada com Spec 04 e Spec 06). A tela/consumidor deve poder exibir a fonte como BCE via Frankfurter.
- **Entradas e saídas:**
  - Entrada: datas `from`/`to` da janela; lista fixa de moedas.
  - Saída: conjunto de observações utilizáveis ou falha.
- **Dados/entidades envolvidos (conceitual):** Pedido de janela; Observação remota; Metadado de fonte (nome a exibir).
- **Estados e transições:** Em curso → sucesso | falha. Sem persistência nesta spec.
- **Regras de negócio:** Só provedor BCE nesta versão; série misturada / outros provedores fora; uma atualização = uma consulta cobrindo intervalo e moedas.
- **Validações:** Resposta sem observações utilizáveis após regras da Spec 01 → conjunto vazio (não confundir com falha de rede).
- **Fluxo do usuário (passo a passo):**
  1. Pessoa abre o app ou puxa para atualizar / tenta de novo.
  2. Sistema consulta a janela.
  3. Em sucesso, observações seguem para snapshot/UI; em falha, estado de erro/aviso.
- **Casos de borda e erros:** Timeout/rede/HTTP/parse → falha; corpo com taxas inválidas só para algumas moedas → só as válidas seguem; todas inválidas/ausentes → vazio utilizável.
- **Impacto no existente:** Nenhum.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado rede ok e resposta com observações válidas Quando atualiza Então o sistema obtém as quatro moedas (quando publicadas) no intervalo sem chamada “latest” extra.
  - Dado falha de rede Quando atualiza Então o resultado é falha (sem inventar cotações).
  - Dado sucesso Quando o conteúdo é mostrado Então a fonte pode ser nomeada como Banco Central Europeu, via Frankfurter.
- **Definição de pronto:** Cliente de API cobre uma chamada por atualização; falhas mapeadas; revisão confirma ausência de provedor misturado.
- **Dependências:** Spec 01 — janela, moedas e validação de taxa.
- **Fora do escopo desta spec:** UI dos estados, PTAX, troca de fonte, acúmulo histórico além da janela pedida.

### Spec 04 — Snapshot local e leitura offline

- **Fase:** 2 — Fonte de dados e snapshot
- **Objetivo (o quê):** Guardar a última resposta bem-sucedida no aparelho e usá-la para leitura offline, sem apagar dado bom em falha.
- **Intenção (por quê):** A pessoa reabre o app sem rede e ainda vê a cotação, com data da observação e momento em que foi guardada.
- **Contexto:** Consome observações obtidas na Spec 03; cotações derivadas pela Spec 01.
- **Atores:** Sistema (persistir/ler); pessoa (abre sem rede).
- **Descrição do comportamento:** Toda resposta bem-sucedida e utilizável substitui por completo o snapshot anterior (metadados + observações) de forma atômica. Falha de atualização não altera o snapshot existente. O histórico offline é o da resposta guardada. Metadado inclui instante em que foi salvo, intervalo pedido e nome da fonte. Cotação em reais é derivada na leitura pelas regras da Spec 01 (um lugar só para arredondamento).
- **Entradas e saídas:**
  - Entrada: observações + metadados de uma resposta de sucesso; ou pedido de leitura local.
  - Saída: snapshot completo ou ausência de snapshot.
- **Dados/entidades envolvidos (conceitual):** Snapshot (salvo em, from, to, fonte); Observações persistidas (moeda, data, taxa como veio).
- **Estados e transições:** Sem snapshot → com snapshot (após sucesso); com snapshot + falha → permanece com snapshot.
- **Regras de negócio:** Substituição total no sucesso; falha não apaga; sem acúmulo dia a dia no aparelho.
- **Validações:** Não gravar snapshot parcial misturado com o anterior em caso de falha no meio da gravação.
- **Fluxo do usuário (passo a passo):**
  1. Atualização bem-sucedida grava snapshot.
  2. Pessoa ativa modo avião, fecha e abre o app.
  3. Conteúdo do snapshot aparece com as duas datas.
- **Casos de borda e erros:** Primeira abertura sem snapshot e sem rede → sem conteúdo local; sucesso novo → apaga série antiga por completo.
- **Impacto no existente:** Nenhum.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado um sucesso recente Quando a pessoa abre sem rede Então vê o conteúdo guardado com data da cotação e data/hora guardada.
  - Dado snapshot existente Quando uma atualização falha Então o snapshot anterior permanece intacto.
  - Dado novo sucesso Quando grava Então a série anterior não permanece misturada com a nova.
- **Definição de pronto:** Gravação/leitura do snapshot verificável; falha de atualização preserva o anterior.
- **Dependências:** Spec 01, Spec 03.
- **Fora do escopo desta spec:** Textos de UI de aviso/erro (Spec 06); histórico como tela (Spec 07).

### Spec 05 — Lista das quatro moedas na tela inicial

- **Fase:** 3 — Tela inicial e estados
- **Objetivo (o quê):** Apresentar a leitura principal: quatro moedas em ordem fixa, com valor, variação, data da observação, duas datas de contexto e fonte.
- **Intenção (por quê):** Entregar o uso em pé — olhar o número e seguir — em poucos segundos.
- **Contexto:** Usa cotações/variações (Spec 01), textos (Spec 02) e dados do snapshot/atualização (Specs 03–04). Estilo pode se inspirar na seção 5.1; o conteúdo e a estrutura seguem a v1 do produto.
- **Atores:** Pessoa que consulta câmbio.
- **Descrição do comportamento:** Tela inicial lista, nesta ordem: Dólar, Euro, Libra esterlina, Franco suíço. Cada linha: nome, código, valor em reais, variação e data da observação daquela moeda. No topo do conteúdo: data da cotação mais recente do snapshot e data/hora em que a resposta foi guardada. Texto curto de fonte: “Fonte: Banco Central Europeu, via Frankfurter”. Tocar uma moeda navega ao histórico (Spec 07).
- **Entradas e saídas:**
  - Entrada: snapshot/observações processadas.
  - Saída: lista ordenada na UI; navegação ao histórico ao toque.
- **Dados/entidades envolvidos (conceitual):** Item de cotação na UI (nome, código, valor visual/falado, variação visual/falada, data observação); cabeçalho (data cotação, guardada em, fonte).
- **Estados e transições:** Descritos em detalhe na Spec 06; esta spec assume estado de conteúdo.
- **Regras de negócio:** Ordem fixa; só moedas com cotação válida; PT-BR; não apresentar como PTAX.
- **Validações:** Não reordenar pela API; não mostrar moeda rejeitada pela Spec 01.
- **Fluxo do usuário (passo a passo):**
  1. Abre o app e chega ao conteúdo.
  2. Vê as quatro linhas (ou as válidas), as duas datas e a fonte.
  3. Opcionalmente toca uma moeda para o histórico.
- **Casos de borda e erros:** Menos de quatro moedas válidas → lista só as válidas; conteúdo com aviso de falha recente → Spec 06.
- **Impacto no existente:** Substitui a UI placeholder do scaffold na experiência principal.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado snapshot com as quatro moedas Quando a pessoa vê o início Então a ordem é USD, EUR, GBP, CHF com valor, variação e data.
  - Dado conteúdo visível Quando a pessoa olha o topo Então vê data da cotação, momento guardado e a fonte nomeada.
- **Definição de pronto:** Tela inicial de conteúdo conferida no emulador/aparelho com dados reais ou de fixture.
- **Dependências:** Specs 01–04; Spec 06 para estados ao redor.
- **Fora do escopo desta spec:** Histórico completo, gráfico, conversor, seleção persistida de moeda além da pilha de navegação.

### Spec 06 — Máquina de estados (carregando, conteúdo, vazio, erro)

- **Fase:** 3 — Tela inicial e estados
- **Objetivo (o quê):** Diferenciar claramente carregando, conteúdo, vazio, erro sem snapshot e conteúdo com aviso após falha.
- **Intenção (por quê):** Falha e ausência de dado não podem se confundir; dado bom não some atrás de um carregando de tela cheia.
- **Contexto:** Aplica-se ao início e, de forma equivalente, ao histórico. Textos fixos em PT-BR.
- **Atores:** Pessoa; sistema de carga.
- **Descrição do comportamento:**

  | Situação | O que a pessoa vê |
  | --- | --- |
  | Sem snapshot e carga em andamento | Indicador + “Carregando cotações” |
  | Snapshot com ao menos uma observação | Lista, duas datas; se a última tentativa falhou, aviso |
  | Carga ok, nenhuma observação utilizável, sem snapshot | “Não há cotação neste período.” + “Tentar de novo” |
  | Falha sem snapshot | “Não foi possível atualizar a cotação. Verifique a conexão e tente de novo.” + “Tentar de novo” |
  | Falha com snapshot | Lista guardada + aviso “Não foi possível atualizar. Mostrando a cotação guardada.” |
  | Abertura sem rede com snapshot | Conteúdo guardado + duas datas, sem indicador infinito |

  Se já existe snapshot, nova carga **não** troca a tela por carregando de tela cheia. Mensagens de erro são texto fixo (detalhe técnico só em log de debug). Área tocável do botão/linha ≥48dp.
- **Entradas e saídas:**
  - Entrada: presença/ausência de snapshot; resultado da carga (sucesso com dados, sucesso vazio, falha).
  - Saída: um dos estados de UI acima; aviso nulo após sucesso desta sessão.
- **Dados/entidades envolvidos (conceitual):** Estado de UI (carregando | vazio | erro | conteúdo com aviso opcional).
- **Estados e transições:** Sem snapshot + início carga → carregando; sucesso com dados → conteúdo; sucesso vazio sem snapshot → vazio; falha sem snapshot → erro; falha com snapshot → conteúdo + aviso; sucesso após aviso → conteúdo sem aviso.
- **Regras de negócio:** Não destruir snapshot em falha; vazio ≠ erro; textos conforme tabela.
- **Validações:** Não se aplica além do mapeamento situação → estado.
- **Fluxo do usuário (passo a passo):**
  1. Abre sem dados → carregando ou erro conforme rede.
  2. “Tentar de novo” dispara a mesma carga que puxar a lista.
  3. Com dado local, atualização falha → continua lendo a lista com aviso.
- **Casos de borda e erros:** Exceção/HTTP/corpo → mesma falha de UI; sucesso parcial de moedas → conteúdo com as válidas (não vazio se houver ao menos uma).
- **Impacto no existente:** Nenhum além da UX principal.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado sem snapshot e falha Quando a carga termina Então vê a mensagem fixa de erro e “Tentar de novo”.
  - Dado snapshot e falha Quando a carga termina Então vê a lista guardada com o aviso de cotação guardada.
  - Dado snapshot Quando uma nova carga começa Então o conteúdo permanece visível (sem carregando de tela cheia).
  - Dado sucesso sem observações utilizáveis e sem snapshot Quando a carga termina Então vê vazio, distinto do erro.
- **Definição de pronto:** Estados cobertos por testes do ViewModel da tela inicial e checagem manual das mensagens.
- **Dependências:** Specs 03–05.
- **Fora do escopo desta spec:** Redação adicional de marketing; analytics de erro.

### Spec 07 — Histórico por moeda

- **Fase:** 4 — Histórico e atualização
- **Objetivo (o quê):** Ao tocar uma moeda, mostrar a série publicada na janela, da mais recente para a mais antiga, com valor e variação.
- **Intenção (por quê):** Permitir entender o movimento recente sem gráfico e sem inventar dias sem publicação.
- **Contexto:** Mesmas regras de variação e formatação (Specs 01–02); mesmos estados equivalentes (Spec 06); dados do snapshot/janela (Specs 03–04).
- **Atores:** Pessoa.
- **Descrição do comportamento:** Navegação início → histórico da moeda tocada. Lista só dias publicados; cada linha: data, valor em reais, variação contra a observação anterior da mesma moeda. A linha mais antiga da janela fica sem variação. A variação da linha mais recente é a mesma da tela inicial para aquela moeda. Voltar do sistema retorna ao início. Não há seleção persistida de moeda além da pilha de navegação.
- **Entradas e saídas:**
  - Entrada: identificador da moeda na navegação; série do snapshot filtrada.
  - Saída: lista ordenada do histórico; retorno ao início.
- **Dados/entidades envolvidos (conceitual):** Linha de histórico (data, valor, variação); moeda selecionada na navegação.
- **Estados e transições:** Equivalentes à Spec 06, filtrados pela moeda.
- **Regras de negócio:** Só dias publicados; ordem mais novo → mais antigo; mesma janela de 30 dias.
- **Validações:** Não fabricar linhas para fim de semana/feriado sem publicação.
- **Fluxo do usuário (passo a passo):**
  1. Toca uma moeda no início.
  2. Percorre o histórico.
  3. Volta ao início pelo botão voltar do sistema.
- **Casos de borda e erros:** Uma única observação na janela → uma linha sem variação; falha de atualização no histórico com snapshot → mantém lista + aviso (Spec 06/08).
- **Impacto no existente:** Acrescenta segunda tela ao fluxo.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado série com buraco de fim de semana Quando abre o histórico Então não há linha para o dia sem publicação.
  - Dado histórico com N pontos Quando olha o mais antigo Então ele não tem variação.
  - Dado a linha mais recente do histórico Quando compara com o início Então a variação é a mesma.
- **Definição de pronto:** Navegação ida e volta conferida; regras de lista cobertas por teste/roteiro.
- **Dependências:** Specs 01–02, 04–06.
- **Fora do escopo desta spec:** Gráfico; favoritos; comparativo entre moedas na mesma tela.

### Spec 08 — Atualizar e tentar de novo

- **Fase:** 4 — Histórico e atualização
- **Objetivo (o quê):** Unificar puxar a lista e “Tentar de novo” na mesma carga da janela, preservando conteúdo visível durante a atualização quando já há snapshot.
- **Intenção (por quê):** Atualizar é barato e previsível; falha não pune quem já tinha um número bom na tela.
- **Contexto:** Dispara Spec 03; persiste via Spec 04; estados via Spec 06; disponível no início e no histórico.
- **Atores:** Pessoa.
- **Descrição do comportamento:** Ação de atualizar chama-se “Atualizar cotações”. Puxar a lista (início ou histórico) e o botão “Tentar de novo” disparam a mesma carga. O indicador fica na lista; o conteúdo já visível permanece no lugar durante a carga quando há snapshot. Sucesso substitui snapshot e limpa aviso. Falha segue Spec 06.
- **Entradas e saídas:**
  - Entrada: gesto de puxar ou toque em “Tentar de novo”.
  - Saída: nova tentativa de carga; UI atualizada conforme resultado.
- **Dados/entidades envolvidos (conceitual):** Pedido de atualização; resultado sucesso/vazio/falha.
- **Estados e transições:** Conforme Spec 06.
- **Regras de negócio:** Uma chamada por atualização; mesma carga para pull e botão.
- **Validações:** Não se aplica.
- **Fluxo do usuário (passo a passo):**
  1. Puxa a lista com rede → vê dados novos.
  2. Puxa sem rede com snapshot → mantém lista + aviso.
  3. Em erro sem snapshot, toca “Tentar de novo” com rede → chega ao conteúdo.
- **Casos de borda e erros:** Timeout curto o bastante para o estado de erro ser alcançável; múltiplos pulls seguidos não devem corromper o snapshot (último sucesso válido vence).
- **Impacto no existente:** Nenhum além das telas do recorte.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado conteúdo visível Quando a pessoa puxa a lista e a carga falha Então o snapshot anterior continua na tela com aviso.
  - Dado tela de erro sem snapshot Quando a rede volta e a pessoa toca “Tentar de novo” Então chega ao conteúdo.
  - Dado início ou histórico Quando atualiza Então a carga pedida é a mesma janela das quatro moedas.
- **Definição de pronto:** Pull e “Tentar de novo” verificados no roteiro manual; ViewModel cobre sucesso/erro com e sem snapshot.
- **Dependências:** Specs 03, 04, 06, 07.
- **Fora do escopo desta spec:** Atualização em segundo plano, push, agendamento.

### Spec 09 — TalkBack e verificação do recorte

- **Fase:** 5 — Acessibilidade e fechamento
- **Objetivo (o quê):** Garantir anúncio acessível do valor (e do histórico) e fechar o recorte com o roteiro de verificação da primeira versão.
- **Intenção (por quê):** Acessibilidade faz parte do produto, não é extra; o roteiro curto prova o recorte para uso e para avaliação do portfólio.
- **Contexto:** Usa frases da Spec 02 nas linhas da Spec 05 e Spec 07; estados da Spec 06; títulos/botões nomeados (“Atualizar cotações”, “Tentar de novo”).
- **Atores:** Pessoa usuária de TalkBack; pessoa avaliando o app.
- **Descrição do comportamento:** No TalkBack, o valor da linha da moeda é anunciado **uma vez**, pela frase por extenso. Alta/queda/estável distinguíveis sem depender só de cor. Área tocável adequada (≥48dp). No histórico, anúncio inclui data por extenso em português, quantia e variação. Textos vêm de recursos de string. Roteiro de verificação (ordem):
  1. Abrir com rede: quatro moedas, fonte, data da cotação e data guardada.
  2. TalkBack: ouvir valor do dólar por extenso.
  3. Histórico do dólar cruzando fim de semana: dia sem publicação sem linha.
  4. Puxar para atualizar.
  5. Modo avião, fechar e abrir: conteúdo com as duas datas.
  6. Limpar dados do app, modo avião, abrir: erro + “Tentar de novo”.
  7. Rede de volta + “Tentar de novo”: a lista volta.
- **Entradas e saídas:**
  - Entrada: foco TalkBack nas linhas; execução do roteiro.
  - Saída: anúncios corretos; roteiro reproduzível.
- **Dados/entidades envolvidos (conceitual):** Conteúdo de acessibilidade por linha; strings de UI.
- **Estados e transições:** Não se aplica além dos já definidos.
- **Regras de negócio:** Anúncio alinhado à cotação arredondada; PT-BR; sem analytics.
- **Validações:** Conferir que o anúncio não duplica o valor numérico cru em conflito com a frase por extenso.
- **Fluxo do usuário (passo a passo):** Conforme roteiro acima.
- **Casos de borda e erros:** Sem variação → não anunciar comparação; estados de erro/vazio com nomes de botão claros.
- **Impacto no existente:** Ajusta semântica das telas já feitas.
- **Critérios de aceite (Dado/Quando/Então):**
  - Dado dólar a 5,42 e TalkBack ativo Quando o foco está na linha Então ouve “dólar, 5 reais e 42 centavos” (uma vez).
  - Dado o roteiro completo Quando executado em aparelho/emulador Então todos os sete passos passam.
  - Dado revisão do recorte Quando se inspeciona o entregue Então itens da seção 6 não foram implementados.
- **Definição de pronto:** Roteiro manual concluído; critérios da seção 9 satisfeitos.
- **Dependências:** Specs 02, 05–08.
- **Fora do escopo desta spec:** Localização para outros idiomas; testes instrumentados obrigatórios além do roteiro manual acordado.

## 14. Ordem recomendada de implementação

1. Spec 01 — Cotação em reais, janela e variação
2. Spec 02 — Formatação visual e falada
3. Spec 03 — Consulta da janela (BCE via Frankfurter)
4. Spec 04 — Snapshot local e leitura offline
5. Spec 05 — Lista das quatro moedas na tela inicial
6. Spec 06 — Máquina de estados (carregando, conteúdo, vazio, erro)
7. Spec 07 — Histórico por moeda
8. Spec 08 — Atualizar e tentar de novo
9. Spec 09 — TalkBack e verificação do recorte

Seguir essa ordem evita UI sem regra de número, persistência sem fonte, e acessibilidade antes das telas existirem. Cada spec assume as anteriores citadas em **Dependências**.

---

### Notas de decisão (confirmadas vs. herdadas)

| Tema | Origem | Status |
| --- | --- | --- |
| Problema, público, escopo v1 / fora | `product-brief.md` | Confirmado |
| Regras de cotação, estados, textos, aceite | este PRD | Confirmado |
| Stack Android Compose + Hilt + rede + Room + testes | `architecture.md` + scaffold | Confirmado |
| `design/style-reference.md` | referência de estilo | Inspiração visual apenas; não redefine escopo nem componentes da v1 |
