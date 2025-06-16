package modele;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    // Ajoutez d'autres champs si nécessaire, par exemple, nombreCommandes, derniereCommandeDate

    public Client(int id, String nom, String prenom, String telephone, String email, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Constructeur sans ID pour l'ajout (l'ID sera généré par la BDD)
    public Client(String nom, String prenom, String telephone, String email, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    // Placeholder for other fields if they exist in your DB/model
    // public int getNombreCommandes() { return 0; }
    // public String getDerniereCommandeDate() { return null; }

    @Override
    public String toString() {
        return "Client{" +
               "id=" + id +
               ", nom='" + nom + '\'' +
               ", prenom='" + prenom + '\'' +
               ", telephone='" + telephone + '\'' +
               ", email='" + email + '\'' +
               ", adresse='" + adresse + '\'' +
               '}';
    }
}