package sample.mrezaei.movies.data.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "api_keys")
public class ApiKeyEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "apiKey")
    private String apiKey;

    @Column(name = "username")
    private String username;
}
