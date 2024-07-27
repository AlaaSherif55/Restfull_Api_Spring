package com.example.test;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserResource {
    private final UserRepositery  userRepository;
    private final PostRepositery postRepository;

    UserResource(UserRepositery  userRepository, PostRepositery  postRepository){
        this.userRepository=userRepository;
        this.postRepository= postRepository;
    }

    @GetMapping()
    public List<User> getAllUsers(){
        return  userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        Optional<User> user =  userRepository.findById(id);
        if(user.isEmpty()){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(user.get());
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody User user){
        userRepository.save(user);
        try {
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,@Valid @RequestBody User user) {
        Optional<User> foundedUser = userRepository.findById(id);
        if (foundedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = foundedUser.get();
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        try {
            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok().body(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id){
       Optional<User> user = userRepository.findById(id);
       if(user.isEmpty()){
           ResponseEntity.notFound().build();
       }
       userRepository.delete(user.get());
       return ResponseEntity.ok().body(user.get());
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(user.get().getPosts());
    }

    @PostMapping("/{id}/posts")
    public ResponseEntity<Post> createUserPost(@PathVariable Long id, @RequestBody Post post){
        Optional<User> user =  userRepository.findById(id);
        if(user.isEmpty()){
            ResponseEntity.notFound().build();
        }
        post.setUser(user.get());
        Post createdPost = postRepository.save(post);
        return ResponseEntity.ok().body(createdPost);
    }

    @DeleteMapping("/{user_id}/posts/{post_id}")
    public ResponseEntity<Post> deleteUserPost(@PathVariable Long user_id,@PathVariable Long post_id){
        Optional<User> user = userRepository.findById(user_id);
        Optional<Post> post = postRepository.findById(post_id);
        if(user.isEmpty()||post.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if (!post.get().getUser().getId().equals(user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        postRepository.delete(post.get());
        return ResponseEntity.ok().body(post.get());
    }


    @GetMapping("/{user_id}/posts/{post_id}")
    public ResponseEntity<Post> getUserPost(@PathVariable Long user_id,@PathVariable Long post_id){
        Optional<User> user = userRepository.findById(user_id);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Optional<Post> post = postRepository.findById(post_id);

        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(post.get());
    }




}
