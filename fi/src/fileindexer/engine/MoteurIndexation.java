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
  // TODO : this.index = index, queue = new FileQueue(500),
  //        indexeurs = new ThreadIndexeur[NB_INDEXEURS], enCours = false
 }

 /**
  * LANCER l'indexation d'un répertoire.
  * MODÈLE : Banque04 → d1.start(), d2.start()
  *
  * ÉTAPES :
  *   1. Vérifier que le chemin existe et est un dossier
  *   2. enCours = true
  *   3. Créer l'Explorateur et le start()
  *   4. Créer les 4 ThreadIndexeur et les start()
  *   5. Créer un thread de sauvegarde périodique (daemon) :
  *      → threadSauvegarde = new Thread(() -> {
  *          while(enCours) { sleep(30000); index.sauvegarder(...); }
  *        });
  *      → threadSauvegarde.setDaemon(true);
  *      → threadSauvegarde.start();
  */
 public void lancer(String chemin) {
  // TODO
 }

 /**
  * ATTENDRE la fin de l'indexation.
  * MODÈLE : Banque04 → d1.join(), d2.join()
  *
  * ÉTAPES :
  *   1. explorateur.join()
  *   2. for chaque indexeur : indexeurs[i].join()
  *   3. enCours = false
  *   4. index.sauvegarder(FICHIER_INDEX)
  *   Entourer de try/catch(InterruptedException)
  */
 public void attendre() {
  // TODO
 }

 public boolean estEnCours() { return enCours; }
 public int getNbFichiersTrouves() {
  return explorateur != null ? explorateur.getNbFichiersTrouves() : 0;
 }
 public String getFichierIndex() { return FICHIER_INDEX; }
}
