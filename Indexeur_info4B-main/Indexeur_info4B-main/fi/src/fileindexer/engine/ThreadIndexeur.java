
package fileindexer.engine;

import fileindexer.data.*;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;

/**
 * THREAD INDEXEUR — Thread CONSOMMATEUR — Couche 2 : Moteur
 *
 * NOTIONS :
 *   1. Thread + volatile    → boucle avec flag d'arrêt
 *   2. wait/notify          → via FileQueue.retirer() qui bloque si vide
 *   3. BufferedReader       → lire un fichier texte ligne par ligne
 *   4. StringTokenizer      → découper une ligne en mots
 *   5. ProcessBuilder       → pipe avec pdftotext pour les PDF
 * MODÈLES :
 *   - SleepingBarber01 → Barber (consommateur, dort si pas de client)
 *   - CompterMots.java → BufferedReader + StringTokenizer + Hashtable
 *   - tube.c           → concept de pipe (ici en Java avec ProcessBuilder)
 *
 * PRINCIPE :
 * Boucle infinie : retirer un fichier de la queue (dort si vide) → l'indexer
 * Exactement comme le Barber qui dort quand il n'y a pas de client.
 *
 * ÉTAPES :
 * 1. Coder run() : boucle while(!arret) + queue.retirer()
 * 2. Coder traiterFichier() : créer FicheDocument + checksum
 * 3. Coder indexerTexte() : RECOPIER CompterMots.java
 * 4. Coder indexerPdf() : ProcessBuilder + pipe
 * 5. Coder calculerChecksum() : MessageDigest MD5
 */
public class ThreadIndexeur extends Thread {

    private FileQueue queue;
    private IndexInverse index;
    private int nbTraites;
    private volatile boolean arret; // volatile comme dans SleepingBarber01

    public ThreadIndexeur(FileQueue queue, IndexInverse index, String nom) {
        super(nom);
        this.queue = queue; 
        this.index = index; 
        nbTraites = 0; 
        arret = false;
    }

    public int getNbTraites() { return nbTraites; }
    public void arreter()     { arret = true; }

    public void run() {
        while(arret==false)
        {
            File f = queue.retirer();
            if(f==null){break;}
            traiterFichier(f);
            nbTraites++;
        }
    }

    private void traiterFichier(File f) {
        try {
            String path = f.getAbsolutePath();
            String name = f.getName();
            long size = f.length();
            long date = f.lastModified();
            String ext = Explorateur.getExtension(f.getName());
            String cs = calculerChecksum(f);
                
            FicheDocument fd = new FicheDocument(path, name, size, date, ext, cs);

            if(Explorateur.estTexte(ext)){indexerTexte(f, fd);}
            else if(Explorateur.estPdf(ext)){indexerPdf(f, fd);}
            else if(Explorateur.estImage(ext)){indexerImage(f, fd);}

            index.ajouterDocument(fd);
        } catch (Exception e) {}
    } 
    
            
    private void indexerTexte(File f, FicheDocument fiche) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String ligne = reader.readLine();
        while(ligne!=null)
        {
            ligne = ligne.toLowerCase();
            StringTokenizer st = new StringTokenizer(ligne, " ,;.:+-=* /{}?!()\t\n\r");
            while(st.hasMoreTokens())
            {
                String mot = st.nextToken();
                if(mot.length()<2) continue;
                if(index.estStopWord(mot)) continue;
                fiche.ajouterMot(mot);
            }
            ligne = reader.readLine();
        }
        reader.close();
    }

    private void indexerPdf(File f, FicheDocument fiche) {
        try {
            ProcessBuilder pb = new ProcessBuilder("pdftotext", f.getAbsolutePath(), "-");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ligne = reader.readLine();
            while(ligne!=null)
            {
                ligne = ligne.toLowerCase();
                StringTokenizer st = new StringTokenizer(ligne, " ,;.:+-=* /{}?!()\t\n\r");
                while(st.hasMoreTokens())
                {
                    String mot = st.nextToken();
                    if(mot.length()<2) continue;
                    if(index.estStopWord(mot)) continue;
                    fiche.ajouterMot(mot);
                }
                ligne = reader.readLine();
            }
            reader.close();
            p.waitFor();
            p.destroyForcibly();
        } catch (Exception e) {}
    }

    private void indexerImage(File f, FicheDocument fiche) {
        fiche.ajouterExif("type", "image");
        fiche.ajouterExif("filename", f.getName());
    }

    private String calculerChecksum(File f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] data = Files.readAllBytes(f.toPath());
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {return null;}
    }
}
