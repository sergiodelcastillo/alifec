package view.components;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JLink extends JLabel implements MouseListener {

    private Color entered = new Color(0, 100, 255);
    private Color exited = Color.BLUE;
    private String label;

    public JLink(String labelText) {
        super(labelText);

        setForeground(exited);
        this.label = labelText;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(this);


    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            URI uri = new URI(label);
            Desktop.getDesktop().browse(uri);
        } catch (IOException e1) {
        } catch (URISyntaxException e1) {
        }
        exited = new Color(0,0, 150);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(entered);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(exited);
    }
}

