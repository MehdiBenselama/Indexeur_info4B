package fileindexer.engine;

import fileindexer.data.*;
import java.io.File;
import java.util.*;

/**
 * EXPLORATEUR — Thread PRODUCTEUR — Couche 2 : Moteur
 *
 * NOTION : Thread (extends Thread, run(), start())
 * MODÈLE : SleepingBarber01.java → SimulArriveeClient
 *          ThreadCreation/TestThread1.java → création basique
 *
 * PRINCIPE :
 * Ce thread parcourt récursivement les dossiers.
 * Pour chaque fichier trouvé → queue.ajouter(fichier)
 * Quand c'est fini → queue.fermer()
 *
 * ÉTAPES :
 * 1. Comprendre TestThread1.java (extends Thread, run, start)
 * 2. Coder explorer() récursif avec File.listFiles()
 * 3. Filtrer par extension (.txt, .java, .pdf, .jpg, etc.)
 * 4. Appeler queue.ajouter(f) pour chaque fichier valide
 * 5. Appeler queue.fermer() à la fin de run()
 */
public class Explorateur extends Thread {

    private File repertoire;
    private FileQueue queue;
    private int nbFichiersTrouves;

    private static final HashSet<String> EXT_TEXTE = new HashSet<>(Arrays.asList(
    ".txt",".java",".py",".c",".h",".cpp",".xml",".json",
    ".html",".css",".js",".md",".csv",".log",".properties"));
    private static final HashSet<String> EXT_PDF = new HashSet<>(Arrays.asList(".pdf"));
    private static final HashSet<String> EXT_IMAGE = new HashSet<>(Arrays.asList(
    ".jpg",".jpeg",".png",".gif",".bmp",".tiff"));

    public Explorateur(File repertoire, FileQueue queue) {
        this.repertoire=repertoire;
        this.queue=queue;
        this.nbFichiersTrouves=0;
    }

    public int getNbFichiersTrouves() { return nbFichiersTrouves; }

    /**
     * Méthode exécutée par le thread (appelée par start()).
     * ÉTAPES : afficher début → explorer(repertoire) → queue.fermer() → afficher fin
     */
    public void run() {
        System.out.println("Exploration démarée.");
        this.explorer(repertoire);
        queue.fermer();
        System.out.println("Exploration terminée, "+ this.nbFichiersTrouves + "fichiers trouvés");
    }

    /**
     * Exploration RÉCURSIVE.
     * ÉTAPES :
     *   1. Vérifier dir != null && dir.isDirectory()
     *   2. File[] contenu = dir.listFiles()
     *   3. Pour chaque élément :
     *      → si dossier : explorer(element)  (récursion)
     *      → si fichier avec bonne extension : queue.ajouter(element), nbFichiersTrouves++
     */
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

    /** Retourne l'extension d'un fichier (ex: ".java"). */
    public static String getExtension(String nom) {
        int p = nom.lastIndexOf('.');
        return p >= 0 ? nom.substring(p).toLowerCase() : "";
    }

    public static boolean estTexte(String ext) { return EXT_TEXTE.contains(ext); }
    public static boolean estPdf(String ext)   { return EXT_PDF.contains(ext); }
    public static boolean estImage(String ext) { return EXT_IMAGE.contains(ext); }
}