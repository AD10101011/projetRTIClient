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
import java.sql.*;

public class Controller extends WindowAdapter implements ActionListener
{

    //#region Variables membres de la classe
    private  ClientModel _client;
    private Connection _con;
    private Socket _socket;
    private MainFrame _mainFrame;
    private String _requete;

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
            DataOutputStream dos = new DataOutputStream(getSocket().getOutputStream());
            DataInputStream dis = new DataInputStream(getSocket().getInputStream());
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
            String userTxt = getMainFrame().getUserTextField().getText();
            String passwordTxt = getMainFrame().getPasswordTextField().getText();
            JOptionPane.showMessageDialog(null,"User : " + userTxt + "\nPassword : " + passwordTxt);
            ImageIcon icon = new ImageIcon("C:\\Users\\La Pute A Nathan\\IdeaProjects\\projetRTIClient\\ClientMaraicher\\images\\concombre.jpg");
            // Créez un JLabel avec l'icône
            getMainFrame().imageLabel.setIcon(icon);
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
}
