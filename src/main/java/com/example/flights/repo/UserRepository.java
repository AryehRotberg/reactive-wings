package com.example.flights.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.flights.model.UserModel;


public interface UserRepository extends ReactiveMongoRepository<UserModel, String> {

}
