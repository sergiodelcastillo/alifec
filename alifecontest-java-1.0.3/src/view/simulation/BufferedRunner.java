/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package view.simulation;

import view.contest.ContestView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BufferedRunner extends JDialog {

    private JPanel centerPanel = new JPanel();
    private JPanel southPanel = new JPanel();

    private final BufferedThread thread;

    /**
     * Create a graphical contest to run the list of battle
     *
     * @param father   Instance of <b>ContestView</b> class
     * @param selected
     */
    public BufferedRunner(ContestView father, boolean selected) {
        super(father, "Artificial Life Contest", true);

        init(father);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        // append windows closing listener
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                thread.setFinished();
                thread.interrupt();
            }
        });

        // create an implementation of keyListener
        KeyAdapter keyListener = new KeyAdapter() {

            public void keyPressed(KeyEvent event) {
                if (KeyEvent.VK_Q == event.getKeyCode()) {
                    thread.setFinished();
                    thread.interrupt();
                    dispose();
                }

                if (KeyEvent.VK_P == event.getKeyCode()) {
                    thread.setPause();
                    thread.continueRun();
                }

                if (KeyEvent.VK_ENTER == event.getKeyCode() || KeyEvent.VK_SPACE == event.getKeyCode()) {
                    thread.setPlay();
                    thread.continueRun();
                }
            }
        };

        // append keyListener
        this.addKeyListener(keyListener);
        this.centerPanel.addKeyListener(keyListener);
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //create a instance of a thread that will be paint the battle.
        this.thread = new BufferedThread(father, this, selected);
        this.thread.setPriority(Thread.MAX_PRIORITY);


        this.thread.start();
        this.setVisible(true);
    }


    private void init(ContestView f) {
        centerPanel = new JPanel();
        southPanel = new JPanel();
        JLabel text = new JLabel("P:advance and pause;  ENTER:advance all;  Q: exit ", SwingConstants.LEFT);
        int height = BufferedThread.PETRI_SIZE + BufferedThread.INFO_HEIGHT + BufferedThread.HISTORY_HEIGHT;
        int width =  BufferedThread.PETRI_SIZE;

        centerPanel.setBackground(Color.BLACK);
        centerPanel.setPreferredSize(new Dimension(width, height));
        this.setLocation(f.getX()+ f.getWidth() - width,f.getY());

        southPanel.setLayout(new BorderLayout());
        southPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        southPanel.add(text);
    }

    public JPanel getCenterPanel() {
        return centerPanel;
    }
}
