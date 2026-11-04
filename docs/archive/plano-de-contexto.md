# Plano de contexto — Câmbioo

> **Supersedido em 2026-11-04.** Preservado só para histórico.
>
> Leia em vez disso:
> - Visão: [../product-brief.md](../product-brief.md)
> - Produto / specs: [../prd/cambioo.md](../prd/cambioo.md)
> - Técnica: [../architecture.md](../architecture.md)
> - Estilo: [../design/style-reference.md](../design/style-reference.md)

Versão 1 deste plano · 3 de novembro de 2026

Este documento foi a referência de escopo da primeira versão antes da reorganização da pasta `docs`.

O produto é uma consulta de câmbio em reais para leitura rápida no celular: a cotação mais recente, a variação contra a observação anterior e o histórico publicado dentro de uma janela de 30 dias. O projeto também é a peça de portfólio que sustenta, num módulo só, Jetpack Compose, Hilt, Retrofit e Room, sobre MVVM, Coroutines, Flow e teste unitário.

## 1. Nome do projeto

**Câmbioo**

Nome de exibição do app: Câmbioo. Conceito: câmbio do dia, em reais.

## 2. Resumo do projeto

O Câmbioo mostra, em reais, a cotação do dólar, do euro, da libra esterlina e do franco suíço. Cada moeda traz a data da observação e a variação em relação à observação anterior publicada. O histórico dessa janela aparece numa lista, e a variação é calculada no aplicativo.

A última resposta bem-sucedida fica gravada no aparelho. Sem rede, o app abre nesse snapshot e deixa visível a data da cotação e o momento em que ela foi guardada. Atualizar é puxar a lista. Carregando, vazio, erro de rede e tentar de novo são telas distintas.

O valor anunciado pelo TalkBack sai por extenso, no formato “dólar, 5 reais e 42 centavos”. A tela, a fala e a variação usam a mesma cotação já arredondada, para a pessoa ouvir o número que está vendo.

A fonte da primeira versão é a taxa de referência do Banco Central Europeu, obtida pela API pública Frankfurter (v2), sem chave. A PTAX do Banco Central do Brasil fica para uma versão seguinte.

## 3. Problema que o produto resolve

- A cotação precisa ser legível em poucos segundos: valor em reais, data daquela cotação e se subiu ou caiu.
- Um dia sem publicação (fim de semana ou feriado) não pode aparecer como queda, alta ou como “hoje”.
- A última consulta bem-sucedida precisa continuar disponível sem rede, com a data do que foi salvo.
- Uma falha de atualização precisa conservar o dado bom que já está no aparelho e oferecer tentar de novo.
- O TalkBack precisa anunciar a moeda e o valor em reais e centavos.

## 4. Público-alvo

Pessoa no Brasil que consulta no celular o valor do dólar e de poucas moedas em reais, com pressa, e que pode abrir o app de novo sem conexão.

O uso previsto é leitura, não operação financeira: não há conta, saldo, ordem nem conversão livre de um valor digitado.

Há um segundo leitor deste repositório: quem avalia o projeto como portfólio de engenharia Android. Os critérios da seção 11 descrevem o que essa leitura precisa encontrar. A interface em si continua sendo a da pessoa que consulta a cotação.

## 5. Objetivos do produto

1. Mostrar as quatro moedas em reais, com a data da observação e a variação contra a observação anterior publicada.
2. Mostrar o histórico publicado na janela dos últimos 30 dias corridos, com a variação calculada no app.
3. Abrir sem rede a partir da última resposta guardada, com a data da cotação e a data em que foi salva.
4. Diferenciar carregando, conteúdo, vazio, erro sem dado local e erro com dado local.
5. Anunciar o valor por extenso no TalkBack, com singular e plural corretos.
6. Cobrir com teste unitário o sucesso do ViewModel, o erro do ViewModel, a variação e o formatador acessível.
7. Fazer isso em um módulo Android, com a regra de negócio no cálculo e na formatação, e com a stack descrita na seção 7.

## 6. Funcionalidades principais

### 6.1 As quatro moedas

A primeira versão consulta só estas moedas, nesta ordem fixa na tela inicial. A ordem da resposta da API não define a ordem da lista.

| Ordem | Código | Nome na tela | Nome falado |
| --- | --- | --- | --- |
| 1 | USD | Dólar | dólar |
| 2 | EUR | Euro | euro |
| 3 | GBP | Libra esterlina | libra esterlina |
| 4 | CHF | Franco suíço | franco suíço |

As quatro são moedas de referência do BCE e, convertidas para reais, ficam na casa dos reais. Com duas casas decimais, o valor na tela e o valor falado em reais e centavos representam a mesma quantia. O iene fica de fora desta versão: a cotação de 1 iene cai abaixo de um centavo, e o anúncio em reais e centavos deixaria de bater com um número útil na tela.

### 6.2 Fluxo da primeira versão

Duas telas, com Navigation Compose.

1. **Início.** Lista das quatro moedas. Em cada linha: nome, código, valor em reais, variação e a data da observação daquela moeda. No topo do conteúdo: a data da cotação mais recente do snapshot e a data/hora em que a resposta foi guardada no aparelho.
2. **Histórico.** Abre ao tocar uma moeda. Lista as observações daquela moeda dentro da janela, da mais recente para a mais antiga. Cada linha tem data, valor em reais e variação contra a observação anterior da mesma moeda.
3. **Atualizar.** Puxar a lista, na tela inicial ou no histórico, repete a mesma carga. O indicador fica na lista. O conteúdo já visível permanece no lugar durante a carga.
4. **Voltar.** Do histórico para o início, pelo botão de voltar do sistema.

Não há seleção persistida de moeda além da pilha de navegação.

### 6.3 Regras de domínio

Estas regras são o núcleo testável do app. Elas ficam em Kotlin puro, sem `Context`, sem Room e sem Retrofit.

**O que a API devolve.** Cada item é uma observação: data de calendário (`YYYY-MM-DD`), base, moeda de destino e taxa. Na chamada desta versão a base é sempre `BRL`. A taxa é quantas unidades da moeda estrangeira valem 1 real. O payload não traz variação.

**Cotação em reais.** O valor do produto é quantos reais valem 1 unidade da moeda estrangeira:

`reais por 1 unidade = 1 / taxa da API`

O cálculo usa `BigDecimal`, arredondamento `HALF_UP` e escala 2. Essa escala 2 é a cotação do app. Tela, TalkBack e variação partem dela, não da taxa crua e não de `Double`.

Exemplo de arredondamento, com a taxa do dólar como veio numa resposta real do provedor (o teste fixa a entrada; não chama a rede):

| Taxa da API (USD por 1 BRL) | Cotação do app |
| --- | --- |
| 0.18408 | 5,43 |

**Janela.** `to` é a data local de `America/Sao_Paulo`. `from` é `to` menos 29 dias. A janela tem 30 dias corridos, com as duas pontas incluídas. O app pede essa janela numa única chamada e separa as quatro moedas no cliente.

**Observação anterior.** A série de cada moeda entra ordenada pela data da observação. A variação de um ponto usa o ponto publicado imediatamente anterior da mesma moeda. Sábado, domingo e feriado sem publicação não entram como zero, nem como queda, nem como “ontem”. A lista mostra só os dias em que o provedor publicou.

**Variação.** Para cotação atual `C` e cotação anterior `A`, ambas na escala 2:

`variação (%) = (C - A) / A × 100`

A porcentagem exibida usa escala 2 e `HALF_UP`. Sinal:

- positiva: a moeda estrangeira ficou mais cara em reais
- negativa: ficou mais barata em reais
- zero: estável
- sem observação anterior: sem variação (a interface mostra “—” e o anúncio omite a comparação)

A primeira linha do histórico da janela, a mais antiga, fica sem variação. A variação da linha mais recente é a mesma da tela inicial.

**Taxa inválida.** Taxa ausente, zero ou negativa não vira cotação. A moeda some da lista. Se as quatro faltarem, o estado é vazio.

**Data honesta.** A data exibida é a data do campo `date` do payload, formatada em `dd/MM/yyyy`, sem converter fuso. Se a observação mais recente for de sexta, a tela diz essa sexta, mesmo aberta no sábado. “Hoje” só aparece se a data da observação for a data local de São Paulo.

**Snapshot.** Uma resposta bem-sucedida substitui por completo a anterior, numa transação. Falha de rede, HTTP ou parse não apaga o snapshot já salvo. O histórico offline é o da resposta guardada, não um acúmulo dia a dia no aparelho.

**Duas datas no conteúdo.**

- Data da cotação: a data da observação.
- Guardada no aparelho: instante em que o snapshot foi persistido, exibido em `dd/MM/yyyy HH:mm` no fuso `America/Sao_Paulo`.

As duas ficam visíveis sempre que há conteúdo.

### 6.4 Estados de tela

O início e o histórico seguem a mesma máquina. Se já existe snapshot, uma carga nova não troca a tela por um carregando de tela cheia.

| Situação | Estado | O que a pessoa vê |
| --- | --- | --- |
| Sem snapshot e carga em andamento | Carregando | Indicador e o texto “Carregando cotações” |
| Snapshot com ao menos uma observação | Conteúdo | Lista, as duas datas e, se a última tentativa falhou, um aviso |
| Carga concluída, nenhuma observação utilizável, sem snapshot | Vazio | “Não há cotação neste período.” e o botão “Tentar de novo” |
| Falha de rede, HTTP ou parse, sem snapshot | Erro | “Não foi possível atualizar a cotação. Verifique a conexão e tente de novo.” e o botão “Tentar de novo” |
| Falha, com snapshot | Conteúdo | Lista guardada, as duas datas e o aviso “Não foi possível atualizar. Mostrando a cotação guardada.” |
| Abertura sem rede, com snapshot | Conteúdo | Lista guardada e as duas datas, sem indicador infinito |

“Tentar de novo” e puxar a lista disparam a mesma carga. Mensagem de erro é texto fixo. Exceção, código HTTP e corpo da resposta ficam no log de debug.

Modelo de estado esperado na tela inicial:

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Content(
        val quotes: List<QuoteUi>,
        val savedAtText: String,
        val notice: String?,
    ) : HomeUiState
}
```

`notice` fica nulo quando a última carga desta sessão terminou com sucesso. `QuoteUi` carrega, para cada moeda, o texto visual, o texto falado, a data da observação e a variação visual e falada. O histórico usa um estado equivalente, filtrado pela moeda recebida na navegação.

### 6.5 Valor falado e apresentação

O formatador recebe a cotação já na escala 2 e o nome falado da moeda. A frase do valor é exatamente:

`{nome falado}, {quantia por extenso}`

| Cotação | Quantia por extenso |
| --- | --- |
| 0,00 | 0 reais |
| 0,01 | 1 centavo |
| 0,02 | 2 centavos |
| 1,00 | 1 real |
| 1,01 | 1 real e 1 centavo |
| 2,00 | 2 reais |
| 5,42 | 5 reais e 42 centavos |

Com a parte inteira 0 e centavos maiores que 0, a frase traz só os centavos. “1” concorda no singular: real, centavo. O exemplo de aceite do dólar a 5,42 é “dólar, 5 reais e 42 centavos”.

A variação falada, quando existe:

- positiva: “alta de 0,32 por cento”
- negativa: “queda de 0,32 por cento” (valor absoluto)
- zero: “estável”

O texto visual do valor é `R$` + espaço normal + inteiros + vírgula + duas casas (`R$ 5,43`). O texto visual da variação usa vírgula decimal, sinal explícito (`+0,32%`, `−0,32%` com o sinal de menos tipográfico, `0,00%`) ou “—” quando não há comparação. Cor pode reforçar alta e queda; o sinal e o texto existem sem ela.

No TalkBack, o valor é anunciado uma vez, pela frase por extenso. A linha do histórico anuncia data por extenso em português (“30 de outubro de 2026”), a quantia e a variação. Textos, datas e números do app usam português do Brasil mesmo quando o idioma do aparelho for outro. A ação de atualizar se chama “Atualizar cotações”. O botão de erro se chama “Tentar de novo”.

A fonte aparece no conteúdo, em texto curto: “Fonte: Banco Central Europeu, via Frankfurter”. O número não é apresentado como PTAX nem como cotação oficial brasileira.

### 6.6 Critérios de aceite das funcionalidades

- A tela inicial lista dólar, euro, libra esterlina e franco suíço, com valor em reais na escala 2, variação e data da observação.
- O histórico da moeda mostra só os dias publicados na janela de 30 dias, do mais novo ao mais antigo, com variação calculada no app.
- Puxar a lista repete a carga e, se ela falhar, o snapshot anterior continua na tela com o aviso.
- Sem snapshot, a falha mostra a tela de erro e “Tentar de novo”. Com rede de volta, o botão chega ao conteúdo.
- Uma resposta utilizável vazia mostra o estado vazio, distinto do erro.
- Sem rede e com snapshot, o app abre no conteúdo e as duas datas estão visíveis.
- O anúncio do valor a 5,42 é “dólar, 5 reais e 42 centavos”.
- Nenhuma taxa é inventada para dia sem publicação.

## 7. Requisitos técnicos

### 7.1 Plataforma e bibliotecas

- Android nativo, Kotlin, um módulo (`:app`). `minSdk` 26. `targetSdk` e `compileSdk` na versão estável atual no momento da implementação.
- Jetpack Compose e Material 3, Navigation Compose, ViewModel e `StateFlow`.
- Coroutines. Carga no `viewModelScope`. A UI coleta o estado no ciclo de vida.
- Hilt para o grafo: aplicação, ViewModels, Retrofit, OkHttp e Room.
- Retrofit, OkHttp e kotlinx.serialization. O conversor é o de kotlinx.serialization.
- Room para o snapshot.
- Teste unitário em JVM: JUnit, MockK, Turbine e kotlinx-coroutines-test.
- Versões estáveis e compatíveis entre si no momento da implementação (BOM do Compose e Kotlin alinhados ao plugin do Compose). O plano não trava número de patch.
- A única permissão é `INTERNET`.
- Timeout de chamada curto o bastante para o estado de erro ser alcançável (conexão, leitura e escrita em torno de 15 segundos). Log de HTTP só em build de debug.
- Gradle com Kotlin DSL e version catalog.

### 7.2 Integração

API pública, sem chave e sem conta: [Frankfurter v2](https://frankfurter.dev/), provedor BCE.

```text
GET https://api.frankfurter.dev/v2/providers/ecb/rates?base=brl&quotes=usd,eur,gbp,chf&from={from}&to={to}
```

A v1 dessa API está deprecada. Esta versão do app usa a v2. O corpo de sucesso é um array JSON, não o objeto antigo `rates` da v1:

```json
[
  {
    "date": "2026-10-30",
    "base": "BRL",
    "quote": "USD",
    "rate": 0.18408
  }
]
```

`rate` é desserializado como string decimal e só então vira `BigDecimal`. O campo numérico JSON não passa por `Double`.

Uma chamada traz as quatro moedas e o intervalo. O app não faz uma chamada por moeda nem uma chamada extra para a cotação “latest”: a observação mais recente é o maior `date` de cada moeda dentro da série.

Erros HTTP seguem o corpo simples da API (`status` e `message`) e caem no mesmo estado de falha da seção 6.4. A série padrão misturada da Frankfurter, sem `providers=ecb`, fica de fora: o decimal misturado pode mudar quando um provedor novo entra, e a tela precisa nomear uma fonte só.

A PTAX do Banco Central (OData, também pública e sem chave) é incremento de versão seguinte. Não entra no parser desta versão.

### 7.3 Persistência

Room guarda a última resposta bem-sucedida.

- Metadado do snapshot: um único registro, com `savedAt` (instante UTC), `from`, `to` e o nome da fonte.
- Observações: moeda, data de calendário e a taxa da API em string decimal, exatamente como chegou.
- A cotação em reais é derivada na leitura, pela regra da seção 6.3, para o arredondamento existir num lugar só.
- A substituição do snapshot é uma transação: apaga as observações anteriores e grava as novas junto com o metadado. Falha no meio não deixa a metade nova misturada com a metade antiga.

### 7.4 Arquitetura e pacotes

MVVM. A tela observa um `StateFlow`. O ViewModel chama o repositório e monta o estado com as funções puras de inversão, variação e formatação.

Pacotes por função, sob um único pacote raiz (o `applicationId` escolhido ao criar o módulo):

```text
di/            Hilt
data/remote/   Retrofit, DTO, API
data/local/    Room
data/          repositório e o contrato que o ViewModel usa
domain/        modelos, inversão, variação, formatador acessível
ui/home/
ui/history/
ui/components/ carregando, vazio, erro
ui/theme/
```

O contrato do repositório expõe a observação do snapshot (`Flow`) e a atualização suspensa da janela. A implementação vive em `data`. `domain` não depende de Android, Retrofit nem Room.

Não há árvore de use cases, mappers e camadas além do que está acima. A regra de negócio é o cálculo da variação, a inversão da taxa e a formatação do valor.

### 7.5 Testes

JVM puro, sem Robolectric, para as regras e para o ViewModel.

**ViewModel da tela inicial**, com repositório falso (MockK), Turbine e dispatcher controlado por kotlinx-coroutines-test:

| Caso | Resultado no estado |
| --- | --- |
| Sucesso | `Content` com as moedas, as datas e `notice` nulo |
| Erro sem snapshot | `Error` com a mensagem fixa |
| Sucesso com série conhecida | a variação emitida é a calculada pela regra (alta, queda e ponto sem observação anterior) |
| Erro com snapshot | `Content` preservado, com aviso e data guardada |

**Formatador do valor acessível**, tabela da seção 6.5, mais a frase completa “dólar, 5 reais e 42 centavos” e as frases de alta, queda e estável.

**Inversão e variação**, no mesmo conjunto de teste de domínio: escala 2, `HALF_UP`, divisão pela observação anterior, ausência de anterior e rejeição de taxa zero. O caso do ViewModel confirma que esse resultado chega ao estado; a tabela numérica mora no teste puro.

### 7.6 Sequência de construção

1. Funções puras de inversão, variação e formatação, com os testes de domínio.
2. DTO, API e repositório com Room, gravando e relendo um snapshot.
3. ViewModel e os testes da seção 7.5.
4. UI: estados, lista, puxar para atualizar, navegação até o histórico e anúncio do TalkBack.
5. Roteiro manual da seção 11 num aparelho ou emulador.

## 8. Requisitos não funcionais

- **Leitura rápida.** A tela inicial cabe no uso em pé: quatro linhas, valor, variação e data, sem fluxo de cadastro.
- **Honestidade do número.** Uma escala, uma fonte nomeada, data da observação separada da data em que o aparelho guardou a resposta. Dia sem publicação não ganha taxa interpolada.
- **Offline de leitura.** Com snapshot, a abertura não depende de rede. A carga que falha não substitui dado bom por tela de erro.
- **Acessibilidade.** TalkBack anuncia o valor por extenso uma vez. Área tocável de ao menos 48 dp. Alta, queda e estável são distinguíveis sem cor. Título de tela e nomes de botão vêm de recursos de string.
- **Idioma.** Interface e formatadores fixos em português do Brasil.
- **Privacidade.** Sem conta, sem analytics e sem dado pessoal. A chamada leva só o intervalo e as moedas. A política pública da Frankfurter trata a consulta como dado não coletado; o app não acrescenta identificador próprio.
- **Rede.** Somente HTTPS. Sem chave no binário. Uma chamada por atualização.
- **Precisão.** `BigDecimal` da string da API até a escala 2. `Double` não participa da cotação nem da variação.
- **Testabilidade.** Domínio e ViewModel rodam em teste JVM local.
- **Desempenho esperado.** A série de 30 dias e quatro moedas é pequena. Não há paginação, cache em memória extra nem pré-busca fora dessa janela.

## 9. Fora de escopo e restrições

Fica para uma versão seguinte, e não deve ser implementado junto com a primeira:

- Gráfico da série.
- Conversor em que a pessoa digita um valor qualquer.
- Moedas além de USD, EUR, GBP e CHF.
- PTAX do Banco Central do Brasil e o parser OData correspondente.
- Troca de fonte dentro do app, série misturada da Frankfurter e qualquer outro provedor.
- Conta, backend próprio, push, widget, favoritos e alerta de variação.

Restrições que acompanham a primeira versão:

- Um módulo, pacotes por função, sem camada extra de arquitetura.
- Quatro moedas fixas, janela fixa de 30 dias corridos, uma chamada por atualização.
- Snapshot único, substituído por inteiro a cada sucesso.
- Cópia de carregando, vazio, erro e aviso conforme a seção 6.4.

## 10. Funcionalidades desejáveis

Na ordem em que fazem sentido depois da primeira versão estar pronta:

1. **PTAX.** Passar a cotação oficial do Banco Central do Brasil a ocupar o lugar da fonte, pela API OData pública e sem chave. O parsing mais trabalhoso é o incremento. A máquina de estados, o cache, a variação e o formatador permanecem.
2. **Gráfico** dos dias já carregados na janela, sem nova regra de cotação.
3. **Conversor** de um valor digitado, usando a cotação já arredondada da moeda selecionada.
4. **Mais moedas**, só com uma revisão explícita da escala e do texto falado. Moeda cuja unidade vale fração de centavo não entra com o formatador atual.

Nenhum item desta seção entra no código enquanto a seção 11 não estiver cumprida.

## 11. Critérios de sucesso

A primeira versão está pronta quando todos os itens abaixo se confirmam. Não há métrica de uso, instalação ou audiência: o app não coleta analytics.

| Critério | Verificação |
| --- | --- |
| Quatro moedas, valor em reais, data da observação e variação contra a observação anterior | Teste do ViewModel de sucesso e roteiro manual |
| Variação alta, queda, estável e ausente conforme a seção 6.3 | Teste de domínio e o caso de variação do ViewModel |
| “dólar, 5 reais e 42 centavos” e a tabela de singular e plural | Teste do formatador |
| Erro sem snapshot mostra a mensagem fixa e tentar de novo | Teste do ViewModel e roteiro manual |
| Erro com snapshot conserva a lista, o aviso e a data guardada | Teste do ViewModel |
| Uma chamada cobre as quatro moedas e o intervalo | Revisão do cliente de API |
| A tela nomeia a fonte como Banco Central Europeu, via Frankfurter | Roteiro manual |
| TalkBack anuncia o valor por extenso uma vez na linha da moeda | Roteiro manual com TalkBack |
| Itens da seção 9 ausentes do código | Revisão do diff da primeira versão |

Roteiro manual, na ordem:

1. Abrir com rede e ver as quatro moedas, a fonte, a data da cotação e a data guardada no aparelho.
2. Ligar o TalkBack e ouvir o valor do dólar por extenso.
3. Abrir o histórico do dólar e percorrer a lista, incluindo um intervalo que cruze um fim de semana, para ver que o dia sem publicação não ganhou linha.
4. Puxar a lista para atualizar.
5. Ativar o modo avião, fechar e abrir o app: o conteúdo volta com as duas datas.
6. Apagar os dados do app, manter o modo avião e abrir: tela de erro e “Tentar de novo”.
7. Desligar o modo avião e tocar em “Tentar de novo”: a lista volta.

Esse roteiro é o que uma sessão curta de avaliação precisa conseguir reproduzir: valor com data, variação calculada, cache e os estados de carregando, vazio, erro e tentar de novo.
