package ADTO.Model;

import java.io.*;

import java.net.*;

public class ClientModel {

    // Variables membres de la classe
    private String nom;
    private String motDePasse;

    // Getters et Setters

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


    // Constructeur
    public ClientModel()
    {
        nom = "";
        motDePasse = "";
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