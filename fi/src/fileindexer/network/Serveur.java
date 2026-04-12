package fileindexer.network;

import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import java.io.IOException;
import java.net.*;


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
