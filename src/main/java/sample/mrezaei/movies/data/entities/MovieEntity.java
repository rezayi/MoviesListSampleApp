package sample.mrezaei.movies.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class MovieEntity{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private int id;
        @Column(name = "title")
        private String title;
        @Column(name = "release_date")
        private String releaseDate;
        @Column(name = "poster_url")
        private String posterUrl;
        @Column(name = "overview")
        private String overview;
        @Column(name = "genres")
        @Enumerated(EnumType.STRING)
        private Genre genre;
        @Column(name = "runtime_minutes")
        private Integer runtimeInMinutes;
        @Column(name = "language")
        @Enumerated(EnumType.STRING)
        private Language language;
        @Column(name = "rating_score")
        private Double ratingScore;
        @Column(name = "rating_count")
        private Integer ratingCount;
}
