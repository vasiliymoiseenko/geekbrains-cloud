package ru.geekbrains.cloud.client.javafx;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

public class ClientApplication extends Application {

  @Getter
  private static Stage primaryStage;

  @Override
  public void start(Stage stage) throws IOException {
    primaryStage = stage;

    FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/views/client-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
    stage.setTitle("GeekBrains Cloud!");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}