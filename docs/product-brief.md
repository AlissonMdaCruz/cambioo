# Product brief — Câmbioo

Visão de produto da primeira versão.

Este documento descreve **o que** o Câmbioo é e **para quem**. Comportamento detalhado: [prd/cambioo.md](prd/cambioo.md). Stack e pacotes: [architecture.md](architecture.md).

---

## Produto

**Câmbioo** — câmbio do dia, em reais.

App Android de consulta de câmbio para leitura rápida no celular: cotação mais recente em reais, variação contra a observação anterior e histórico publicado numa janela de 30 dias.

Não é ferramenta de operação financeira. Não há conta, saldo, ordem nem conversão de valor digitado. O uso é **leitura**.

---

## Público

Pessoa no Brasil que consulta no celular o valor do dólar e de poucas moedas em reais, com pressa, e que pode abrir o app de novo sem conexão.

Há um segundo leitor do repositório (avaliação de portfólio Android). A interface continua sendo a da pessoa que consulta a cotação.

---

## Proposta de valor

**Cotação em reais, legível em segundos, com data e variação honestas — e a última consulta boa disponível sem rede.**

- Quatro moedas (USD, EUR, GBP, CHF), sempre em reais.
- Histórico de 30 dias só com dias publicados.
- Snapshot local para offline de leitura.
- Estados claros e TalkBack com valor por extenso.
- Fonte nomeada: Banco Central Europeu, via Frankfurter (não PTAX).

---

## Na v1

Android nativo, celular, interface fixa em português do Brasil, sem conta nem backend próprio.

## Fora da v1

Gráfico, conversor, moedas extras, PTAX, troca de fonte, conta, push, widget, favoritos, alerta, iOS/web.

---

## Resumo

O Câmbioo é um app Android de **leitura rápida de câmbio em reais** — quatro moedas, data e variação honestas, histórico de 30 dias e último snapshot disponível sem rede — sem conta e sem operação financeira.
