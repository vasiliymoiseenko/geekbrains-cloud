package ru.geekbrains.cloud.client.javafx.actions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.geekbrains.cloud.client.javafx.ClientApplication;
import ru.geekbrains.cloud.client.javafx.Controller;

public class ShowProgressBar {

  public static void action(Controller controller) {
    ProgressBar progressBar = controller.getProgressBar();
    Label statusProgressBar = controller.getStatusProgressBar();

    progressBar.setProgress(0);
    progressBar.setPrefSize(325, 25);

    Button btnOk = new Button("OK");
    btnOk.setPrefSize(80, 30);

    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(btnOk);

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(20));
    vBox.getChildren().addAll(statusProgressBar, progressBar, hBox);

    Scene secondScene = new Scene(vBox, 350, 125);

    Stage newWindow = new Stage();
    newWindow.setTitle("Progress bar");
    newWindow.setScene(secondScene);
    newWindow.setX(ClientApplication.getPrimaryStage().getX() + 200);
    newWindow.setY(ClientApplication.getPrimaryStage().getY() + 200);

    newWindow.show();

    btnOk.setOnAction(event -> newWindow.close());
  }

}
