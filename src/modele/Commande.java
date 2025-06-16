package modele; // Généralement, les classes de modèle sont placées dans un package 'models' ou 'entity'

import java.time.LocalDate; // Pour gérer la date de commande

public class Commande {

    private int idCommande;
    private int idClient; // Clé étrangère vers la table Clients
    private LocalDate dateCommande;
    private String statutCommande; // Ex: "En attente", "En cours", "Terminée", "Annulée"
    private double montantTotal;
    private String serviceDemande; // Ex: "Lavage à sec", "Repassage", "Nettoyage humide", "Retouches"
    private String modePaiement; // Ex: "Espèces", "Carte bancaire", "Mobile Money"
    private LocalDate dateLivraisonPrevue; // Optionnel : date de livraison estimée
    private LocalDate dateLivraisonEffective; // Optionnel : date de livraison réelle

    // Constructeur complet
    public Commande(int idCommande, int idClient, LocalDate dateCommande, String statutCommande,
            double montantTotal, String serviceDemande, String modePaiement,
            LocalDate dateLivraisonPrevue, LocalDate dateLivraisonEffective) {
        this.idCommande = idCommande;
        this.idClient = idClient;
        this.dateCommande = dateCommande;
        this.statutCommande = statutCommande;
        this.montantTotal = montantTotal;
        this.serviceDemande = serviceDemande;
        this.modePaiement = modePaiement;
        this.dateLivraisonPrevue = dateLivraisonPrevue;
        this.dateLivraisonEffective = dateLivraisonEffective;
    }

    // Constructeur simplifié (par exemple, pour une nouvelle commande sans ID ni
    // dates de livraison finales)
    public Commande(int idClient, int aInt1, LocalDate dateCommande, String statutCommande, double montantTotal) {
        this.idClient = idClient;
        this.dateCommande = dateCommande;
        this.statutCommande = statutCommande;
        this.montantTotal = montantTotal;
        this.serviceDemande = serviceDemande;
        this.modePaiement = modePaiement;
    }

    // --- Getters ---
    public int getIdCommande() {
        return idCommande;
    }

    public int getIdClient() {
        return idClient;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public String getStatutCommande() {
        return statutCommande;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public String getServiceDemande() {
        return serviceDemande;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public LocalDate getDateLivraisonEffective() {
        return dateLivraisonEffective;
    }

    // --- Setters (si vous avez besoin de modifier les champs après la création de
    // l'objet) ---
    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setStatutCommande(String statutCommande) {
        this.statutCommande = statutCommande;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public void setServiceDemande(String serviceDemande) {
        this.serviceDemande = serviceDemande;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public void setDateLivraisonEffective(LocalDate dateLivraisonEffective) {
        this.dateLivraisonEffective = dateLivraisonEffective;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", idClient=" + idClient +
                ", dateCommande=" + dateCommande +
                ", statutCommande='" + statutCommande + '\'' +
                ", montantTotal=" + montantTotal +
                ", serviceDemande='" + serviceDemande + '\'' +
                ", modePaiement='" + modePaiement + '\'' +
                ", dateLivraisonPrevue=" + dateLivraisonPrevue +
                ", dateLivraisonEffective=" + dateLivraisonEffective +
                '}';
    }
}