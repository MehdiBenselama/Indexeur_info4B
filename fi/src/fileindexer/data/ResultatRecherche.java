package fileindexer.data;
import java.io.Serializable;


public class ResultatRecherche implements Comparable<ResultatRecherche>, Serializable {
    private static final long serialVersionUID = 1L;
    private FicheDocument fiche;
    private double score;

    public ResultatRecherche(FicheDocument fiche, double score) {
        this.fiche=fiche;
        this.score=score;
    }
    public FicheDocument getFiche() { 
        
        return this.fiche; 
    
    }
    public double getScore() { 
        return this.score;
    }


    public int compareTo(ResultatRecherche autre) {

        return Double.compare(autre.score, this.score) ;//Tri décroissant
    
    }

    public String toString() {

      return "[" + this.score + "] " + this.fiche.getChemin();
      
    }

}
