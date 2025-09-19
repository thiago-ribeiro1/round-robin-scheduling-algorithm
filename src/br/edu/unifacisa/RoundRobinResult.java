package br.edu.unifacisa;

import java.util.List;
import java.util.Map;

public class RoundRobinResult {
    public final List<ExecutionStep> steps;
    public final Map<String, Proc> finalState;

    public RoundRobinResult(List<ExecutionStep> steps, Map<String, Proc> finalState) {
        this.steps = steps;
        this.finalState = finalState;
    }
}
