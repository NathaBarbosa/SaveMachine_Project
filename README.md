# SaveMachine — Manutenção Preditiva Industrial

Sistema completo de manutenção preditiva com IA desenvolvido para o processo seletivo da Samsung.
[cite_start]**Backend Python (Flask + Machine Learning) + App Android (Java)**[cite: 5, 153].

---

## ⚠️ IMPORTANTE: Alinhamento de Versões (JDK 26)

[cite_start]Para evitar erros de sincronização do Gradle e garantir que o projeto compile sem falhas, é **obrigatório** utilizar as seguintes versões:

* **Java Development Kit (JDK):** Oracle JDK 26 ou OpenJDK 26.
* **Android Gradle Plugin (AGP):** 8.10.0.
* **Gradle Wrapper:** 8.11.1 (ou 9.0).
* **Android Studio:** Jellyfish 2024.2.1 ou superior.

**Configurando no Android Studio:**
1. Vá em **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**.
2. Em **Gradle JDK**, aponte para a pasta de instalação do seu **JDK 26**.
3. Se o Sync falhar com erro de metadados, feche a IDE e limpe a pasta `C:\Users\SEU_USUARIO\.gradle\caches\`.

---

## Arquitetura

```
savemachine/
├── backend/
│   ├── app.py              ← API REST Flask
│   ├── requirements.txt
│   └── db.json             ← gerado automaticamente
├── modelo_savemachine.pkl  ← gerado pelo treinar.py
├── dados_maquinas.csv      ← gerado pelo preparar_dados.py
├── preparar_dados.py
├── treinar.py
└── android/
    └── app/src/main/
        ├── java/com/savemachine/app/
        │   ├── ui/login/       ← LoginActivity
        │   ├── ui/machines/    ← MachinesActivity + Adapter
        │   ├── ui/predict/     ← PredictActivity
        │   ├── ui/history/     ← HistoryActivity + Adapter
        │   ├── model/          ← DTOs (Request/Response)
        │   ├── network/        ← ApiClient + ApiService (Retrofit)
        │   └── utils/          ← SessionManager
        ├── res/layout/         ← XMLs de tela
        └── AndroidManifest.xml
```

---

## PARTE 1 — Backend Python

### 1.1 Instalar dependências

```bash
pip install -r backend/requirements.txt
```

### 1.2 Treinar o modelo (se ainda não tiver o .pkl)

```bash
# Baixa dataset real da UCI e prepara os dados
python preparar_dados.py

# Treina o RandomForest e salva modelo_savemachine.pkl
python treinar.py
```

### 1.3 Iniciar o servidor Flask

```bash
python backend/app.py
```

O servidor vai rodar em: `http://0.0.0.0:5000`

Usuário padrão criado automaticamente:
- **Email:** admin@savemachine.com  
- **Senha:** admin123

---

## PARTE 2 — App Android

### 2.1 Instalar o Android Studio

1. Acesse: https://developer.android.com/studio
2. Baixe e instale para Windows/Mac/Linux
3. Na primeira abertura, instale o **Android SDK** (aceite os padrões)

### 2.2 Abrir o projeto

1. Abra o Android Studio
2. Clique em **"Open"** (não "New Project")
3. Navegue até a pasta `savemachine/android/`
4. Aguarde o Gradle sincronizar (pode demorar alguns minutos na primeira vez)

### 2.3 Configurar o IP do servidor

Abra o arquivo:
```
android/app/src/main/java/com/savemachine/app/network/ApiClient.java
```

Altere a linha `BASE_URL` conforme seu caso:

| Situação | URL |
|----------|-----|
| Emulador Android Studio | `http://10.0.2.2:5000/` |
| Celular físico na mesma rede Wi-Fi | `http://SEU_IP_LOCAL:5000/` |

Para descobrir seu IP local:
- **Windows:** `ipconfig` no CMD → IPv4
- **Linux/Mac:** `ifconfig` ou `ip a`

### 2.4 Rodar no emulador

1. No Android Studio, vá em **Device Manager** (ícone de celular na barra lateral)
2. Clique em **"Create Device"**
3. Escolha **Pixel 6** → **Android 13 (API 33)**
4. Clique em ▶ **Run** (ou `Shift+F10`)

### 2.5 Rodar em celular físico

1. No celular, ative o **Modo Desenvolvedor**:  
   Configurações → Sobre o telefone → toque 7x em "Número da versão"
2. Ative **Depuração USB**
3. Conecte o cabo USB
4. No Android Studio, selecione seu celular e clique em ▶ Run

---

## Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/auth/login` | Login com email e senha |
| POST | `/auth/register` | Cadastro de novo usuário |
| GET | `/machines` | Lista todas as máquinas |
| POST | `/machines` | Adiciona nova máquina |
| DELETE | `/machines/<id>` | Remove uma máquina |
| POST | `/predict` | Executa predição com IA |
| GET | `/history?machine_id=X` | Histórico de análises |

### Exemplo de predição (curl)

```bash
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "machine_id": "MAQ-001",
    "temperatura_ar": 300.5,
    "temperatura_processo": 310.8,
    "rotacao_rpm": 1500,
    "torque": 40.5,
    "desgaste_ferramenta": 120
  }'
```

Resposta:
```json
{
  "machine_id": "MAQ-001",
  "probabilidade": 12.3,
  "status": "Normal",
  "mensagem": "Máquina operando normalmente.",
  "proxima_manutencao": "07/05/2025"
}
```

---

## Fluxo do App

```
Login
  └── Lista de Máquinas
        ├── [+] Adicionar Máquina
        ├── [Analisar] → Tela de Sensores → Resultado da IA
        ├── [Histórico] → Lista de análises anteriores
        └── [✕] Remover máquina
```

---

## Tecnologias

**Backend:**  Python 3 · Flask · Scikit-learn · RandomForest · SMOTE · Pandas · Joblib

**Android:**  Java · Retrofit 2 · OkHttp · Gson · Material Design · RecyclerView · CardView

---

## Níveis de Risco

| Status | Probabilidade | Ação |
|--------|--------------|------|
| 🔴 Risco Crítico | ≥ 80% | Manutenção imediata |
| 🟠 Risco Alto | ≥ 60% | Até 2 dias |
| 🟡 Risco Moderado | ≥ 40% | Até 5 dias |
| 🟢 Normal | < 40% | Operação normal |
