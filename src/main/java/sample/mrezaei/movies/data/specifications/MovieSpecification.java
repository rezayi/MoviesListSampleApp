package sample.mrezaei.movies.data.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import sample.mrezaei.movies.data.entities.MovieEntity;

import java.util.Date;

/**
 * This class includes some methods to customize Movies Specification class to handle dynamic filtering
 */
public class MovieSpecification {

    public static Specification<MovieEntity> searchByTitleQuery(String title) {
        return (Root<MovieEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<MovieEntity> filterByReleaseDateFrom(Date releaseDateFrom) {
        return (Root<MovieEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                releaseDateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("releaseDate").as(Date.class), releaseDateFrom);
    }

    public static Specification<MovieEntity> filterByReleaseDateTo(Date releaseDateTo) {
        return (Root<MovieEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                releaseDateTo == null ? null : cb.lessThanOrEqualTo(root.get("releaseDate").as(Date.class), releaseDateTo);
    }

    public static Specification<MovieEntity> filterByMinimumRating(Double minRating) {
        return (Root<MovieEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                minRating == null ? null : cb.greaterThanOrEqualTo(root.get("ratingScore"), minRating);
    }
}