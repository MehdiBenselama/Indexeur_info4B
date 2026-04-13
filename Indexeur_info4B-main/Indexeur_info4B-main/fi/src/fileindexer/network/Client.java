package fileindexer.network;

import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import java.io.*;
import java.net.*;
import java.util.List;

/**
 * CONNEXION CLIENT — 1 thread par client connecté — Couche 3 : Réseau
 *
 * NOTION : BufferedReader + PrintWriter sur un Socket
 * MODÈLE : Chat/ServeurMC.java → classe client
 *
 * PRINCIPE :
 * Lit les commandes du client (telnet), appelle les méthodes de l'index,
 * renvoie les résultats. Même pattern que le chat multi-clients du prof.
 *
 * ÉTAPES :
 * 1. Recopier le constructeur de client du prof
 *    (sisr = BufferedReader, sisw = PrintWriter)
 * 2. run() : boucle readLine → traiterCommande → println
 * 3. traiterCommande() : switch sur SEARCH, STATUS, META, etc.
 */
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

    /**
     * BOUCLE de lecture — même pattern que ServeurMC.
     * ÉTAPES :
     *   sisw.println("=== FileIndexer ===");
     *   while ((str = sisr.readLine()) != null) {
     *     if (str.equals("QUIT")) break;
     *     String reponse = traiterCommande(str);
     *     sisw.println(reponse);
     *   }
     *   sisr.close(); sisw.close(); s.close();
     */
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
            String[] parts = ligne.split("\\s+",2);
            String cmd = parts[0].toUpperCase();
            String args = (parts.length>1) ? parts[1] : "";
            switch(cmd){
                case "SEARCH":
                    List<ResultatRecherche> res = index.rechercher(args.split("\\s+"));
                    if(res.isEmpty()) return "Aucun résultat";
                    StringBuilder sb = new StringBuilder();
                    for(ResultatRecherche r : res)
                    {
                        sb.append(r.toString());
                        sb.append("\n");
                    }
                    return sb.toString();
                
                case "STATUS":
                    return "Fichiers : "+ index.getNombreFichiers() +", Terme : " + index.getNombreTermes();     
                case "DUPLICATES":
                    List<List<String>> doublons = index.trouverDoublons() ;
                    if(doublons.isEmpty()) return "Aucun doublon";
                    StringBuilder sb2 = new StringBuilder();
                    for(List<String> groupe : doublons)
                    {
                        sb2.append("Groupe : ").append(groupe.toString()).append("\n");
                    }
                    return sb2.toString();
                case "ADDSTOP":
                    index.ajouterStopWord(args);
                    return "Stop word ajouté : "+args;
                case "HELP":
                    return "SEARCH <mots> | STATUS | DUPLICATES | ADDSTOP <mot> | QUIT";
                default:
                    return "Commande inconnue : "+cmd;
    
            }
    }
}