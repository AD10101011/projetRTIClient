package ADTO.View;

import ADTO.Controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

public class MainFrame extends  JFrame
{
    JFrame frame;
    public JTextField UserTextField;
    public JTextField PasswordTextField;
    public JButton loginButton;
    public JButton logoutButton;
    public JCheckBox nouveauClientCheckBox;
    public JPanel ConnexionPanel;
    public JPanel MainPanel;
    public JLabel TitleLabel;
    public JPanel MagasinPanel;
    public JTextField ArticleTextField;

    public DefaultTableModel model;
    public JTextField PrixUnitaireTextField;
    public JTextField StockTextField;
    public JSpinner quantiteSpinner;
    public JButton previousArticleButton;
    public JButton nextArticleButton;
    public JPanel PanierPanel;
    public JTable table;
    public JButton viderLePanierButton;
    public JButton supprimerArticleButton;
    public JButton confirmerAchatButton;
    public JTextField TotalTextField;
    public JLabel imageLabel;
    public JButton acheterButton;
    public JScrollPane TabScollPane;


    //#region Propriétés (Getters)

    public JTextField getUserTextField() {
        return UserTextField;
    }

    public JTextField getPasswordTextField() {
        return PasswordTextField;
    }
    public JFrame getFrame() { return frame; }

    public JButton getLoginButton() {
        return loginButton;
    }

    //#endregion

    public MainFrame()  {

        // Affichage de la fenêtre
        frame = new JFrame("Le Maraicher d'abdel karim");
        frame.setContentPane(MainPanel);
        frame.setMinimumSize(new Dimension(800,600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);



        // A la Fermeture de l'application
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    private void createUIComponents()
    {
        // Création des colonne du tableau
        model = new DefaultTableModel(null, new String[]{"Article", "Prix Unitaire", "Quantité"})
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Création du tableau
        table = new JTable();
        table.setModel(model);

        // Centrer les données du tableau
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Création du scrollPane
        TabScollPane = new JScrollPane(table);
    }

    //Set Controller
    public void setController(Controller c)
    {
        loginButton.addActionListener(c);
        loginButton.setActionCommand("loginbutton");

        nouveauClientCheckBox.addActionListener(c);
        nouveauClientCheckBox.setActionCommand("nouveauclientcheckbox");

        logoutButton.addActionListener(c);
        logoutButton.setActionCommand("logoutbutton");

        nextArticleButton.addActionListener(c);
        nextArticleButton.setActionCommand("nextarticlebutton");

        previousArticleButton.addActionListener(c);
        previousArticleButton.setActionCommand("previousarticlebutton");

        acheterButton.addActionListener(c);
        acheterButton.setActionCommand("acheterbutton");

        viderLePanierButton.addActionListener(c);
        viderLePanierButton.setActionCommand("viderlepanierbutton");

        supprimerArticleButton.addActionListener(c);
        supprimerArticleButton.setActionCommand("supprimerarticlebutton");

        confirmerAchatButton.addActionListener(c);
        confirmerAchatButton.setActionCommand("confirmerachatbutton");

        getFrame().addWindowListener(c);
    }
}
