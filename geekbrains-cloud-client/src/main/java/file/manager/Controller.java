package file.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class Controller {

  @FXML
  VBox clientPanel;
  @FXML
  VBox serverPanel;

  public void exitAction(ActionEvent actionEvent) {
    Platform.exit();
  }

  public void copyAction(ActionEvent actionEvent) {
    PanelController leftPC = (PanelController) clientPanel.getProperties().get("ctrl");
    PanelController rightPC = (PanelController) serverPanel.getProperties().get("ctrl");

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
      Files.copy(srcPath, dstPath);
      dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
    } catch (IOException e) {
      Alert alert  = new Alert(AlertType.WARNING, "Не удалось скопировать файл", ButtonType.OK);
      alert.showAndWait();
    }
  }
}