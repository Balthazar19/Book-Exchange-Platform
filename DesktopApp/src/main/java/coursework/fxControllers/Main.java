package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//Norėdama pasiekti formos elementus iki forma yra sugeneruojama, turiu implementuoti Initializable interfeisą.
//Jums automatiškai siūlys initialize metodą įgyvendint, ten dedat kodą, kuris turi būt vykdomas pirmiausia
//Mūsų atveju gavosi, kad reikia ištraukt userius iš DB
public class Main implements Initializable {
    public static User currentUser;
    @FXML
    //Stenkitės nurodyti tikslius duomenų tipus, kurie bus saugomi ListView
    public ListView<User> userListField;
    @FXML
    public TextField loginField;
    @FXML
    public TextField nameField;
    @FXML
    public PasswordField pswField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField addressField;
    @FXML
    public DatePicker bDate;
    @FXML
    public TextField phoneNumField;
    @FXML
    public RadioButton adminChk;
    @FXML
    public RadioButton clientChk;
    @FXML
    public TableView<UserTableParameters> userTable;
    @FXML
    public TableColumn<UserTableParameters, Integer> colId;
    @FXML
    public TableColumn<UserTableParameters, String> colLogin;
    @FXML
    public TableColumn<UserTableParameters, String> colPsw;
    @FXML
    public TableColumn<UserTableParameters, String> colName;
    @FXML
    public TableColumn<UserTableParameters, String> colSurname;
    @FXML
    public TableColumn<UserTableParameters, String> colAddress;
    @FXML
    public TableColumn<UserTableParameters, String> colPhone;
    @FXML
    public TableColumn dummyCol;

    @FXML
    public ListView<Publication> availableBookList;
    @FXML
    public TextArea aboutBook;
    @FXML
    public TextArea ownerBio;
    @FXML
    public Label ownerInfo;
    @FXML
    public ListView<Comment> chatList;
    @FXML
    public TextArea messageArea;
    @FXML
    public Tab publicationManagementTab;
    @FXML
    public Tab userManagementTab;
    @FXML
    public Tab clientBookManagementTab;
    @FXML
    public TabPane allTabs;
    @FXML
    public Button leaveReviewButton;
    @FXML
    public Tab bookExchangeTab;

    @FXML
    public TableView<BookTableParameters> myBookList;
    @FXML
    public TableColumn<BookTableParameters, String> colBookTitle;
    @FXML
    public TableColumn<BookTableParameters, String> colRequestUser;
    @FXML
    public TableColumn colBookStatus; //Panasiai kaip dummyCol
    @FXML
    public TableColumn<BookTableParameters, String> colRequestDate;
    @FXML
    public TableColumn<BookTableParameters, Integer> collBookId;
    @FXML
    public TableColumn colHistory; //Panasiai kaip dummyCol
    @FXML
    public Tab userTab;
    @FXML
    public ListView<Publication> borrowedBookList;
    public TableColumn colChat;

    Integer activePublicationId = null;

    private ObservableList<BookTableParameters> bookList = FXCollections.observableArrayList();


    EntityManagerFactory entityManagerFactory;
    private CustomHibernate hibernate;
    private GenericHibernate genericHibernate;

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.hibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = user;
        fillUserList();


        //Priklausomai nuo prisijungusio, apribojam matomuma
        enableVisibility();
    }


    public void fillUserList() {
        userListField.getItems().clear();
        List<User> userList = hibernate.getAllRecords(User.class);
        userListField.getItems().addAll(userList);
    }

    private void enableVisibility() {
        if (currentUser instanceof Client) {
            allTabs.getTabs().remove(publicationManagementTab);
            allTabs.getTabs().remove(userManagementTab);
            allTabs.getTabs().remove(userTab);
        } else {
            allTabs.getTabs().remove(clientBookManagementTab);
            allTabs.getTabs().remove(bookExchangeTab);
            allTabs.getTabs().remove(publicationManagementTab);
            leaveReviewButton.setDisable(false);
        }
    }


    public void createNewUser() {
        if (clientChk.isSelected()) {
            Client client = new Client(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), addressField.getText(), bDate.getValue());
            hibernate.create(client);
        } else {
            Admin admin = new Admin(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), phoneNumField.getText());
            hibernate.create(admin);
        }
        fillUserList();
    }

    public void disableFields() {
        if (clientChk.isSelected()) {
            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
        } else {
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
        }
    }


    //Sioje vietoje initialize mums reikia, kad nustatytume tam tikras reiksmes, kai dar nereikia duomenu bazes
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userTable.setEditable(true);

        //Atvaizdavimui
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPsw.setCellValueFactory(new PropertyValueFactory<>("password"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        //likusius baigt

        //Jei noriu padaryt redaguojamas celes
        colLogin.setCellFactory(TextFieldTableCell.forTableColumn());
        colLogin.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setLogin(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setLogin(event.getNewValue());
            hibernate.update(user);
        });

        //Jei turesite lentele, kurioje saugosime ir Admin ir Customer, tiems stulpeliams, kur yra specifiniai pagal klases
        //Reikia pasitikrinti koks ten tas User
        colAddress.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setAddress(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Client client) {
                client.setAddress(event.getNewValue());
                hibernate.update(user);
            }
        });

        colPhone.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhoneNum(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Admin admin) {
                admin.setPhoneNum(event.getNewValue());
                hibernate.update(admin);
            }
        });

        //Cia dabar bus knopke
        Callback<TableColumn<UserTableParameters, Void>, TableCell<UserTableParameters, Void>> callback = param -> {
            final TableCell<UserTableParameters, Void> cell = new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        UserTableParameters row = getTableView().getItems().get(getIndex());
                        hibernate.delete(User.class, row.getId());
                        fillUserTable();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        };

        dummyCol.setCellFactory(callback);

        availableBookList.setEditable(true);

        collBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("publicationTitle"));
        colRequestUser.setCellValueFactory(new PropertyValueFactory<>("publicationUser"));
        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> callbackBookStatus = param -> {
            final TableCell<BookTableParameters, Void> cell = new TableCell<>() {

                private final ChoiceBox<PublicationStatus> bookStatus = new ChoiceBox<>();

                {
                    bookStatus.getItems().addAll(PublicationStatus.values());
                    bookStatus.setOnAction(event -> {
                        BookTableParameters rowData = getTableRow().getItem();
                        if (rowData != null) {
                            rowData.setPublicationStatus(bookStatus.getValue());

                            Publication publication = hibernate.getEntityById(Publication.class, rowData.getId());
                            publication.setPublicationStatus(bookStatus.getValue());
                            hibernate.update(publication);

                            insertPublicationRecord(publication);
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        BookTableParameters rowData = getTableRow().getItem();
                        bookStatus.setValue(rowData.getPublicationStatus());
                        setGraphic(bookStatus);
                    }
                }
            };
            return cell;
        };

        colBookStatus.setCellFactory(callbackBookStatus);

        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> chatButtonCallback = param -> {
            return new TableCell<>() {
                private final Button viewChatButton = new Button("View Chat");

                {
                    viewChatButton.setOnAction(event -> {
                        BookTableParameters bookRow = getTableView().getItems().get(getIndex());
                        viewChatForBook(bookRow.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewChatButton);
                    }
                }
            };
        };

        colChat.setCellFactory(chatButtonCallback);
        availableBookList.setEditable(true);

        collBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("publicationTitle"));
        colRequestUser.setCellValueFactory(new PropertyValueFactory<>("publicationUser"));

        //Cia dabar bus knopke
        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> historyButton = param -> new TableCell<>() {
            private final Button viewHistoryBtn = new Button("View history");

            {
                viewHistoryBtn.setOnAction(event -> {
                    BookTableParameters row = getTableView().getItems().get(getIndex());
                    try {
                        loadHistory(row.getId(), currentUser);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewHistoryBtn);
                }
            }
        };

        colHistory.setCellFactory(historyButton);

        myBookList.setItems(bookList);
    }


    public void loadUserData() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        nameField.setText(userInfoFromDb.getName());
        surnameField.setText(userInfoFromDb.getSurname());
        loginField.setText(userInfoFromDb.getLogin());
        pswField.setText(userInfoFromDb.getPassword());

        if (userInfoFromDb instanceof Client) {
            clientChk.setSelected(true);
            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
            phoneNumField.clear();
            addressField.setText(((Client) userInfoFromDb).getAddress());
            bDate.setValue(((Client) userInfoFromDb).getBirthDate());
        } else {
            adminChk.setSelected(true);
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
            addressField.clear();
            bDate.setValue(null);
            bDate.getEditor().clear();
            phoneNumField.setText(((Admin) userInfoFromDb).getPhoneNum());

        }

    }

    public void updateUser() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        userInfoFromDb.setName(nameField.getText());
        userInfoFromDb.setSurname(surnameField.getText());
        userInfoFromDb.setLogin(loginField.getText());
        userInfoFromDb.setPassword(pswField.getText());

        hibernate.update(userInfoFromDb);
        fillUserList();
    }

    public void deleteUser() {

        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        hibernate.delete(User.class, selectedUser.getId());
        fillUserList();
    }

    public void loadProductForm() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("productWindow.fxml"));
        Parent parent = fxmlLoader.load();

        ProductWindow productWindow = fxmlLoader.getController();
        productWindow.setCurrentUser(currentUser);

        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void loadChatMessages(Chat chat) {
        chatList.getItems().clear();

        List<Comment> messages = hibernate.getChatMessages(chat);

        if (messages != null) {
            chatList.getItems().addAll(messages);
        }

        chatList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Comment message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    String sender;
                    if (message.getCommentOwner().getId() == (currentUser.getId())) {
                        sender = "Owner";
                    }
                    else {
                        sender = "Lender";
                    }
                    setText(sender + ": " + message.getBody());
                }
            }});


    }

    private void fillUserTable() {
        userTable.getItems().clear();
        List<User> users = hibernate.getAllRecords(User.class);
        for (User u : users) {
            UserTableParameters userTableParameters = new UserTableParameters();
            userTableParameters.setId(u.getId());
            userTableParameters.setLogin(u.getLogin());
            userTableParameters.setPassword(u.getPassword());
            //likusius pabaigt
            userTable.getItems().add(userTableParameters);
        }
    }

    public void loadData() {
        //Kai spaudziam ant tab, tik tam tab pildom duomenis
        if (userManagementTab.isSelected()) {
            fillUserTable();
            fillBookList();
        } else if (bookExchangeTab.isSelected()) {
            availableBookList.getItems().clear();
            availableBookList.getItems().addAll(hibernate.getAvailablePublications(currentUser));
            fillBookList();
        } else if (clientBookManagementTab.isSelected()) {
            fillBookList();
        }
    }


    public void loadReviewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("userReview.fxml"));
        Parent parent = fxmlLoader.load();
        UserReview userReview = fxmlLoader.getController();
        userReview.setData(entityManagerFactory, currentUser, availableBookList.getSelectionModel().getSelectedItem().getOwner());
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }


    public void loadPublicationInfo() {
        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());

        if (publicationFromDb instanceof Book book)
            aboutBook.setText( book.getSummary());
        loadChatMessages(hibernate.getChatByPublication(publicationFromDb));
        ownerInfo.setText(publicationFromDb.getOwner().getName());
        ownerBio.setText(publicationFromDb.getOwner().getClientBio());
    }

    public void chatWithOwner() {
        Publication selectedBook = availableBookList.getSelectionModel().getSelectedItem();
        if (selectedBook == null && activePublicationId != null) {
            selectedBook = hibernate.getEntityById(Publication.class, activePublicationId);
        }

        Chat chat = hibernate.getChatByPublication(selectedBook);
        if (chat == null) {
            chat = new Chat((Book) selectedBook, new ArrayList<>());
            Comment comment = new Comment( "test", messageArea.getText(), (Client) currentUser);
            chat.getMessages().add(comment);
            ((Book) selectedBook).getChatList().add(chat);
            hibernate.update(selectedBook);
        }


        loadPublicationInfo();
    }

    public void loadChatForOwner(int id) {
        loadChatMessages(hibernate.getChatByPublication(hibernate.getEntityById(Publication.class, id)));
    }

    private void viewChatForBook(int bookId) {
        activePublicationId = bookId;

        allTabs.getSelectionModel().select(bookExchangeTab);

        for (Publication book : availableBookList.getItems()) {
            if (book.getId() == bookId) {
                availableBookList.getSelectionModel().select(book);
                break;
            }
        }

        loadChatForOwner(bookId);
        chatWithOwner();
    }

    public void sendMessage() {
        String messageContent = messageArea.getText().trim();

        if (messageContent.isEmpty()) {
            return;
        }


        Publication selectedBook = availableBookList.getSelectionModel().getSelectedItem();
        if (selectedBook == null && activePublicationId != null) {
            selectedBook = hibernate.getEntityById(Publication.class, activePublicationId);
        }

        if (selectedBook == null) {
            return;
        }

        Client bookOwner = selectedBook.getOwner();
        if (bookOwner == null) {
            return;
        }

        Chat chat = hibernate.getChatByPublication(selectedBook);
        if (chat == null) {
            chat = new Chat((Book) selectedBook, new ArrayList<>());
            hibernate.create(chat); // Save the chat entity first
        }

        Comment newMessage = new Comment("Chat", messageContent, (Client) currentUser, bookOwner);
        newMessage.setChat(chat);

        hibernate.create(newMessage);

        chat.getMessages().add(newMessage);
        messageArea.clear();
        loadChatMessages(chat);
    }

    public void reserveBook() {

        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());
        publicationFromDb.setPublicationStatus(PublicationStatus.REQUESTED);
        publicationFromDb.setClient((Client) currentUser);
        hibernate.update(publicationFromDb);

        PeriodicRecord periodicRecord = new PeriodicRecord((Client) currentUser, publicationFromDb, LocalDate.now(), PublicationStatus.REQUESTED);
        hibernate.create(periodicRecord);

    }


    private void fillBookList() {
        bookList.clear();
        List<Publication> publications = hibernate.getOwnPublications(currentUser);
        for (Publication p : publications) {
            BookTableParameters bookTableParameters = new BookTableParameters();
            bookTableParameters.setId(p.getId());
            bookTableParameters.setPublicationTitle(p.getTitle());
            bookTableParameters.setPublicationUser(p.getClient() != null ? p.getClient().getName() : "N/A");
            bookTableParameters.setPublicationStatus(p.getPublicationStatus());
            bookTableParameters.setPublicationUser(p.getClient() != null ? p.getClient().getName() : "N/A");
            bookList.add(bookTableParameters);
        }
    }

    private void insertPublicationRecord(Publication publication) {
        PeriodicRecord periodicRecord = new PeriodicRecord(publication.getClient(), publication, LocalDate.now(), publication.getPublicationStatus());
        hibernate.create(periodicRecord);
    }

    //UZDUOTIS. Logika mano pasiskolintu knygu atvaizdavimui

    //Logika istoriniu knygos duomenu perziurai

    private void loadHistory(int id, User currentUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("history.fxml"));
        Parent parent = fxmlLoader.load();
        History history = fxmlLoader.getController();
        history.setData(entityManagerFactory, currentUser, id);
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void loadCreationWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("publicationCreationWindow.fxml"));
        Parent parent = fxmlLoader.load();

        PublicationCreationWindow publicationCreationWindow = fxmlLoader.getController();
        publicationCreationWindow.setCurrentUser(currentUser);

        Scene scene = new Scene(parent);
        stage.setTitle("Publication Editor");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void deletePublication(ActionEvent actionEvent) {
        genericHibernate = new GenericHibernate(entityManagerFactory);
        BookTableParameters selectedBook = myBookList.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            int selectedBookID = selectedBook.getId();
            Publication publication = genericHibernate.getEntityById(Publication.class, selectedBookID);
            if (publication != null && publication.getPublicationStatus() != PublicationStatus.REQUESTED) {

                genericHibernate.delete(Publication.class, selectedBookID);
                myBookList.getItems().remove(selectedBook);
            } else {
                System.out.println("Cannot delete a publication with status REQUESTED.");
            }
        }
    }

    public void fillBorrowedBookList() {
        borrowedBookList.getItems().clear();
        List<Publication> borrowedBooks = hibernate.getBorrowedBooks(currentUser);
        borrowedBookList.getItems().addAll(borrowedBooks);
    }


}
