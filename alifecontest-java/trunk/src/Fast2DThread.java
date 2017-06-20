/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package src;

import exceptions.MoveMicroorganismException;
import lib.Colony;
import lib.Defs;
import lib.Environment;
import lib.battles.BattleRun;
import lib.contest.ContestUI;
import lib.contest.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Vector;

import static java.lang.Math.*;


public class Fast2DThread extends Thread {
    public final int pause = 1;
    public final int play = 0;
    public boolean end = false;
    private int estate = play;

    private int timewait = 2000;
    private ContestUI cui;
    private JDialog father;
    private JPanel panel;
    private Image dobleBuffer;
    private Graphics2D miCG;
    private DefaultListModel battles;

    private Color color1 = Color.BLUE;
    private Color color2 = Color.RED;
    private Color colorNutri = Color.YELLOW;
    private Color backGround = new Color(238, 238, 238);
    private Color petriColor = new Color(184, 207, 229);

    private int historyCount = 0;
    private int historyIndex = 0;
    private int energyMax = 0;
    private int[] history1 = new int[Defs.DIAMETER + 4];
    private int[] history2 = new int[Defs.DIAMETER + 4];

    private int light = 3;

    private String textOponent;
    private int anchoText;
    private int anchoNum;
    private Point textPosition;

    private Rectangle history;
    float interLine = 50000.0f;

    private int[] h_xPoints;
    private int[] h1_yPoints;
    private int[] h2_yPoints;
    public volatile boolean closing = false;

    private Colony firstOponent;
    private Colony secondOponent;

    private final Environment environment;


    public Fast2DThread(ContestUI cui, JDialog p, JPanel panel, DefaultListModel battles) {
        this.cui = cui;
        this.father = p;
        this.panel = panel;
        this.battles = battles;
        this.timewait = cui.getContest().getTimeWait();

        h_xPoints = new int[Defs.DIAMETER + 4];
        h1_yPoints = new int[Defs.DIAMETER + 4];
        h2_yPoints = new int[Defs.DIAMETER + 4];

        history = new Rectangle();
        history.x = GUIdosD.K * GUIdosD.rel.x - GUIdosD.K;
        history.y = GUIdosD.K * (5 + 2 * GUIdosD.rel.y + (18 + Defs.DIAMETER) + GUIdosD.rel.y);
        history.width = GUIdosD.K * (Defs.DIAMETER) + 2 * GUIdosD.K;
        history.height = 15 * GUIdosD.K;

        for (int i = 0; i < h_xPoints.length; i++) {
            h_xPoints[i] = history.x + (i) * GUIdosD.K;
            h1_yPoints[i] = history.y;
            h2_yPoints[i] = history.y;
        }
        environment = cui.getContest().getEnvironment();

    }

    @Override
    public void run() {

        try {
            paintInit();

            while (battles.size() > 0 && !closing) {
                BattleRun b = (BattleRun) battles.firstElement();
                battles.removeElement(battles.firstElement());
                cui.setMessage(b.toString());
                environment.generateOponents(b);
                firstOponent = environment.getFirstOponent();
                secondOponent = environment.getSecondOponent();
                updateNames(b);

                try {
                    while (!end) {
                        if (estate == play) {
                            while (estate == play && !end) {
                                end = environment.moveColonies();
                                paintImage();
                            }
                        }
                        if (estate == pause && !end) {
                            end = environment.moveColonies();
                            paintImage();

                            if (!end) pauseRun();
                        }
                    }

                    Thread.sleep(5 * timewait);
                    resetHistory();
                    end = false;

                    // update the results !!
                    BattleRun r = environment.getResults();
                    cui.getContest().getTournamentManager().lastElement().add(r.name1, r.name2, r.nutrient, r.energy1(), r.energy2());
                    cui.getTournamentUI().updateLast();

                    /*
                    cui.getBattleUI().remove(b);
                    battles.remove(b);
                    */
                    panel.repaint();

                } catch (MoveMicroorganismException ex) {
                    penalize(ex.getColonyName());
                    Message.printErr(panel, ex.getMessage());
                }
            }
            father.dispose();
        } catch (InterruptedException ignored) {

        }
    }

    private void penalize(String colonyName) {

        DefaultListModel battles = cui.getBattleUI().getBattles();

        cui.getTournamentUI().penalice(colonyName);

        Vector<BattleRun> indexs = new Vector<BattleRun>();

        for (int i = 0; i < battles.size(); i++) {
            BattleRun b = (BattleRun) battles.elementAt(i);

            if (b.name1.equals(colonyName) || b.name2.equals(colonyName))
                indexs.addElement(b);
        }

        for (BattleRun b : indexs) {
            battles.removeElement(b);
        }
    }

    private void resetHistory() {
        for (int i = 0; i < history1.length; i++)
            history1[i] = 0;

        for (int i = 0; i < history2.length; i++)
            history2[i] = 0;

        this.energyMax = 0;
        this.historyCount = 0;
        this.historyIndex = 0;
    }

    public synchronized void setPause() {
        estate = pause;
    }

    public synchronized void continueRun() {
        this.notify();
    }

    public synchronized void pauseRun() throws InterruptedException {
        this.wait();
    }

    public synchronized void setPlay() {
        estate = play;
    }

    public boolean isEnd() {
        return end;
    }

    public void paintInit() {
        panel.setBackground(backGround);
        panel.setForeground(backGround);

        dobleBuffer = panel.createImage(panel.getWidth(), panel.getHeight());
        dobleBuffer.getGraphics().setColor(backGround);
        dobleBuffer.getGraphics().clearRect(
                panel.getBounds().x,
                panel.getBounds().y,
                panel.getBounds().width,
                panel.getBounds().height);

        dobleBuffer.setAccelerationPriority(1);
        panel.getGraphics().drawImage(dobleBuffer, 0, 0, panel);

        miCG = (Graphics2D) dobleBuffer.getGraphics();
        int size = (int) (1.5 * GUIdosD.K + 4);
        miCG.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
        miCG.setBackground(backGround);
        panel.setForeground(backGround);

        miCG.setColor(Color.LIGHT_GRAY);

        for (int i = 10; i >= 0; i--) {
            try {
                miCG.drawString("Welcome To Alifecontest-Java ", 100, 120);
                miCG.drawString("      Wait " + i + " to run", 100, 140);
                panel.getGraphics().drawImage(dobleBuffer, 0, 0, panel);
                Thread.sleep(timewait);
                miCG.clearRect(0, 100, panel.getWidth(), 40);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void paintImage() {
        try {
            // clear All!
            miCG.clearRect(panel.getBounds().x,
                    panel.getBounds().y,
                    panel.getBounds().width,
                    panel.getBounds().height);
            int t1 = 3 * GUIdosD.K;
            miCG.setColor(petriColor);

            miCG.fill(new Ellipse2D.Float(
                    GUIdosD.K * (GUIdosD.rel.x - (float) 0.5) - light,
                    GUIdosD.K * (GUIdosD.rel.y - (float) 0.5) - light,
                    GUIdosD.K * (Defs.DIAMETER + 1) + 2 * light,
                    GUIdosD.K * (Defs.DIAMETER + 1) + 2 * light));

            for (int i = 0; i < Defs.DIAMETER; i++) {
                for (int j = 0; j < Defs.DIAMETER; j++) {
                    Point pos = new Point(i, j);
                    if (inDish((float) GUIdosD.K * pos.x, (float) GUIdosD.K * pos.y)) {
                        try {
                            float nutri = environment.getAgar().getNutrient(pos.x, pos.y);


                            Color c = new Color(colorNutri.getRed(),
                                    colorNutri.getGreen(),
                                    colorNutri.getBlue(),
                                    (int) ((pow(3316.275 * nutri, 0.333333333f))));

                            miCG.setColor(c);
                            miCG.fillRect(GUIdosD.K * (GUIdosD.rel.x + pos.x),
                                    GUIdosD.K * (GUIdosD.rel.y + pos.y),
                                    GUIdosD.K, GUIdosD.K);
                        } catch (Exception ex) {
                            System.out.println("error");
                        }
                    }
                }
            }

            for (int i = 0; i < firstOponent.size(); i++) {
                Point pos = firstOponent.getMO(i).pos;
                float ene = firstOponent.getMO(i).ene;

                float a = ene / (Defs.E_INITIAL * 2);
                if (a > 1) a = 1;

                miCG.setColor(new Color(
                        color1.getRed(), color1.getGreen(),
                        color1.getBlue(), (int) (255 * a)));

                miCG.fillOval(GUIdosD.K * (GUIdosD.rel.x + pos.x),
                        GUIdosD.K * (GUIdosD.rel.y + pos.y),
                        GUIdosD.K, GUIdosD.K);
            }

            for (int i = 0; i < secondOponent.size(); i++) {
                Point pos = secondOponent.getMO(i).pos;
                float ene = secondOponent.getMO(i).ene;
                float a = ene / (Defs.E_INITIAL * 2);
                if (a > 1) a = 1;

                miCG.setColor(new Color(color2.getRed(), color2.getGreen(),
                        color2.getBlue(), (int) (255 * a)));
                miCG.fillOval(GUIdosD.K * (GUIdosD.rel.x + pos.x),
                        GUIdosD.K * (GUIdosD.rel.y + pos.y),
                        GUIdosD.K, GUIdosD.K);
            }


            miCG.setColor(backGround);
            miCG.setStroke(new BasicStroke(t1));
            miCG.draw(new Ellipse2D.Float(
                    GUIdosD.K * GUIdosD.rel.x - t1 / 2 - light,
                    GUIdosD.K * GUIdosD.rel.y - t1 / 2 - light,
                    GUIdosD.K * Defs.DIAMETER + t1 + 2 * light,
                    GUIdosD.K * Defs.DIAMETER + t1 + 2 * light));

            miCG.setColor(Color.GRAY);
            miCG.setStroke(new BasicStroke(GUIdosD.K));
            miCG.draw(new Ellipse2D.Float(
                    GUIdosD.K * (GUIdosD.rel.x - (float) 0.5) - light,
                    GUIdosD.K * (GUIdosD.rel.y - (float) 0.5) - light,
                    GUIdosD.K * (Defs.DIAMETER + 1) + 2 * light,
                    GUIdosD.K * (Defs.DIAMETER + 1) + 2 * light));


            // Draw the history
            int t3 = (int) (0.25 * GUIdosD.K);

            miCG.setStroke(new BasicStroke(t3));
            miCG.drawLine(history.x, history.y - history.height, history.x, history.y);
            miCG.drawLine(history.x + history.width, history.y - history.height,
                    history.x + history.width, history.y);

            for (int i = 1; i * interLine + 0.1 * interLine < energyMax; i++) {
                int posy = (int) (history.y - (i * interLine) * history.height / energyMax);
                miCG.drawLine(history.x, posy, history.x + history.width, posy);
            }

            updateHistory();

            miCG.setColor(color1);
            miCG.drawString("" + (long) firstOponent.getEnergy(), textPosition.x, textPosition.y);
            miCG.drawPolyline(h_xPoints, h1_yPoints, historyCount);
            miCG.setColor(color2);
            miCG.drawString("" + (long) secondOponent.getEnergy(), textPosition.x + anchoText + anchoNum, textPosition.y);
            miCG.drawPolyline(h_xPoints, h2_yPoints, historyCount);

            miCG.setColor(Color.GRAY);
            miCG.drawString(textOponent, textPosition.x + anchoNum, textPosition.y);
            miCG.drawLine(history.x, textPosition.y - GUIdosD.K * 5, history.x + history.width, textPosition.y - GUIdosD.K * 5);
            // updateUI();
            panel.getGraphics().drawImage(dobleBuffer, 0, 0, panel);
        } catch (Throwable ignored) {
            ignored.printStackTrace();

        }
    }


    private void updateHistory() {
        // update the energy of the colonies
        history1[historyIndex] = round(firstOponent.getEnergy());
        history2[historyIndex] = round(secondOponent.getEnergy());

        energyMax = max(getMax(history1), getMax(history2));

        double factor = pow(energyMax, -1) * history.height;

        if (historyCount == h_xPoints.length - 1) {
            for (int index = 1; index <= historyCount; index++) {
                int i = (historyIndex + index) % history1.length;

                h1_yPoints[index - 1] = (int) (history.y - history1[i] * factor);
                h2_yPoints[index - 1] = (int) (history.y - history2[i] * factor);
            }
        }

        if (historyCount < h_xPoints.length - 1)
            ++historyCount;

        h1_yPoints[historyCount - 1] = (int) (history.y - history1[historyIndex] * factor);
        h2_yPoints[historyCount - 1] = (int) (history.y - history2[historyIndex] * factor);

        historyIndex = (historyIndex + 1) % history1.length;
    }

    public int getMax(int[] a) {
        int max = 0;

        for (int anA : a) {
            if (anA > max)
                max = anA;
        }

        return max;
    }

    private boolean inDish(float x, float y) {
        float radio = (2 + Defs.RADIUS) * GUIdosD.K;
        float rel = 2 * GUIdosD.K;

        return ((x + rel - radio) * (x + rel - radio) + (y + rel - radio) * (y + rel - radio)) <
                ((radio) * (radio));
    }

    private void updateNames(BattleRun b) {
        anchoNum = miCG.getFontMetrics().stringWidth("0000000");
        textOponent = environment.getName(b.ID1) + " vs " + environment.getName(b.ID2);
        anchoText = miCG.getFontMetrics().stringWidth(textOponent + "   ");
        textPosition = new Point((panel.getWidth() - 2 * anchoNum - anchoText) / 2,
                GUIdosD.K * (GUIdosD.rel.y + Defs.DIAMETER + 10));

    }
}