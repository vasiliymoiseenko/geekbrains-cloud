package ru.geekbrains.cloud.client.javafx;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ru.geekbrains.cloud.common.type.FileInfo;

public class PanelController implements Initializable {

  @FXML
  TextField pathField;
  @FXML
  TableView<FileInfo> filesTable;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
    fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
    fileTypeColumn.setPrefWidth(25);

    TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
    fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
    fileNameColumn.setPrefWidth(250);

    TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
    fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
    fileSizeColumn.setPrefWidth(100);
    fileSizeColumn.setCellFactory(column -> new TableCell<FileInfo, Long>() {
      @Override
      protected void updateItem(Long item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText("");
          setStyle("");
        } else {
          String text = String.format("%,d b", item);
          if (item == -1L) {
            text = "";
          }
          setText(text);
        }
      }
    });

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
    fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
    fileDateColumn.setPrefWidth(150);

    filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
    filesTable.getSortOrder().add(fileTypeColumn);

    filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (event.getClickCount() == 2) {
          Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
          if (Files.isDirectory(path)) {
            updateList(path);
          }
        }
      }
    });
  }

  public void updateList(Path path) {
    try {
      pathField.setText(path.normalize().toAbsolutePath().toString());
      filesTable.getItems().clear();
      filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
      filesTable.sort();
    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
      alert.showAndWait();
    }
  }

  public void updateList() {
    try {
      filesTable.getItems().clear();
      filesTable.getItems().addAll(Files.list(Paths.get(pathField.getText())).map(FileInfo::new).collect(Collectors.toList()));
      filesTable.sort();
    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
      alert.showAndWait();
    }
  }

  public void updateList(List<FileInfo> list) {
    filesTable.getItems().clear();
    filesTable.getItems().addAll(list);
    filesTable.sort();
  }

/*  public void pathUpAction(ActionEvent event) {
    Path upperPath = Paths.get(pathField.getText()).getParent();
    if (upperPath != null) {
      updateList(upperPath);
    }
  }*/

  public String getSelectedFileName() {
    if (!filesTable.isFocused()) {
      return null;
    }
    if (filesTable.getSelectionModel().getSelectedItem() == null) {
      return null;
    }
    return filesTable.getSelectionModel().getSelectedItem().getFileName();
  }

  public String getCurrentPath() {
    return pathField.getText();
  }
}
