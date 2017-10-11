/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package view.contest;

import view.Properties;
import view.components.DialogView;
import view.components.JLink;

import javax.swing.*;

public class AboutView extends DialogView {

    public AboutView(ContestView parent) {
        super(parent, "About");
        initComponents();
        this.setLocationRelativeTo(parent);
        getRootPane().setDefaultButton(closeButton);

        this.setVisible(true);
    }

    private void initComponents() {
        closeButton = new javax.swing.JButton("Close");
        JLabel appTitleLabel = new JLabel();
        JLabel versionLabel = new JLabel();
        JLabel version = new JLabel();
        JLabel developers = new JLabel();
        JLabel diego = new JLabel();
        JLabel homepageLabel = new JLabel();
        JLink wikidot = new JLink("http://alifecontest.wikidot.com/");
        JLabel yeyo = new JLabel();
        JLabel georgina = new JLabel();
        JLabel jLabel1 = new JLabel();
        JLabel daniel = new JLabel();
        JLabel collaborators = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setTitle("About");
        setModal(true);
        setName("About");
        setResizable(false);

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize() + 4));
        appTitleLabel.setText("Welcome to Artificial Life Contest");

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD));
        versionLabel.setText("Version:");
        version.setText(Properties.getInstance().getVersionValue());

        developers.setFont(developers.getFont().deriveFont(developers.getFont().getStyle() | java.awt.Font.BOLD));
        developers.setText("Developers: ");
        diego.setText("Milone, Diego");
        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | java.awt.Font.BOLD));
        homepageLabel.setText("Homepage:");

        yeyo.setText("Del Castillo, Sergio");
        georgina.setText("Stegmayer, Georgina");

        collaborators.setFont(developers.getFont().deriveFont(developers.getFont().getStyle() | java.awt.Font.BOLD));
        collaborators.setText("Collaborators: ");
        daniel.setText("Beber, Daniel");

        jLabel1.setIcon(new javax.swing.ImageIcon(Properties.getInstance().getAboutIcon()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel1)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(versionLabel)
                                                        .addComponent(homepageLabel)
                                                        .addComponent(collaborators)
                                                        .addComponent(developers))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(version)
                                                        .addComponent(wikidot)
                                                        .addComponent(daniel)
                                                        .addComponent(yeyo)
                                                        .addComponent(diego)
                                                        .addComponent(georgina))
                                                .addGap(75, 75, 75))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(closeButton)
                                        .addGap(34, 34, 34))))
                                .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(appTitleLabel)))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(appTitleLabel)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel1)
                                .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(versionLabel)
                                        .addComponent(version))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                        .addComponent(homepageLabel)
                                        .addComponent(wikidot))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                        .addComponent(collaborators)
                                        .addComponent(daniel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(developers)
                                        .addComponent(yeyo))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(diego)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(georgina)
                                .addGap(18, 18, 18)
                                .addComponent(closeButton)))
                        .addGap(22, 22, 22))
        );

        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        pack();
    }

    private javax.swing.JButton closeButton;
}

