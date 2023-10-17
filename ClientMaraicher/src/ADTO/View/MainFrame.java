package ADTO.View;

import ADTO.Controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

public class MainFrame extends  JFrame
{
    JFrame frame;
    private JTextField UserTextField;
    private JTextField PasswordTextField;
    private JButton loginButton;
    private JButton logoutButton;
    private JCheckBox nouveauClientCheckBox;
    private JPanel ConnexionPanel;
    private JPanel MainPanel;
    private JLabel TitleLabel;
    private JPanel MagasinPanel;
    private JTextField ArticleTextField;
    private JTextField PrixUnitaireTextField;
    private JTextField StockTextField;
    private JSpinner QuantiteSpinner;
    private JButton previousArticleButton;
    private JButton nextArticleButton;
    private JPanel PanierPanel;
    private JTable table;
    private JButton viderLePanierButton;
    private JButton supprimerArticleButton;
    private JButton confirmerAchatButton;
    private JTextField TotalTextField;
    private JLabel imageLabel;
    private JButton acheterButton;
    private JScrollPane TabScollPane;


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
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // A la Fermeture de l'application
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    private void createUIComponents()
    {
        // Création des colonne du tableau
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Article");
        model.addColumn("Prix Unitaire");
        model.addColumn("Quantité");

        table = new JTable(model);
        TabScollPane = new JScrollPane(table);
    }

    //Set Controller
    public void setController(Controller c)
    {
        loginButton.addActionListener(c);
        loginButton.setActionCommand("loginbutton");

        getFrame().addWindowListener(c);
    }
}
