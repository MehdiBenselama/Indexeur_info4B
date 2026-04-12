package fileindexer.data;

import java.io.File;
import java.util.LinkedList;

/**
 * ═══════════════════════════════════════════════════════════════
 *  FILE D'ATTENTE DES FICHIERS À INDEXER
 *  Couche 1 : Données
 * ═══════════════════════════════════════════════════════════════
 *
 *  NOTION NÉCESSAIRE : synchronized + wait() + notifyAll()
 *  EXEMPLE DU PROF   : SleepingBarber01.java → classe SalleAttente
 *
 *  RÔLE : Pont entre l'Explorateur (producteur) et les ThreadIndexeur (consommateurs).
 *         L'Explorateur AJOUTE des fichiers.
 *         Les Indexeurs RETIRENT des fichiers.
 *         Quand la file est vide, les indexeurs DORMENT (wait).
 *         Quand un fichier arrive, on les RÉVEILLE (notifyAll).
 *
 *  POURQUOI synchronized + wait/notify ?
 *         Plusieurs threads accèdent à la même LinkedList.
 *         Sans synchronized → crash ou bug (deux threads retirent le même fichier).
 *         wait() → l'indexeur dort quand il n'y a rien à faire.
 *         notifyAll() → on le réveille quand un fichier arrive.
 */
public class FileQueue {

    private LinkedList<File> list;
    private int maxElements;
    private boolean ferme;

    public FileQueue(int max) {
        this.list=new LinkedList<File>();
        this.maxElements=max;
        this.ferme=false;
    }

    /**
     * Ajoute un fichier dans la file. Réveille les indexeurs.
     *
     * ÉTAPES :
     *   1. Déclarer la méthode "synchronized" (dans la signature)
     *   2. if (list.size() < maxElements) → list.addLast(f)
     *   3. notifyAll()  ← réveille les indexeurs qui dorment
     *   4. return 0 (succès)
     *   5. else → return -1 (file pleine)
     *
     * REGARDE : SalleAttente.ajouter() dans SleepingBarber01.java ligne 25-33
     */
    public synchronized int ajouter(File f) {
        if(this.list.size()<maxElements)
        {
            this.list.addLast(f);
            notifyAll();
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Retire un fichier. BLOQUE si la file est vide (l'indexeur dort).
     *
     * ÉTAPES :
     *   1. Déclarer la méthode "synchronized"
     *   2. while (list.size() == 0 && !ferme) { wait(); }
     *      → le thread dort ici jusqu'à notifyAll()
     *      → ATTENTION : wait() dans un try/catch(InterruptedException)
     *   3. if (list.size() == 0) return null  ← file fermée et vide = fin
     *   4. return list.removeFirst()
     *
     * REGARDE : SalleAttente.retirer() dans SleepingBarber01.java ligne 36-43
     */
    public synchronized File retirer() {
        while(this.list.isEmpty() && this.ferme==false)
        {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        if(this.list.isEmpty())
        {
            return null;
        }
        return this.list.removeFirst();
    }

    /**
     * Nombre d'éléments.
     * ÉTAPE : déclarer synchronized, retourner list.size()
     */
    public synchronized int nombreElements() {
        return this.list.size();
    }

    /**
     * Ferme la file et réveille tous les threads bloqués.
     *
     * ÉTAPES :
     *   1. Déclarer synchronized
     *   2. ferme = true
     *   3. notifyAll()  ← sinon les indexeurs restent bloqués dans wait() pour toujours
     *
     * REGARDE : SalleAttente.fermer() dans SleepingBarber01.java ligne 50-52
     */
    public synchronized void fermer() {
        ferme=true;
        notifyAll();
    }

    public synchronized boolean estTermine() {
        return ferme && this.list.isEmpty();
    }
}