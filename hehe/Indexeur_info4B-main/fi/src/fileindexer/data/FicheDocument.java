package fileindexer.data;

import java.io.Serializable;
import java.util.Hashtable;

public class FicheDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    private String chemin;
    private String nom;
    private long taille;
    private long dateModif;
    private String extension;
    private String checksum;

        
    private Hashtable<String, Integer> motFrequence;    // mot -> nb occurrences dans le document
    private Hashtable<String, String> metaDonnees;      // métadonnée -> valeur ( "auteur" -> "mehdi")
    private Hashtable<String, String> exifDonnees;      // donnée EXIF -> valeur ("GPS" -> "48.85 N")

    public FicheDocument(String chemin, String nom, long taille,
                         long dateModif, String extension, String checksum) 
    {

        this.chemin=chemin;
        this.nom=nom;
        this.taille=taille;
        this.dateModif=dateModif;
        this.extension=extension;
        this.checksum=checksum;
        this.motFrequence=new Hashtable<>();
        this.metaDonnees=new Hashtable<>();
        this.exifDonnees=new Hashtable<>();
    }


    public void ajouterMot(String mot) {
    
        Integer cmpt=this.motFrequence.get(mot);
        if(cmpt==null){
            this.motFrequence.put(mot, 1);
        }
        else{
            this.motFrequence.put(mot, cmpt+1);
        }
    }

    public int getNombreTotal() {
        int total = 0;
        for (Integer cmpt : this.motFrequence.values()) {
            total += cmpt;
        }
        return total;
    }

 
    public void ajouterMeta(String cle, String val) {

        this.metaDonnees.put(cle, val);
        
    }


    public void ajouterExif(String cle, String val) {
        this.exifDonnees.put(cle, val);

    }


    public String getChemin()    { return this.chemin; }
    public String getNom()       { return this.nom; }
    public long getTaille()      { return this.taille; }
    public long getDateModif()   { return this.dateModif; }
    public String getExtension() { return this.extension; }
    public String getChecksum()  { return this.checksum; }

    public Hashtable<String, Integer> getmotFrequence() { return this.motFrequence; }
    public Hashtable<String, String> getMetaDonnees()      { return this.metaDonnees; }
    public Hashtable<String, String> getExifDonnees()      { return this.exifDonnees; }


    @Override

    public String toString() {

        double tailleKo = taille / 1024.0;
        return "[." + this.extension + "] " + this.nom + " (" + tailleKo + " Ko, " + this.getNombreTotal() + " termes)";
    }


}