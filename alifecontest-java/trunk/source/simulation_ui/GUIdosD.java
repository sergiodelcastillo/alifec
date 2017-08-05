/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package simulation_ui;

import lib.Defs;
import lib.contest.ContestUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

public class GUIdosD extends JDialog {
    static int K = 8;
    static Point rel = new Point(2, 2);
    AtomicReference<JPanel> centerPanel = new AtomicReference<JPanel>();
    AtomicReference<JPanel> southPanel = new AtomicReference<JPanel>();

    final MiThread thread;

    /**
     * Create a graphical contest to run the list of battles
     *
     * @param father  Instance of <b>ContestUI</b> class
     * @param battles vector of battles.
     */
    public GUIdosD(ContestUI father, DefaultListModel battles) {
        super(father, "ALifeContest-java", true);

        init(father);
        getContentPane().add(centerPanel.get(), BorderLayout.CENTER);
        getContentPane().add(southPanel.get(), BorderLayout.SOUTH);

        // add windows closing listener
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                thread.setClosed();
                thread.interrupt();
            }
        });

        // create an implementation of keyListener
        KeyAdapter keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (KeyEvent.VK_Q == event.getKeyCode()) {
                    thread.setClosed();
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
        
        // add keyListener
        this.addKeyListener(keyListener);
        this.centerPanel.get().addKeyListener(keyListener);

        //create a instance of a thread that will be paint the battles.
        this.thread = new MiThread(father, this, centerPanel.get(), battles);
        this.thread.setPriority(Thread.MAX_PRIORITY);


        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.thread.start();
        this.setVisible(true);
    }


    private void init(ContestUI f) {
        centerPanel.set(new JPanel());
        southPanel.set(new JPanel());
        Rectangle r = new Rectangle();
        JLabel text = new JLabel("P:advance and pause;  ENTER:advance all;  Q: exit ", SwingConstants.LEFT);

        r.width = K * (2 * rel.x + Defs.DIAMETER);
        r.height = K * (2 * rel.x + Defs.DIAMETER + 26);
        r.x = f.getX() + f.getWidth() - r.width;
        r.y = f.getY();

        centerPanel.get().setBackground(Color.BLACK);
        centerPanel.get().setPreferredSize(new Dimension(K * (2 * rel.x + Defs.DIAMETER), K * (2 * rel.x + Defs.DIAMETER + 26)));
        setBounds(r);

        southPanel.get().setLayout(new BorderLayout());

        southPanel.get().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        southPanel.get().add(text);

    }

}
