package modele;

import java.sql.Time;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;

   // Simplifiée et corrigée
public class Livraison {
    private int id;
    private int clientId;
    private int livreurId;
    private String adresseLivraison;
    private Date dateLivraison;
    private Time heureLivraison;
    private String priorite;
    private String articles;
    private String statut;
    private Date dateCreation;
    private String nomClient;
    private String nomLivreur;

    // Constructeurs
    public Livraison() {}

    public Livraison(int clientId, int livreurId, String adresseLivraison, Date dateLivraison, Time heureLivraison, String priorite, String articles, String statut) {
        this.clientId = clientId;
        this.livreurId = livreurId;
        this.adresseLivraison = adresseLivraison;
        this.dateLivraison = dateLivraison;
        this.heureLivraison = heureLivraison;
        this.priorite = priorite;
        this.articles = articles;
        this.statut = statut;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getLivreurId() { return livreurId; }
    public void setLivreurId(int livreurId) { this.livreurId = livreurId; }

    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }

    public String getAdresse() { return adresseLivraison; }

    public Date getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(Date dateLivraison) { this.dateLivraison = dateLivraison; }

    public Time getHeureLivraison() { return heureLivraison; }
    public void setHeureLivraison(Time heureLivraison) { this.heureLivraison = heureLivraison; }

    public String getHeure() {
        if (this.heureLivraison != null) {
            return this.heureLivraison.toString().substring(0, 5); // HH:mm
        }
        return "";
    }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public String getArticles() { return articles; }
    public void setArticles(String articles) { this.articles = articles; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public String getNomLivreur() { return nomLivreur; }
    public void setNomLivreur(String nomLivreur) { this.nomLivreur = nomLivreur; }

    public Object getClient() {
        throw new UnsupportedOperationException("getClient() n'est pas encore implémenté.");
    }
}



