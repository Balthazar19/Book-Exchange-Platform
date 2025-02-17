package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.model.User;
import coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {

    public TextField usernameField;
    public PasswordField passwordField;
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);

    public void validateUser() throws IOException {
        User user = customHibernate.getUserByCredentials(usernameField.getText(), passwordField.getText());
        if(user != null){
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("main.fxml"));
            Parent parent = fxmlLoader.load();

            Main main = fxmlLoader.getController();
            main.setData(entityManagerFactory, user);

            Scene scene = new Scene(parent);
            var stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Book Exchange Test");
            stage.setScene(scene);
            stage.show();
        }else{
            FxUtils.generateAlert(Alert.AlertType.WARNING, "User login", "Wrong data");
        }


    }

    public void newUserRegistration() {
    }
}
