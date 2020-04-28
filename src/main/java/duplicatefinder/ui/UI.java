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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void init() {
        photoFileDao = new PhotoFileDao();
        directoryDao = new DirectoryDao(photoFileDao);
        photoFileService = new PhotoFileService(photoFileDao);

        // Set Home Folder as initial folder
        directoryInfoTree = directoryDao.readDirectoryTree(new File(System.getenv("HOME")));
//        directoryInfoTree = directoryDao.readDirectoryTree(new File("src/"));
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
        MenuItem menuItemScanForDuplicates = new MenuItem("Scan folder for duplicates");
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

        files.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        VBox singleImageView = new VBox();

        ImageView imageView = new ImageView();

        TableColumn<Map, String> tagColumn = new TableColumn<>("Tag");
        TableColumn<Map, String> valueColumn = new TableColumn<>("Value");

        tagColumn.setCellValueFactory(new MapValueFactory("Tag"));
        tagColumn.setSortable(false);

        valueColumn.setCellValueFactory(new MapValueFactory("Value"));
        valueColumn.setSortable(false);

        TableView<Map> metadata = new TableView<>();
        metadata.setPlaceholder(new Label("No file selected"));

        metadata.getColumns().add(tagColumn);
        metadata.getColumns().add(valueColumn);

        metadata.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        folders.setOnMouseClicked(
                e -> {
                    TreeItem<DirectoryInfo> item = folders.getSelectionModel().getSelectedItem();

                    if (item == null) {
                        return;
                    }

                    DirectoryInfo dir = item.getValue();

                    try {
                        dir.setFiles(directoryDao.read(new File(dir.getAbsolutePath())));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    files.getItems().clear();
                    for (MediaFileInfo file : dir.getFiles()) {
                        files.getItems().add(file);
                    }
                });

//        files.setOnMouseClicked(e -> {
//            metadata.getItems().clear();
//
//            Metadata md = files.getSelectionModel().getSelectedItem().getMetadata();
//
//            if (md == null) {
//                return;
//            }
//
//            metadata.getItems().add(md);
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

        files.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) change -> {
            if (change.getList().size() > 1 || change.getList().size() == 0) {
                contextOpenFile.setDisable(true);
                contextProperties.setDisable(true);
                imageView.setVisible(false);
            } else {
                contextOpenFile.setDisable(false);
                contextProperties.setDisable(false);
                imageView.setVisible(true);
            }
        });

        contextOpenFile.setOnAction(
                e -> openFile(files));

        contextDeleteFile.setOnAction(event -> deleteFiles(files));

        contextMenuFile
                .getItems()
                .addAll(contextOpenFile, contextRenameFile, contextDeleteFile, contextProperties);

        files.setContextMenu(contextMenuFile);

        files.setOnMouseClicked(
                e -> {
                    MediaFileInfo selected = files.getSelectionModel().getSelectedItem();
                    if (selected == null) {
                        return;
                    }

                    FileInputStream input = null;

                    try {
                        input = new FileInputStream(files.getSelectionModel().getSelectedItem().getAbsolutePath());
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                    if (input != null) {
                        Image image = new Image(input);
                        imageView.setImage(image);

                        if (selected.getMetadata() != null) {
                            metadata.setItems(generateDataInMap(selected));
                        } else {
                            metadata.setPlaceholder(new Label("No metadata"));
                        }
                    }

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
                            event -> openFile(duplicateFiles));

                    contextDuplicatesDeleteFile.setOnAction(event -> deleteFiles(duplicateFiles));

                    duplicateFiles.setContextMenu(contextMenuDuplicates);

                    File folderToScan =
                            new File(folders.getSelectionModel().getSelectedItem().getValue().getAbsolutePath());

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
                    File selectedDirectory = directoryChooser.showDialog(stage);
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
                    }
                });

        menuItemQuit.setOnAction(
                e -> Platform.exit());

        singleImageView.getChildren().addAll(metadata, imageView);

        HBox hbox = new HBox(folders, files, singleImageView);
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

        imageView.setPreserveRatio(true);

        BorderPane preview = new BorderPane();
        preview.setCenter(metadata);
        preview.setBottom(imageView);

        imageView.setPreserveRatio(true);

        imageView.setFitWidth(248);
        preview.setPrefWidth(120);

        borderPane.setTop(menuBar);
        borderPane.setLeft(folders);
        borderPane.setCenter(files);
        borderPane.setRight(preview);
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

    private ObservableList<Map> generateDataInMap(MediaFileInfo mediaFileInfo) {
        Map<String, String> metadata = mediaFileInfo.getMetadata().getMetadata();
        ObservableList<Map> allData = FXCollections.observableArrayList();

        for (String tag : metadata.keySet()) {
            Map<String, String> dataRow = new HashMap<>();

            String value = metadata.get(tag);

            dataRow.put("Tag", tag);
            dataRow.put("Value", value);

            allData.add(dataRow);
        }

        if (mediaFileInfo.getMetadata().hasGpsData()) {
            Map<String, String> gpsData = mediaFileInfo.getMetadata().getGpsData();
            for (String tag : gpsData.keySet()) {
                Map<String, String> dataRow = new HashMap<>();

                String value = gpsData.get(tag);

                dataRow.put("Tag", tag);
                dataRow.put("Value", value);

                allData.add(dataRow);
            }

        }

        return allData;
    }

    private void deleteFiles(TableView<MediaFileInfo> table) {
        List<MediaFileInfo> selected = table.getSelectionModel().getSelectedItems();

        for (MediaFileInfo mf : selected) {
            photoFileDao.delete(mf);
        }

        table.getItems().removeAll(selected);
    }

    private void openFile(TableView<MediaFileInfo> table) {
        getHostServices().showDocument(table.getSelectionModel().getSelectedItem().getAbsolutePath());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
