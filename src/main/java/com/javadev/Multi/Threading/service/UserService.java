package com.javadev.Multi.Threading.service;

import com.javadev.Multi.Threading.entity.User;
import com.javadev.Multi.Threading.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  Object target;
  Logger logger = LoggerFactory.getLogger(UserService.class);


  @Async
  public CompletableFuture<List<User>> saveUser(MultipartFile file) throws Exception {
    long start = System.currentTimeMillis();
    List<User> users = parseCSVFile(file);
    logger.info("Saving list of users of size " + users.size() + "" +Thread.currentThread().getName());
    users = userRepository.saveAll(users);
    long end = System.currentTimeMillis();
    logger.info("Total time {}", (end - start));
    return CompletableFuture.completedFuture(users);
  }


  @Async
  public CompletableFuture<List<User>> getAllUsers() {
    logger.info("Get list of user by " +Thread.currentThread().getName());
    List<User> users = userRepository.findAll();
    return CompletableFuture.completedFuture(users);
  }

  private List<User> parseCSVFile(final MultipartFile file) throws Exception {
    final List<User> users = new ArrayList<>();
    try {
      try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        String line;
        while ((line = br.readLine()) != null) {
          final String[] data = line.split(",");
          final User user = new User();
          user.setFirstName(data[0]);
          user.setLastName(data[1]);
          user.setEmail(data[2]);
          user.setGender(data[3]);
          user.setIpAddress(data[4]);
          users.add(user);
        }
        return users;
      }
    } catch (final IOException e) {
      logger.error("Failed to parse CSV file {}", e);
      throw new Exception("Failed to parse CSV file {}", e);
    }
  }
}
