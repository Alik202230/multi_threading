package com.javadev.Multi.Threading.controller;

import com.javadev.Multi.Threading.entity.User;
import com.javadev.Multi.Threading.service.UserService;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
  public ResponseEntity<?> saveUser(@RequestParam(value = "file") MultipartFile[] files) throws Exception {
    for (MultipartFile file : files) {
      userService.saveUser(file);
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @GetMapping(value = "/users", produces = "application/json")
  public CompletableFuture<ResponseEntity<?>> getAllUsers() {
    return userService.getAllUsers().thenApply(ResponseEntity::ok);
  }

  @GetMapping(value = "/byThread", produces = "application/json")
  public ResponseEntity<?> getUsers() {
    CompletableFuture<List<User>> users1 = userService.getAllUsers();
    CompletableFuture<List<User>> users2 = userService.getAllUsers();
    CompletableFuture<List<User>> users3 = userService.getAllUsers();
    CompletableFuture.allOf(users1, users2, users3).join();
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
