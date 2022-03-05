package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;
    private final EntityManager entityManager;

    public DataJpaMealRepository(CrudMealRepository crudRepository, EntityManager entityManager) {
        this.crudRepository = crudRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = entityManager.getReference(User.class, userId);
        meal.setUser(user);
        if (meal.isNew()) {
            entityManager.persist(meal);
            return meal;
        } else if (get(meal.id(), userId) == null) {
            return null;
        }
        return entityManager.merge(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findMealById(id, userId).orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAllByUserId(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findBetweenHalfOpen(startDateTime, endDateTime, userId);
    }
}
