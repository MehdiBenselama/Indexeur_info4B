package fileindexer.data;

import java.io.File;
import java.util.LinkedList;


public class FileQueue {

    private LinkedList<File> list;
    private int max;
    private boolean ferme;

    public FileQueue(int max) {
        this.list=new LinkedList<>();
        this.max=max;
        this.ferme=false;
    }

    public synchronized int ajouter(File f) {
        // TODO
        if(this.list.size()<this.max){
            this.list.addLast(f);
            notifyAll();
            return 0;
        }
        return -1;
    }


    public synchronized File retirer() {

        while (this.list.size()==0 && ! this.ferme) { 
            
            try {
                wait();
            } catch (InterruptedException ex) {
                System.getLogger(FileQueue.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

        }

        if(this.list.size()==0){
            return null;
        }

        return this.list.removeFirst();
    }


    public synchronized int getNombreElements() {
        return this.list.size();
    }


    public synchronized  void fermer() {
        // TODO
        this.ferme=true;
        notifyAll();
    }

    public boolean estTermine() {

        return this.ferme && this.list.size() == 0;

    }
}
