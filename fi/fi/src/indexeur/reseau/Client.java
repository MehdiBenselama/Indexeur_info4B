package indexeur.reseau;

import indexeur.data.*;
import indexeur.moteur.MoteurIndexation;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;


public class Client extends Thread {

    private int id;
    private Socket s;
    private BufferedReader reader;
    private PrintWriter writer;
    private IndexInverse index;
    private MoteurIndexation moteur;

    public Client(int id, Socket s, IndexInverse index, MoteurIndexation moteur) {

        this.id=id;
        this.s=s;
        this.index=index;
        this.moteur=moteur;

        try {

            this.reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.writer = new PrintWriter(new BufferedWriter( new OutputStreamWriter(s.getOutputStream())), true);

        } catch (IOException ex) {
            System.getLogger(Client.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }


    public void run() {
        try {
            writer.println("===== INDEXEUR DE FICHIERS =====");

            String str = reader.readLine();
            while(str!=null)
            {
                if(str.equals("QUIT")){break;}
                String rep = traiterCommande(str);
                writer.println(rep);
                str = reader.readLine();
            }
            reader.close();
            writer.close();
            s.close();
        } catch (IOException e) {}
    }


    private String traiterCommande(String ligne) {
        String[] parts = ligne.split("\\s+", 2);
        String cmd = parts[0].toUpperCase();
        String args;
        if (parts.length > 1) {
            args = parts[1];
        } else {
            args = "";
        }

        switch (cmd) {
            case "RECHERCHER":
                List<ResultatRecherche> res = index.rechercher(args.split("\\s+"));
                if (res.isEmpty()) return "Aucun résultat\n";
                String rep1 = "";
                for (ResultatRecherche r : res)
                    rep1 += r.toString() + "\n";
                return rep1;

            case "STATUS":
                return "Fichiers : " + index.getNombreFichiers() + ", Termes : " + index.getNombreTermes() + "\n";

            case "DOUBLONS":

                List<List<String>> doublons = index.trouverDoublons();
                if (doublons.isEmpty()) return "Aucun doublon\n";
                String rep2 = "";
                for (List<String> groupe : doublons)
                    rep2 += "Groupe : " + groupe.toString() + "\n";
                return rep2;

            case "ADDSTOP":
                index.ajouterStopWord(args);
                return "Stop-word ajouté : " + args + "\n";

            case "META":

                String[] metaParts = args.split("\\s+", 2);
                if (metaParts.length < 2) return "Usage : META <clé> <valeur>\n";
                List<ResultatRecherche> resMeta = index.rechercherParMetaDonnees(metaParts[0], metaParts[1]);
                if (resMeta.isEmpty()) return "Aucun résultat\n";
                String rep3 = "";
                for (ResultatRecherche r : resMeta)
                    rep3 += r.toString() + "\n";
                return rep3;

            case "ANNOTER":

                String[] annParts = args.split("\\s+", 3);
                if (annParts.length < 3) return "Usage : ANNOTER <chemin> <clé> <valeur>\n";
                FicheDocument fiche = index.getFiche(annParts[0]);
                if (fiche == null) return "Fichier non trouvé\n";
                fiche.ajouterMeta(annParts[1], annParts[2]);
                return "Annotation ajoutée.\n";

            case "LIST":
                Enumeration<FicheDocument> fiches = index.getTousFichiers();
                if (!fiches.hasMoreElements()) return "Aucun fichier indexé\n";
                String rep4 = "";
                while (fiches.hasMoreElements())
                    rep4 += fiches.nextElement().toString() + "\n";
                return rep4;

            case "ADDTERME":
                index.ajouterTermePerso(args);
                return "Terme ajouté : " + args + "\n";

            case "RECUPERER": // fait par IA
                try {
                    File f = new File(args);
                    if (!f.exists()) return "Fichier non trouvé\n";
                    byte[] data = Files.readAllBytes(f.toPath());
                    return Base64.getEncoder().encodeToString(data) + "\n";
                } catch (Exception e) {
                    return "Erreur : \n";
                }

            case "AIDE":
                return "RECHERCHER <mots> | META <clé> <val> | ANNOTER <chemin> <clé> <val> | ADDSTOP <mot> | ADDTERME <mot> | LIST | STATUS | DOUBLONS | RECUPERER <chemin> | QUIT\n";

            default:
                return "Commande inconnue : " + cmd + "\n";
        }
}

}