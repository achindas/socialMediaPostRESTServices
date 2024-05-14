package com.springwebservices.socialmediapostservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.springwebservices.socialmediapostservices.jpa.PostRepository;
import com.springwebservices.socialmediapostservices.jpa.UserRepository;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;

@RestController
public class UserJpaResource {

	private UserRepository userRepository;
	
	private PostRepository postRepository;
	
	private Logger logger = 
			LoggerFactory.getLogger(UserJpaResource.class);
	
	@Autowired
	private postServiceConfiguration postConfig;

	public UserJpaResource(UserRepository userRepository, PostRepository postRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}

	@GetMapping("/jpa/users")
	@Bulkhead(name="get-all-users-api")
	public MappingJacksonValue retrieveAllUsers() {
		
		//Test
		//System.out.println(postConfig.getUserFilter());
		
		MappingJacksonValue responseJson = new MappingJacksonValue (userRepository.findAll());
		responseJson.setFilters((new userDataFilter(postConfig)).getUserFilter());
		
		return responseJson;
	}

	
	//http://localhost:8080/users
	
	//EntityModel
	//WebMvcLinkBuilder
	
	@GetMapping("/jpa/users/{id}")
	@RateLimiter(name="get-one-user-api")
	public MappingJacksonValue retrieveUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		EntityModel<User> entityModel = EntityModel.of(user.get());
		
		WebMvcLinkBuilder link =  linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		
		MappingJacksonValue responseJson = new MappingJacksonValue(entityModel);
		responseJson.setFilters((new userDataFilter(postConfig)).getUserFilter());
		
		return responseJson;
	}
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
	}

	
	@GetMapping("/jpa/users/{id}/posts")
	@CircuitBreaker(name = "get-user-posts-api", fallbackMethod = "circuitBreakerResponse")		
	public List<Post> retrievePostsForUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		return user.get().getPosts();

	}

	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		
		User savedUser = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
						.path("/{id}")
						.buildAndExpand(savedUser.getId())
						.toUri();	
		
		return ResponseEntity.created(location).build();
	}


	@PostMapping("v1/jpa/users/{id}/posts")
	public EntityModel<Post> createPostForUserV1(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> user = userRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		EntityModel<Post> entityModel = EntityModel.of(savedPost);
		
		return entityModel;

	}
	
	// Send the path for the new post in location header in Version V2 of the API
	@PostMapping("v2/jpa/users/{id}/posts")
	public ResponseEntity<Post> createPostForUserV2(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> user = userRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId())
				.toUri();   

		return ResponseEntity.created(location).build();

	}
	
	@GetMapping("/broken-api")
	@Retry(name = "broken-api", fallbackMethod = "circuitBreakerResponse")
	public String brokenApi() {
		logger.info("Broken API call received");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/broken-url", 
					String.class);
		return forEntity.getBody();
	}
	
	public String circuitBreakerResponse(Exception ex) {
		return "System is down! Circuit Breaker activated. Please try after sometime";
	}
	
}
