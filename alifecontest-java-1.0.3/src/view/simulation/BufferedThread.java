/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package view.simulation;

import controller.java.Agar;
import controller.java.Cell;
import controller.java.Colony;
import controller.java.Environment;
import controller.java.battle.BattleRun;
import controller.java.tournament.Tournament;
import data.java.Config;
import data.java.Defs;
import data.java.Log;
import exceptions.MoveMicroorganismException;
import view.Properties;
import view.battle.BattleListView;
import view.contest.ContestView;
import view.contest.Message;
import view.tournament.TournamentPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;

public class BufferedThread extends java.lang.Thread {
    static final int PETRI_SIZE;
    static final int HISTORY_HEIGHT;
    static final int INFO_HEIGHT;

    private static final double INTERLINE;
    private static final int MO_SIZE;
    private static final double K_MOS;
    private static final int NUTRIENT_SIZE;
    private static final double K_NUTRIENTS;
    private static final Color COLOR_BACKGROUND;
    private static final Color COLOR_LINE;

    private static final Color COLOR_PETRI;
    private static final Color[] COLOR_MO1;
    private static final Color[] COLOR_MO2;
    private static final Color[] COLOR_NUTRIENT;

    private static final BasicStroke INFO_STROKE;
    private static final BasicStroke MOS_STROKE;
    private static final BasicStroke PETRI_STROKE;

    static {
        COLOR_NUTRIENT = new Color[256];
        COLOR_MO2 = new Color[256];
        COLOR_MO1 = new Color[256];

        for (int i = 0; i < 256; i++) {
            COLOR_NUTRIENT[i] = new Color(255, 255, 0, i);
            COLOR_MO1[i] = new Color(255, 0, 0, i);
            COLOR_MO2[i] = new Color(0, 0, 255, i);
        }

        COLOR_BACKGROUND = new Color(238, 238, 238);
        COLOR_PETRI = new Color(184, 207, 229);
        COLOR_LINE = Color.GRAY;//Color.LIGHT_GRAY;

        NUTRIENT_SIZE = MO_SIZE = 8;
        INTERLINE = 50000;
        K_MOS = 255.0 / Math.sqrt(4 * Defs.E_INITIAL);
        K_NUTRIENTS = 255.0 / Math.sqrt(Defs.MAX_NUTRI);

        PETRI_SIZE = 2 * 5 + MO_SIZE * (2 + Defs.DIAMETER);
        INFO_HEIGHT = 60; // 2 * height of info font + 2*5
        HISTORY_HEIGHT = 100 + 3 * 5;

        INFO_STROKE = new BasicStroke(2);
        PETRI_STROKE = new BasicStroke(MO_SIZE + 3);
        MOS_STROKE = new BasicStroke(1);
    }

    enum Status {
        RUNNING,
        PAUSED,
        FINISHED,
        CANCELED
    }

    private Status status;

    private int timeWait;
    private ContestView father;
    private BufferedRunner dialog;

    private JPanel panel;

    private Image petri;
    private Image history;
    private Image info;


    private Graphics2D bufferPetri;
    private Graphics2D bufferHistory;
    private Graphics2D bufferInfo;

    private Environment environment;
    private Agar agar;
    private Colony colony1;
    private Colony colony2;

    private boolean runSelected;

    private int infoFontHeight;
    private int infoXEnergy;
    private int infoXMOs;

    //history
    private ArrayDeque<Float> historyMO1;
    private ArrayDeque<Float> historyMO2;
    private ArrayDeque<Integer> references;
    private int[] historyX;
    private int[] historyMO1Y;
    private int[] historyMO2Y;

    //info
    private int ene1;
    private int ene2;
    private int size1;
    private int size2;

    public BufferedThread(ContestView father, BufferedRunner dialog, boolean selected) {
        this.dialog = dialog;
        this.father = father;
        this.panel = dialog.getCenterPanel();
        this.environment = father.getContest().getEnvironment();
        this.agar = environment.getAgar();
        this.runSelected = selected;
        this.timeWait = Config.getInstance().getPauseBetweenBattles();
    }


    @Override
    public void run() {
        try {
            try {
                initComponents();
                paintPresentation();
                runBattles();
                paintFinish();
            } catch (InterruptedException ignored) {
            }

            dialog.dispose();

        } catch (NullPointerException ignored) {
        }
    }

    private void initComponents() {
        //Initialize
        panel.setBackground(COLOR_BACKGROUND);

        //162590 ; 182641
        info = panel.createImage(PETRI_SIZE, INFO_HEIGHT);
        history = panel.createImage(PETRI_SIZE, HISTORY_HEIGHT);
        petri = panel.createImage(PETRI_SIZE, PETRI_SIZE);


        bufferPetri = (Graphics2D) petri.getGraphics();
        bufferHistory = (Graphics2D) history.getGraphics();
        bufferInfo = (Graphics2D) info.getGraphics();

        bufferPetri.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        bufferInfo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        infoFontHeight = bufferInfo.getFontMetrics().getHeight() + 5;
        infoXEnergy = 2 * PETRI_SIZE / 3;
        infoXMOs = PETRI_SIZE - 5;

        historyMO1 = new ArrayDeque<Float>();
        historyMO2 = new ArrayDeque<Float>();
        references = new ArrayDeque<Integer>();

        historyX = new int[Defs.DIAMETER + 2 + 1];
        historyMO1Y = new int[Defs.DIAMETER + 2 + 1];
        historyMO2Y = new int[Defs.DIAMETER + 2 + 1];

        for (int i = historyX.length - 1; i >= 0; i--) {
            historyX[i] = 5 + MO_SIZE * i;
        }
    }

    private void runBattles() throws InterruptedException {
        BattleListView battleList = this.father.getBattleView().getBattleList();

        if (runSelected) {
            Object obj = battleList.getSelectedValue();

            if (obj instanceof BattleRun) {
                if (runBattle((BattleRun) obj))
                    battleList.getModel().remove(battleList.getSelectedIndex());
            }
        } else {
            DefaultListModel model = battleList.getModel();
            int size = model.size();

            for (int index = 0; index < size; index++) {
                Object obj = model.get(0);
                battleList.setSelectedIndex(0);

                if (obj instanceof BattleRun) {
                    if (runBattle((BattleRun) obj))
                        battleList.getModel().remove(0);
                }

                /*if (battleList.getModel().getSize() == 12 && father.getContest().isCompetitionMode()) {
                    dialog.setLocation(father.getX(), father.getY());
                }     */
            }
        }
    }

    private boolean runBattle(BattleRun battleRun) throws InterruptedException {
        //paint battle...
        if (!environment.createBattle(battleRun)) {
            Log.printlnAndSave("The battle " + battleRun.toCSV() + " is invalid.");
            return true;
        }
        Log.println("Running: " + battleRun.toLog());
        colony1 = environment.getFirstColony();
        colony2 = environment.getSecondColony();
        status = Status.RUNNING;

        paintBeforeBattle(battleRun);

        try {
            while (status == Status.RUNNING || status == Status.PAUSED) {
                if (status == Status.PAUSED) {
                    pauseRun();
                }

                switch (status) {
                    case RUNNING:
                        while (status == Status.RUNNING) {
                            if (environment.moveColonies()) {
                                status = Status.FINISHED;
                            }
                            paintBattle();
                        }
                        break;

                    case PAUSED:
                        if (environment.moveColonies()) {
                            status = Status.FINISHED;
                        }
                        paintBattle();
                        break;

                    case CANCELED:
                        Log.println("canceled by user");
                        return false;
                }
            }

            saveResult(environment.getCurrentBattle());

        } catch (MoveMicroorganismException ex) {
            synchronized (TournamentPanel.class) {
                synchronized (BattleListView.class) {
                    penalize(ex.getColonyName());
                    Log.println("", ex.getException());
                    Message.printErr(panel, ex.getMessage());
                    return false;
                }
            }
        } finally {
            Thread.sleep(timeWait);
            reset();
        }
        return true;
    }

    private void paintBeforeBattle(BattleRun battle) {
        /*
         * paint static info:
         * - the name of the colonies
         * - the separator line
         */
        bufferInfo.setStroke(INFO_STROKE);
        bufferInfo.setColor(COLOR_LINE);
        bufferInfo.drawLine(5, 1, PETRI_SIZE - 5, 1);
        bufferInfo.drawLine(5, INFO_HEIGHT - 1, PETRI_SIZE - 5, INFO_HEIGHT - 1);

        bufferInfo.setColor(COLOR_MO1[COLOR_MO1.length - 1]);
        bufferInfo.drawString(battle.getFirstName(), 5, infoFontHeight);

        bufferInfo.setColor(COLOR_MO2[COLOR_MO2.length - 1]);
        bufferInfo.drawString(battle.getSecondName(), 5, 2 * infoFontHeight);

        /*
         * eat history properties
         */

        bufferHistory.setStroke(INFO_STROKE);
        bufferHistory.setColor(COLOR_PETRI);
    }

    private void reset() {
        historyMO1.clear();
        historyMO2.clear();
        bufferPetri.clearRect(0, 0, PETRI_SIZE, PETRI_SIZE);
        bufferInfo.clearRect(0, 0, PETRI_SIZE, INFO_HEIGHT);
        bufferHistory.clearRect(0, 0, PETRI_SIZE, HISTORY_HEIGHT);
        panel.getGraphics().drawImage(info, 0, PETRI_SIZE, null);
        panel.getGraphics().drawImage(history, 0, PETRI_SIZE + INFO_HEIGHT, null);
        panel.getGraphics().drawImage(petri, 0, 0, null);
    }

    private void saveResult(BattleRun results) {
        Tournament last = father.getContest().getTournamentManager().getLastTournament();

        try {
            last.addBattle(results);
        } catch (IOException e) {
            Log.printlnAndSave("Cant save [" + results.toCSV() + "]\nThe exception is", e);
        }

        father.getTournamentView().updateLast();
    }


    private synchronized void paintBattle() {
        //paint battles
        bufferPetri.setStroke(MOS_STROKE);

        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                if (inDish(i + 1, j + 1)) {
                    Cell mo = environment.getCell(i, j);

                    drawNutrient(bufferPetri, i, j, agar.getNutrient(i, j));

                    if (mo != null) {
                        drawMO(bufferPetri, i, j, mo.id, mo.ene);
                    }
                }
            }
        }

        bufferPetri.setStroke(PETRI_STROKE);
        bufferPetri.setColor(COLOR_LINE);
        bufferPetri.drawOval(5 + 4, 5 + 4, 8 * (1 + Defs.DIAMETER), 8 * (1 + Defs.DIAMETER));
        panel.getGraphics().drawImage(petri, 0, 0, null);

        //paint info
        paintInfo();
        paintHistory();

    }

    private boolean inDish(int i, int j) {
        float r = Defs.RADIUS + 0.30f;
        return (i - r) * (i - r) + (j - r) * (j - r) < r * r;
    }

    private void paintInfo() {
        // clean info
        paintInfo(String.valueOf(ene1),
                String.valueOf(ene2),
                String.valueOf(size1),
                String.valueOf(size2),
                COLOR_BACKGROUND,
                COLOR_BACKGROUND);

        //update info
        ene1 = (int) colony1.getEnergy();
        ene2 = (int) colony2.getEnergy();
        size1 = colony1.getSize();
        size2 = colony2.getSize();

        // paint new info
        paintInfo(String.valueOf(ene1),
                String.valueOf(ene2),
                String.valueOf(size1),
                String.valueOf(size2),
                COLOR_MO1[COLOR_MO1.length - 1],
                COLOR_MO2[COLOR_MO2.length - 1]);
    }

    private  void paintInfo(String ene1, String ene2, String size1, String size2, Color color1, Color color2) {
        int energy1Width = bufferInfo.getFontMetrics().stringWidth(ene1);
        int energy2Width = bufferInfo.getFontMetrics().stringWidth(ene2);
        int mos1Width = bufferInfo.getFontMetrics().stringWidth(String.valueOf(size1));
        int mos2Width = bufferInfo.getFontMetrics().stringWidth(String.valueOf(size2));

        bufferInfo.setColor(color1);
        bufferInfo.drawString(ene1, infoXEnergy - energy1Width, infoFontHeight);
        bufferInfo.drawString(size1, infoXMOs - mos1Width, infoFontHeight);

        bufferInfo.setColor(color2);
        bufferInfo.drawString(ene2, infoXEnergy - energy2Width, 2 * infoFontHeight);
        bufferInfo.drawString(size2, infoXMOs - mos2Width, 2 * infoFontHeight);
        panel.getGraphics().drawImage(info, 0, PETRI_SIZE, null);
    }

    private void paintHistory() {
        //cleaning
        paintHistory(COLOR_BACKGROUND, COLOR_BACKGROUND);
        paintReferences(COLOR_BACKGROUND);

        //add to history
        historyMO1.add(colony1.getEnergy());
        historyMO2.add(colony2.getEnergy());

        // ensure size.
        if (historyMO1.size() > 53) historyMO1.pop();
        if (historyMO2.size() > 53) historyMO2.pop();

        float max = getMax();
        updateY(historyMO1, historyMO1Y, max);
        updateY(historyMO2, historyMO2Y, max);
        updateReferences(max);

        //paint references
        paintReferences(COLOR_LINE);

        //painting history
        paintHistory(COLOR_MO1[COLOR_MO1.length - 1], COLOR_MO2[COLOR_MO2.length - 1]);

        // painting the lines..
        bufferHistory.setColor(COLOR_LINE);
        bufferHistory.drawLine(5, 9, PETRI_SIZE - 5, 9);
        bufferHistory.drawLine(5, 9, 5, HISTORY_HEIGHT - 3);
        bufferHistory.drawLine(PETRI_SIZE - 5, 9, PETRI_SIZE - 5, HISTORY_HEIGHT - 3);
        bufferHistory.drawLine(5, HISTORY_HEIGHT - 3, PETRI_SIZE - 5, HISTORY_HEIGHT - 3);

        panel.getGraphics().drawImage(history, 0, PETRI_SIZE + INFO_HEIGHT, null);
    }

    private void paintHistory(Color color1, Color color2) {
        bufferHistory.setColor(color1);
        bufferHistory.drawPolyline(historyX, historyMO1Y, historyMO1.size());
        bufferHistory.setColor(color2);
        bufferHistory.drawPolyline(historyX, historyMO2Y, historyMO2.size());
    }

    private void paintReferences(Color color) {
        bufferHistory.setColor(color);

        for (Integer posy : references) {
            bufferHistory.drawLine(6, posy, PETRI_SIZE - 6, posy);
        }
    }

    private void updateReferences(float max) {
        references.clear();
        int height = HISTORY_HEIGHT - 3 - 9;

        for (double line = INTERLINE; line * 1.1 < max; line += INTERLINE) {
            references.addLast(9 + (int) (height - height * line / max));
        }
    }

    private void drawNutrient(Graphics2D gr, int i, int j, float value) {
        int index = (int) (K_NUTRIENTS * Math.sqrt(value));

        gr.setColor(COLOR_PETRI);
        gr.fillRect(5 + 9 + i * NUTRIENT_SIZE, 5 + 9 + j * NUTRIENT_SIZE, NUTRIENT_SIZE + 1, NUTRIENT_SIZE + 1);

        if (index > 0) {
            gr.setColor(COLOR_NUTRIENT[index]);
            gr.fillRect(5 + 9 + i * NUTRIENT_SIZE, 5 + 9 + j * NUTRIENT_SIZE, NUTRIENT_SIZE + 1, NUTRIENT_SIZE + 1);
        }
    }

    public void drawMO(Graphics2D gr, int x, int y, int id, float energy) {
        int index = (int) (K_MOS * Math.sqrt(Math.min(energy, 4 * Defs.E_INITIAL)));
        gr.setColor(id == colony1.getId() ? COLOR_MO1[index] : COLOR_MO2[index]);
        gr.fillOval(5 + 9 + MO_SIZE * x, 5 + 9 + MO_SIZE * y, MO_SIZE, MO_SIZE);
    }

    private void penalize(String colonyName) {
        synchronized (TournamentPanel.class) {
            synchronized (BattleListView.class) {

                this.environment.delete(colonyName);
                this.father.getContest().getTournamentManager().getLastTournament().penalize(colonyName);
                this.father.getTournamentView().updateLast();
                this.father.getBattleView().deleteColony(colonyName);
            }
        }
    }

    /**
     * Set the status to PAUSED
     */

    public void setPause() {
        if (status != Status.FINISHED) {
            status = Status.PAUSED;
        }
    }

    /**
     * Unblock the thread and continue the running of battle
     */
    public synchronized void continueRun() {
        this.notify();
    }

    /**
     * Pause the running of battle
     *
     * @throws InterruptedException if the thread was interrupted
     */
    public synchronized void pauseRun() throws InterruptedException {
        if (status == Status.RUNNING || status == Status.PAUSED) {
            this.wait();
        }
    }

    /**
     * Set status to RUNNING
     */
    public void setPlay() {
        status = Status.RUNNING;
    }

    /**
     * Set status to FINISHED
     */
    public void setFinished() {
        status = Status.FINISHED;
    }

    public void paintPresentation() throws InterruptedException {
        bufferPetri.setBackground(COLOR_BACKGROUND);

        bufferPetri.setColor(COLOR_BACKGROUND);
        bufferPetri.fillRect(0, 0, PETRI_SIZE + 1, PETRI_SIZE + 1);

        bufferPetri.setColor(Color.GRAY);
        drawCenteredString("Welcome to Artificial Life Contest " + Properties.getInstance().getVersionValue(), 140, bufferPetri, Color.gray);
        Thread.sleep(400);

        for (int i = 9; i >= 0; i--) {
            drawCenteredString("Wait " + i + " to run", 180, bufferPetri, Color.gray);
            panel.getGraphics().drawImage(petri, 0, 0, null);
            Thread.sleep(600);
        }
    }

    private void paintFinish() throws InterruptedException {
        bufferPetri.setColor(COLOR_BACKGROUND);
        bufferPetri.fillRect(0, 0, PETRI_SIZE + 1, PETRI_SIZE + 1);
        drawCenteredString("bye ;)", 180, bufferPetri, Color.gray);
        panel.getGraphics().drawImage(petri, 0, 0, null);
        Thread.sleep(1500);
    }


    private void drawCenteredString(String s, int y, Graphics2D buffer, Color color) {
        int size = buffer.getFontMetrics().stringWidth(s);

        buffer.setColor(COLOR_BACKGROUND);
        buffer.fillRect((PETRI_SIZE - size) / 2, y - buffer.getFontMetrics().getHeight(), size + 1, 20 + 1);

        buffer.setColor(color);
        buffer.drawString(s, (PETRI_SIZE - size) / 2, y);
    }

    private float getMax() {
        float max = 0.0f;

        for (Float value : historyMO1) {
            if (value > max) max = value;
        }

        for (Float value : historyMO2) {
            if (value > max) max = value;
        }
        return max;
    }

    private void updateY(ArrayDeque<Float> stack, int[] history, float max) {
        Iterator<Float> it = stack.iterator();

        for (int i = 0; i < stack.size(); i++) {
            history[i] = HISTORY_HEIGHT - 5 - (int) (100 * it.next() / max);
        }
    }
}


