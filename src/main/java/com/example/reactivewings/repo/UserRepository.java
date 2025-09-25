package com.example.reactivewings.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.reactivewings.model.User;


public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
