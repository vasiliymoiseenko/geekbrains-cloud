package ru.geekbrains.cloud.client.javafx.actions;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.list.FileInfo;

public class CreateTableView {

  public static void action(Controller controller) {
    TableView<FileInfo> filesTable = controller.getFilesTable();

    TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
    fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
    fileTypeColumn.setPrefWidth(25);
    fileTypeColumn.setResizable(false);

    TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
    fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
    fileNameColumn.setPrefWidth(350);

    TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
    fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
    fileSizeColumn.setPrefWidth(125);
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
    TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Last modified");
    fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
    fileDateColumn.setPrefWidth(160);
    fileDateColumn.setResizable(false);

    filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
    filesTable.getSortOrder().add(fileTypeColumn);
  }
}
