# Algoritmo de Escalonamento Round Robin

O Round Robin é um algoritmo que alterna os processos em ordem circular, dando a cada um, um tempo fixo (quantum) de uso da CPU. Ao término desse tempo, o processo é interrompido e enviado ao fim da fila, garantindo que todos tenham chance de executar sem prioridades fixas.

Nós seguimos esse princípio: usamos uma interface gráfica em Java que mostra a execução de 3 processos em uma fila circular, com quantum de 2. Cada processo usa 2 unidades de tempo por vez, se não finalizar, volta para a fila até concluir. No total, são 12 execuções.

---

<img width="1151" height="980" alt="Image" src="https://github.com/user-attachments/assets/5eaedaa2-03d8-42d7-bba9-1ba7d2855466" />

---

## Ordem das execuções

**Total de execuções: 12**

- P1 duração: 10
- P2 duração: 5
- P3 duração: 8

```
t= 0..2  : P1 (usado=2)  (restam=8)
t= 2..4  : P2 (usado=2)  (restam=3)
t= 4..6  : P3 (usado=2)  (restam=6)
t= 6..8  : P1 (usado=2)  (restam=6)
t= 8..10 : P2 (usado=2)  (restam=1)
t=10..12 : P3 (usado=2)  (restam=4)
t=12..14 : P1 (usado=2)  (restam=4)
t=14..15 : P2 (usado=1)  (restam=0)  P2 finaliza quando faltava apenas 1
t=15..17 : P3 (usado=2)  (restam=2)
t=17..19 : P1 (usado=2)  (restam=2)
t=19..21 : P3 (usado=2)  (restam=0)  P3 finaliza
t=21..23 : P1 (usado=2)  (restam=0)  P1 finaliza
```

Ao final, o programa calcula métricas como tempo médio de resposta e tempo médio de espera. O algoritmo é muito rápido, mas optamos por deixar a execução de forma mais lenta na interface para facilitar a visualização do ciclo completo.

---

## Execuções Round Robin (Quantum = 2)

| Tempo  | Processo | Duração inicial | Executado | Restante | Observação                                                     |
|--------|-----------|-------------------|-------------|--------------|----------------------------------------------------------------|
| 0–2    | P1        | 10                | 2           | 8            | P1 começou com 10, executou 2 e restaram 8                      |
| 2–4    | P2        | 5                 | 2           | 3            | P2 começou com 5, executou 2 e restaram 3                       |
| 4–6    | P3        | 8                 | 2           | 6            | P3 começou com 8, executou 2 e restaram 6                       |
| 6–8    | P1        | 10                | 2           | 6            | P1 estava com 8, executou 2 e restaram 6                        |
| 8–10   | P2        | 5                 | 2           | 1            | P2 estava com 3, executou 2 e restou 1                          |
| 10–12  | P3        | 8                 | 2           | 4            | P3 estava com 6, executou 2 e restaram 4                        |
| 12–14  | P1        | 10                | 2           | 4            | P1 estava com 6, executou 2 e restaram 4                        |
| 14–15  | P2        | 5                 | 1           | 0            | P2 estava com 1, executou 1 e finalizou                         |
| 15–17  | P3        | 8                 | 2           | 2            | P3 estava com 4, executou 2 e restaram 2                        |
| 17–19  | P1        | 10                | 2           | 2            | P1 estava com 4, executou 2 e restaram 2                        |
| 19–21  | P3        | 8                 | 2           | 0            | P3 estava com 2, executou 2 e finalizou                         |
| 21–23  | P1        | 10                | 2           | 0            | P1 estava com 2, executou 2 e finalizou                         |

---

## Execução

### Requisitos
- Java JDK 17

### Executar na IDE
Abra o projeto na sua IDE e rode a classe `RoundRobinApp.java`.

### Executar no Terminal Windows PowerShell (Opcional)

1. Abra o PowerShell na raiz do projeto.
2. Execute este comando abaixo para compilar:

```powershell
javac -d out -encoding UTF-8 .\src\br\edu\unifacisa\*.java
```

3. Em seguida use este outro comando para executar a aplicação:

```powershell
java -cp out br.edu.unifacisa.RoundRobinApp
```

---

## Round Robin

### Funcionamento geral
- Executa processos em ordem circular (fila)
- Cada processo recebe um tempo fixo (quantum)
- Se não termina no seu quantum, é preemptado e volta ao fim da fila
- Se um processo bloqueia por I/O, sai temporariamente da fila e retorna quando estiver pronto

### Características
- **Justiça:** todos recebem tempo de CPU de forma igual
- **Tempo de resposta:** previsível e adequado a tarefas interativas
- **Preempção:** interrompe processos ao fim do quantum ou em bloqueios, evitando que monopolizem a CPU
