package ADTO;

import ADTO.Infrastructure.ConnectionFactory;
import ADTO.Model.ClientModel;
import ADTO.View.MainFrame;
import ADTO.Controller.Controller;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.net.Socket;
import java.sql.Connection;


public class App {

    // LAUNCHER DE L'APPLICATION
    public static void main(String[] args)
    {
        System.out.println("Lancement de l'application");

        //Mise en place du LookAndFeel
        try
        {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        }
        catch (Exception e) {
            System.out.println("LookAndFeel : "  + e);
        }

        // Création du client
        ClientModel client = new ClientModel();

        //Création de la fenetre
        MainFrame window = new MainFrame();

        // Connexion au serveur
        Socket socket = client.ConnexionToServer("192.168.175.128",50000);
        System.out.println("Connexion au serveur réussie");

        // Connexion à la base de données
        new ConnectionFactory("com.mysql.cj.jdbc.Driver","PassStudent1_","Student","jdbc:mysql://192.168.175.128:3306/PourStudent");
        Connection con = ConnectionFactory.createConnection();
        System.out.println("Connexion à la base de données réussie");

        // Création du controller
        Controller Controller = new Controller(client,window,con,socket);

        //Assignation du controller à la fenetre
        window.setController(Controller);

    }
}
