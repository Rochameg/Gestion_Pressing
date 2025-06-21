package modele;
import java.util.Date;

public class Commande {
    private int id;
    private int client_id;
    private Date date_reception;
    private Date date_livraison;
    private String statut;
    private String article;
    private double total;
    private String priorite;
    
    // Champs pour afficher les informations complètes du client
    private String clientNomComplet;
    private String clientEmail;
    private String clientTelephone;
    private String clientAdresse;
    
    // Constructeur principal (utilisé par le DAO)
    public Commande(int id, int client_id, Date date_reception, Date date_livraison, 
                   String statut, String article, double total, String priorite) {
        this.id = id;
        this.client_id = client_id;
        this.date_reception = date_reception;
        this.date_livraison = date_livraison;
        this.statut = statut;
        this.article = article;
        this.total = total;
        this.priorite = priorite;
        this.clientNomComplet = ""; // Par défaut vide
        this.clientEmail = "";
        this.clientTelephone = "";
        this.clientAdresse = "";
    }
    
    // Constructeur pour créer de nouvelles commandes
    public Commande(int client_id, Date date_reception, Date date_livraison, 
                   String statut, String article, double total, String priorite) {
        this.client_id = client_id;
        this.date_reception = date_reception;
        this.date_livraison = date_livraison;
        this.statut = statut;
        this.article = article;
        this.total = total;
        this.priorite = priorite;
        this.clientNomComplet = "";
        this.clientEmail = "";
        this.clientTelephone = "";
        this.clientAdresse = "";
    }
    
    // Getters et Setters existants
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getClient_id() {
        return client_id;
    }
    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
    public Date getDate_reception() {
        return date_reception;
    }
    public void setDate_reception(Date date_reception) {
        this.date_reception = date_reception;
    }
    public Date getDate_livraison() {
        return date_livraison;
    }
    public void setDate_livraison(Date date_livraison) {
        this.date_livraison = date_livraison;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
    public String getArticle() {
        return article;
    }
    public void setArticle(String article) {
        this.article = article;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getPriorite() {
        return priorite;
    }
    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }
    
    // Getters/Setters pour les informations complètes du client
    public String getClientNomComplet() {
        return clientNomComplet;
    }
    public void setClientNomComplet(String clientNomComplet) {
        this.clientNomComplet = clientNomComplet;
    }
    
    public String getClientEmail() {
        return clientEmail;
    }
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public String getClientTelephone() {
        return clientTelephone;
    }
    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
    }
    
    public String getClientAdresse() {
        return clientAdresse;
    }
    public void setClientAdresse(String clientAdresse) {
        this.clientAdresse = clientAdresse;
    }
    
    // Méthode utilitaire pour obtenir l'affichage du client (nom complet ou ID)
    public String getClientDisplay() {
        if (clientNomComplet != null && !clientNomComplet.trim().isEmpty()) {
            return clientNomComplet;
        } else {
            return "Client #" + client_id;
        }
    }
    
    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", client_id=" + client_id +
                ", clientNomComplet='" + clientNomComplet + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ", clientTelephone='" + clientTelephone + '\'' +
                ", clientAdresse='" + clientAdresse + '\'' +
                ", date_reception=" + date_reception +
                ", date_livraison=" + date_livraison +
                ", statut='" + statut + '\'' +
                ", article='" + article + '\'' +
                ", total=" + total +
                ", priorite='" + priorite + '\'' +
                '}';
    }
}