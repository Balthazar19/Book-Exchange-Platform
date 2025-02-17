package coursework.fxControllers;

import coursework.hibenateControllers.CustomHibernate;
import coursework.model.Client;
import coursework.model.PeriodicRecord;
import coursework.model.User;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class History implements Initializable {

    @FXML
    public TableView<PeriodicRecord> bookHistoryTable;
    @FXML
    public TableColumn<PeriodicRecord, Integer> colId;
    @FXML
    public TableColumn<PeriodicRecord, String> colReturnDate;
    @FXML
    public TableColumn<PeriodicRecord, String> colStatus;
    @FXML
    public TableColumn<PeriodicRecord, String> colTransactionDate;
    @FXML
    public TableColumn<PeriodicRecord, String> colPublication;
    @FXML
    public TableColumn<PeriodicRecord, String> colClient;
    @FXML
    public DatePicker startDateField;
    @FXML
    public DatePicker endDateField;
    @FXML
    public ComboBox<PublicationStatus> PublicationStatusField;
    @FXML
    public ComboBox<Client> clientField;
    @FXML
    public TextField publicationTitleField;

    @FXML
    public Label publishedCount;
    @FXML
    public Label unpublishedCount;
    @FXML
    public Label availableCount;
    @FXML
    public Label requestedCount;
    @FXML
    public Label releasedCount;
    @FXML
    public Label unavailableCount;
    @FXML
    public Label soldCount;

    private EntityManagerFactory entityManagerFactory;
    private CustomHibernate hibernate;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTransactionDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        colPublication.setCellValueFactory(new PropertyValueFactory<>("publicationTitle"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
    }

    private void initializeComboBoxes() {
        PublicationStatusField.getItems().addAll(PublicationStatus.values());
        List<Client> clients = hibernate.getAllRecords(Client.class);
        clientField.getItems().addAll(clients);
    }

    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.hibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = currentUser;
        initializeComboBoxes();
        loadAllRecords();
        updateSummary();
    }

    public void setData(EntityManagerFactory entityManagerFactory, User currentUser, int id) {
        this.entityManagerFactory = entityManagerFactory;
        this.hibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = currentUser;
        initializeComboBoxes();
        loadPublicationsById(id);
        updateSummary();
    }

    private void loadAllRecords() {
        bookHistoryTable.getItems().clear();
        bookHistoryTable.getItems().addAll(hibernate.getAllRecords(PeriodicRecord.class));
    }

    private void loadPublicationsById(int id) {
        bookHistoryTable.getItems().clear();
        bookHistoryTable.getItems().addAll(hibernate.getPeriodicById(id));
    }

    public void filterRecords() {
        String titleFilter = publicationTitleField.getText();
        Client selectedClient = clientField.getValue();
        PublicationStatus selectedStatus = PublicationStatusField.getValue();
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();

        List<PeriodicRecord> filteredRecords = hibernate.getFilteredPeriodicRecords(
                titleFilter, selectedClient, selectedStatus, startDate, endDate
        );

        bookHistoryTable.getItems().clear();
        bookHistoryTable.getItems().addAll(filteredRecords);
        updateSummary();
    }

    private void updateSummary() {
        List<PeriodicRecord> records = bookHistoryTable.getItems();

        long publishedCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.PUBLISHED).count();
        long unpublishedCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.UNPUBLISHED).count();
        long availableCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.AVAILABLE).count();
        long requestedCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.REQUESTED).count();
        long releasedCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.RELEASED).count();
        long unavailableCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.UNAVAILABLE).count();
        long soldCount = records.stream().filter(r -> r.getStatus() == PublicationStatus.SOLD).count();

        this.publishedCount.setText(String.valueOf(publishedCount));
        this.unpublishedCount.setText(String.valueOf(unpublishedCount));
        this.availableCount.setText(String.valueOf(availableCount));
        this.requestedCount.setText(String.valueOf(requestedCount));
        this.releasedCount.setText(String.valueOf(releasedCount));
        this.unavailableCount.setText(String.valueOf(unavailableCount));
        this.soldCount.setText(String.valueOf(soldCount));
    }
}