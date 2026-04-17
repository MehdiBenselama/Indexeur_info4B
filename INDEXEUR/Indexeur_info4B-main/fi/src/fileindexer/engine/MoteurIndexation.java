package fileindexer.engine;

import fileindexer.data.*;
import java.io.File;

public class MoteurIndexation {

    private static final int NB_INDEXEURS = 4;
    private static final String FICHIER_INDEX = "index.dat";
    private static final long INTERVALLE_SAUVEGARDE = 30000;

    private IndexInverse index;
    private FileQueue queue;
    private Explorateur explorateur;
    private ThreadIndexeur[] indexeurs;
    private Thread threadSauvegarde;
    private volatile boolean enCours;

    public MoteurIndexation(IndexInverse index) {
        this.index = index; 
        this.queue = new FileQueue(5000);
        this.indexeurs = new ThreadIndexeur[NB_INDEXEURS];
        this.enCours = false;
    }

    public void lancer(String chemin) {
        File f = new File(chemin);
        if(!f.exists() || !f.isDirectory())
        {
            System.out.println("Répertoire invalide !");
            return;
        }
        enCours=true;

        this.explorateur = new Explorateur(f, queue);
        explorateur.start();
        for(int i=0; i<NB_INDEXEURS; i++)
        {
            indexeurs[i] = new ThreadIndexeur(queue, index, "Indexeur-"+i);
            indexeurs[i].start();
        }
        threadSauvegarde = new Thread() {
            public void run() {
                while(enCours)
                {
                    try {
                        Thread.sleep(INTERVALLE_SAUVEGARDE);
                    } catch (InterruptedException e) {}
                    index.sauvegarder(FICHIER_INDEX);
                }
            }
        };
        threadSauvegarde.setDaemon(true);
        threadSauvegarde.start();
    }

 
    public void attendre() {
        try {
            explorateur.join();
            for(int i=0; i<NB_INDEXEURS; i++)
            {
                indexeurs[i].join();
            }
            enCours=false;
            index.sauvegarder(FICHIER_INDEX);
        } catch (InterruptedException e) {}
    }

    public boolean estEnCours() {return enCours;}
    public int getNbFichiersTrouves() {return explorateur!=null ? explorateur.getNbFichiersTrouves() : 0;}
    public String getFichierIndex() {return FICHIER_INDEX;}
}