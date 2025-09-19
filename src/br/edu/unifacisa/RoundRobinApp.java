package br.edu.unifacisa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RoundRobinApp extends JFrame {
    private final JTextField quantumField = new JTextField("2", 4);
    private final JTable processTable;
    private final DefaultTableModel processModel;
    // HTML para permitir cores por processo
    private final JEditorPane logPane = new JEditorPane("text/html", "");
    private final JTable metricsTable;
    private final DefaultTableModel metricsModel;
    private final JLabel avgResponseLabel = new JLabel("—");
    private final JLabel avgWaitLabel = new JLabel("—");
    private final CircularQueuePanel circlePanel = new CircularQueuePanel();

    private java.util.List<ExecutionStep> scheduleSteps = new ArrayList<>();
    private Map<String, Integer> remaining;

    public RoundRobinApp() {
        super("Round Robin Unifacisa");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabela com processos
        processModel = new DefaultTableModel(new Object[]{"Processo", "Duração"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        processTable = new JTable(processModel);
        processTable.setRowHeight(24);
        processTable.setEnabled(false); // não editável valores fixos conforme requisitos
        JScrollPane procScroll = new JScrollPane(processTable);
        procScroll.setPreferredSize(new Dimension(260, 110));
        // Processos P1, P2 e P3
        processModel.addRow(new Object[]{"P1", 10});
        processModel.addRow(new Object[]{"P2", 5});
        processModel.addRow(new Object[]{"P3", 8});

        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        configPanel.add(new JLabel("Quantum:"));
        quantumField.setHorizontalAlignment(JTextField.CENTER);
        quantumField.setEditable(false); // quantum = 2 fixo
        quantumField.setEnabled(false);
        quantumField.setToolTipText("Quantum = 2");
        configPanel.add(quantumField);

        JButton simulateBtn = new JButton("Simular");
        simulateBtn.addActionListener(this::onSimulate);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            processModel.setRowCount(0);
            processModel.addRow(new Object[]{"P1", 10});
            processModel.addRow(new Object[]{"P2", 5});
            processModel.addRow(new Object[]{"P3", 8});
            clearResults();
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.add(simulateBtn);
        btns.add(resetBtn);

        JPanel leftTop = new JPanel(new BorderLayout(0, 8));
        leftTop.add(new JLabel("⚙️ Processos (P1=10, P2=5, P3=8)"), BorderLayout.NORTH);
        leftTop.add(procScroll, BorderLayout.CENTER);
        JPanel cfgWrap = new JPanel(new BorderLayout());
        cfgWrap.add(configPanel, BorderLayout.WEST);
        cfgWrap.add(btns, BorderLayout.CENTER);
        leftTop.add(cfgWrap, BorderLayout.SOUTH);
        top.add(leftTop, BorderLayout.WEST);

        JPanel metrics = new JPanel(new BorderLayout(0, 6));
        metrics.setBorder(new EmptyBorder(0, 8, 0, 0));
        metrics.add(new JLabel("📊 Métricas"), BorderLayout.NORTH);

        metricsModel = new DefaultTableModel(new Object[]{"Processo", "Chegada", "Duração", "Término", "Turnaround", "Espera"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        metricsTable = new JTable(metricsModel);
        metricsTable.setRowHeight(24);
        JScrollPane metScroll = new JScrollPane(metricsTable);
        metScroll.setPreferredSize(new Dimension(520, 110));

        JPanel agg = new JPanel(new GridLayout(3, 1, 6, 6));
        // medidas de tempo médio de resposta e espera
        agg.add(labeled("Tempo médio de resposta (23 + 15 + 21) / 3 : ", avgResponseLabel));
        agg.add(labeled("Tempo médio de espera (13 + 10 + 13) / 3 : ", avgWaitLabel));
        metrics.add(metScroll, BorderLayout.CENTER);
        metrics.add(agg, BorderLayout.SOUTH);
        top.add(metrics, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        circlePanel.setPreferredSize(new Dimension(520, 420));
        add(circlePanel, BorderLayout.CENTER);

        // LOG em destaque com HTML e cores
        logPane.setEditable(false);
        logPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); // respeitar fontes do L&F
        JScrollPane logScroll = new JScrollPane(logPane);
        logScroll.setBorder(new TitledBorderLike("Sequência de Execução (Gantt simplificado)"));
        logScroll.setPreferredSize(new Dimension(100, 200)); // maior para dar mais destaque
        add(logScroll, BorderLayout.SOUTH);

        setSize(940, 800);
        setLocationRelativeTo(null);
    }

    private JPanel labeled(String title, JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JLabel l = new JLabel(title);
        l.setFont(l.getFont().deriveFont(Font.PLAIN));
        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
        p.add(l);
        p.add(comp);
        return p;
    }

    private void clearResults() {
        // reset HTML do log
        logPane.setText("");
        metricsModel.setRowCount(0);
        avgResponseLabel.setText("—");
        avgWaitLabel.setText("—");
        circlePanel.reset(java.util.Collections.emptyList(), java.util.Collections.emptyMap());
    }

    private void onSimulate(ActionEvent e) {
        try {
            clearResults();

            final int quantum = 2;

            // Constrói a lista com cenário proposto (P1=10, P2=5, P3=8)
            java.util.List<Proc> procs = new ArrayList<>();
            procs.add(new Proc("P1", 0, 10));
            procs.add(new Proc("P2", 0, 5));
            procs.add(new Proc("P3", 0, 8));

            // Algoritmo puro
            RoundRobinResult rr = RoundRobinScheduler.compute(procs, quantum);

            // Cores dos processos
            Map<String, String> colorByProc = new LinkedHashMap<>();
            colorByProc.put("P1", "#2E86C1");
            colorByProc.put("P2", "#27AE60");
            colorByProc.put("P3", "#E67E22");

            // Cabeçalho + CSS + linhas coloridas com (restam=...)
            StringBuilder html = new StringBuilder();
            html.append("<html><head><style>")
                    .append("body{font-family:monospace;font-size:13px;padding:6px 10px;color:#111;}")
                    .append(".hdr{font-weight:bold;font-size:14px;margin:4px 0 8px 0;}")
                    .append(".ln{padding:2px 0;}")
                    .append(".pname{font-weight:bold;}")
                    .append(".rest{color:#B03A2E;font-weight:bold;}")
                    .append(".time{font-weight:bold;}")
                    .append("</style></head><body>");
            html.append("<div class='hdr'>=== SEQUÊNCIA DE EXECUÇÃO ===</div>");
            html.append("<div style='margin:2px 0 8px 0;'><b>Total de execuções:</b> ")
                    .append(rr.steps.size())
                    .append("</div>");

            Map<String, Integer> remainingForLog = new LinkedHashMap<>();
            for (Proc p0 : procs) remainingForLog.put(p0.name, p0.burst);

            for (ExecutionStep s : rr.steps) {
                int currRem = remainingForLog.getOrDefault(s.procName, 0);
                int remAfter = Math.max(0, currRem - s.used);
                remainingForLog.put(s.procName, remAfter);
                String color = colorByProc.getOrDefault(s.procName, "#2C3E50");

                // Linha do log: tempo, processo colorido, usado no passo e restante
                html.append("<div class='ln'>")
                        .append("<span class='time'>t=")
                        .append(String.format("%2d..%-2d", s.startTime, s.endTime))
                        .append(" :</span> ")
                        .append("<span class='pname' style='color:").append(color).append(";'>")
                        .append(s.procName)
                        .append("</span> ")
                        .append("(usado=").append(s.used).append(") ")
                        .append("<span class='rest'>(restam=").append(remAfter).append(")</span>")
                        .append("</div>");
            }
            // Finaliza o HTML e atualiza o log na interface
            html.append("</body></html>");
            logPane.setText(html.toString());
            logPane.setCaretPosition(0);

            // Métricas
            double sumTurn = 0, sumWait = 0;
            for (Proc p : rr.finalState.values()) {
                int turnaround = p.completion - p.arrival;
                int wait = turnaround - p.burst;
                sumTurn += turnaround;
                sumWait += wait;
                metricsModel.addRow(new Object[]{p.name, p.arrival, p.burst, p.completion, turnaround, wait});
            }
            int n = rr.finalState.size();
            avgResponseLabel.setText(String.format(Locale.US, "%.2f", sumTurn / n));
            avgWaitLabel.setText(String.format(Locale.US, "%.2f", sumWait / n));

            // Animação visual
            this.scheduleSteps = rr.steps;
            this.remaining = new LinkedHashMap<>();
            for (Proc p : procs) remaining.put(p.name, p.burst);
            circlePanel.reset(new ArrayList<>(remaining.keySet()), remaining);
            circlePanel.updateStep(0, rr.steps.size()); // se seu CircularQueuePanel tiver esse método
            animateSchedule();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Entrada inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void animateSchedule() {
        if (scheduleSteps.isEmpty()) return;
        final int total = scheduleSteps.size();
        final int[] idx = {0};
        final int finalTime = scheduleSteps.get(total - 1).endTime;

        javax.swing.Timer timer = new javax.swing.Timer(900, ev -> { // 900 ms para ficar mais visível
            ExecutionStep step = scheduleSteps.get(idx[0]);

            // destaca no painel
            circlePanel.highlight(step.procName);

            // atualiza restante para o badge "rem"
            int rem = Math.max(0, remaining.get(step.procName) - step.used);
            remaining.put(step.procName, rem);

            // atualiza o contador "Execução X de Y"
            circlePanel.updateStep(idx[0] + 1, total);

            circlePanel.repaint();
            idx[0]++;

            if (idx[0] >= total) {
                ((javax.swing.Timer) ev.getSource()).stop();

                // === EXIBE A FINALIZAÇÃO ===
                // 1) Banner no log
                String content = logPane.getText();
                String banner = "<div class='hdr' style='color:#2E7D32;'>"
                        + "=== FINALIZADO em t=" + finalTime + " (total de execuções: " + total + ") ==="
                        + "</div>";
                content = content.replace("</body></html>", banner + "</body></html>");
                logPane.setText(content);
                logPane.setCaretPosition(logPane.getDocument().getLength());

                // 2) Diálogo
                JOptionPane.showMessageDialog(
                        RoundRobinApp.this,
                        "Simulação concluída!\nTodos os processos foram finalizados em " + total + " execuções",
                        "Finalizado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        timer.setInitialDelay(500); // um respiro antes da primeira execução
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new RoundRobinApp().setVisible(true);
        });
    }
}
