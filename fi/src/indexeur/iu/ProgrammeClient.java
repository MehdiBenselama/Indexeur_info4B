package indexeur.iu;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Scanner;

public class ProgrammeClient {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Scanner sc;

    public void demarrer() throws IOException {
        sc = new Scanner(System.in);
        System.out.print("IP du serveur : ");
        String ip = sc.nextLine();
        socket = new Socket(ip, 9876);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        System.out.println(in.readLine());
        afficherAide();
        boucleCommandes();
    }

    private void boucleCommandes() throws IOException {
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

            case "RECHERCHER":

                if (args.isEmpty()) { System.out.println("Usage : RECHERCHER <mots>"); break; }
                out.println("RECHERCHER " + args);
                lireReponse();
                break;

            case "META":

                if (args.isEmpty()) { System.out.println("Usage : META <clé> <valeur>"); break; }
                out.println("META " + args);
                lireReponse();
                break;

            case "ANNOTER":

                if (args.isEmpty()) { System.out.println("Usage : ANNOTER <chemin> <clé> <valeur>"); break; }
                out.println("ANNOTER " + args);
                lireReponse();
                break;

            case "ADDSTOP":

                if (args.isEmpty()) { System.out.println("Usage : ADDSTOP <mot>"); break; }
                out.println("ADDSTOP " + args);
                lireReponse();
                break;

            case "ADDTERME":

                if (args.isEmpty()) { System.out.println("Usage : ADDTERME <mot>"); break; }
                out.println("ADDTERME " + args);
                lireReponse();
                break;

            case "LIST":

                out.println("LIST");
                lireReponse();
                break;

            case "STATUS":

                out.println("STATUS");
                lireReponse();
                break;

            case "DOUBLONS":

                out.println("DOUBLONS");
                lireReponse();
                break;

            case "RECUPERER":

                String[] r = args.split("\\s+", 2);
                if (r.length < 2) { System.out.println("Usage : RECUPERER <chemin> <destination>"); break; }
                out.println("RECUPERER " + r[0]);
                String base64 = in.readLine();
                if (base64 == null || base64.startsWith("Fichier") || base64.startsWith("Erreur")) {
                    System.out.println(base64);
                    break;
                }
                byte[] data = Base64.getDecoder().decode(base64);
                FileOutputStream fos = new FileOutputStream(r[1]);
                fos.write(data);
                fos.close();
                System.out.println("Fichier récupéré : " + r[1]);
                break;

            case "AIDE":

                afficherAide();
                break;

            case "QUIT":
                
                out.println("QUIT");
                socket.close();
                System.out.println("Au revoir !");
                return;

            default:
                System.out.println("Commande inconnue");
        }
    }
    
    }

    private void lireReponse() throws IOException {
        String ligne = in.readLine();
        while (ligne != null && !ligne.isEmpty()) {
            System.out.println(ligne);
            ligne = in.readLine();
        }
    }

    private void afficherAide() {

        System.out.println("RECHERCHER <mots>               | recherche TF-IDF");
        System.out.println("META <clé> <valeur>             | recherche par métadonnée");
        System.out.println("ANNOTER <chemin> <clé> <val>    | annoter un fichier");
        System.out.println("ADDSTOP <mot>                   | ajouter stop-word");
        System.out.println("ADDTERME <mot>                  | ajouter terme perso");
        System.out.println("LIST                            | lister les fichiers");
        System.out.println("STATUS                          | état de l'indexeur");
        System.out.println("DOUBLONS                        | détecter les doublons");
        System.out.println("RECUPERER <chemin> <dest>       | récupérer un fichier");
        System.out.println("QUIT                            | quitter");
    }

    public static void main(String[] args) throws IOException {
        new ProgrammeClient().demarrer();
    }
}