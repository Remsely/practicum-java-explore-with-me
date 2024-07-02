package ru.practicum.explorewithme.mainsvc.common.utils.repositories;

public interface RepositoryByIdHelper<T> {
    T findById(Long id);

    void checkExistenceById(Long id);
}
