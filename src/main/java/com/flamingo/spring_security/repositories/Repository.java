package com.flamingo.spring_security.repositories;

public interface Repository<T> {

    public T save(T row);

    public T find(String id);

    public T delete(T row);

}
