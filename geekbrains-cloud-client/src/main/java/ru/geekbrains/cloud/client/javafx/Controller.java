package ru.geekbrains.cloud.client.javafx;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.actions.CreateTableView;
import ru.geekbrains.cloud.client.javafx.actions.Delete;
import ru.geekbrains.cloud.client.javafx.actions.Download;
import ru.geekbrains.cloud.client.javafx.actions.MakeDirectory;
import ru.geekbrains.cloud.client.javafx.actions.ShowProgressBar;
import ru.geekbrains.cloud.client.javafx.actions.Upload;
import ru.geekbrains.cloud.client.netty.NettyClient;
import ru.geekbrains.cloud.common.constants.Const;
import ru.geekbrains.cloud.common.messages.auth.AuthRequest;
import ru.geekbrains.cloud.common.messages.list.FileInfo.FileType;
import ru.geekbrains.cloud.common.messages.list.ListRequest;
import ru.geekbrains.cloud.common.messages.reg.RegRequest;
import ru.geekbrains.cloud.common.messages.list.FileInfo;

@Log4j2
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
  @Getter
  TextField pathField;
  @FXML
  @Getter
  TableView<FileInfo> filesTable;

  @Getter
  @Setter
  String login;
  @Getter
  private final FileChooser fileChooser = new FileChooser();
  @Getter
  private final Label statusProgressBar = new Label();
  @Getter
  private final ProgressBar progressBar = new ProgressBar();
  @Getter
  private NettyClient nettyClient;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createRepositoryFolder();
    changeStageToAuth();
    CreateTableView.action(this);

    filesTable.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        String fileName = filesTable.getSelectionModel().getSelectedItem().getFileName();
        String path = pathField.getText();
        if (filesTable.getSelectionModel().getSelectedItem().getType() == FileType.DIRECTORY) {
          nettyClient.send(new ListRequest(Paths.get(path, fileName).toString()));
        }
      }
    });
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

  public void changeStageToReg() {
    regLogin.clear();
    regPassword.clear();
    regPasswordRep.clear();
    regMessage.setVisible(false);

    authPane.setVisible(false);
    regPane.setVisible(true);
    cloudPane.setVisible(false);
  }

  public void changeStageToAuth() {
    authLogin.clear();
    authPassword.clear();
    authMessage.setVisible(false);

    authPane.setVisible(true);
    regPane.setVisible(false);
    cloudPane.setVisible(false);
  }

  public void changeStageToCloud() {
    authPane.setVisible(false);
    regPane.setVisible(false);
    cloudPane.setVisible(true);

    nettyClient.send(new ListRequest(login));
  }

  public void showRegMessage(String reason, Color color) {
    regMessage.setTextFill(color);
    regMessage.setText(reason);
    regMessage.setVisible(true);
  }

  public void showAuthMessage(String reason, Color color) {
    authMessage.setTextFill(color);
    authMessage.setText(reason);
    authMessage.setVisible(true);
  }

  public void updateList(List<FileInfo> list) {
    filesTable.getItems().clear();
    filesTable.getItems().addAll(list);
    filesTable.sort();
  }

  public void pathUpAction() {
    String path = pathField.getText();
    if (!path.equals(login)) {
      nettyClient.send(new ListRequest(Paths.get(path).getParent().toString()));
    }
  }

  public void exitAction() {
    Platform.exit();
  }

  public void uploadAction() {
    Upload.action(this);
  }

  public void downloadAction() {
    Download.action(this);
  }

  public void updatePathField(String path) {
    pathField.setText(path);
  }

  public void deleteAction() {
    Delete.action(this);
  }

  public void makeDirectory() {
    MakeDirectory.action(this);
  }

  public void showProgressBar() {
    ShowProgressBar.action(this);
  }

  public void changeProgressBar(double progress) {
    progressBar.setProgress(progress);
  }

  public void setStatusProgressBar(String status) {
    statusProgressBar.setText(status);
  }

  private void createRepositoryFolder() {
    File folder = new File(Const.CLIENT_REP);
    if (!folder.exists()) {
      folder.mkdir();
      System.out.println("Folder " + folder.getName() + " created");
    }
  }

  private void connection() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    nettyClient = new NettyClient(this, countDownLatch);
    new Thread(nettyClient).start();
    countDownLatch.await();
  }
}