package fileindexer.engine;

import fileindexer.data.*;
import java.io.File;
import java.util.*;


public class Explorateur extends Thread {

    private File repertoire;
    private FileQueue queue;
    private int nbFichiersTrouves;

    private static final HashSet<String> EXT_TEXTE = new HashSet<>(Arrays.asList(".txt",".java",".py",".c",".h",".cpp",".html",".css",".js"));
    private static final HashSet<String> EXT_PDF = new HashSet<>(Arrays.asList(".pdf"));
    private static final HashSet<String> EXT_IMAGE = new HashSet<>(Arrays.asList(".jpg",".jpeg",".png"));

    public Explorateur(File repertoire, FileQueue queue) {
        this.repertoire=repertoire;
        this.queue=queue;
        this.nbFichiersTrouves=0;
    }

    public int getNbFichiersTrouves() {
         return nbFichiersTrouves;
          }


    public void run() {
        System.out.println("Exploration démarée.");
        this.explorer(repertoire);
        queue.fermer();
        System.out.println("Exploration terminée, "+ this.nbFichiersTrouves + "fichiers trouvés");
    }

    private void explorer(File dir) {
        if(dir==null || !dir.isDirectory())
        {
            return;
        }

        File[] elements = dir.listFiles();
        if(elements==null){return;}
        
        for(File element : elements)
        {
            if(element.isDirectory())
            {
                this.explorer(element);
            } else if(element.isFile()){
                String ext = getExtension(element.getName());
                if(estTexte(ext) || estPdf(ext) || estImage(ext))
                {
                    queue.ajouter(element);
                    nbFichiersTrouves++;
                }
            }

        }

    }

    public static String getExtension(String nom) {
        int p = nom.lastIndexOf('.');
        return p >= 0 ? nom.substring(p).toLowerCase() : "";
    }

    public static boolean estTexte(String ext) { return EXT_TEXTE.contains(ext); }
    public static boolean estPdf(String ext)   { return EXT_PDF.contains(ext); }
    public static boolean estImage(String ext) { return EXT_IMAGE.contains(ext); }
}