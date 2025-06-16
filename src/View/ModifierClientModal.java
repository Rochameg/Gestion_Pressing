package View;

import modele.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModifierClientModal extends JDialog {

    private JTextField idField; // Souvent non modifiable, mais affiché
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField telephoneField;
    private JTextField emailField;
    private JTextField adresseField;

    private Client clientToModify;
    private boolean clientUpdated = false;

    public ModifierClientModal(JFrame parent, Client client) {
        super(parent, "Modifier le Client", true);
        this.clientToModify = client;
        setSize(400, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        populateFields();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10)); // 7 lignes pour l'ID
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("ID Client:"));
        idField = new JTextField(20);
        idField.setEditable(false); // L'ID n'est pas modifiable
        formPanel.add(idField);

        formPanel.add(new JLabel("Nom:"));
        nomField = new JTextField(20);
        formPanel.add(nomField);

        formPanel.add(new JLabel("Prénom:"));
        prenomField = new JTextField(20);
        formPanel.add(prenomField);

        formPanel.add(new JLabel("Téléphone:"));
        telephoneField = new JTextField(20);
        formPanel.add(telephoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        formPanel.add(emailField);

        formPanel.add(new JLabel("Adresse:"));
        adresseField = new JTextField(20);
        formPanel.add(adresseField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    clientToModify.setNom(nomField.getText());
                    clientToModify.setPrenom(prenomField.getText());
                    clientToModify.setTelephone(telephoneField.getText());
                    clientToModify.setEmail(emailField.getText());
                    clientToModify.setAdresse(adresseField.getText());
                    clientUpdated = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(ModifierClientModal.this,
                            "Veuillez remplir tous les champs obligatoires.",
                            "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (clientToModify != null) {
            idField.setText(String.valueOf(clientToModify.getId()));
            nomField.setText(clientToModify.getNom());
            prenomField.setText(clientToModify.getPrenom());
            telephoneField.setText(clientToModify.getTelephone());
            emailField.setText(clientToModify.getEmail());
            adresseField.setText(clientToModify.getAdresse());
        }
    }

    private boolean validateFields() {
        return !nomField.getText().trim().isEmpty() &&
               !prenomField.getText().trim().isEmpty() &&
               !telephoneField.getText().trim().isEmpty() &&
               !emailField.getText().trim().isEmpty() &&
               !adresseField.getText().trim().isEmpty();
    }

    public boolean isClientUpdated() {
        return clientUpdated;
    }

    public Client getClientToModify() {
        return clientToModify;
    }
}