package indexeur.iu;
import indexeur.data.*;
import indexeur.moteur.MoteurIndexation;
import indexeur.reseau.Serveur;
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
            serveur = new Serveur(this.PORT, this.index, this.moteur);
            serveur.setDaemon(true); 
            serveur.start();
        } catch (Exception e) {System.out.println("Erreur");}
           
        System.err.println("=====INDEXEUR DE FICHIER=====");
        this.afficherAide();
        this.boucleCommandes();        
    }

    private void boucleCommandes() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\n> ");
            String ligne = sc.nextLine().trim();
            if (ligne.isEmpty()) continue;
            String[] parts = ligne.split("\\s+", 2);
            String cmd = parts[0].toUpperCase();
            String args;
            if (parts.length > 1) {
                args = parts[1];
            } else {
                args = "";
            }
            switch (cmd) {
                case "INDEX":
                    this.moteur=new MoteurIndexation(index);
                    this.moteur.lancer(args);
                    this.moteur.attendre();
                    System.out.println("Indexation terminée.");
                    break;
                    
                case "RECHERCHER":

                    List<ResultatRecherche> res = index.rechercher(args.split(" "));
                    if (res.isEmpty()){
                        System.out.println("Aucun résultat.");
                    }
                    else{ 
                        for (ResultatRecherche r : res){ 
                            System.out.println(r);
                        }    
                    }
                    break;

                case "META":

                    String[] m = args.split("\\s+", 2);
                    if (m.length < 2) {
                        System.out.println("Usage : META <clé> <valeur>");
                        break;
                    
                    }
                    List<ResultatRecherche> resMeta = index.rechercherParMetaDonnees(m[0], m[1]);
                    if (resMeta.isEmpty()){
                        System.out.println("Aucun résultat.");
                    }
                    else{
                        for (ResultatRecherche r : resMeta){
                            System.out.println(r);
                        }
                    
                    }    
                    break;

                case "ANNOTER":

                    String[] a = args.split("\\s+", 3);

                    if (a.length < 3) {
                        System.out.println("Usage : ANNOTER <chemin> <clé> <valeur>");
                        break; 
                    }
                    FicheDocument fiche = index.getFiche(a[0]);
                    if (fiche == null) {
                        System.out.println("Fichier non trouvé.");
                        break;
                    }
                    fiche.ajouterMeta(a[1], a[2]);
                    System.out.println("Annotation ajoutée.");
                    break;

                case "ADDSTOP":

                    index.ajouterStopWord(args);
                    System.out.println("Stop-word ajouté.");
                    break;

                case "ADDTERME":

                    index.ajouterTermePerso(args);
                    System.out.println("Terme ajouté.");
                    break;
                    
                case "DOUBLONS":

                    List<List<String>> doublons = index.trouverDoublons();

                    if (doublons.isEmpty()) {

                        System.out.println("Aucun doublon.");
                    }
                    else{

                    for (List<String> groupe : doublons) {
                        System.out.println(groupe);
                    }
                    }

                    break;

                case "STATUS":
                    
                    System.out.println("Fichiers : " + index.getNombreFichiers());
                    System.out.println("Termes   : " + index.getNombreTermes());
                    System.out.println("En cours : " + moteur.estEnCours());
                    break;

                case "LIST":

                    Enumeration<FicheDocument> fiches = index.getTousFichiers();
                    if (!fiches.hasMoreElements()) 
                    {
                        System.out.println("Aucun fichier indexé.");
                    }
                    
                    else{ 
                        while (fiches.hasMoreElements()) 
                        {
                            System.out.println(fiches.nextElement());
                        }
                        break;
                    }    

                case "SAVE":

                    index.sauvegarder(FICHIER_INDEX);
                    System.out.println("Sauvegardé.");
                    break;

                case "AIDE":

                    afficherAide();
                    break;

                case "QUIT":

                    index.sauvegarder(FICHIER_INDEX);
                    System.out.println("Au revoir !");
                    return;

                default:
                    System.out.println("Commande inconnue");
            }
        }
    }

    private void afficherAide() {
        
        System.out.println("INDEX <rép>                    | indexer un répertoire");
        System.out.println("RECHERCHER <mots>              | recherche TF-IDF");
        System.out.println("META <clé> <valeur>            | recherche par métadonnée");
        System.out.println("ANNOTER <chemin> <clé> <val>   | annoter un fichier");
        System.out.println("ADDSTOP <mot>                  | ajouter stop-word");
        System.out.println("ADDTERME <mot>                 | ajouter terme perso");
        System.out.println("DOUBLONS                       | détecter les doublons");
        System.out.println("STATUS                         | état de l'indexeur");
        System.out.println("LIST                           | lister les fichiers");
        System.out.println("SAVE                           | sauvegarder");
        System.out.println("QUIT                           | quitter");
    }

    

    public static void main(String[] args) {
        new ProgrammePrincipal().demarrer();
    }
}