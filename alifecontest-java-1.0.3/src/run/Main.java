package run;

import view.contest.ContestView;

import javax.swing.*;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 11, 2010
 * Time: 10:48:49 PM
 * Email: yeyo@druidalabs.com
 */
public class Main {
   public static void main(String[] args) {

       SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               new ContestView();
           }
       });
    }

}
