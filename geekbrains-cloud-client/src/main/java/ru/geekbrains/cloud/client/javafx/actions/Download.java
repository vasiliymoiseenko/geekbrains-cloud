package ru.geekbrains.cloud.client.javafx.actions;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.client.netty.NettyClient;
import ru.geekbrains.cloud.common.constants.Const;
import ru.geekbrains.cloud.common.messages.file.FileRequest;
import ru.geekbrains.cloud.common.messages.list.FileInfo;
import ru.geekbrains.cloud.common.messages.list.FileInfo.FileType;

@Log4j2
public class Download {

  public static void action(Controller controller) {
    TableView<FileInfo> filesTable = controller.getFilesTable();
    TextField pathField = controller.getPathField();
    NettyClient nettyClient = controller.getNettyClient();
    FileInfo fileInfo = filesTable.getSelectionModel().getSelectedItem();

    if (fileInfo == null) {
      Alert alert = new Alert(AlertType.WARNING, "File not selected", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    if (fileInfo.getType() == FileType.DIRECTORY) {
      Alert alert = new Alert(AlertType.WARNING, "Directory selected, select file", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    File file = new File(Paths.get(Const.CLIENT_REP, fileInfo.getFileName()).toString());

    if (file.exists()) {
      Alert alert = new Alert(AlertType.CONFIRMATION, "File already exists, overwrite?");
      Optional<ButtonType> option = alert.showAndWait();

      if (option.get() == ButtonType.OK) {
        String fileName = filesTable.getSelectionModel().getSelectedItem().getFileName();
        String path = pathField.getText();

        controller.showProgressBar();
        nettyClient.send(new FileRequest(fileName, path));
        log.info("FileRequest sent: " + Paths.get(path, fileName));
      }
    } else {
      String fileName = filesTable.getSelectionModel().getSelectedItem().getFileName();
      String path = pathField.getText();

      controller.showProgressBar();
      nettyClient.send(new FileRequest(fileName, path));
      log.info("FileRequest sent: " + Paths.get(path, fileName));
    }
  }
}
