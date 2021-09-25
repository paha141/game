package com.game.repository;

import com.game.entity.Player;
import com.game.entity.PlayerPage;
import com.game.entity.PlayerSearchCriteria;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class PlayerCriteriaRepository {

    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public PlayerCriteriaRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.criteriaBuilder = this.entityManager.getCriteriaBuilder();
    }

    public Page<Player> findAllWithFilter(PlayerPage playerPage, PlayerSearchCriteria playerSearchCriteria) {
        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);
        Predicate predicate = getPredicate(playerSearchCriteria, playerRoot);
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.asc(playerRoot.get(playerPage.getOrder())));

        TypedQuery<Player> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(playerPage.getPageNumber() * playerPage.getPageSize());
        typedQuery.setMaxResults(playerPage.getPageSize());

        Pageable pageable = getPageable(playerPage);
        long playersCount = getPlayersCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, playersCount);
    }

    private long getPlayersCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Player> countRoot = countQuery.from(Player.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Pageable getPageable(PlayerPage playerPage) {
        Sort sort = Sort.by(playerPage.getOrder());
        return PageRequest.of(playerPage.getPageNumber(), playerPage.getPageSize(), sort);
    }

    private Predicate getPredicate(PlayerSearchCriteria playerSearchCriteria, Root<Player> playerRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(playerSearchCriteria.getName())) {
            predicates.add(criteriaBuilder.like(playerRoot.get("name"), "%" + playerSearchCriteria.getName() + "%"));
        }
        if (Objects.nonNull(playerSearchCriteria.getTitle())) {
            predicates.add(criteriaBuilder.like(playerRoot.get("title"), "%" + playerSearchCriteria.getTitle() + "%"));
        }
        if (Objects.nonNull(playerSearchCriteria.getRace())) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("race"), playerSearchCriteria.getRace()));
        }
        if (Objects.nonNull(playerSearchCriteria.getProfession())) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("profession"), playerSearchCriteria.getProfession()));
        }

        predicates.add(criteriaBuilder.between(playerRoot.get("birthday"), playerSearchCriteria.getAfter(), playerSearchCriteria.getBefore()));

        if (Objects.nonNull(playerSearchCriteria.getBanned())) {
            if (playerSearchCriteria.getBanned()) {
                predicates.add(criteriaBuilder.isTrue(playerRoot.get("banned")));
            } else {
                predicates.add(criteriaBuilder.isFalse(playerRoot.get("banned")));
            }
        }
        predicates.add(criteriaBuilder.between(playerRoot.get("experience"), playerSearchCriteria.getMinExperience(), playerSearchCriteria.getMaxExperience()));

        predicates.add(criteriaBuilder.between(playerRoot.get("level"), playerSearchCriteria.getMinLevel(), playerSearchCriteria.getMaxLevel()));


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
