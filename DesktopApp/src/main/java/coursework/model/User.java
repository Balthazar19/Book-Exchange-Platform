package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(unique = true)
    protected String login;
    protected String password;
    protected String name;
    protected String surname;
    //jei daugiau yra, prirasot

    public User(String login, String password, String name, String surname) {
        this.login = login;
        this.makePassword(password);
        this.name = name;
        this.surname = surname;
    }

    public void makePassword(String password) {
        this.password = hashintPassword(password);
    }

    private String hashintPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkintPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }

}
