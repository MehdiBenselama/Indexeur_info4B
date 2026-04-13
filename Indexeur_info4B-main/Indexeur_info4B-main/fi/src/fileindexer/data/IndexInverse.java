package fileindexer.data;
import java.io.*;
import java.util.*;

public class IndexInverse implements Serializable {

    private static final long serialVersionUID = 1L;
    private Hashtable<String, HashSet<String>> index;  // mot -> ensemble de chemins
    private Hashtable<String, FicheDocument> catalogue; //chemin ->ficheDocument
    private Hashtable<String, HashSet<String>> doublons; // checksum -> ensemble de chemins
    private HashSet<String> stopWords;
    private HashSet<String> termesPerso;

    public IndexInverse() {

        this.index = new Hashtable<>();
        this.catalogue = new Hashtable<>();
        this.doublons = new Hashtable<>();
        this.stopWords = new HashSet<>();   
        this.termesPerso = new HashSet<>(); 
        this.initStopWords();

    }

    private void initStopWords() // Fait par L'IA
    {
        // Français
        String[] mots = {
            "le", "la", "les", "de", "du", "des", "un", "une",
            "et", "en", "au", "aux", "ce", "se", "sa", "son",
            "que", "qui", "quoi", "dont", "ou", "si", "car",
            "mais", "donc", "or", "ni", "par", "sur", "sous",
            "avec", "sans", "pour", "dans", "est", "sont",
            // Anglais
            "the", "is", "are", "and", "or", "not", "in",
            "on", "at", "to", "of", "a", "an", "it", "its"
        };

        for (String mot : mots) {
            stopWords.add(mot);
            
        }
    }

    synchronized public void ajouterDocument(FicheDocument fiche) {

        catalogue.put(fiche.getChemin(), fiche);

        for (String mot : fiche.getmotFrequence().keySet()) {
            
            if (!estStopWord(mot)) {

                HashSet<String> fichiers = index.get(mot);
                if (fichiers == null) {
                    fichiers = new HashSet<>();
                    index.put(mot, fichiers);
                }
                fichiers.add(fiche.getChemin());
            }
        }

        String checksum = fiche.getChecksum();
        HashSet<String> Doublons = this.doublons.get(checksum);

        if (Doublons == null) {
            Doublons = new HashSet<>();
            this.doublons.put(checksum, Doublons);
        }

        Doublons.add(fiche.getChemin());
    }

    synchronized public void supprimerDocument(String chemin) {

        FicheDocument fiche = catalogue.get(chemin);

        if (fiche == null) {
            return;
        }     

        catalogue.remove(chemin);

        for (String mot : fiche.getmotFrequence().keySet()) {

            HashSet<String> chemins = index.get(mot);
            if (chemins != null) {
                chemins.remove(chemin);

                if (chemins.isEmpty()) {
                    index.remove(mot);
                }
            }
        }

        // 4) Retirer des doublons
        String checksum = fiche.getChecksum();
        HashSet<String> copies = doublons.get(checksum);
        if (copies != null) {
            copies.remove(chemin);
            // Si plus aucun fichier pour ce checksum → supprimer le groupe
            if (copies.isEmpty()) {
                doublons.remove(checksum);
            }
        }
    }

    /**
     * RECHERCHE TF-IDF — synchronized
     * TF = freq(mot,doc) / totalMots(doc)
     * IDF = log(nbTotalDocs / nbDocsContenant(mot))
     * Score = somme(TF*IDF) pour chaque mot-clé
     */

    private double calculerTF(String mot, FicheDocument fiche) {
        Integer freq = fiche.getmotFrequence().get(mot);
        if (freq == null) return 0;
        int total = fiche.getNombreTotal();
        if (total == 0) return 0;
        return (double) freq / total;
    }

    private double calculerIDF(String mot) {
        HashSet<String> fichiers = this.index.get(mot);
        if (fichiers == null || fichiers.isEmpty()) return 0;
        return Math.log((double) this.catalogue.size() / fichiers.size());
    }


    synchronized public List<ResultatRecherche> rechercher(String[] motsCles) //Fait par l'IA 
    {
        Hashtable<String, Double> scores = new Hashtable<>();

        for (String mot : motsCles) {
            if (stopWords.contains(mot)) continue;

            HashSet<String> fichiers = index.get(mot);
            if (fichiers == null) continue;

            double idf = calculerIDF(mot);

            for (String chemin : fichiers) {
                FicheDocument fiche = catalogue.get(chemin);
                if (fiche == null) continue;

                double score = calculerTF(mot, fiche) * idf;

                Double ancien = scores.get(chemin);
                scores.put(chemin, ancien == null ? score : ancien + score);
            }
        }

        List<ResultatRecherche> resultats = new ArrayList<>();
        for (String chemin : scores.keySet()) {
            resultats.add(new ResultatRecherche(catalogue.get(chemin), scores.get(chemin)));
        }
        Collections.sort(resultats);
        return resultats;
    }

    /** RECHERCHE PAR MÉTADONNÉES — parcourir catalogue, vérifier metaDonnees et exifDonnees */
    synchronized public List<ResultatRecherche> rechercherParMetaDonnees(String cle, String valeur) {
        
        List<ResultatRecherche> resultats = new ArrayList<>();
        
        for (FicheDocument fiche : this.catalogue.values()) {
            
            // Vérifier dans les métadonnées
            String valeurMeta = fiche.getMetaDonnees().get(cle);
            if (valeurMeta != null && valeurMeta.equals(valeur)) {
                
                resultats.add(new ResultatRecherche(fiche, 1.0));
                continue;
            }
            
            // Vérifier dans les données EXIF
            String valeurExif = fiche.getExifDonnees().get(cle);
            if (valeurExif != null && valeurExif.equals(valeur)) {
                resultats.add(new ResultatRecherche(fiche, 1.0));
            }
        }
        
        return resultats;
    }

    /** DOUBLONS — parcourir doublons, garder les groupes avec >1 fichier */
    synchronized public List<List<String>> trouverDoublons() {
        
        List<List<String>> r = new ArrayList<>();
        
        // Parcourir tous les groupes de doublons
        for (HashSet<String> grp : this.doublons.values()) {
            
            // Garder seulement les groupes avec plus d'un fichier
            if (grp.size() > 1) {
                ArrayList<String> listeGroupe = new ArrayList<>(grp);      // Convertir le HashSet en ArrayList
                r.add(listeGroupe);            
            }
        }
        
        return r;
    }


    synchronized public boolean estStopWord(String mot) { 

        return this.stopWords.contains(mot);  
    
    }

    synchronized public void ajouterStopWord(String mot) { 

        this.stopWords.add(mot);
    }
     
    synchronized public void ajouterTermePerso(String t) { 

        this.termesPerso.add(t);
     
    }

    synchronized public int getNombreFichiers() { 
    
        return this.catalogue.size(); 
        
    }
    
    synchronized public int getNombreTermes() { 
        
        return this.index.size();
    
    }
    
    synchronized public Enumeration<FicheDocument> getTousFichiers()  //fait par IA
    {
        return catalogue.elements();
    }
    
    synchronized public void sauvegarder(String cheminSauvgarde) {
        try {
            FileOutputStream out = new FileOutputStream(cheminSauvgarde);
            ObjectOutputStream outstream = new ObjectOutputStream(out);
            outstream.writeObject(this);
            outstream.close();
            out.close();
            System.out.println("Index sauvegardé : " + cheminSauvgarde);
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde : " + e.getMessage());
        }
    }
    
    public static IndexInverse charger(String cheminFichier) {
        try {
            File f = new File(cheminFichier);
            if (!f.exists()) return new IndexInverse();

            FileInputStream in = new FileInputStream(cheminFichier);
            ObjectInputStream instream = new ObjectInputStream(in);
            IndexInverse index = (IndexInverse) instream.readObject();
            instream.close();
            in.close();
            return index;

        } catch (Exception e) {
            System.out.println("Erreur chargement : " + e.getMessage());
            return new IndexInverse();
        }
    }
    
}