package ADTO.Model;

import java.io.*;

import java.net.*;

public class ClientModel {

    // Variables membres de la classe
    private String nom;
    private String motDePasse;
    private boolean isNewClient;
    private boolean connect;

    // Getters et Setters

    // isNewClient

    public boolean getIsNewClient() {
        return isNewClient;
    }

    public void setIsNewClient(boolean isNewClient) {
        this.isNewClient = isNewClient;
    }

    // Nom

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Mot de passe

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setConnect(boolean connect) { this.connect = connect; }

    public boolean getConnect() { return connect; }


    // Constructeur
    public ClientModel()
    {
        this.nom = "";
        this.motDePasse = "";
        this.connect = false;
    }

    // Méthode de connexion au serveur
    public Socket ConnexionToServer(String hostIp, int port){
        // Création de la socket et connexion au serveur
        try {
            return new Socket(hostIp,port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}