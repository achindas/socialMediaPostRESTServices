package com.springwebservices.socialmediapostservices.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springwebservices.socialmediapostservices.user.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
