package ADTO.Controller;

import ADTO.Model.ClientModel;
import ADTO.View.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Controller extends WindowAdapter implements ActionListener
{

    //#region Variables membres de la classe
    private  ClientModel _client;
    private Connection _con;
    private Socket _socket;
    private MainFrame _mainFrame;
    private String _requete;

    public DataOutputStream dos;
    public DataInputStream dis;

    private int indiceArticle = 1;

    private float total = 0;

    public String reponse;
    public String[] reponseSplit;

    //#endregion

    //#region Propriétés (Getters et Setters)
    // Getters et Setters

    // Client
    public ClientModel getClient() {
        return _client;
    }

    public void setClient(ClientModel client) {
        _client = client;
    }

    // Connection

    public Connection getCon() {
        return _con;
    }

    public void setCon(Connection con) {
        _con = con;
    }

    // Socket

    public Socket getSocket() {
        return _socket;
    }

    public void setSocket(Socket socket) {
        _socket = socket;
    }

    // MainFrame

    public MainFrame getMainFrame() {
        return _mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        _mainFrame = mainFrame;
    }

    // Requete

    public String getRequete() {
        return _requete;
    }

    public void setRequete(String requete) {
        _requete = requete;
    }

    //#endregion

    //#region Constructeur

    public Controller(ClientModel client, MainFrame window, Connection con, Socket socket)
    {
        //Ininitialisation de l'application

        setClient(client); //Model
        setMainFrame(window); //View

        setCon(con);
        setSocket(socket);

        logoutOk();

        // Création des flux
        try
        {
            dos = new DataOutputStream(new BufferedOutputStream(getSocket().getOutputStream()));
            dis = new DataInputStream(new BufferedInputStream(getSocket().getInputStream()));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    //Detection d'une action
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("loginbutton"))
        {
            on_pushButtonLogin_clicked();
        }

        if(e.getActionCommand().equals("nouveauclientcheckbox"))
        {
            getClient().setIsNewClient(getMainFrame().nouveauClientCheckBox.isSelected());
        }

        if(e.getActionCommand().equals("logoutbutton"))
        {
            on_pushButtonLogout_clicked();
        }

        if(e.getActionCommand().equals("nextarticlebutton"))
        {
            indiceArticle++;
            if(consult_Article(indiceArticle) != 1)
            {
                indiceArticle--;
            }
        }

        if(e.getActionCommand().equals("previousarticlebutton"))
        {
            indiceArticle--;
            if(consult_Article(indiceArticle) != 1)
            {
                indiceArticle++;
            }
        }

        if(e.getActionCommand().equals("acheterbutton"))
        {
            on_pushButtonAcheter_clicked();
        }

        if(e.getActionCommand().equals("confirmerachatbutton"))
        {
            on_pushButtonConfirmerAchat_clicked();
        }

        if(e.getActionCommand().equals("viderlepanierbutton"))
        {
            on_pushButtonViderLePanier_clicked();
        }

        if(e.getActionCommand().equals("supprimerarticlebutton"))
        {
            on_pushButtonSupprimerArticle_clicked();
        }
    }

    //A la fermeture de l'App
    @Override
    public void windowClosing(WindowEvent e)
    {
        System.out.println("Fermeture de l'application");
        if(getClient().getConnect() == true)
        {
            on_pushButtonLogout_clicked();
        }

        try {
            getSocket().close();
            System.out.printf("Socket fermée\n");
            getCon().close();
            System.out.println("Connexion à la base de données fermée");
        } catch (IOException | SQLException ex) {
            throw new RuntimeException(ex);
        }
        System.exit(0);
    }
    //#endregion

    public String Echange() {
        // Envoi de la requete
        try {
            dos.write(String.format("%04d", getRequete().length()).getBytes()); //Entete
            dos.write(getRequete().getBytes());
            dos.flush();
        } catch (IOException e) { throw new RuntimeException(e); }
        System.out.println("Envoye : " + getRequete());

        // Lecture de l'entete de la requête
        StringBuffer bufferTaille = new StringBuffer();
        for(int i=0; i < 4; i++)
        {
            try {
                byte b = dis.readByte();
                bufferTaille.append((char) b);
            } catch (IOException e) { throw new RuntimeException(e); }
        }
        int tailleRequete = Integer.parseInt(bufferTaille.toString());
        System.out.println("Taille de la Requete : " + tailleRequete);

        //Lecture des données la requete
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i < tailleRequete; i++) // boucle de lecture byte par byte
        {
            try {
                byte b1 = dis.readByte();
                System.out.println("b1 : --" + (char) b1 + "--");
                buffer.append((char) b1);
            } catch (IOException e) { throw new RuntimeException(e); }
        }

        String requete = buffer.toString();
        System.out.println("Reçu : --" + requete + "--");

        return requete;
    }

    public int consult_Article(int indice)
    {
        String reponse , intitule,image,prix,stock;
        String[] reponseSplit;

        setRequete("CONSULT#" + indice);
        reponse = Echange();

        reponseSplit = reponse.split("#");

        if(reponseSplit.length != 6)
        {
            JOptionPane.showMessageDialog(null, "Article introuvable");
            return 0;
        }
        else
        {
            intitule = reponseSplit[2];
            prix = remplacePointparVirgule(reponseSplit[3]);
            stock = reponseSplit[4];
            image = "images/" + reponseSplit[5];
            //Path path = Paths.get(image);
            //System.out.println(path.toAbsolutePath());
            setArticle(intitule,prix,stock,image);

            return 1;
        }
    }

    public String remplacePointparVirgule(String prix)
    {
        String prixVirgule = "";
        for(int i = 0; i < prix.length(); i++)
        {
            if(prix.charAt(i) == '.')
            {
                prixVirgule += ',';
            }
            else
            {
                prixVirgule += prix.charAt(i);
            }
        }
        return prixVirgule;
    }

    public void setArticle(String intitule, String prix, String stock, String image)
    {
        getMainFrame().ArticleTextField.setText(intitule);
        getMainFrame().PrixUnitaireTextField.setText(prix);
        getMainFrame().StockTextField.setText(stock);
        System.out.println(image);
        getMainFrame().imageLabel.setIcon(new ImageIcon(image));
    }

    public void ajouteArticleCaddie(String intitule, String prix, String quantite)
    {
        getMainFrame().model.addRow(new Object[]{intitule,prix,quantite});

    }

    public void setTotalTextField(int total)
    {
        getMainFrame().TotalTextField.setText(String.valueOf(total));
    }

    public void logoutOk()
    {
        getMainFrame().loginButton.setEnabled(true);
        getMainFrame().logoutButton.setEnabled(false);
        getMainFrame().nextArticleButton.setEnabled(false);
        getMainFrame().previousArticleButton.setEnabled(false);
        getMainFrame().acheterButton.setEnabled(false);
        getMainFrame().confirmerAchatButton.setEnabled(false);
        getMainFrame().viderLePanierButton.setEnabled(false);
        getMainFrame().supprimerArticleButton.setEnabled(false);
        getMainFrame().nouveauClientCheckBox.setEnabled(true);
        getMainFrame().quantiteSpinner.setEnabled(false);
    }
    public void loginOk()
    {
        getMainFrame().loginButton.setEnabled(false);
        getMainFrame().logoutButton.setEnabled(true);
        getMainFrame().nextArticleButton.setEnabled(true);
        getMainFrame().previousArticleButton.setEnabled(true);
        getMainFrame().acheterButton.setEnabled(true);
        getMainFrame().confirmerAchatButton.setEnabled(true);
        getMainFrame().viderLePanierButton.setEnabled(true);
        getMainFrame().supprimerArticleButton.setEnabled(true);
        getMainFrame().nouveauClientCheckBox.setEnabled(false);
        getMainFrame().quantiteSpinner.setEnabled(true);
    }

    public void on_pushButtonLogout_clicked()
    {
        on_pushButtonViderLePanier_clicked();

        setRequete("LOGOUT#");
        reponse = Echange();
        reponseSplit = reponse.split("#");

        if(reponseSplit[1].equals("ok"))
        {
            JOptionPane.showMessageDialog(null, "Déconnexion réussie !");
            logoutOk();
            getClient().setConnect(false);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Déconnexion échouée : " + reponseSplit[2]);
        }
    }

    public void on_pushButtonLogin_clicked()
    {
        String userTxt = getMainFrame().getUserTextField().getText();
        String passwordTxt = getMainFrame().getPasswordTextField().getText();

        if(userTxt.isEmpty())
        {
            if(passwordTxt.isEmpty())
            {
                JOptionPane.showMessageDialog(getMainFrame().getFrame(),"Veuillez entrer un nom d'utilisateur et un mot de passe");
            }
            else
            {
                JOptionPane.showMessageDialog(getMainFrame().getFrame(),"Veuillez entrer un nom d'utilisateur");
            }
        }
        else
        {
            if(passwordTxt.isEmpty())
            {
                JOptionPane.showMessageDialog(getMainFrame().getFrame(),"Veuillez entrer un mot de passe");
            }
            else
            {
                // Envoi de la requete
                if(getClient().getIsNewClient())
                {
                    loginOk();
                    getClient().setConnect(true);
                    setRequete("LOGIN#" + userTxt + "#" + passwordTxt + "#oui");
                    reponse = Echange();
                    reponseSplit = reponse.split("#");

                    if(reponseSplit[1].equals("ok")) {
                        JOptionPane.showMessageDialog(null, "Création de compte réussie ! Bienvenue " + userTxt);
                        //consult_Article(indiceArticle);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Création de compte échouée ! : " + reponseSplit[2]);
                    }

                }
                else
                {
                    loginOk();
                    getClient().setConnect(true);
                    setRequete("LOGIN#" + userTxt + "#" + passwordTxt + "#non");
                    reponse = Echange();

                    reponseSplit = reponse.split("#");

                    if(reponseSplit[1].equals("ok")) {
                        JOptionPane.showMessageDialog(null, "Connexion réussie ! Bienvenue " + userTxt);
                        consult_Article(indiceArticle);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Connexion échouée : " + reponseSplit[2]);
                    }
                }

            }

        }
    }

    public void on_pushButtonAcheter_clicked()
    {
        String intitule;
        String quantite;
        float prix;
        String prixFormater;
        int nbrArticle;

        if((int) getMainFrame().quantiteSpinner.getValue() <= 0)
        {
            JOptionPane.showMessageDialog(null, "Veuillez entrer une quantité supérieure à 0");
            return;
        }

        setRequete("ACHAT#" + indiceArticle + "#" + getMainFrame().quantiteSpinner.getValue());

        reponse = Echange();

        reponseSplit = reponse.split("#");

        if(reponseSplit[1].equals("-2"))
        {
            JOptionPane.showMessageDialog(null,"ACHAT REFUSE, Caddie Rempli !");
        } else if (reponseSplit[1].equals("-1")) {
            JOptionPane.showMessageDialog(null,"ACHAT REFUSE, Article Inexistant !");

        } else if (reponseSplit[2].equals("0")) {
            JOptionPane.showMessageDialog(null, "ACHAT REFUSE, Quantite Insufisante !");
        }
        else
        {
            JOptionPane.showMessageDialog(null,"ACHAT REUSSI !, Merci pour votre achat !");
            quantite = reponseSplit[2];

            // MAJ DU PRIX TOTAL
            total += Integer.parseInt(quantite) * Float.parseFloat(reponseSplit[3]);
            getMainFrame().TotalTextField.setText(String.valueOf(total));

            // MAJ DE L'INTERFACE D'ACHAT

            consult_Article(indiceArticle);

            //MAJ DU CADDIE
            setRequete("CADDIE#");
            reponse = Echange();
            reponseSplit = reponse.split("#");
            nbrArticle = Integer.parseInt(reponseSplit[1]);

            getMainFrame().model.setRowCount(0); //Vide le tableau

            for(int i = 0; i < nbrArticle; i++)
            {
                intitule = reponseSplit[2 + (i * 3)];
                prix = Float.parseFloat(reponseSplit[3 + (i * 3)]);
                quantite = reponseSplit[4 + (i * 3)];
                prixFormater = String.format("%.2f",prix);
                ajouteArticleCaddie(intitule,prixFormater,quantite);
            }
        }
    }

    public void on_pushButtonConfirmerAchat_clicked()
    {
        getMainFrame().model.setRowCount(0);
        setRequete("CONFIRMER");
        Echange();
        setTotalTextField(0);
        total = 0;
    }

    public void on_pushButtonViderLePanier_clicked()
    {
        getMainFrame().model.setRowCount(0);
        setRequete("CANCELALL");
        Echange();
        setTotalTextField(0);
        total = 0;
        consult_Article(indiceArticle);
    }

    public void on_pushButtonSupprimerArticle_clicked()
    {
        int indice;



        if((indice = getMainFrame().model.getRowCount()) == 0)
        {
            JOptionPane.showMessageDialog(null,"Veuillez sélectionner un article à supprimer");
            return;
        }
        else{
            String intitule , reponse, prixFormater, quantite;
            int nbrArticle;
            float prix;
            String[] reponseSplit;
            indice--;
            setRequete("Cancel#" + indice);
            reponse = Echange();
            reponseSplit = reponse.split("#");
            if(reponseSplit[1].equals("oui"))
            {
                getMainFrame().model.setRowCount(0);
                setRequete("CADDIE#");
                reponse = Echange();
                reponseSplit = reponse.split("#");

                nbrArticle = Integer.parseInt(reponseSplit[1]);
                total = 0;

                for(int i = 0; i < nbrArticle; i++)
                {
                    intitule = reponseSplit[2 + (i * 3)];
                    prix = Float.parseFloat(reponseSplit[3 + (i * 3)]);
                    quantite = reponseSplit[4 + (i * 3)];
                    prixFormater = String.format("%.2f",prix);
                    ajouteArticleCaddie(intitule,prixFormater,quantite);
                }


            }
            else
            {
                JOptionPane.showMessageDialog(null,"Suppression échouée");
            }
        }
    }

}
