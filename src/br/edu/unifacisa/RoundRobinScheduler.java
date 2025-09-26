package br.edu.unifacisa;

import java.util.*;

public class RoundRobinScheduler {

    public static RoundRobinResult compute(List<Proc> input, int quantum) {

        // Cria um mapa de processos para preservar a ordem de inserção
        Map<String, Proc> map = new LinkedHashMap<>();
        for (Proc p : input) map.put(p.name, new Proc(p.name, p.arrival, p.burst));

        // Fila de prontos (ready queue) — simula a fila circular do Round Robin
        Queue<Proc> ready = new ArrayDeque<>(map.values());

        // Lista de passos de execução para construir o log (quem executou, quando começou e terminou)
        List<ExecutionStep> steps = new ArrayList<>();

        // Tempo atual do "relógio" da CPU
        int t = 0;

        // Loop principal — roda até a fila de prontos ficar vazia
        while (!ready.isEmpty()) {
            // Retira o próximo processo da fila
            Proc p = ready.poll();

            // Se já terminou, ignora (se não entra de novo na fila)
            if (p.remaining == 0) continue;

            // Define quanto tempo o processo vai usar nesta rodada
            int used = Math.min(quantum, p.remaining);

            // Marca início e fim da execução neste ciclo
            int start = t;
            t += used; // avança o tempo global
            p.remaining -= used; // reduz o tempo restante do processo

            // Registra este passo da execução
            steps.add(new ExecutionStep(p.name, start, t, used));

            // Se o processo ainda não terminou, volta para o fim da fila
            if (p.remaining > 0) ready.offer(p);

            // Se terminou, salva o tempo de conclusão
            else p.completion = t;
        }
        // Retorna o resultado da simulação (passos + mapa de processos)
        return new RoundRobinResult(steps, map);
    }
}
