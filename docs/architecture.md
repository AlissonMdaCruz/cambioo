# Arquitetura — Câmbioo

SoT técnica da v1. Comportamento de produto: [prd/cambioo.md](prd/cambioo.md).

## 1. Plataforma e bibliotecas

- Android nativo, Kotlin, um módulo (`:app`). `minSdk` 26. `targetSdk` / `compileSdk` na versão estável atual.
- Jetpack Compose e Material 3, Navigation Compose, ViewModel e `StateFlow`.
- Coroutines. Carga no `viewModelScope`. UI coleta com `collectAsStateWithLifecycle`.
- Hilt: aplicação, ViewModels, Retrofit, OkHttp e Room.
- Retrofit, OkHttp e kotlinx.serialization (conversor kotlinx.serialization).
- Room para o snapshot.
- Testes JVM: JUnit, MockK, Turbine, kotlinx-coroutines-test.
- Única permissão: `INTERNET`.
- Timeout de rede ~15 s (conexão, leitura e escrita). Log HTTP só em debug.
- Gradle com Kotlin DSL e version catalog.

## 2. Integração — Frankfurter v2 (BCE)

```text
GET https://api.frankfurter.dev/v2/providers/ecb/rates?base=brl&quotes=usd,eur,gbp,chf&from={from}&to={to}
```

Corpo de sucesso: array JSON (não o objeto `rates` da v1):

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

- `rate` desserializado como string decimal e só então `BigDecimal`. Sem `Double`.
- Uma chamada cobre as quatro moedas e o intervalo. Sem chamada extra `latest`.
- Observação mais recente = maior `date` por moeda.
- Série misturada (sem `providers=ecb`) fora de escopo.
- PTAX / OData fora desta versão.

## 3. Persistência (Room)

- Metadado do snapshot: um registro — `savedAt` (UTC), `from`, `to`, nome da fonte.
- Observações: moeda, data de calendário, taxa da API em string decimal (como chegou).
- Cotação em reais derivada na leitura (regras de domínio), um só lugar de arredondamento.
- Substituição atômica em transação: apaga observações anteriores e grava novas + metadado.

## 4. Arquitetura e pacotes

MVVM. A tela observa `StateFlow`. O ViewModel chama o repositório e monta o estado com funções puras de inversão, variação e formatação.

Pacotes sob `com.amdcruz.cambioo`:

```text
di/            Hilt
data/remote/   Retrofit, DTO, API
data/local/    Room
data/          repositório e contrato usado pelo ViewModel
domain/        modelos, inversão, variação, formatador acessível
ui/home/
ui/history/
ui/components/ carregando, vazio, erro
ui/theme/
```

Contrato do repositório: `observeSnapshot(): Flow<…>` e `suspend fun refreshWindow()`.
`domain` não depende de Android, Retrofit nem Room. Sem árvore de use cases/mappers além disso.

### Modelo de estado da tela inicial

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

`notice` é nulo quando a última carga desta sessão terminou com sucesso. Histórico usa estado equivalente filtrado pela moeda.

## 5. Domínio (resumo técnico)

- Cotação: `1 / taxa`, `BigDecimal`, `HALF_UP`, escala 2.
- Exemplo: taxa API `0.18408` → cotação `5,43`.
- Janela: `to` = data local `America/Sao_Paulo`; `from` = `to − 29` dias (30 dias corridos, pontas incluídas).
- Variação: `(C − A) / A × 100`, escala 2, `HALF_UP`; sem anterior → ausente.
- Fonte exibida: “Banco Central Europeu, via Frankfurter”.

## 6. Testes JVM

| Área | Casos |
| --- | --- |
| ViewModel home | Sucesso → Content `notice=null`; erro sem snapshot → Error; erro com snapshot → Content + aviso; variação conhecida no estado |
| Formatador | Tabela singular/plural; “dólar, 5 reais e 42 centavos”; alta / queda / estável |
| Inversão e variação | Escala 2, HALF_UP, sem anterior, rejeição de taxa ≤ 0 |

Sem Robolectric para domínio/ViewModel.

## 7. Requisitos não funcionais (técnicos)

- HTTPS apenas; sem chave no binário; uma chamada por atualização.
- Precisão: `BigDecimal` da string da API até escala 2.
- Interface e formatadores fixos em PT-BR.
- Sem analytics / dado pessoal.
- Série pequena (30 dias × 4 moedas): sem paginação nem pré-busca extra.
