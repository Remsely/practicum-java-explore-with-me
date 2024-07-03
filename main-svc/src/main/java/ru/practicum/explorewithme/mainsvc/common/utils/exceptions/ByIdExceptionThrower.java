package ru.practicum.explorewithme.mainsvc.common.utils.exceptions;

public interface ByIdExceptionThrower<T, K> {
    T findById(K id);

    void checkExistenceById(K id);
}
