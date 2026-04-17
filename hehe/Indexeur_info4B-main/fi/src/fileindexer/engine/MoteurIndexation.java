package fileindexer.engine;

import fileindexer.data.*;
import java.io.File;

/**
 * MOTEUR D'INDEXATION — Crée et coordonne les threads — Couche 2 : Moteur
 *
 * NOTIONS :
 *   1. Thread.start() → lancer un thread
 *   2. Thread.join()  → attendre qu'un thread se termine
 *   3. setDaemon(true) → thread qui s'arrête quand le main s'arrête
 * MODÈLES :
 *   - Banque04.java → d1.start(), d2.start(), d1.join(), d2.join()
 *   - SleepingBarber01 → b.start(), simul.start(), simul.join()
 *
 * PRINCIPE :
 * Crée 1 Explorateur + 4 ThreadIndexeur + 1 thread sauvegarde.
 * Les lance tous, puis peut attendre leur fin avec join().
 *
 * ÉTAPES :
 * 1. lancer() : créer les threads et les start()
 * 2. attendre() : join() sur chaque thread
 */
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
        this.queue = new FileQueue(500);
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