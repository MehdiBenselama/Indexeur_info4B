package indexeur.reseau;

import indexeur.data.*;
import indexeur.moteur.MoteurIndexation;
import java.io.IOException;
import java.net.*;


public class Serveur extends Thread {

    private int port;
    private IndexInverse index;
    private MoteurIndexation moteur;
    private boolean arret;
    private int numClient;
    private ServerSocket serverSocket;

    public Serveur(int port, IndexInverse index, MoteurIndexation moteur) throws IOException {
        this.port=port;
        this.index=index;
        this.moteur=moteur;
        this.arret=false;
        this.numClient=0;
    }

    public void arreter() throws IOException {
        this.arret=true;
        this.serverSocket.close();
    }


    public void run() {
    
        try {
            this.serverSocket=new ServerSocket(this.port);
            
            while(!this.arret){
            
            Socket s=this.serverSocket.accept();
            Client c= new Client(numClient,s,this.index,this.moteur);
            this.numClient++;
            c.start();

        }
        } catch (IOException ex) {
            System.getLogger(Serveur.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }


    }
}