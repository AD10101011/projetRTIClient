package ADTO.View;

import ADTO.Controller.ClientController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

public class MainFrame extends  JFrame{
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
    private JPanel PublicitePanel;
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

    public JButton getLoginButton() {
        return loginButton;
    }

    //#endregion



    public MainFrame(Socket socket, Connection con)  {


        // Affichage de la fenêtre
        JFrame frame = new JFrame("Le Maraicher d'abdel karim");
        frame.setContentPane(MainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Fermeture de l'application
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Fermeture de l'application");

                try {
                    socket.close();
                    System.out.printf("Socket fermée\n");
                    con.close();
                    System.out.println("Connexion à la base de données fermée");
                } catch (IOException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });

    }
    private void createUIComponents() {
        // Création des colonne du tableau
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Article");
        model.addColumn("Prix Unitaire");
        model.addColumn("Quantité");

        table = new JTable(model);
        TabScollPane = new JScrollPane(table);
    }
}
