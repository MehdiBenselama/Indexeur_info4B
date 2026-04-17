package fileindexer.data;

import java.io.File;
import java.util.LinkedList;

public class FileQueue {

    private LinkedList<File> list;
    private int maxElements;
    private boolean ferme;

    public FileQueue(int max) {
        this.list=new LinkedList<File>();
        this.maxElements=max;
        this.ferme=false;
    }

   
    public synchronized int ajouter(File f) {
        if(this.list.size()<maxElements)
        {
            this.list.addLast(f);
            notifyAll();
            return 0;
        } else {
            return -1;
        }
    }
 
    public synchronized File retirer() {
        
        while(this.list.isEmpty() && this.ferme==false)
        {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        if(this.list.isEmpty())
        {
            return null;
        }
        return this.list.removeFirst();
    }

    public synchronized int nombreElements() {
        return this.list.size();
    }

    
    public synchronized void fermer() {
        ferme=true;
        notifyAll();
    }

    public synchronized boolean estTermine() {
        return ferme && this.list.isEmpty();
    }
}