package fileindexer.ui;
import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import fileindexer.network.Serveur;
import java.util.*;


public class ProgrammePrincipal {
    static final int PORT = 9876;
    static final String FICHIER_INDEX = "index.dat";
    private IndexInverse index;
    private MoteurIndexation moteur;
    private Serveur serveur;

    public void demarrer() {
        this.index=IndexInverse.charger(FICHIER_INDEX);
        this.moteur = new MoteurIndexation(index);
        try {
            System.out.println("=== FileIndexer — Projet Info 4B ===\n");
            serveur = new Serveur(PORT, index, moteur);
            serveur.setDaemon(true); 
            serveur.start();
            afficherAide();
        boucleCommandes();
        } catch (Exception e) {System.out.println("Erreur");}
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

            switch(cmd){
                case "help": case "?": afficherAide(); break;
                case "index":
                    moteur.lancer(args);
                    moteur.attendre();
                    System.out.println("Indexation terminée.");
                break;
                case "search":
                    List<ResultatRecherche> resultats = index.rechercher(args.split("\\s+"));
                    if(resultats.isEmpty()){
                        System.out.println("Aucun résultat.");
                    }else{
                        for(ResultatRecherche r : resultats) {
                            System.out.println(r);
                        }
                    }
                break;
                case "doublons":
                    List<List<String>> doublons = index.trouverDoublons();
                    if(doublons.isEmpty()) {
                        System.out.println("Aucun doublons.");
                    } else {
                        for(List<String> groupe : doublons) System.out.println(groupe);
                    }
                break;
                case "status":
                    System.out.println("Fichiers : "+index.getNombreFichiers());
                    System.out.println("Termes : "+index.getNombreTermes());
                break;
                case "save":
                    index.sauvegarder(FICHIER_INDEX);
                break;
                case "addstop":
                    index.ajouterStopWord(args);
                    System.out.println("Stop word ajouté.");
                break;
                case "list":
                    Enumeration<FicheDocument> e = index.getTousFichiers();
                    while(e.hasMoreElements())
                    {
                        FicheDocument fiche = e.nextElement();
                        System.out.println(fiche);
                    }
                break;
                case "quit": case "exit": index.sauvegarder(FICHIER_INDEX);
                return;
                default: 
                    System.out.println("Commande inconnue: " + cmd);
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