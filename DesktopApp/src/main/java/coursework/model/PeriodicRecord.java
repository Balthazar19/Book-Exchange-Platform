package coursework.model;

import coursework.model.enums.PublicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PeriodicRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Client user;
    @ManyToOne
    private Publication publication;
    private LocalDate transactionDate;
    private PublicationStatus status;


    public PeriodicRecord(Client user, Publication publication, LocalDate transactionDate, PublicationStatus status) {
        this.user = user;
        this.publication = publication;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    public LocalDate getReturnDate() {
        return null;
    }

    public String getPublicationTitle() {
        return publication != null ? publication.getTitle() : "N/A";
    }

    public String getClientName() {
        return user != null ? user.getName() + " " + user.getSurname() : "N/A";
    }
}
