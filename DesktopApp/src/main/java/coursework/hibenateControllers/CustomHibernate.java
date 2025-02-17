package coursework.hibenateControllers;

import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomHibernate extends GenericHibernate {
    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String psw) {

        User user = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.equal(root.get("login"), username));

            Query q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();

            if (user != null && !user.checkintPassword(psw)) {
                user = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<Publication> getAvailablePublications(User user) {

        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.and(cb.equal(root.get("publicationStatus"), PublicationStatus.AVAILABLE), cb.notEqual(root.get("owner"), user)));
            Query q = entityManager.createQuery(query);
            publications = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }

    public void deleteComment(int id) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            var comment = entityManager.find(Comment.class, id);

            if (comment.getParentComment() != null) {
                Comment parentComment = entityManager.find(Comment.class, comment.getParentComment().getId());
                parentComment.getReplies().remove(comment);
                entityManager.merge(parentComment);
            }

            comment.getReplies().clear();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public List<Publication> getOwnPublications(User user) {

        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.equal(root.get("owner"), user));
            query.orderBy(cb.desc(root.get("requestDate")));

            Query q = entityManager.createQuery(query);
            publications = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }

    public List<PeriodicRecord> getPeriodicById(int id) {
        List<PeriodicRecord> periodicRecords = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<PeriodicRecord> query = cb.createQuery(PeriodicRecord.class);
            Root<PeriodicRecord> root = query.from(PeriodicRecord.class);
            Publication publication = entityManager.find(Publication.class, id);

            query.select(root).where(cb.equal(root.get("publication"), publication));
            query.orderBy(cb.desc(root.get("transactionDate")));

            Query q = entityManager.createQuery(query);
            periodicRecords = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return periodicRecords;

    }

    public List<Publication> getBorrowedBooks(User user) {
        List<Publication> borrowedBooks = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.equal(root.get("owner"), user));
            Query q = entityManager.createQuery(query);
            borrowedBooks = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return borrowedBooks;
    }

    public List<PeriodicRecord> getFilteredPeriodicRecords(String titleFilter, Client client,
                                                           PublicationStatus status, LocalDate startDate, LocalDate endDate) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PeriodicRecord> query = cb.createQuery(PeriodicRecord.class);
        Root<PeriodicRecord> root = query.from(PeriodicRecord.class);

        List<Predicate> predicates = new ArrayList<>();

        if (titleFilter != null && !titleFilter.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("publication").get("title")), "%" + titleFilter.toLowerCase() + "%"));
        }

        if (client != null) {
            predicates.add(cb.equal(root.get("user"), client));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }

    public Chat getChatByPublication(Publication publication) {
        Chat chat = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Chat> query = cb.createQuery(Chat.class);
            Root<Chat> root = query.from(Chat.class);

            // Fetch the messages collection along with the Chat entity
            root.fetch("messages", JoinType.LEFT);

            query.select(root).where(cb.equal(root.get("book"), publication));

            Query q = entityManager.createQuery(query);
            chat = (Chat) q.getResultStream().findFirst().orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return chat;
    }

    public List<Comment> getChatMessages(Chat chat) {
        List<Comment> messages = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();

            Query query = entityManager.createQuery(
                    "SELECT c FROM Comment c WHERE c.chat = :chat ORDER BY c.timestamp ASC",
                    Comment.class
            );
            query.setParameter("chat", chat);
            messages = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return messages;
    }
}
