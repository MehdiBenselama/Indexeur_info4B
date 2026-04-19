package indexeur.moteur;

import indexeur.data.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ThreadIndexeur extends Thread {

    private FileQueue queue;
    private IndexInverse index;
    private int nbTraites;
    private boolean arret; 

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
            File f = this.queue.retirer();
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

            this.index.ajouterDocument(fd);
        } catch (Exception e) {}
    } 
    
            
    private void indexerTexte(File f, FicheDocument fiche) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String ligne = reader.readLine();
        while (ligne != null) {
            ligne = ligne.toLowerCase();
            StringTokenizer st = new StringTokenizer(ligne, " ,;.:+-=* /{}?!()\t\n\r");
            while (st.hasMoreTokens()) {
                String mot = st.nextToken();
                if (mot.length() <= 2 && !index.getTermesPerso().contains(mot)) continue;
                if (index.estStopWord(mot)) continue;
                fiche.ajouterMot(mot);
            }
            ligne = reader.readLine();
        }
        reader.close();
    }

    private void indexerPdf(File f, FicheDocument fiche) 
    {
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
            p.waitFor();          // généré par IA
            p.destroyForcibly();  // généré par IA  
        } catch (Exception e) {}
    }

    private void indexerImage(File f, FicheDocument fiche) {
        fiche.ajouterExif("type", "image");
        fiche.ajouterExif("filename", f.getName());
    }

   private String calculerChecksum(File f) {
    try {
        byte[] data = Files.readAllBytes(f.toPath());
        long sum = 0;
        for (byte b : data) {
            sum += b;
        }
        return Long.toHexString(sum);
    } catch (Exception e) {
        return null;
    }
}
}
