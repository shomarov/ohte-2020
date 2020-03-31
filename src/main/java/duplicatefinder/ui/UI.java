package duplicatefinder.ui;

import duplicatefinder.dao.PhotoFileDao;
import duplicatefinder.domain.DuplicateSet;
import duplicatefinder.domain.MediaFile;
import duplicatefinder.domain.MediaFileService;
import duplicatefinder.domain.ScanResult;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class UI extends Application {
  private MediaFileService mediaFileService;
  private String folderToScan;

  @Override
  public void init() throws Exception {
    PhotoFileDao photoFileDao = new PhotoFileDao();
    mediaFileService = new MediaFileService(photoFileDao);
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("Main");

    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Choose folder");

    Label folderSelected = new Label();

    Button selectScan = new Button("Scan for duplicates");

    VBox vbox = new VBox(selectScan);
    vbox.setAlignment(Pos.CENTER);

    Scene mainScene = new Scene(vbox, 1280, 720);

    Button cancel = new Button("Back");
    Button startScan = new Button("Scan");
    VBox scanPane = new VBox(folderSelected, startScan, cancel);
    scanPane.setAlignment(Pos.CENTER);

    Scene scanScene = new Scene(scanPane, 1280, 720);

    ListView<DuplicateSet> results = new ListView<>();

    results.setCellFactory(
        param ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(DuplicateSet item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(
                                    item.getHumanReadableSizeOfAllFiles() + " in " + item.getSize() + " files");
                        }
                    }
                });

    TableView<MediaFile> files = new TableView<>();

    files.setPlaceholder(new Label("No files to display"));

    TableColumn<MediaFile, String> pathColumn = new TableColumn<>("Path");
    pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
    TableColumn<MediaFile, String> sizeColumn = new TableColumn<>("Size");
    sizeColumn.setCellValueFactory(new PropertyValueFactory<>("humanReadableSize"));

    pathColumn.prefWidthProperty().bind(files.widthProperty().multiply(0.8));
    sizeColumn.prefWidthProperty().bind(files.widthProperty().multiply(0.2));

    files.getColumns().add(pathColumn);
    files.getColumns().add(sizeColumn);

    ContextMenu contextMenu = new ContextMenu();

    MenuItem open = new MenuItem("Open");
    MenuItem delete = new MenuItem("Delete");
    MenuItem details = new MenuItem("Details");

    open.setOnAction(
        e -> {
          getHostServices().showDocument(files.getSelectionModel().getSelectedItem().getPath());
        });

    delete.setOnAction(
        e -> {
          MediaFile selected = files.getSelectionModel().getSelectedItem();
          File file = new File(files.getSelectionModel().getSelectedItem().getPath());
          if (file.exists()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation required");
            confirmation.setHeaderText("Are you sure you want to delete the following files:");
            confirmation.setContentText(files.getSelectionModel().getSelectedItem().getPath());

            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
              boolean deleted = file.delete();
              if (deleted) {
                files.getItems().remove(selected);
              }
            }
          }
        });

    details.setOnAction(e -> {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Not implemented yet");
        info.showAndWait();
    });

    contextMenu.getItems().addAll(open, delete, details);

    files.setContextMenu(contextMenu);

    ScrollPane resultsScrollPane = new ScrollPane(results);
    ScrollPane filesScrollPane = new ScrollPane(files);

    resultsScrollPane.fitToWidthProperty().set(true);
    resultsScrollPane.fitToHeightProperty().set(true);
    filesScrollPane.fitToWidthProperty().set(true);
    filesScrollPane.fitToHeightProperty().set(true);

    SplitPane resultsPane = new SplitPane(resultsScrollPane, filesScrollPane);

    resultsPane.setDividerPosition(0, 0.2);
    resultsPane.setDividerPosition(1, 0.8);

    Scene resultScene = new Scene(resultsPane, 1280, 720);

    results.setOnMouseClicked(
        e -> {
          files.getItems().clear();
          for (MediaFile mediaFile :
              results.getSelectionModel().getSelectedItem().getMediaFiles()) {
            files.getItems().add(mediaFile);
          }
        });

    selectScan.setOnAction(
        e -> {
          File selectedDirectory = directoryChooser.showDialog(stage);

          if (selectedDirectory != null) {
            folderToScan = selectedDirectory.getAbsolutePath();
            folderSelected.setText("Folder selected: \n" + selectedDirectory.getAbsolutePath());
            stage.setScene(scanScene);
          }
        });

    startScan.setOnAction(
        e -> {
          try {
            ScanResult scanResult = mediaFileService.scanFolderForDuplicates(folderToScan);
            for (String hash : scanResult.getResults().keySet()) {
              if (scanResult.getResults().get(hash).size() < 2) {
                continue;
              }
              results.getItems().add(new DuplicateSet(hash, scanResult.getResults().get(hash)));
            }

            stage.setScene(resultScene);
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        });

    cancel.setOnAction(
        e -> {
          stage.setScene(mainScene);
        });

    stage.setScene(mainScene);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
