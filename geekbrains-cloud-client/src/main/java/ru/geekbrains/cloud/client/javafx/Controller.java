package ru.geekbrains.cloud.client.javafx;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.netty.NettyClient;
import ru.geekbrains.cloud.common.messages.auth.AuthRequest;
import ru.geekbrains.cloud.common.messages.file.DeleteRequest;
import ru.geekbrains.cloud.common.messages.file.FileRequest;
import ru.geekbrains.cloud.common.messages.file.MakeDirRequest;
import ru.geekbrains.cloud.common.messages.list.FileInfo.FileType;
import ru.geekbrains.cloud.common.messages.list.ListRequest;
import ru.geekbrains.cloud.common.messages.reg.RegRequest;
import ru.geekbrains.cloud.common.messages.list.FileInfo;
import ru.geekbrains.cloud.common.service.FileService;

@Log4j2
public class Controller implements Initializable {

  NettyClient nettyClient;

  @FXML GridPane authPane;
  @FXML TextField authLogin;
  @FXML PasswordField authPassword;
  @FXML Label authMessage;

  @FXML GridPane regPane;
  @FXML TextField regLogin;
  @FXML PasswordField regPassword;
  @FXML PasswordField regPasswordRep;
  @FXML Label regMessage;

  @FXML VBox cloudPane;
  @FXML TextField pathField;
  @FXML TableView<FileInfo> filesTable;

  @Getter
  @Setter
  String login;
  private FileChooser fileChooser;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fileChooser = new FileChooser();

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
          String fileName = filesTable.getSelectionModel().getSelectedItem().getFileName();
          String path = pathField.getText();
          if (filesTable.getSelectionModel().getSelectedItem().getType() == FileType.DIRECTORY) {
            nettyClient.send(new ListRequest(Paths.get(path, fileName).toString()));
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

 /* public void updateFileListServer() {
    nettyClient.updateFileList();
  }*/

  public void changeStageToReg() {
    regLogin.clear();
    regPassword.clear();
    regPasswordRep.clear();
    regMessage.setVisible(false);

    authPane.setVisible(false);
    regPane.setVisible(true);
    cloudPane.setVisible(false);
  }

  public void enterCloud() throws InterruptedException {
    connection();

    if (authLogin.getText().isEmpty() || authPassword.getText().isEmpty()) {
      authMessage.setText("Enter login and password");
      authMessage.setVisible(true);
    } else {
      log.info("Trying to log in: " + authLogin.getText());
      nettyClient.send(new AuthRequest(authLogin.getText(), authPassword.getText()));
    }
  }

  public void register() throws InterruptedException {
    connection();

    if (regLogin.getText().isEmpty() || regPassword.getText().isEmpty() || regPasswordRep.getText().isEmpty()) {
      regMessage.setTextFill(Color.RED);
      regMessage.setText("Enter login, password and name");
      regMessage.setVisible(true);
    } else if (!regPassword.getText().equals(regPasswordRep.getText())) {
      regMessage.setTextFill(Color.RED);
      regMessage.setText("Passwords do not match");
      regMessage.setVisible(true);
    } else {
      log.info("Trying to register a new user: " + regLogin.getText());
      nettyClient.send(new RegRequest(regLogin.getText(), regPassword.getText(), 2));
    }
  }

  private void connection() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    nettyClient = new NettyClient(this, countDownLatch);
    new Thread(nettyClient).start();
    countDownLatch.await();
  }

  public void changeStageToAuth() {
    authLogin.clear();
    authPassword.clear();
    authMessage.setVisible(false);

    authPane.setVisible(true);
    regPane.setVisible(false);
    cloudPane.setVisible(false);
  }

  public void showRegMessage(String reason, Color color) {
    regMessage.setTextFill(color);
    regMessage.setText(reason);
    regMessage.setVisible(true);
  }

  public void changeStageToCloud() {
    authPane.setVisible(false);
    regPane.setVisible(false);
    cloudPane.setVisible(true);

    nettyClient.send(new ListRequest(login));
  }

  public void showAuthMessage(String reason, Color color) {
    authMessage.setTextFill(color);
    authMessage.setText(reason);
    authMessage.setVisible(true);
  }

  public void uploadAction(ActionEvent event) {
    File file = fileChooser.showOpenDialog(ClientApplication.getPrimaryStage());
    if (file != null) {
      log.info("File chosen: " + file.getPath());
      FileService.sendFile(nettyClient.getChannelFuture().channel(), file, pathField.getText());
    }
  }

  public void downloadAction(ActionEvent event) {
    if (filesTable.getSelectionModel().getSelectedItem() == null){
      Alert alert  = new Alert(AlertType.WARNING, "File not selected", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    String fileName = filesTable.getSelectionModel().getSelectedItem().getFileName();
    String path = pathField.getText();

    nettyClient.send(new FileRequest(fileName, path));
    log.info("FileRequest sent: " + Paths.get(path,fileName));
  }

  public void updatePathField(String path) {
    pathField.setText(path);
  }

  public void deleteAction(ActionEvent event) {
    if (filesTable.getSelectionModel().getSelectedItem() == null){
      Alert alert  = new Alert(AlertType.WARNING, "File not selected", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    String fileName = filesTable.getSelectionModel().getSelectedItem().getFileName();
    String path = pathField.getText();

    nettyClient.send(new DeleteRequest(fileName, path));
    log.info("DeleteRequest sent: " + Paths.get(path,fileName));
  }

  public void makeDirectory(ActionEvent event) {
    Label secondLabel = new Label("Enter directory name:");
    
    TextField textField = new TextField();
    textField.setPrefWidth(150);
    
    Button btnCreate = new Button("Create");
    Button btnCancel = new Button("Cancel");
    
    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER);
    hBox.setSpacing(20);
    hBox.getChildren().addAll(btnCreate, btnCancel);

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(20));
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(secondLabel, textField, hBox);

    Scene secondScene = new Scene(vBox, 250, 100);

    // New window (Stage)
    Stage newWindow = new Stage();
    newWindow.setTitle("Second Stage");
    newWindow.setScene(secondScene);

    // Set position of second window, related to primary window.
    newWindow.setX(ClientApplication.getPrimaryStage().getX());
    newWindow.setY(ClientApplication.getPrimaryStage().getY());

    newWindow.show();

    btnCreate.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        String fileName = textField.getText();
        String path = pathField.getText();

        nettyClient.send(new MakeDirRequest(fileName, path));
        log.info("MakeDirRequest sent: " + Paths.get(path, fileName));

        newWindow.close();
      }
    });

    btnCancel.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        newWindow.close();
      }
    });
  }

  public void pathUpAction(ActionEvent event) {
    String path = pathField.getText();
    if (!path.equals(login)) {
      nettyClient.send(new ListRequest(Paths.get(path).getParent().toString()));
    }
  }
}