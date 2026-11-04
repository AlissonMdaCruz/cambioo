# Documentação — Câmbioo

Ordem de leitura recomendada:

1. [product-brief.md](product-brief.md) — visão de produto (o quê / para quem)
2. [prd/cambioo.md](prd/cambioo.md) — comportamento, regras, specs e aceite (SoT de produto)
3. [architecture.md](architecture.md) — stack, API, persistência, pacotes e testes (SoT técnica)
4. [design/style-reference.md](design/style-reference.md) — inspiração visual (não redefine escopo)

## Papel de cada arquivo

| Arquivo | Contém | Não contém |
| --- | --- | --- |
| `product-brief.md` | Visão, público, valor, in/out de escopo em alto nível | Fórmulas, endpoint, pacotes, estados Kotlin |
| `prd/cambioo.md` | Regras de negócio, fluxos, specs, critérios de aceite | Versões de lib, schema Room |
| `architecture.md` | Stack, Frankfurter v2, Room, pacotes, matriz de testes | Narrativa de problema de produto |
| `design/style-reference.md` | Cores, tipografia, hierarquia visual | Requisitos de produto; componentes fora da v1 |

Histórico: [archive/plano-de-contexto.md](archive/plano-de-contexto.md) (supersedido).
