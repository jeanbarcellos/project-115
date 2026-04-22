## Estrutura Múdulo Wallet

```
com.jeanbarcellos.project115.wallet
│
├── adapter
│   └── api
│       └── WalletController.java
│
├── application
│   │
│   ├── command
│   │   ├── WalletCommand.java
│   │   ├── DepositCommand.java
│   │   ├── WithdrawCommand.java
│   │   └── TransferCommand.java
│   │
│   ├── handler
│   │   ├── WalletCommandHandler.java
│   │   ├── WalletCommandHandlerRegistry.java
│   │   ├── DepositHandler.java
│   │   ├── WithdrawHandler.java
│   │   └── TransferHandler.java
│   │
│   ├── policy
│   │   ├── WalletPolicy.java
│   │   ├── WalletPolicyEngine.java
│   │   └── AntiFraudPolicy.java
│   │
│   ├── service
│   │   ├── WalletCommandService.java   // WRITE (CQRS)
│   │   └── WalletQueryService.java     // READ  (CQRS)
│   │
│   ├── dto
│   │   ├── WalletResponse.java
│   │   ├── WalletOperationRequest.java
│   │   └── WalletTransferRequest.java
│   │
│   ├── mapper
│   │   └── WalletMapper.java
│   │
│   ├── repository
│   │   ├── WalletRepository.java
│   │   ├── TransactionRepository.java
│   │   └── LedgerEntryRepository.java
│   │
│   └── translator
│       └── WalletExceptionTranslator.java
│
├── domain
│   │
│   ├── model
│   │   ├── Wallet.java
│   │   ├── Transaction.java
│   │   ├── LedgerEntry.java
│   │   └── TransactionType.java
│   │
│   └── builder
│       ├── TransactionBuilder.java
│       ├── TransactionContext.java
│       ├── TransactionBuilderRegistry.java
│       ├── DepositTransactionBuilder.java
│       ├── WithdrawTransactionBuilder.java
│       └── TransferTransactionBuilder.java
│
└── (dependências do core)
    ├── com.jeanbarcellos.core.error.*
    └── com.jeanbarcellos.core.exception.*
```

🧠 COMO TUDO FUNCIONA (FLUXO REAL)

🔹 Exemplo: Depósito

```java
Controller
  ↓
CommandService
  ↓
DepositHandler
  ↓
PolicyEngine (antifraude)
  ↓
TransactionBuilder
  ↓
Transaction (ledger entries)
  ↓
Persistência
  ↓
Snapshot salvo
  ↓
Resposta
```

---

# 🧠 1. VISÃO GERAL DO SISTEMA

Você construiu um sistema baseado em três pilares:

```
1. Command (intenção)
2. Ledger (registro contábil)
3. Snapshot (otimização de leitura)
```

Fluxo macro:

```
API → Command → Handler → Policy → Builder → Transaction → LedgerEntries → Persistência → Snapshot → Response
```

---

# 🧩 2. ENTIDADES (O QUE CADA COISA É)

## 🟦 Wallet (Aggregate)

Representa a **conta do usuário**, mas com uma decisão importante:

Wallet NÃO é a fonte da verdade do saldo

Ela contém:

- `id` → identificador
- `version` → controle otimista (ETag)
- `balanceSnapshot` → cache do saldo

👉 O saldo real vem do ledger.

---

## 🟪 Transaction

Representa uma **operação financeira completa e atômica**.

Contém:

- `idempotencyKey` → evita duplicação
- `payloadHash` → garante integridade
- `entries` → lista de lançamentos (ledger)
- `snapshot` → resposta congelada da operação

👉 Pense assim:

```
Transaction = "o que aconteceu"
```

---

## 🟨 LedgerEntry

Representa um **lançamento contábil individual**.

Contém:

- walletId
- tipo: `DEBIT` ou `CREDIT`
- valor

👉 Esse é o nível mais importante do sistema.

```
LedgerEntry = "como o dinheiro se moveu"
```

---

## 🟩 TransactionBuilder

Define **como uma operação vira lançamentos**.

Exemplo:

```
Withdraw:
  Wallet → DEBIT
  System → CREDIT
```

👉 Ele é o responsável pelas regras financeiras.

---

## 🟥 Command

Representa uma **intenção do usuário**:

```
"quero sacar"
"quero depositar"
"quero transferir"
```

👉 Não executa nada — só carrega dados.

---

## 🟧 Handler

Orquestra a execução:

```
- valida idempotência
- aplica policies
- chama builder
- persiste
- monta resposta
```

👉 É o “use case executor”.

---

## 🟫 Policy Engine

Executa validações transversais:

- antifraude
- limites
- regras externas

👉 Não é domínio puro.

---

## 🟦 QueryService

```
Responsável por leitura:
```

```
- findById
- getBalance
```

👉 separado do write (CQRS)

---

# 💰 3. CONCEITO DE LEDGER (O PONTO CENTRAL)

Você não está fazendo CRUD de saldo.

Você está fazendo:

```
contabilidade
```

---

## 🔹 Double-entry (partida dobrada)

Toda operação gera:

```
DEBIT + CREDIT = 0
```

Exemplo:

### Depósito de 100

```
Wallet A     +100 (CREDIT)
System       -100 (DEBIT)
```

---

### Saque de 50

```
Wallet A     -50 (DEBIT)
System       +50 (CREDIT)
```

---

### Transferência

```
Wallet A     -30
Wallet B     +30
System        0 (balanceado)
```

---

## ⚠️ Invariante CRÍTICO

```
Soma de todos os lançamentos = 0
```

Se isso quebrar:

👉 seu sistema financeiro está errado

---

# 🔁 4. CONCEITO DE TRANSAÇÃO

Aqui “transação” NÃO é só banco de dados.

É:

```
um evento financeiro completo
```

Ela garante:

### ✔ Atomicidade lógica

Tudo ou nada.

---

### ✔ Idempotência

```
mesma operação → mesmo resultado
```

---

### ✔ Reprodutibilidade

Você consegue dizer:

```
"essa operação gerou esse saldo"
```

---

# 🔐 5. IDEMPOTÊNCIA (PONTO CRÍTICO)

Você protege o sistema contra:

```
- retry de API
- duplicação
- race condition
```

Mecanismo:

```
idempotencyKey + payloadHash
```

---

## Casos

### ✔ Primeira chamada

→ executa normalmente

---

### ✔ Segunda chamada (igual)

→ retorna snapshot

---

### ❌ Payload diferente

→ erro

---

# ⚙️ 6. FLUXO COMPLETO (PASSO A PASSO)

## Exemplo: saque

```
1. Controller recebe request
2. Cria WithdrawCommand
3. CommandService executa
4. Registry resolve WithdrawHandler
5. Handler:
   - verifica idempotência
   - valida versão (ETag)
   - executa PolicyEngine
6. Cria TransactionContext
7. TransactionBuilder:
   - valida saldo
   - cria LedgerEntries
8. Transaction:
   - valida soma = 0
9. Persiste:
   - transaction
   - entries
10. Atualiza snapshot na Wallet
11. Salva snapshot na transaction
12. Retorna response
```

---

# 🧠 7. POR QUE ESSE DESIGN FUNCIONA

Você resolveu problemas clássicos:

---

## ✔ Evitou saldo inconsistente

```
saldo não é fonte da verdade
ledger é
```

---

## ✔ Evitou duplicação de operações

```
idempotência forte
```

---

## ✔ Evitou service inflado

```
Command + Handler
```

---

## ✔ Evitou ledger inflado

```
TransactionBuilder
```

---

## ✔ Separou leitura de escrita

```
CQRS
```

---

# ⚠️ 8. LIMITES DO SEU SISTEMA

Agora vem a parte que normalmente ninguém fala:

---

## ❌ Snapshot pode ficar inconsistente

Se der problema entre:

```
transaction salva
wallet não atualizada
```

---

## ❌ Não é auditável como event sourcing

Você tem histórico, mas não tem:

```
replay completo garantido
```

---

## ❌ Não escala distribuído

Sem:

- fila
- particionamento
- consistência eventual

---

# 🔍 9. O QUE VOCÊ REALMENTE CONSTRUIU

Não é CRUD.

Também não é ainda fintech completa.

É:

```
um core financeiro consistente e extensível
```

---

# 📌 RESUMO FINAL

```
Wallet → identidade + snapshot
Transaction → operação financeira
LedgerEntry → movimento de dinheiro
Builder → regra da operação
Handler → orquestração
Policy → validação transversal
```

---

# 🔥 Pergunta que você deveria se fazer agora

Seu sistema hoje é:

```
orientado a operações
```

A próxima evolução natural é:

```
orientado a eventos
```

---

Se quiser, o próximo passo lógico é:

👉 migrar isso para Event Sourcing (sem quebrar o que já fez)

Isso separa quem entende arquitetura de quem só replica padrão.
