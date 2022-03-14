package ru.geekbrains.cloud.server.db;

import java.io.File;

public class AuthService {

  public void start() {
    System.out.println("AuthService started");
    File folder = new File("server_repository");
    if (!folder.exists()) {
      folder.mkdir();
      System.out.println("Folder " + folder.getName() + " created");
    }
  }

  public void stop() {
    System.out.println("AuthService stopped");
  }
}
