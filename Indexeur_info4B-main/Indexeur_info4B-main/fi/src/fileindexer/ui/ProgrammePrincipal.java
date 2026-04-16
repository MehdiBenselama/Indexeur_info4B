package fileindexer.ui;
import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import fileindexer.network.Serveur;
import java.util.*;


public class ProgrammePrincipal {
    static final int PORT = 9876;
    static final String FICHIER_INDEX = "sauvgarde";
    private IndexInverse index;
    private MoteurIndexation moteur;
    private Serveur serveur;

    public void demarrer() {
        this.index=IndexInverse.charger(FICHIER_INDEX);
        this.moteur = new MoteurIndexation(index);
        try {
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
        int choix = sc.nextInt();
        sc.nextLine();
        switch (choix) {
            case 1:
                System.out.print("Répertoire : ");
                moteur.lancer(sc.nextLine());
                moteur.attendre();
                System.out.println("Indexation terminée.");
                break;

            case 2:
                System.out.print("Mots : ");
                List<ResultatRecherche> res = index.rechercher(sc.nextLine().split(" "));
                if (res.isEmpty()) System.out.println("Aucun résultat.");
                else for (ResultatRecherche r : res) System.out.println(r);
                break;

            case 3:
                System.out.print("Clé : ");
                String cle = sc.nextLine();
                System.out.print("Valeur : ");
                String valeur = sc.nextLine();
                List<ResultatRecherche> resMeta = index.rechercherParMetaDonnees(cle, valeur);
                if (resMeta.isEmpty()) System.out.println("Aucun résultat.");
                else for (ResultatRecherche r : resMeta) System.out.println(r);
                break;

            case 4:
                System.out.print("Chemin du fichier : ");
                FicheDocument fiche = index.getFiche(sc.nextLine());
                if (fiche == null) { System.out.println("Fichier non trouvé."); break; }
                System.out.print("Clé : ");
                String cleMeta = sc.nextLine();
                System.out.print("Valeur : ");
                fiche.ajouterMeta(cleMeta, sc.nextLine());
                System.out.println("Annotation ajoutée.");
                break;

            case 5:
                System.out.print("Stop-word : ");
                index.ajouterStopWord(sc.nextLine());
                System.out.println("Stop-word ajouté.");
                break;

            case 6:
                List<List<String>> doublons = index.trouverDoublons();
                if (doublons.isEmpty()) System.out.println("Aucun doublon.");
                else for (List<String> groupe : doublons) System.out.println(groupe);
                break;

            case 7:
                System.out.println("Fichiers : " + index.getNombreFichiers());
                System.out.println("Termes   : " + index.getNombreTermes());
                System.out.println("En cours : " + moteur.estEnCours());
                break;

            case 8:
                Enumeration<FicheDocument> fiches = index.getTousFichiers();
                while (fiches.hasMoreElements())
                    System.out.println(fiches.nextElement());
                break;

            case 9:
                index.sauvegarder(FICHIER_INDEX);
                System.out.println("Sauvegardé.");
                break;

            case 0:
                index.sauvegarder(FICHIER_INDEX);
                System.out.println("Au revoir !");
                return;

            default:
                System.out.println("Choix invalide.");
        }
        
        this.afficherAide();

    }
}

    private void afficherAide() {

        System.out.println("\n1 - Indexer un répertoire");
        System.out.println("2 - Rechercher par mots clés");
        System.out.println("3 - Rechercher par métadonnées");
        System.out.println("4 - Annoter un fichier");
        System.out.println("5 - Ajouter un stop-word");
        System.out.println("6 - Détecter les doublons");
        System.out.println("7 - Status");
        System.out.println("8 - Lister les fichiers");
        System.out.println("9 - Sauvegarder");
        System.out.println("0 - Quitter");
        System.out.print("\n> ");
    }

    public static void main(String[] args) {
        new ProgrammePrincipal().demarrer();
    }
}