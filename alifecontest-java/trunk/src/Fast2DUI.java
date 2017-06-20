/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package src;

import lib.Defs;
import lib.contest.ContestUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Fast2DUI extends JDialog implements KeyListener {
    static int K = 8;
    static Point rel = new Point(2, 2);
    //java.util.concurrent.atomic.AtomicReference<JPanel> centerPanel = new java.util.concurrent.atomic.AtomicReference<JPanel>();
    //java.util.concurrent.atomic.AtomicReference<JPanel> southPanel = new java.util.concurrent.atomic.AtomicReference<JPanel>();
   JPanel centerPanel = new JPanel();
   JPanel southPanel = new JPanel();
    MiThread thread;

    /**
     * Create a graphic contest to run the battles
     * @param father contestUI instance
     * @param b vector of battles to run.
     */
    public Fast2DUI(ContestUI father, DefaultListModel b) {
        super(father, "ALifeContest-java", true);

        init(father);
        //getContentPane().add(centerPanel.get(), BorderLayout.CENTER);
        //getContentPane().add(southPanel.get(), BorderLayout.SOUTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        addKeyListener(this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //thread = new MiThread(father, this, centerPanel.get(), b);
        thread = new MiThread(father, this, centerPanel, b);

        pack();
        setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                thread.interrupt();

                System.out.println("<<<<<falta stop>>>>>");
//		      thread.stop();
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        setVisible(true);
    }


    /*private void init(ContestUI f) {
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
        centerPanel.get().addKeyListener(this);

        southPanel.get().setLayout(new BorderLayout());
        //	southPanel.setBackground(Color.BLACK);
        southPanel.get().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        southPanel.get().add(text);
//		southPanel.setPreferredSize(new Dimension(K*(2*rel.x+Defs.DIAMETER), 20));
    }*/
    private void init(ContestUI f) {
//           centerPanel.set(new JPanel());
  //         southPanel.set(new JPanel());
           Rectangle r = new Rectangle();
           JLabel text = new JLabel("P:advance and pause;  ENTER:advance all;  Q: exit ", SwingConstants.LEFT);

           r.width = K * (2 * rel.x + Defs.DIAMETER);
           r.height = K * (2 * rel.x + Defs.DIAMETER + 26);
           r.x = f.getX() + f.getWidth() - r.width;
           r.y = f.getY();

           centerPanel.setBackground(Color.BLACK);
           centerPanel.setPreferredSize(new Dimension(K * (2 * rel.x + Defs.DIAMETER), K * (2 * rel.x + Defs.DIAMETER + 26)));
           setBounds(r);
           centerPanel.addKeyListener(this);

           southPanel.setLayout(new BorderLayout());
           //	southPanel.setBackground(Color.BLACK);
           southPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
           southPanel.add(text);
//		southPanel.setPreferredSize(new Dimension(K*(2*rel.x+Defs.DIAMETER), 20));
       }



    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent ev) {

        if (ev.getKeyCode() == KeyEvent.VK_Q) {
            this.thread.interrupt();
            dispose();
        }

        ///TODO: ver esto
        /*if (thread.isBattleRunning() || thread.isInterrupted()) {
            return;
        } */

        if (ev.getKeyCode() == KeyEvent.VK_P) {
            thread.setPause();

            if (thread.isAlive())
                thread.continueRun();
            else {
                try {
                    thread.pauseRun();
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (ev.getKeyCode() == KeyEvent.VK_ENTER ||
                ev.getKeyCode() == KeyEvent.VK_SPACE) {
            thread.setPlay();

            if (thread.isAlive())
                thread.continueRun();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}