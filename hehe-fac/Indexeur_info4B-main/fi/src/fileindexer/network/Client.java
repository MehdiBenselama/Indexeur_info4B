package fileindexer.network;

import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.List;


public class Client extends Thread {

    private int id;
    private Socket s;
    private BufferedReader sisr;
    private PrintWriter sisw;
    private IndexInverse index;
    private MoteurIndexation moteur;

    /**
     * Constructeur — MÊME CODE QUE client dans ServeurMC.java.
    */

    public Client(int id, Socket s, IndexInverse index, MoteurIndexation moteur) {

        this.id=id;
        this.s=s;
        this.index=index;
        this.moteur=moteur;

        try {

            this.sisr=new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.sisw = new PrintWriter(new BufferedWriter( new OutputStreamWriter(s.getOutputStream())), true);

        } catch (IOException ex) {
            System.getLogger(Client.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }


    public void run() {
        try {
            sisw.println("===FileIndexer===");
            String str = sisr.readLine();
            while(str!=null)
            {
                if(str.equals("QUIT")){break;}
                String reponse = traiterCommande(str);
                sisw.println(reponse);
                str = sisr.readLine();
            }
            sisr.close();
            sisw.close();
            s.close();
        } catch (IOException e) {}
    }






    

    /**
     * DISPATCHER de commandes.
     * ÉTAPES :
     *   Découper : String[] parts = ligne.split("\\s+", 2);
     *   switch(parts[0].toUpperCase()) :
     *     "SEARCH"     → index.rechercher(args.split(" ")) → formater résultats
     *     "META"       → index.rechercherParMeta(clé, valeur)
     *     "DUPLICATES" → index.trouverDoublons()
     *     "STATUS"     → "Fichiers: X, Termes: Y, Threads: Z"
     *     "ANNOTATE"   → trouver fiche + ajouterMeta
     *     "ADDSTOP"    → index.ajouterStopWord(args)
     *     "LIST"       → parcourir getTousFichiers()
     *     "HELP"       → retourner la liste des commandes
     *     default      → "Commande inconnue"
     */
    private String traiterCommande(String ligne) {
    String[] parts = ligne.split("\\s+", 2);
    String cmd = parts[0].toUpperCase();
    String args = (parts.length > 1) ? parts[1] : "";

    switch (cmd) {
        case "SEARCH":
            List<ResultatRecherche> res = index.rechercher(args.split("\\s+"));
            if (res.isEmpty()) return "Aucun résultat";
            StringBuilder sb = new StringBuilder();
            for (ResultatRecherche r : res) {
                sb.append(r.toString()).append("\n");
            }
            return sb.toString();

        case "STATUS":
            return "Fichiers : " + index.getNombreFichiers() + ", Termes : " + index.getNombreTermes();
 
        case "DUPLICATES":
            List<List<String>> doublons = index.trouverDoublons();
            if (doublons.isEmpty()) return "Aucun doublon";
            StringBuilder sb2 = new StringBuilder();
            for (List<String> groupe : doublons)
                sb2.append("Groupe : ").append(groupe.toString()).append("\n");
            return sb2.toString();

        case "ADDSTOP":
            index.ajouterStopWord(args);
            return "Stop-word ajouté : " + args;

        case "META":
            String[] metaParts = args.split("\\s+", 2);
            if (metaParts.length < 2) return "Usage : META <clé> <valeur>";
            List<ResultatRecherche> resMeta = index.rechercherParMetaDonnees(metaParts[0], metaParts[1]);
            if (resMeta.isEmpty()) return "Aucun résultat";
            StringBuilder sb3 = new StringBuilder();
            for (ResultatRecherche r : resMeta)
                sb3.append(r.toString()).append("\n");
            return sb3.toString();

        case "ANNOTATE":
            String[] annParts = args.split("\\s+", 3);
            if (annParts.length < 3) return "Usage : ANNOTATE <chemin> <clé> <valeur>";
            FicheDocument fiche = index.getFiche(annParts[0]);
            if (fiche == null) return "Fichier non trouvé";
            fiche.ajouterMeta(annParts[1], annParts[2]);
            return "Annotation ajoutée.";

        case "LIST":
            Enumeration<FicheDocument> fiches = index.getTousFichiers();
            if (!fiches.hasMoreElements()) return "Aucun fichier indexé";
            StringBuilder sb4 = new StringBuilder();
            while (fiches.hasMoreElements())
                sb4.append(fiches.nextElement().toString()).append("\n");
            return sb4.toString();

        case "ADDTERME":
            index.ajouterTermePerso(args);
            return "Terme ajouté : " + args;

        case "HELP":
            return "SEARCH <mots> | META <clé> <val> | ANNOTATE <chemin> <clé> <val> | ADDSTOP <mot> | ADDTERME <mot> | LIST | STATUS | DUPLICATES | QUIT";

        default:
            return "Commande inconnue : " + cmd;
    }
}
}