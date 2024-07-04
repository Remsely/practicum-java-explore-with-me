package ru.practicum.explorewithme.mainsvc.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    @Query(" select count (e) > 0 " +
            "from Event e " +
            "where e.category.id = :categoryId")
    boolean existsEventByCategoryId(long categoryId);
}
