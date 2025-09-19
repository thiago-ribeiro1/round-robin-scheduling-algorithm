package br.edu.unifacisa;

public class Proc {
    public final String name;
    public final int arrival;
    public final int burst;
    public int remaining;
    public int completion;

    public Proc(String name, int arrival, int burst) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
    }
}
