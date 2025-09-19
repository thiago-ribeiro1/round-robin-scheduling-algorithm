package br.edu.unifacisa;

public class ExecutionStep {
    public final String procName;
    public final int startTime;
    public final int endTime;
    public final int used;

    public ExecutionStep(String procName, int startTime, int endTime, int used) {
        this.procName = procName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.used = used;
    }
}
