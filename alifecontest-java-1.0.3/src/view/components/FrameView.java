package view.components;

import data.java.Log;

import javax.swing.*;
import java.util.Locale;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 18, 2010
 * Time: 3:56:23 PM
 * Email: yeyo@druidalabs.com
 */
public class FrameView extends JFrame {
    static {
        try {
            // eat look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);

            //eat locale!!!
            JFileChooser.setDefaultLocale(Locale.ENGLISH);
            JOptionPane.setDefaultLocale(Locale.ENGLISH);
        } catch (ClassNotFoundException ex) {
            Log.printlnAndSave("FrameView setting look and feel error", ex);
        } catch (InstantiationException ex) {
            Log.printlnAndSave("FrameView setting look and feel error", ex);
        } catch (IllegalAccessException ex) {
            Log.printlnAndSave("FrameView setting look and feel error", ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Log.printlnAndSave("FrameView setting look and feel error", ex);
        }
    }
    
}
