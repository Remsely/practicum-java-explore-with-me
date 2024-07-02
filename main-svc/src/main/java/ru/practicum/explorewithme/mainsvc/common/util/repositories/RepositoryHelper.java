package ru.practicum.explorewithme.mainsvc.common.util.repositories;

public interface RepositoryHelper<T> {
    T findById(Long id);

    void checkExistence(Long id);
}
