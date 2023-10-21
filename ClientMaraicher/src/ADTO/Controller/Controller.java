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
        String reponse;
        String[] reponseSplit;

        if(e.getActionCommand().equals("loginbutton"))
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
                        setRequete("LOGIN#" + userTxt + "#" + passwordTxt + "#oui");
                        reponse = Echange();
                        reponseSplit = reponse.split("#");

                        if(reponseSplit[1].equals("ok")) {
                            JOptionPane.showMessageDialog(null, "Création de compte réussie ! Bienvenue " + userTxt);
                            consult_Article(indiceArticle);
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Création de compte échouée ! : " + reponseSplit[2]);
                        }

                    }
                    else
                    {
                        setRequete("LOGIN#" + userTxt + "#" + passwordTxt + "#non)");
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

        if(e.getActionCommand().equals("nouveauclientcheckbox"))
        {
            getClient().setIsNewClient(getMainFrame().nouveauClientCheckBox.isSelected());
        }

        if(e.getActionCommand().equals("logoutbutton"))
        {
            setRequete("LOGOUT#");
            reponse = Echange();
            reponseSplit = reponse.split("#");

            if(reponseSplit[1].equals("ok"))
            {
                JOptionPane.showMessageDialog(null, "Déconnexion réussie !");
                windowClosing(null);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Déconnexion échouée : " + reponseSplit[2]);
            }
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
            String intitule;
            String quantite,nbrArticle;
            String prix;

            if((int) getMainFrame().QuantiteSpinner.getValue() <= 0)
            {
                JOptionPane.showMessageDialog(null, "Veuillez entrer une quantité supérieure à 0");
            }

            setRequete("ACHAT#" + indiceArticle + "#" + getMainFrame().QuantiteSpinner.getValue());

            reponse = Echange();

            reponseSplit = reponse.split("#");

            if(reponseSplit[1].equals("-2"))
            {
                JOptionPane.showMessageDialog(null,"ACHAT REFUSE, Caddie Rempli !");
            } else if (reponseSplit[1].equals("-1")) {
                JOptionPane.showMessageDialog(null,"ACHAT REFUSE, Article Inexistant !");

            } else if (reponseSplit[2].equals("0")) {
                    JOptionPane.showMessageDialog(null, "CHAT REFUSE, Quantite Insufisante !");
            }
            else
            {
                JOptionPane.showMessageDialog(null,"ACHAT REUSSI !, Merci pour votre achat !");
                quantite = reponseSplit[2];
                prix = remplacePointparVirgule(reponseSplit[3]);

                // MAJ DU PRIX TOTAL
                total += Integer.parseInt(quantite) * Float.parseFloat(prix);
                getMainFrame().TotalTextField.setText(String.valueOf(total));

                // MAJ DE L'INTERFACE D'ACHAT

                consult_Article(indiceArticle);

                /* MAJ DU CADDIE
                setRequete("CADDIE#");
                reponse = Echange();
                reponseSplit = reponse.split("#");
                nbrArticle = reponseSplit[1];

                for(int i = 0; i < Integer.parseInt(nbrArticle); i++)
                {
                    intitule = reponseSplit[2 + (i * 3)];
                    prix = reponseSplit[3 + (i * 3)];
                    quantite = reponseSplit[3 + (i * 3)];

                    ajouteArticleCaddie(intitule,prix,quantite);
                }
                */
            }
        }

    }

    //A la fermeture de l'App
    @Override
    public void windowClosing(WindowEvent e)
    {
        System.out.println("Fermeture de l'application");

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

    public String Echange()
    {
        // Envoi de la requete
        try {
            dos.write(getRequete().getBytes());
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Lecture de la requête
        StringBuffer buffer = new StringBuffer();
        boolean EOT = false;
        while(!EOT) // boucle de lecture byte par byte
        {
            try {


                byte b1 = dis.readByte();
                System.out.println("b1 : --" + (char) b1 + "--");
                if (b1 == (byte) '/'){
                    b1 = dis.readByte();
                    if(b1 == (byte) '0') EOT = true;
                } else buffer.append((char) b1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            image = "C:\\Users\\La Pute A Nathan\\IdeaProjects\\projetRTIClient\\ClientMaraicher\\images\\" + reponseSplit[5];
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
}
