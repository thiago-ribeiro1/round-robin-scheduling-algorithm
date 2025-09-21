package br.edu.unifacisa;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CircularQueuePanel extends JPanel {
    // Lista com os nomes dos processos na fila
    private java.util.List<String> names = new ArrayList<>();

    // Mapeia cada processo com seu tempo restante de execução (Remaining)
    private Map<String, Integer> remaining = new HashMap<>();

    // Processo atualmente em destaque (executando na CPU)
    private String highlighted = null;

    // Passo atual da simulação
    private int currentStep = 0;

    // Total de passos da simulação
    private int totalSteps = 0;

    public CircularQueuePanel() {
        // Define borda e cor de fundo do painel
        setBorder(new TitledBorderLike("Round Robin (Fila Circular)"));
        setBackground(new Color(0x0E0B1F));
    }

    // Reseta a fila circular
    public void reset(java.util.List<String> names, Map<String, Integer> remaining) {
        this.names = new ArrayList<>(names);
        this.remaining = remaining;
        this.highlighted = null;
        repaint();
    }

    // Atualiza o passo atual e total de passos (para exibir na tela)
    public void updateStep(int current, int total) {
        this.currentStep = current;
        this.totalSteps = total;
        repaint();
    }

    // Define qual processo deve ser destacado como em execução
    public void highlight(String name) {
        this.highlighted = name;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcula centro e raio do círculo da fila
        int w = getWidth(), h = getHeight();
        int cx = w / 2, cy = h / 2;
        int radius = Math.min(w, h) / 2 - 60;

        // Desenha círculo central representando a CPU
        g2.setColor(new Color(255, 255, 255, 28));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Texto "CPU" no centro
        g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
        drawCentered(g2, "CPU", cx, cy - 4, Color.WHITE);

        // Desenha os processos em volta do círculo
        if (!names.isEmpty()) {
            double step = Math.PI * 2 / names.size(); // ângulo entre cada processo
            int nodeR = 36; // raio de cada "nó" (círculo do processo)

            for (int i = 0; i < names.size(); i++) {
                double ang = -Math.PI / 2 + i * step; // posição angular
                int nx = cx + (int) (Math.cos(ang) * radius);
                int ny = cy + (int) (Math.sin(ang) * radius);
                String name = names.get(i);
                Integer rem = remaining.getOrDefault(name, 0);

                // Define cores dependendo se o processo está destacado ou não
                boolean isHi = name.equals(highlighted);
                Color fill = isHi ? new Color(0x2E86C1) : new Color(0x1B4F72);
                Color stroke = isHi ? new Color(0xA9CCE3) : new Color(0x5D6D7E);

                // Desenha círculo do processo
                g2.setColor(fill);
                g2.fillOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);
                g2.setColor(stroke);
                g2.setStroke(new BasicStroke(isHi ? 3f : 1.8f));
                g2.drawOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);

                // Nome do processo (P1, P2, P3)
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                drawCentered(g2, name, nx, ny - 2, Color.WHITE);

                // Tempo restante (Remaining) abaixo do nome
                g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
                drawCentered(g2, "rem=" + rem, nx, ny + 14, new Color(230, 230, 230));

                // Se for o processo em execução, desenha linha até a CPU
                if (isHi) {
                    g2.setColor(new Color(0xA9CCE3));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(nx, ny, cx, cy);
                }
            }
        }

        // Exibe o passo da execução atual (Execução X de Y)
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        String stepTxt = (totalSteps > 0)
                ? ("Execução " + Math.max(currentStep, 0) + " de " + totalSteps)
                : "Pronto"; // se não houver passos, mostra "Pronto"
        FontMetrics fmc = g2.getFontMetrics();
        int tw2 = fmc.stringWidth(stepTxt);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawString(stepTxt, cx - tw2 / 2, cy + 22);

        g2.dispose();
    }

    // Função auxiliar para desenhar texto centralizado em (x, y)
    private void drawCentered(Graphics2D g2, String text, int x, int y, Color color) {
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getAscent() - fm.getDescent();
        g2.setColor(color);
        g2.drawString(text, x - tw / 2, y + th / 2);
    }
}
