/**
 * @author Sergio Del Castillo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.contest.simulationUI;


import alifec.contest.view.ContestUI;
import alifec.contest.view.Message;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.battles.BattleRun;
import alifec.core.exception.MoveMicroorganismException;
import alifec.core.simulation.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.round;

public class MiThread extends Thread {

    Logger logger = Logger.getLogger(getClass());

    enum Status {STATUS_PLAY, STATUS_PAUSE, STATUS_STOPPED}

    /**
     * Status value.
     * It can be:
     * STATUS_PLAY when the application is running.
     * STATUS_PAUSE when the application is in pause.
     * STATUS_STOPPED when the user press close button.
     */
    private Status status = Status.STATUS_PLAY;

    private int timewait = 2000;
    private ContestUI cui;
    private JDialog father;
    private JPanel panel;
    private Image image;
    private Graphics2D graphics;
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

    private Colony firstOponent;
    private Colony secondOponent;

    private final Environment environment;
    private final Tournament lastTournament;

    /**
     * This class is a thread that paint the panel of battles.
     *
     * @param cui     a instance of ContestUI
     * @param father  this is a JDialog class and it contains the panel that will be painted
     * @param panel   A instance of Panel class where the thread will paint the battles.
     * @param battles list of battles that will be run
     */
    public MiThread(ContestUI cui, JDialog father, JPanel panel, DefaultListModel battles) {
        this.cui = cui;
        this.panel = panel;
        this.father = father;
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
        lastTournament = cui.getContest().getTournamentManager().lastElement();

    }

    @Override
    public void run() {
        try {
            paintInit();

            while (battles.size() > 0) {

                BattleRun b = (BattleRun) battles.firstElement();
                battles.removeElement(battles.firstElement());
                cui.setMessage(b.toString());
                environment.generateOpponents(b);
                firstOponent = environment.getFirstOpponent();
                secondOponent = environment.getSecondOpponent();
                updateNames(b);

                try {
                    boolean battleRunning = true;

                    while (battleRunning) {
                        switch (status) {
                            case STATUS_PLAY:
                                while (status == Status.STATUS_PLAY && battleRunning) {
                                    battleRunning = !environment.moveColonies();
                                    paintImage();
                                }
                                break;

                            case STATUS_PAUSE:
                                //noinspection ConstantConditions
                                if (battleRunning) {
                                    battleRunning = !environment.moveColonies();
                                    paintImage();

                                    if (battleRunning) pauseRun();
                                }
                                break;

                            case STATUS_STOPPED:
                                // cosed by user!
                                return;
                        }
                    }

                    Thread.sleep(5 * timewait);
                    resetHistory();


                    // update the results !!
                    BattleRun r = environment.getResults();
                    lastTournament.add(r.name1, r.name2, r.nutrient, r.energy1(), r.energy2());
                    cui.getTournamentUI().updateLast();

                    panel.repaint();

                } catch (MoveMicroorganismException ex) {
                    logger.info(ex.getMessage(), ex);
                    penalize(ex.getColonyName());
                    Message.printErr(panel, ex.getMessage());
                }
            }
            father.dispose();
        } catch (InterruptedException ignored) {
            logger.warn(ignored.getMessage(), ignored);
        }

    }

    private void penalize(String colonyName) {

        DefaultListModel battles = cui.getBattleUI().getBattles();

        cui.getTournamentUI().penalize(colonyName);

        List<BattleRun> indexs = new ArrayList<>();

        for (int i = 0; i < battles.size(); i++) {
            BattleRun b = (BattleRun) battles.elementAt(i);

            if (b.name1.equals(colonyName) || b.name2.equals(colonyName))
                indexs.add(b);
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

    /**
     * Set the status to STATUS_PAUSE
     */
    public synchronized void setPause() {
        status = Status.STATUS_PAUSE;
    }

    /**
     * Unblock the thread and continue the running of battles
     */
    public synchronized void continueRun() {
        this.notify();
    }

    /**
     * Pause the running of battles
     *
     * @throws InterruptedException if the thread was interrupted
     */
    public synchronized void pauseRun() throws InterruptedException {
        this.wait();
    }

    /**
     * Set status to STATUS_PLAY
     */
    public synchronized void setPlay() {
        status = Status.STATUS_PLAY;
    }

    /**
     * Set status to STATUS_STOPPED
     */
    public synchronized void setClosed() {
        status = Status.STATUS_STOPPED;
    }


    public void paintInit() {

        panel.setBackground(backGround);
        panel.setForeground(backGround);

        image = panel.createImage(panel.getWidth(), panel.getHeight());
        image.getGraphics().setColor(backGround);
        image.getGraphics().clearRect(
                panel.getBounds().x,
                panel.getBounds().y,
                panel.getBounds().width,
                panel.getBounds().height);

        image.setAccelerationPriority(1);
        panel.getGraphics().drawImage(image, 0, 0, panel);

        graphics = (Graphics2D) image.getGraphics();
        int size = (int) (1.5 * GUIdosD.K + 4);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
        graphics.setBackground(backGround);


        graphics.setColor(Color.LIGHT_GRAY);

        for (int i = 10; i >= 0; i--) {
            try {
                graphics.drawString("Welcome To Alifecontest-Java ", 100, 120);
                graphics.drawString("      Wait " + i + " to run", 100, 140);
                panel.getGraphics().drawImage(image, 0, 0, panel);
                Thread.sleep(timewait);
                graphics.clearRect(0, 100, panel.getWidth(), 40);
            } catch (InterruptedException ignored) {
                logger.trace(ignored.getMessage());
            }
        }
    }

    public synchronized void paintImage() {
        // if the application is closed by user ...
        if (status == Status.STATUS_STOPPED) {
            return;
        }

        try {
            // clear All!
            graphics.clearRect(panel.getBounds().x,
                    panel.getBounds().y,
                    panel.getBounds().width,
                    panel.getBounds().height);
            int t1 = 3 * GUIdosD.K;
            graphics.setColor(petriColor);

            graphics.fill(new Ellipse2D.Float(
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

                            graphics.setColor(c);
                            graphics.fillRect(GUIdosD.K * (GUIdosD.rel.x + pos.x),
                                    GUIdosD.K * (GUIdosD.rel.y + pos.y),
                                    GUIdosD.K, GUIdosD.K);
                        } catch (Exception ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
            }

            for (int i = 0; i < firstOponent.size(); i++) {
                Cell mo = firstOponent.getMO(i);
                float ene = firstOponent.getMO(i).ene;

                float a = ene / (Defs.E_INITIAL * 2);
                if (a > 1) a = 1;

                graphics.setColor(new Color(
                        color1.getRed(), color1.getGreen(),
                        color1.getBlue(), (int) (255 * a)));

                graphics.fillOval(GUIdosD.K * (GUIdosD.rel.x + mo.x),
                        GUIdosD.K * (GUIdosD.rel.y + mo.y),
                        GUIdosD.K, GUIdosD.K);
            }

            for (int i = 0; i < secondOponent.size(); i++) {
                Cell mo = secondOponent.getMO(i);
                float ene = secondOponent.getMO(i).ene;
                float a = ene / (Defs.E_INITIAL * 2);
                if (a > 1) a = 1;

                graphics.setColor(new Color(color2.getRed(), color2.getGreen(),
                        color2.getBlue(), (int) (255 * a)));
                graphics.fillOval(GUIdosD.K * (GUIdosD.rel.x + mo.x),
                        GUIdosD.K * (GUIdosD.rel.y + mo.y),
                        GUIdosD.K, GUIdosD.K);
            }


            graphics.setColor(backGround);
            graphics.setStroke(new BasicStroke(t1));
            graphics.draw(new Ellipse2D.Float(
                    GUIdosD.K * GUIdosD.rel.x - t1 / 2 - light,
                    GUIdosD.K * GUIdosD.rel.y - t1 / 2 - light,
                    GUIdosD.K * Defs.DIAMETER + t1 + 2 * light,
                    GUIdosD.K * Defs.DIAMETER + t1 + 2 * light));

            graphics.setColor(Color.GRAY);
            graphics.setStroke(new BasicStroke(GUIdosD.K));
            graphics.draw(new Ellipse2D.Float(
                    GUIdosD.K * (GUIdosD.rel.x - (float) 0.5) - light,
                    GUIdosD.K * (GUIdosD.rel.y - (float) 0.5) - light,
                    GUIdosD.K * (Defs.DIAMETER + 1) + 2 * light,
                    GUIdosD.K * (Defs.DIAMETER + 1) + 2 * light));


            // Draw the history 
            int t3 = (int) (0.25 * GUIdosD.K);

            graphics.setStroke(new BasicStroke(t3));
            graphics.drawLine(history.x, history.y - history.height, history.x, history.y);
            graphics.drawLine(history.x + history.width, history.y - history.height,
                    history.x + history.width, history.y);

            for (int i = 1; i * interLine + 0.1 * interLine < energyMax; i++) {
                int posy = (int) (history.y - (i * interLine) * history.height / energyMax);
                graphics.drawLine(history.x, posy, history.x + history.width, posy);
            }

            updateHistory();

            graphics.setColor(color1);
            graphics.drawString("" + (long) firstOponent.getEnergy(), textPosition.x, textPosition.y);
            graphics.drawPolyline(h_xPoints, h1_yPoints, historyCount);
            graphics.setColor(color2);
            graphics.drawString("" + (long) secondOponent.getEnergy(), textPosition.x + anchoText + anchoNum, textPosition.y);
            graphics.drawPolyline(h_xPoints, h2_yPoints, historyCount);

            graphics.setColor(Color.GRAY);
            graphics.drawString(textOponent, textPosition.x + anchoNum, textPosition.y);
            graphics.drawLine(history.x, textPosition.y - GUIdosD.K * 5, history.x + history.width, textPosition.y - GUIdosD.K * 5);
            panel.getGraphics().drawImage(image, 0, 0, panel);
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
        anchoNum = graphics.getFontMetrics().stringWidth("0000000");
        textOponent = environment.getName(b.ID1) + " vs " + environment.getName(b.ID2);
        anchoText = graphics.getFontMetrics().stringWidth(textOponent + "   ");
        textPosition = new Point((panel.getWidth() - 2 * anchoNum - anchoText) / 2,
                GUIdosD.K * (GUIdosD.rel.y + Defs.DIAMETER + 10));

    }
}


