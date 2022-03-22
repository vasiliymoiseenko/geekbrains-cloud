package ru.geekbrains.cloud.client.javafx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ru.geekbrains.cloud.client.netty.NettyClient;
import ru.geekbrains.cloud.common.type.FileInfo;

public class Controller implements Initializable {

  @FXML
  GridPane authPane;
  @FXML
  TextField authLogin;
  @FXML
  PasswordField authPassword;
  @FXML
  Label authMessage;

  @FXML
  GridPane regPane;
  @FXML
  TextField regLogin;
  @FXML
  PasswordField regPassword;
  @FXML
  PasswordField regPasswordRep;
  @FXML
  Label regMessage;


  @FXML
  VBox cloudPane;
  @FXML
  TextField pathField;
  @FXML
  TableView<FileInfo> filesTable;

  NettyClient nettyClient;

  @Override
  public void initialize(URL location, ResourceBundle resources){
    changeStageToAuth();
    createRepositoryFolder();

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
            //updateList(path);
          }
        }
      }
    });
  }

  public void updateList(List<FileInfo> list) {
    filesTable.getItems().clear();
    filesTable.getItems().addAll(list);
    filesTable.sort();
  }

  private void createRepositoryFolder() {
    File folder = new File("client_repository");
    if (!folder.exists()) {
      folder.mkdir();
      System.out.println("Folder " + folder.getName() + " created");
    }
  }

  public void exitAction(ActionEvent actionEvent) {
    Platform.exit();
  }

  public void updateFileListServer() {
    nettyClient.updateFileList();
  }

  public void changeStageToReg() {
    regLogin.clear();
    regPassword.clear();
    regPasswordRep.clear();
    regMessage.setVisible(false);

    authPane.setVisible(false);
    regPane.setVisible(true);
    cloudPane.setVisible(false);
  }

  public void enterCloud() {
    if (nettyClient == null) {
      nettyClient = new NettyClient(this);
      new Thread(nettyClient).start();
    }
  }

  public void register() {
  }

  public void changeStageToAuth() {
    authLogin.clear();
    authPassword.clear();
    authMessage.setVisible(false);

    authPane.setVisible(true);
    regPane.setVisible(false);
    cloudPane.setVisible(false);
  }
}