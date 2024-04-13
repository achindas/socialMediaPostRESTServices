package com.springwebservices.socialmediapostservices.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springwebservices.socialmediapostservices.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
