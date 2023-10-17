package ADTO;

import ADTO.Infrastructure.ConnectionFactory;
import ADTO.Model.ClientModel;
import ADTO.View.MainFrame;
import ADTO.Controller.ClientController;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class App {

    private static ClientModel client;

    // LAUNCHER DE L'APPLICATION

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            System.out.println("LookAndFeel : "  + e);
        }
        Socket socket;
        System.out.println("Lancement de l'application");

        // Création du client
        client = new ClientModel();

        // Connexion au serveur
        socket = client.ConnexionToServer("192.168.89.128",50000);
        System.out.println("Connexion au serveur réussie");

        // Connexion à la base de données
        new ConnectionFactory("com.mysql.cj.jdbc.Driver","PassStudent1_","Student","jdbc:mysql://192.168.89.128:3306/PourStudent");
        Connection con = ConnectionFactory.createConnection();
        System.out.println("Connexion à la base de données réussie");

        // Création du controller
        ClientController clientController = new ClientController(client,con,socket);

    }
}
