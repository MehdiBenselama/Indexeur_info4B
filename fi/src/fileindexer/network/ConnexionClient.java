package fileindexer.network;

import fileindexer.data.*;
import fileindexer.engine.MoteurIndexation;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * CONNEXION CLIENT — 1 thread par client connecté — Couche 3 : Réseau
 *
 * NOTION : BufferedReader + PrintWriter sur un Socket
 * MODÈLE : Chat/ServeurMC.java → classe ConnexionClient
 *
 * PRINCIPE :
 * Lit les commandes du client (telnet), appelle les méthodes de l'index,
 * renvoie les résultats. Même pattern que le chat multi-clients du prof.
 *
 * ÉTAPES :
 * 1. Recopier le constructeur de ConnexionClient du prof
 *    (sisr = BufferedReader, sisw = PrintWriter)
 * 2. run() : boucle readLine → traiterCommande → println
 * 3. traiterCommande() : switch sur SEARCH, STATUS, META, etc.
 */
public class ConnexionClient extends Thread {

 private int id;
 private Socket s;
 private BufferedReader sisr;
 private PrintWriter sisw;
 private IndexInverse index;
 private MoteurIndexation moteur;

 /**
  * Constructeur — MÊME CODE QUE ConnexionClient dans ServeurMC.java.
  * ÉTAPES :
  *   this.id = id; this.s = s; this.index = index; this.moteur = moteur;
  *   try {
  *     sisr = new BufferedReader(new InputStreamReader(s.getInputStream()));
  *     sisw = new PrintWriter(new BufferedWriter(
  *             new OutputStreamWriter(s.getOutputStream())), true);
  *   } catch(IOException e) { e.printStackTrace(); }
  */
 public ConnexionClient(int id, Socket s, IndexInverse index, MoteurIndexation moteur) {
  // TODO
 }

 /**
  * BOUCLE de lecture — même pattern que ServeurMC.
  * ÉTAPES :
  *   sisw.println("=== FileIndexer ===");
  *   while ((str = sisr.readLine()) != null) {
  *     if (str.equals("QUIT")) break;
  *     String reponse = traiterCommande(str);
  *     sisw.println(reponse);
  *   }
  *   sisr.close(); sisw.close(); s.close();
  */
 public void run() {
  // TODO
 }

 /**
  * DISPATCHER de commandes.
  * ÉTAPES :
  *   Découper : String[] parts = ligne.split("\\s+", 2);
  *   switch(parts[0].toUpperCase()) :
  *     "SEARCH"     → index.rechercher(args.split(" ")) → formater résultats
  *     "META"       → index.rechercherParMeta(clé, valeur)
  *     "DUPLICATES" → index.trouverDoublons()
  *     "STATUS"     → "Fichiers: X, Termes: Y, Threads: Z"
  *     "ANNOTATE"   → trouver fiche + ajouterMeta
  *     "ADDSTOP"    → index.ajouterStopWord(args)
  *     "LIST"       → parcourir getTousFichiers()
  *     "HELP"       → retourner la liste des commandes
  *     default      → "Commande inconnue"
  */
 private String traiterCommande(String ligne) {
  // TODO
  return "Commande non implémentée";
 }
}
