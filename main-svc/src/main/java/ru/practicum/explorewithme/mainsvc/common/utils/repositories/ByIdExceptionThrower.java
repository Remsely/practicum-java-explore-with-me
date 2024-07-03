package ru.practicum.explorewithme.mainsvc.common.utils.repositories;

public interface ByIdExceptionThrower<T, K> {
    T findById(K id);

    void checkExistenceById(K id);
}
