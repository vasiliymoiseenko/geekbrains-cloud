package ru.geekbrains.cloud.client.javafx;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import ru.geekbrains.cloud.client.netty.Network;
import ru.geekbrains.cloud.common.service.FileService;

public class Controller implements Initializable {

  @FXML
  VBox clientPanel;
  @FXML
  VBox serverPanel;

  PanelController leftPC;
  PanelController rightPC;
  Network network;


  public PanelController getRightPC() {
    return rightPC;
  }

  public PanelController getLeftPC() {
    return leftPC;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources){
    network = new Network(this);

    leftPC = (PanelController) clientPanel.getProperties().get("ctrl");
    rightPC = (PanelController) serverPanel.getProperties().get("ctrl");

    leftPC.updateList(Paths.get("client_repository"));

    //rightPC.updateList(Paths.get("server_repository"));
  }

  public void exitAction(ActionEvent actionEvent) {
    Platform.exit();
  }

  public void copyAction(ActionEvent actionEvent) {

    if (leftPC.getSelectedFileName() == null && rightPC.getSelectedFileName() == null) {
      Alert alert  = new Alert(AlertType.WARNING, "Ни один файл не выбран", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    PanelController srcPC, dstPC = null;
    if (leftPC.getSelectedFileName() != null) {
      srcPC = leftPC;
      dstPC = rightPC;
    } else {
      srcPC = rightPC;
      dstPC = leftPC;
    }

    Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFileName());
    Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPC.getSelectedFileName());

    try {
      Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
      dstPC.updateList();
    } catch (IOException e) {
      Alert alert  = new Alert(AlertType.WARNING, "Не удалось скопировать файл", ButtonType.OK);
      alert.showAndWait();
    }
  }

  public void deleteAction(ActionEvent actionEvent) {
    PanelController leftPC = (PanelController) clientPanel.getProperties().get("ctrl");
    PanelController rightPC = (PanelController) serverPanel.getProperties().get("ctrl");

    if (leftPC.getSelectedFileName() == null && rightPC.getSelectedFileName() == null) {
      Alert alert  = new Alert(AlertType.WARNING, "Ни один файл не выбран", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    PanelController srcPC = leftPC.getSelectedFileName() != null ? leftPC : rightPC;

    Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFileName());

    try {
      Files.deleteIfExists(srcPath);
      srcPC.updateList();
    } catch (IOException e) {
      Alert alert  = new Alert(AlertType.WARNING, "Не удалось удалить файл", ButtonType.OK);
      alert.showAndWait();
    }
  }


  public void updateFileList() {
    network.updateFileList();
  }

  public void uploadAction(ActionEvent actionEvent) throws IOException{
    if (leftPC.getSelectedFileName() == null) {
      Alert alert  = new Alert(AlertType.WARNING, "Файл не выбран", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    Path path = Paths.get(leftPC.getCurrentPath()).resolve(leftPC.getSelectedFileName());
    System.out.println(path);
    FileService.sendFile(path, network.getChannel(), future -> {
      //Alert alert = new Alert(AlertType.INFORMATION, "Файл загружается. Это займет какое-то время", ButtonType.OK);
      //alert.showAndWait();
      if (!future.isSuccess()) {
        future.cause().printStackTrace();
      }
      if (future.isSuccess()) {
        //alert.close();
        System.out.println("Файл успешно передан");
      }
    });
  }

  public void downloadAction(ActionEvent actionEvent) {
    if (rightPC.getSelectedFileName() == null){
      Alert alert  = new Alert(AlertType.WARNING, "Файл не выбран", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    String fileName = rightPC.getSelectedFileName();

    network.sendDownloadRequest(fileName);
  }
}