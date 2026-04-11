package fileindexer.network;

import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import java.io.IOException;
import java.net.*;

/**
 * SERVEUR TCP — Couche 3 : Réseau
 *
 * NOTION : ServerSocket + accept() + 1 Thread par client
 * MODÈLE : Chat/ServeurMC.java DU PROF (recopier et adapter)
 *
 * PRINCIPE :
 * Le serveur écoute sur un port. Quand un client se connecte
 * (telnet localhost 9876), il crée un thread ConnexionClient.
 * C'est EXACTEMENT le même code que ServeurMC.java.
 *
 * ÉTAPES :
 * 1. Lire Chat/ServeurMC.java du prof (15 min)
 * 2. Recopier : ServerSocket → boucle accept() → new Thread → start()
 * 3. Adapter ConnexionClient pour les commandes de l'indexeur
 * 4. Tester avec : telnet localhost 9876
 */
    public class Serveur extends Thread {

    private int port;
    private IndexInverse index;
    private MoteurIndexation moteur;
    private volatile boolean arret;
    private int numClient;
    private ServerSocket serverSocket;

    public Serveur(int port, IndexInverse index, MoteurIndexation moteur) throws IOException {
    // TODO : affecter les attributs, arret = false, numClient = 0
        this.port=port;
        this.index=index;
        this.moteur=moteur;
        this.arret=false;
        this.numClient=0;
    }

    /** Arrêter le serveur : arret = true + serverSocket.close() */
    public void arreter() throws IOException {
        this.arret=true;
        this.serverSocket.close();
    }

    /**
     * BOUCLE DU SERVEUR — même code que ServeurMC.java du prof.
     * ÉTAPES :
     *   serverSocket = new ServerSocket(port);
     *   while (!arret) {
     *     Socket soc = serverSocket.accept();   // BLOQUE jusqu'à connexion
     *     ConnexionClient cc = new ConnexionClient(numClient, soc, index, moteur);
     *     numClient++;
     *     cc.start();
     *   }
     *   Entourer de try/catch(IOException)
     */
    public void run() {
    
        try {
            this.serverSocket=new ServerSocket();
            
            while(!this.arret){
            
            Socket s=this.serverSocket.accept();
            Client c= new Client(numClient,s,this.index,this.moteur);
            numClient++;
            c.start();

        }
        } catch (IOException ex) {
            System.getLogger(Serveur.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }


    }
}
