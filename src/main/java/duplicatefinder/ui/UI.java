package duplicatefinder.ui;

import duplicatefinder.dao.DirectoryDao;
import duplicatefinder.dao.PhotoFileDao;
import duplicatefinder.domain.DirectoryInfo;
import duplicatefinder.domain.DuplicateSet;
import duplicatefinder.domain.MediaFileInfo;
import duplicatefinder.domain.PhotoFileService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UI extends Application {
    PhotoFileDao photoFileDao;
    DirectoryDao directoryDao;
    DirectoryInfo directoryInfoTree;
    DirectoryInfo folderSelected;
    PhotoFileService photoFileService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws IOException {
        photoFileDao = new PhotoFileDao();
        directoryDao = new DirectoryDao();
        directoryDao.setMediaFileDao(photoFileDao);
        photoFileService = new PhotoFileService(photoFileDao);

        // Set Home Folder as initial folder
        directoryInfoTree = directoryDao.readDirectoryTree(new java.io.File(System.getenv("HOME")));
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Main");

        // Menu Bar

        MenuBar menuBar = new MenuBar();

        Menu menu1 = new Menu("File");
        MenuItem menuItemOpenFolder = new MenuItem("Open folder...");
        MenuItem menuItemQuit = new MenuItem("Quit");

        menu1.getItems().addAll(menuItemOpenFolder, menuItemQuit);

        Menu menu2 = new Menu("View");
        MenuItem menuItemViewPhotoPreview = new MenuItem("Photo preview");
        MenuItem menuItemViewMetadata = new MenuItem("Metadata");
        MenuItem menuItemViewStatusBar = new MenuItem("Status bar");

        menu2.getItems().addAll(menuItemViewMetadata, menuItemViewPhotoPreview, menuItemViewStatusBar);

        Menu menu3 = new Menu("Actions");
        MenuItem menuItemScanFolderForFilesRecursively = new MenuItem("Scan folder recursively");
        MenuItem menuItemScanForDuplicates = new MenuItem("Scan for duplicates");
        MenuItem menuItemBatchRenameFiles = new MenuItem("Batch rename files");
        MenuItem menuItemDeleteSelected = new MenuItem("Delete selected files");

        menu3
                .getItems()
                .addAll(
                        menuItemScanFolderForFilesRecursively,
                        menuItemScanForDuplicates,
                        menuItemBatchRenameFiles,
                        menuItemDeleteSelected);

        Menu menu4 = new Menu("Help");
        MenuItem menuItemAbout = new MenuItem("About");

        menu4.getItems().addAll(menuItemAbout);

        menuBar.getMenus().addAll(menu1, menu2, menu3, menu4);


        TreeView<DirectoryInfo> folders = new TreeView<>();
        folders.setCellFactory(
                param ->
                        new TreeCell<>() {
                            @Override
                            protected void updateItem(DirectoryInfo item, boolean empty) {
                                super.updateItem(item, empty);

                                if (empty || item == null) {
                                    setText(null);
                                } else if (item.getAbsolutePath().equalsIgnoreCase(System.getenv("HOME"))) {
                                    setText("Home");
                                } else {
                                    setText(item.getFilename());
                                }
                            }
                        });

        TreeItem<DirectoryInfo> rootItem = new TreeItem<>(directoryInfoTree);
        populateTreeView(rootItem, directoryInfoTree);

        folders.setRoot(rootItem);

        TableView<MediaFileInfo> files = new TableView<>();
        files.setPlaceholder(new Label("No files to display"));

        TableColumn<MediaFileInfo, String> fileNameColumn = new TableColumn<>("Files");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        TableColumn<MediaFileInfo, String> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("humanReadableSize"));

        fileNameColumn.prefWidthProperty().bind(files.widthProperty().multiply(0.85));
        fileSizeColumn.prefWidthProperty().bind(files.widthProperty().multiply(0.15));

        files.getColumns().add(fileNameColumn);
        files.getColumns().add(fileSizeColumn);

        TableView<String> metadata = new TableView<>();
        metadata.setPlaceholder(new Label("No file selected"));

        TableColumn<String, String> metadataKeyColumn = new TableColumn<>("Tag");
        TableColumn<String, String> metadataValueColumn = new TableColumn<>("Value");

        metadata.getColumns().add(metadataKeyColumn);
        metadata.getColumns().add(metadataValueColumn);

        metadata.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        folders.setOnMouseClicked(
                e -> {
                    TreeItem<DirectoryInfo> item = folders.getSelectionModel().getSelectedItem();

                    if (item == null) {
                        return;
                    }

                    DirectoryInfo dir = item.getValue();

                    try {
                        dir.setFiles(directoryDao.read(new java.io.File(dir.getAbsolutePath())));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    files.getItems().clear();
                    for (MediaFileInfo file : dir.getFiles()) {
                        files.getItems().add(file);
                    }
                });

//        files.setOnMouseClicked(e -> {
//            Metadata md = files.getSelectionModel().getSelectedItem().getMetadata();
//
//            if (md == null) {
//                return;
//            }
//
//            ObservableList<String> keys = FXCollections.observableArrayList(md.getMetadata().keySet());
//            ObservableList<String> values = FXCollections.observableArrayList(md.getMetadata().values());
//
//
//        });

        ContextMenu contextMenuFolder = new ContextMenu();

        MenuItem contextExpand = new MenuItem("Expand");
        MenuItem contextExpandAll = new MenuItem("Expand all");
        MenuItem contextScanRecursively = new MenuItem("Scan recursively");
        MenuItem contextScanForDuplicates = new MenuItem("Scan for duplicates");
        MenuItem contextScanForDuplicatesRecursively = new MenuItem("Scan for duplicates recursively");

        contextMenuFolder
                .getItems()
                .addAll(
                        contextExpand,
                        contextExpandAll,
                        contextScanRecursively,
                        contextScanForDuplicates,
                        contextScanForDuplicatesRecursively);

        folders.setContextMenu(contextMenuFolder);

        ContextMenu contextMenuFile = new ContextMenu();

        MenuItem contextOpenFile = new MenuItem("Open");
        MenuItem contextRenameFile = new MenuItem("Rename");
        MenuItem contextDeleteFile = new MenuItem("Delete");
        MenuItem contextProperties = new MenuItem("Properties");

        contextOpenFile.setOnAction(
                e -> {
                    openFile(files.getSelectionModel().getSelectedItem().getAbsolutePath());
                });

        contextMenuFile
                .getItems()
                .addAll(contextOpenFile, contextRenameFile, contextDeleteFile, contextProperties);

        files.setContextMenu(contextMenuFile);

        files.setOnMouseClicked(
                e -> {
                    if (e.getClickCount() == 2) {
                        getHostServices()
                                .showDocument(files.getSelectionModel().getSelectedItem().getAbsolutePath());
                    }
                });

        contextScanForDuplicatesRecursively.setOnAction(
                e -> {
                    Stage duplicatesScanStage = new Stage();
                    duplicatesScanStage.setTitle("Duplicate Scan");

                    ListView<DuplicateSet> duplicateResults = new ListView<>();

                    duplicateResults.setCellFactory(
                            param ->
                                    new ListCell<>() {
                                        @Override
                                        protected void updateItem(DuplicateSet item, boolean empty) {
                                            super.updateItem(item, empty);

                                            if (empty || item == null) {
                                                setText(null);
                                            } else {
                                                setText(
                                                        item.getHumanReadableSizeOfAllFiles()
                                                                + " in "
                                                                + item.getSize()
                                                                + " files");
                                            }
                                        }
                                    });

                    TableView<MediaFileInfo> duplicateFiles = new TableView<>();
                    duplicateFiles.setPlaceholder(new Label("No files to display"));

                    TableColumn<MediaFileInfo, String> filePathColumn = new TableColumn<>("Files");
                    filePathColumn.setCellValueFactory(new PropertyValueFactory<>("absolutePath"));

                    duplicateFiles.getColumns().add(filePathColumn);
                    duplicateFiles.getColumns().add(fileSizeColumn);

                    filePathColumn.prefWidthProperty().bind(duplicateFiles.widthProperty().multiply(0.8));
                    fileSizeColumn.prefWidthProperty().bind(duplicateFiles.widthProperty().multiply(0.2));

                    ContextMenu contextMenuDuplicates = new ContextMenu();

                    MenuItem contextDuplicatesOpenFile = new MenuItem("Open");
                    MenuItem contextDuplicatesRenameFile = new MenuItem("Rename");
                    MenuItem contextDuplicatesDeleteFile = new MenuItem("Delete");
                    MenuItem contextDuplicatesProperties = new MenuItem("Properties");

                    contextMenuDuplicates
                            .getItems()
                            .addAll(
                                    contextDuplicatesOpenFile,
                                    contextDuplicatesRenameFile,
                                    contextDuplicatesDeleteFile,
                                    contextDuplicatesProperties);

                    contextDuplicatesOpenFile.setOnAction(
                            event -> {
                                openFile(duplicateFiles.getSelectionModel().getSelectedItem().getAbsolutePath());
                            });

                    duplicateFiles.setContextMenu(contextMenuDuplicates);

                    java.io.File folderToScan =
                            new java.io.File(folders.getSelectionModel().getSelectedItem().getValue().getAbsolutePath());

                    try {
                        List<DuplicateSet> duplicates = photoFileService.scanFolderForDuplicates(folderToScan);
                        duplicates.forEach(d -> duplicateResults.getItems().add(d));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }



                    duplicateResults.setOnMouseClicked(
                            event -> {
                                if (duplicateResults.getSelectionModel().getSelectedItem() == null) {
                                    return;
                                }
                                duplicateFiles.getItems().clear();
                                for (MediaFileInfo file :
                                        duplicateResults.getSelectionModel().getSelectedItem().getMediaFileInfos()) {
                                    duplicateFiles.getItems().add(file);
                                }
                            });

                    SplitPane resultsPane =
                            new SplitPane(duplicateResults, duplicateFiles);

                    resultsPane.setDividerPosition(0, 0.2);
                    resultsPane.setDividerPosition(1, 0.8);

                    Scene duplicatesScanScene = new Scene(resultsPane, 1280, 720);

                    duplicatesScanStage.setScene(duplicatesScanScene);
                    duplicatesScanStage.show();
                });

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose folder");

        menuItemOpenFolder.setOnAction(
                e -> {
                    java.io.File selectedDirectory = directoryChooser.showDialog(stage);
                    if (selectedDirectory == null) return;

                    TreeItem<DirectoryInfo> newRoot = new TreeItem<>();

                    directoryInfoTree = directoryDao.readDirectoryTree(selectedDirectory);

                    populateTreeView(newRoot, directoryInfoTree);

                    folders.setRoot(newRoot);

                    folderSelected = directoryInfoTree;

                    try {
                        folderSelected.setFiles(directoryDao.read(selectedDirectory));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    for (MediaFileInfo f : folderSelected.getFiles()) {
                        files.getItems().add(f);
                        System.out.println(f);
                    }
                });

        menuItemQuit.setOnAction(
                e -> {
                    Platform.exit();
                });

        //    ImageView imageView = new ImageView();

        HBox hbox = new HBox(folders, files, metadata);
        VBox vbox = new VBox(menuBar, hbox);

        Scene mainScene = new Scene(vbox, 1280, 720);

        // Clear Selection in table view on clicking on empty rows in javafx
        ObjectProperty<TableRow<MediaFileInfo>> lastSelectedRow = new SimpleObjectProperty<>();

        files.setRowFactory(
                tableView -> {
                    TableRow<MediaFileInfo> row = new TableRow<>();

                    row.selectedProperty()
                            .addListener(
                                    (obs, wasSelected, isNowSelected) -> {
                                        if (isNowSelected) {
                                            lastSelectedRow.set(row);
                                        }
                                    });
                    return row;
                });

        mainScene.addEventFilter(
                MouseEvent.MOUSE_CLICKED,
                e -> {
                    if (lastSelectedRow.get() != null) {
                        Bounds boundsOfSelectedRow =
                                lastSelectedRow.get().localToScene(lastSelectedRow.get().getLayoutBounds());
                        if (!boundsOfSelectedRow.contains(e.getSceneX(), e.getSceneY())) {
                            files.getSelectionModel().clearSelection();
                        }
                    }
                });

        BorderPane borderPane = new BorderPane();

        Scene borderScene = new Scene(borderPane, 1280, 720);

        borderPane.prefHeightProperty().bind(borderScene.heightProperty());
        borderPane.prefWidthProperty().bind(borderScene.widthProperty());

        borderPane.setTop(menuBar);
        borderPane.setLeft(folders);
        borderPane.setCenter(files);
        borderPane.setRight(metadata);
        borderPane.setBottom(new Label("Status bar"));

        stage.setScene(borderScene);
        stage.show();
    }

    private void populateTreeView(TreeItem<DirectoryInfo> treeItem, DirectoryInfo directoryInfo) {
        treeItem.setValue(directoryInfo);

        if (directoryInfo.getFolders().isEmpty()) {
            return;
        }

        for (DirectoryInfo dir : directoryInfo.getFolders()) {
            TreeItem<DirectoryInfo> newTreeItem = new TreeItem<>(dir);
            treeItem.getChildren().add(newTreeItem);
            populateTreeView(newTreeItem, dir);
        }
    }

    private void openFile(String absolutePath) {
        getHostServices().showDocument(absolutePath);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
