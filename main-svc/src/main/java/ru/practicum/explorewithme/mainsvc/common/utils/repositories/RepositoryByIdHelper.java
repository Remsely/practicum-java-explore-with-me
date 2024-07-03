package ru.practicum.explorewithme.mainsvc.common.utils.repositories;

public interface RepositoryByIdHelper<T, K> {
    T findById(K id);

    void checkExistenceById(K id);
}
