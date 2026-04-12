package fileindexer.ui;
import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import fileindexer.network.Serveur;
import java.util.*;

/**
 * PROGRAMME PRINCIPAL — Assemble les 4 couches — Couche 4
 *
 * ARCHITECTURE :
 *   Couche 4: UI (ce fichier)        — Scanner, commandes         — Java basique
 *   Couche 3: Réseau (network)       — ServeurIndex, sockets      — MODÈLE: ServeurMC.java
 *   Couche 2: Moteur (engine)        — Threads producteur/conso   — MODÈLE: SleepingBarber01
 *   Couche 1: Données (data)         — synchronized, Serializable — MODÈLE: Banque04+Annuaire
 *
 * ORDRE DE DÉVELOPPEMENT :
 *   1. FicheDocument + ResultatRecherche (pas de thread, le plus simple)
 *   2. IndexInverse (synchronized + sérialisation)
 *   3. FileQueue (wait/notify)
 *   4. Explorateur + ThreadIndexeur (threads)
 *   5. MoteurIndexation (assemblage)
 *   6. ProgrammePrincipal (CLI)
 *   7. ServeurIndex + ConnexionClient (sockets)
 */
public class ProgrammePrincipal {
 static final int PORT = 9876;
 static final String FICHIER_INDEX = "index.dat";
 private IndexInverse index;
 private MoteurIndexation moteur;
 private Serveur serveur;

 public void demarrer() {
  System.out.println("=== FileIndexer — Projet Info 4B ===\n");
  // TODO Étape 1: index = IndexInverse.charger(FICHIER_INDEX)
  // TODO Étape 2: moteur = new MoteurIndexation(index)
  // TODO Étape 3: serveur = new ServeurIndex(PORT, index, moteur)
  //               serveur.setDaemon(true); serveur.start();
  afficherAide();
  boucleCommandes();
 }

 private void boucleCommandes() {
  Scanner sc = new Scanner(System.in);
  while (true) {
   System.out.print("\n> ");
   if (!sc.hasNextLine()) break;
   String ligne = sc.nextLine().trim();
   if (ligne.isEmpty()) continue;
   String[] p = ligne.split("\\s+", 2);
   String cmd = p[0].toLowerCase(), args = p.length > 1 ? p[1] : "";

   // TODO: switch(cmd) pour chaque commande:
   //  "index"    → moteur.lancer(args)
   //  "search"   → index.rechercher(args.split(" ")) + afficher résultats
   //  "meta"     → index.rechercherParMeta(clé, valeur)
   //  "doublons" → index.trouverDoublons()
   //  "annotate" → trouver fiche + ajouterMeta
   //  "addstop"  → index.ajouterStopWord(args)
   //  "status"   → afficher nb fichiers, nb termes, threads
   //  "list"     → parcourir getTousFichiers()
   //  "save"     → index.sauvegarder(FICHIER_INDEX)
   //  "help"     → afficherAide()
   //  "quit"     → sauvegarder + return
   switch (cmd) {
    case "help": case "?": afficherAide(); break;
    case "quit": case "exit": return;
    default: System.out.println("Commande inconnue: " + cmd);
   }
  }
 }

 private void afficherAide() {
  System.out.println("  index <rép>     — indexer un répertoire");
  System.out.println("  search <mots>   — recherche TF-IDF");
  System.out.println("  meta <clé> <val>— recherche métadonnées");
  System.out.println("  doublons        — détecter doublons");
  System.out.println("  status          — état de l'indexeur");
  System.out.println("  list / save / help / quit");
 }

 public static void main(String[] args) {
  new ProgrammePrincipal().demarrer();
 }
}
