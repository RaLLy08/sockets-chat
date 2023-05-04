package src.server.models;
import java.util.Date;
import java.util.UUID; 

public class User {
    private int id;
    private String username ;
    private Date createdAt;
    
    public User(String username) {
        this.username = username;

        this.id = UUID.randomUUID().hashCode();
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
