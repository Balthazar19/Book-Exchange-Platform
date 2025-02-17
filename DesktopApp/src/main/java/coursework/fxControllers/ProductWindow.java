package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.*;
import coursework.model.enums.Format;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductWindow implements Initializable {

    @FXML
    public ListView<Publication> publicationsListField;
    @FXML
    public TextField authorField;
    @FXML
    public TextField titleField;
    @FXML
    public TextField editorField;
    @FXML
    public ComboBox<Frequency> frequencyField;
    @FXML
    public TextField issueNumberField;
    @FXML
    public DatePicker publicationDate;
    @FXML
    public TextField publisherField;
    @FXML
    public ComboBox<Demographic> demographicField;
    @FXML
    public TextField illustratorField;
    @FXML
    public CheckBox isColorChk;
    @FXML
    public ComboBox<Language> originalLanguageField;
    @FXML
    public TextField volumeNumberField;
    @FXML
    public RadioButton bookChk;
    @FXML
    public RadioButton mangaChk;
    @FXML
    public RadioButton periodicalChk;
    @FXML
    public ComboBox<Format> formatField;
    @FXML
    public ComboBox<Genre> genreField;
    @FXML
    public TextField isbnField;
    @FXML
    public ComboBox<Language> languageField;
    @FXML
    public TextField pageCountField;
    @FXML
    public TextField publicationYearField;
    @FXML
    public TextArea summaryField;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    @Setter
    private User currentUser;

    public void fillPublicationList() {

        publicationsListField.getItems().clear();
        List<Publication> publicationsList = hibernate.getAllRecords(Publication.class);
        publicationsListField.getItems().addAll(publicationsList);

        demographicField.getItems().clear();
        demographicField.getItems().addAll(Demographic.values());
        frequencyField.getItems().clear();
        frequencyField.getItems().addAll(Frequency.values());
        originalLanguageField.getItems().clear();
        originalLanguageField.getItems().addAll(Language.values());
        formatField.getItems().clear();
        formatField.getItems().addAll(Format.values());
        genreField.getItems().clear();
        genreField.getItems().addAll(Genre.values());
        languageField.getItems().clear();
        languageField.getItems().addAll(Language.values());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {fillPublicationList();}

    public void createNewPublication() {
        if (currentUser instanceof Client) {
            Client client = (Client) currentUser;
            Publication publication;
            if (bookChk.isSelected()) {
                publication = new Book(titleField.getText(), authorField.getText(), publisherField.getText(), isbnField.getText(), Genre.valueOf(String.valueOf(genreField.getValue())), Integer.parseInt(pageCountField.getText()), Language.valueOf(String.valueOf(languageField.getValue())), Integer.parseInt(publicationYearField.getText()), Format.valueOf(formatField.getValue().name()), summaryField.getText());
            } else if (mangaChk.isSelected()) {
                publication = new Manga(titleField.getText(), authorField.getText(), illustratorField.getText(), originalLanguageField.getValue(), Integer.parseInt(volumeNumberField.getText()), Demographic.valueOf(String.valueOf(demographicField.getValue())), isColorChk.isSelected());
            } else {
                publication = new Periodical(titleField.getText(), authorField.getText(), Integer.parseInt(issueNumberField.getText()), publicationDate.getValue(), editorField.getText(), Frequency.valueOf(String.valueOf(frequencyField.getValue())), publisherField.getText());
            }
            publication.setOwner(client);
            publication.setPublicationStatus(PublicationStatus.AVAILABLE);
            hibernate.create(publication);
            fillPublicationList();
        } else {
            System.out.println("Current user is not a client.");
        }
    }

    public void disableFields() {
        editorField.setDisable(true);
        frequencyField.setDisable(true);
        issueNumberField.setDisable(true);
        publicationDate.setDisable(true);
        illustratorField.setDisable(true);
        originalLanguageField.setDisable(true);
        volumeNumberField.setDisable(true);
        demographicField.setDisable(true);
        isColorChk.setDisable(true);
        publisherField.setDisable(true);
        isbnField.setDisable(true);
        genreField.setDisable(true);
        pageCountField.setDisable(true);
        languageField.setDisable(true);
        publicationYearField.setDisable(true);
        formatField.setDisable(true);
        summaryField.setDisable(true);

        if(bookChk.isSelected()) {
            publisherField.setDisable(false);
            isbnField.setDisable(false);
            genreField.setDisable(false);
            pageCountField.setDisable(false);
            languageField.setDisable(false);
            publicationYearField.setDisable(false);
            formatField.setDisable(false);
            summaryField.setDisable(false);
        } else if (mangaChk.isSelected()) {
            illustratorField.setDisable(false);
            originalLanguageField.setDisable(false);
            volumeNumberField.setDisable(false);
            demographicField.setDisable(false);
            isColorChk.setDisable(false);
        } else{
            issueNumberField.setDisable(false);
            publicationDate.setDisable(false);
            editorField.setDisable(false);
            frequencyField.setDisable(false);
            publisherField.setDisable(false);
        }
    }

    public void loadPublisherData() {
        Publication selectedPub = publicationsListField.getSelectionModel().getSelectedItem();

        Publication publicationInfoFromDb = hibernate.getEntityById(Publication.class, selectedPub.getId());
        if(selectedPub == null) return;

        titleField.setText(publicationInfoFromDb.getTitle());
        authorField.setText(publicationInfoFromDb.getAuthor());

        if(selectedPub instanceof Book) {
            Book book = (Book) publicationInfoFromDb;
            publisherField.setText(book.getPublisher());
            isbnField.setText(book.getIsbn());
            genreField.setValue(book.getGenre());
            pageCountField.setText(String.valueOf(book.getPageCount()));
            languageField.setValue(book.getLanguage());
            publicationYearField.setText(String.valueOf(book.getPublicationYear()));
            formatField.setValue(book.getFormat());
            summaryField.setText(book.getSummary());

            bookChk.setSelected(true);
            mangaChk.setSelected(false);
            periodicalChk.setSelected(false);
            disableFields();
        } else if (selectedPub instanceof Manga) {
            Manga manga = (Manga) publicationInfoFromDb;
            illustratorField.setText(manga.getIllustrator());
            originalLanguageField.setValue(manga.getOriginalLanguage());
            volumeNumberField.setText(String.valueOf(manga.getVolumeNumber()));
            demographicField.setValue(manga.getDemographic());

            if (manga.isColor() == true)
                isColorChk.setSelected(true);
            else isColorChk.setSelected(false);

            bookChk.setSelected(false);
            mangaChk.setSelected(true);
            periodicalChk.setSelected(false);
            disableFields();
        } else{
            Periodical periodical = (Periodical) publicationInfoFromDb;

            issueNumberField.setText(String.valueOf(periodical.getIssueNumber()));
            publicationDate.setValue(periodical.getPublicationDate());
            editorField.setText(periodical.getEditor());
            frequencyField.setValue(Frequency.valueOf(periodical.getFrequency().toString()));
            publisherField.setText(periodical.getPublisher());

            bookChk.setSelected(false);
            mangaChk.setSelected(false);
            periodicalChk.setSelected(true);
            disableFields();
        }
    }

    public void updatePublisher() {
        Publication selectedPub = publicationsListField.getSelectionModel().getSelectedItem();
        Publication publicationInfoFromDb = hibernate.getEntityById(Publication.class, selectedPub.getId());

        publicationInfoFromDb.setTitle(publicationInfoFromDb.getTitle());
        publicationInfoFromDb.setAuthor(authorField.getText());

        if(publicationInfoFromDb instanceof Book) {
            Book book = (Book) publicationInfoFromDb;
            book.setPublisher(publisherField.getText());
            book.setIsbn(isbnField.getText());
            book.setGenre(Genre.valueOf(genreField.getValue().name()));
            book.setPageCount(Integer.parseInt(pageCountField.getText()));
            book.setLanguage(Language.valueOf(languageField.getValue().name()));
            book.setPublicationYear(Integer.parseInt(publicationYearField.getText()));
            book.setFormat(Format.valueOf(formatField.getValue().name()));
            book.setSummary(summaryField.getText());
            book.setTitle(titleField.getText());
        } else if (publicationInfoFromDb instanceof Manga) {
            Manga manga = (Manga) publicationInfoFromDb;
            manga.setIllustrator(illustratorField.getText());
            manga.setOriginalLanguage(originalLanguageField.getValue());
            manga.setVolumeNumber(Integer.parseInt(volumeNumberField.getText()));
            manga.setDemographic(Demographic.valueOf(demographicField.getValue().name()));
            manga.setColor(isColorChk.isSelected());
            manga.setTitle(titleField.getText());
        }else{
            Periodical periodical = (Periodical) publicationInfoFromDb;
            periodical.setIssueNumber(Integer.parseInt(issueNumberField.getText()));
            periodical.setPublicationDate(publicationDate.getValue());
            periodical.setEditor(editorField.getText());
            periodical.setFrequency(Frequency.valueOf(frequencyField.getValue().name()));
            periodical.setPublisher(publisherField.getText());
            periodical.setTitle(titleField.getText());
        }

        hibernate.update(publicationInfoFromDb);
        fillPublicationList();
    }

    public void deletePublication() {
        Publication selectedPub = publicationsListField.getSelectionModel().getSelectedItem();
        hibernate.delete(Publication.class, selectedPub.getId());
        fillPublicationList();
    }
}
