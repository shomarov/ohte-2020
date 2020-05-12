package duplicatephotofinder.ui;

import duplicatephotofinder.dao.DatabaseUserDao;
import duplicatephotofinder.dao.DirectoryDao;
import duplicatephotofinder.dao.PhotoFileDao;
import duplicatephotofinder.db.Database;
import duplicatephotofinder.domain.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UI extends Application {
    Database db = new Database("jdbc:sqlite:database.db");

    File homeDirectory = new File(System.getenv("HOME"));
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();

    DatabaseUserDao userDao;
    UserService userService;
    DirectoryDao directoryDao;
    DirectoryService directoryService;
    PhotoFileDao photoFileDao;
    PhotoFileService photoFileService;

    Label username;
    Label password;
    TextField usernameField;
    TextField passwordField;
    Label confirmPassword;
    TextField confirmPasswordField;
    Text notification;
    Button button1;
    Button button2;

    GridPane grid;
    Scene loginScene;
    BorderPane rootPane;
    Scene mainScene;
    TabPane tabPane;

    Stage mainStage;

    public UI() throws SQLException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        userDao = new DatabaseUserDao(db);
        userService = new UserService(userDao);

        photoFileDao = new PhotoFileDao();
        directoryDao = new DirectoryDao(photoFileDao);
        directoryService = new DirectoryService(directoryDao);
        photoFileService = new PhotoFileService(directoryService, photoFileDao);

        username = new Label("Username:");
        password = new Label("Password:");
        confirmPassword = new Label("Confirm password:");

        usernameField = new TextField();
        passwordField = new TextField();
        confirmPasswordField = new TextField();

        usernameField.setMaxWidth(200);
        usernameField.setPrefWidth(200);

        notification = new Text();
        notification.setFill(Color.FIREBRICK);

        button1 = new Button("Login");
        button2 = new Button("Register");

        grid = new GridPane();
        loginScene = new Scene(grid, 640, 480);
        rootPane = new BorderPane();
        mainScene = new Scene(rootPane, 1600, 900);
        tabPane = new TabPane();
    }

    @Override
    public void start(Stage stage) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        HBox buttons = new HBox(10);

        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.getChildren().addAll(button1, button2);

        HBox notificationBox = new HBox(10);

        notificationBox.setAlignment(Pos.BOTTOM_RIGHT);
        notificationBox.getChildren().add(notification);

        grid.add(username, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(password, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(confirmPassword, 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(buttons, 1, 4);
        grid.add(notificationBox, 1, 6);

        confirmPassword.setVisible(false);
        confirmPasswordField.setVisible(false);

        button1.setOnAction(e -> login());
        button2.setOnAction(e -> showRegistrationForm());

        mainStage = stage;
        mainStage.setScene(loginScene);
        mainStage.show();

        openFolder(homeDirectory);
    }

    private void showApp() {
        mainStage.close();

        MenuBar menuBar = createMenuBar();

        tabPane.setSide(Side.BOTTOM);

        rootPane.setTop(menuBar);
        rootPane.setCenter(tabPane);

        mainStage.setScene(mainScene);
        mainStage.show();
    }

    private void showRegistrationForm() {
        confirmPassword.setVisible(true);
        confirmPasswordField.setVisible(true);
        button1.setText("Submit");
        button2.setText("Cancel");

        button1.setOnAction(
                e -> register(usernameField.getText(),
                        passwordField.getText(), confirmPasswordField.getText()));

        button2.setOnAction(e -> showLoginForm());
    }

    private void showLoginForm() {
        confirmPassword.setVisible(false);
        confirmPasswordField.setVisible(false);

        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();

        button1.setText("Login");
        button2.setText("Register");

        button1.setOnAction(e -> login());
        button2.setOnAction(e -> showRegistrationForm());
    }

    private void login() {
        if (usernameField.getText().isEmpty()) {
            notify("Username missing");
        } else if (passwordField.getText().isEmpty()) {
            notify("Password missing");
        } else {
            try {
                if (userService.comparePasswords(usernameField.getText(), passwordField.getText())) {
                    usernameField.clear();
                    passwordField.clear();
                    showApp();
                } else {
                    notify("Invalid username or password");
                }
            } catch (SQLException e) {
                showInternalErrorAlert();
            }
        }
    }

    private void logout() {
        mainStage.setScene(loginScene);
        mainStage.centerOnScreen();
    }

    private void register(String username, String password, String passwordConfirmation) {
        if (username.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            notify("Please fill in all fields");
        } else if (!password.equals(passwordConfirmation)) {
            notify("Passwords do not match");
        } else {
            try {
                if (userService.userExists(username)) {
                    notify("Username not available");
                } else {
                    userService.createUser(username, password);
                    showLoginForm();
                }
            } catch (SQLException e) {
                showInternalErrorAlert();
            }
        }
    }

    private void showInternalErrorAlert() {
        Alert error = new Alert(AlertType.ERROR);
        error.setTitle("Error");
        error.setContentText("Database Error");
        error.showAndWait();
    }

    private void notify(String message) {
        notification.setText(message);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> notification.setText("")));
        timeline.play();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu menu1 = new Menu("File");
        MenuItem menuItemOpenFolder = new MenuItem("Open folder...");
        MenuItem menuItemLogout = new MenuItem("Log out");
        MenuItem menuItemQuit = new MenuItem("Quit");

        menu1.getItems().addAll(menuItemOpenFolder, menuItemLogout, menuItemQuit);

        Menu menu2 = new Menu("Help");
        MenuItem menuItemHelp = new MenuItem("Help");
        MenuItem menuItemAbout = new MenuItem("About");

        menu2.getItems().addAll(menuItemHelp, menuItemAbout);

        menuBar.getMenus().addAll(menu1, menu2);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose folder");

        menuItemOpenFolder.setOnAction(
                e -> {
                    File selectedDirectory = directoryChooser.showDialog(mainStage);
                    openFolder(selectedDirectory);
                });

        menuItemLogout.setOnAction(e -> logout());

        menuItemQuit.setOnAction(
                e -> {
                    try {
                        stop();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

        menuItemHelp.setOnAction(
                e -> getHostServices().showDocument("https://github.com/shomarov/ohte-2020/blob/master/documentation/user_manual.md"));

        menuItemAbout.setOnAction(e -> showAbout());

        return menuBar;
    }

    private void showAbout() {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");

        VBox aboutBox = new VBox();

        aboutBox.setAlignment(Pos.CENTER);
        aboutBox.setPadding(new Insets(25, 25, 25, 25));

        Label title = new Label("DuplicatePhotoFinder");
        title.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 18));
        title.setTextAlignment(TextAlignment.CENTER);
        Label version = new Label("version 1.0");
        version.setPadding(new Insets(0, 25, 25, 25));

        Hyperlink link = new Hyperlink("https://github.com/shomarov/ohte-2020");
        link.setBorder(Border.EMPTY);
        link.setPadding(new Insets(0, 25, 25, 25));

        link.setOnAction(e -> getHostServices().showDocument(link.getText()));

        Label license = new Label("This program is licenced under the GNU General Public License v3.0");
        Hyperlink licenseLink = new Hyperlink("https://www.gnu.org/licenses/gpl-3.0.html");

        licenseLink.setOnAction(e -> getHostServices().showDocument(licenseLink.getText()));

        aboutBox.getChildren().addAll(title, version, link, license, licenseLink);

        Scene aboutScene = new Scene(aboutBox, 640, 480);
        aboutStage.setScene(aboutScene);
        aboutStage.show();
    }

    private TreeView<DirectoryInfo> createFoldersView(DirectoryInfo directoryInfoTree) {
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

        ContextMenu contextMenuFolder = new ContextMenu();

        MenuItem contextExpand = new MenuItem("Expand");
        MenuItem contextExpandAll = new MenuItem("Expand all");
        MenuItem contextCollapseAll = new MenuItem("Collapse all");
        MenuItem contextScanRecursively = new MenuItem("Scan folder and subfolders");
        MenuItem contextScanForDuplicates = new MenuItem("Scan current folder for duplicates");
        MenuItem contextScanForDuplicatesRecursively = new MenuItem("Scan folder and subfolder for duplicates");

        contextMenuFolder
                .getItems()
                .addAll(
                        contextExpand,
                        contextExpandAll,
                        contextCollapseAll,
                        contextScanRecursively,
                        contextScanForDuplicates,
                        contextScanForDuplicatesRecursively);

        folders.setContextMenu(contextMenuFolder);

        contextExpand.setOnAction(e ->
                folders.getSelectionModel().getSelectedItem().setExpanded(true));

        contextExpandAll.setOnAction(e ->
                expandAll(folders.getSelectionModel().getSelectedItem()));

        contextCollapseAll.setOnAction(e ->
                collapseAll(folders.getSelectionModel().getSelectedItem()));

        contextScanRecursively.setOnAction(e ->
                scanFolderRecursively(folders.getSelectionModel().getSelectedItem().getValue()));

        contextScanForDuplicates.setOnAction(e ->
                scanForDuplicates(new File(folders
                        .getSelectionModel()
                        .getSelectedItem()
                        .getValue()
                        .getAbsolutePath())));

        contextScanForDuplicatesRecursively.setOnAction(e ->
                scanForDuplicatesRecursively(new File(folders
                        .getSelectionModel()
                        .getSelectedItem()
                        .getValue()
                        .getAbsolutePath())));

        folders.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> populateFileViewFromFolder(newValue));

        return folders;
    }

    private void expandAll(TreeItem<DirectoryInfo> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);

            for (TreeItem<DirectoryInfo> child : item.getChildren()) {
                expandAll(child);
            }
        }
    }

    private void collapseAll(TreeItem<DirectoryInfo> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(false);

            for (TreeItem<DirectoryInfo> child : item.getChildren()) {
                collapseAll(child);
            }
        }
    }

    private TableView<MediaFileInfo> createFilesView(String fileProperty) {
        TableView<MediaFileInfo> files = new TableView<>();
        files.setPlaceholder(new Label("No files to display"));

        TableColumn<MediaFileInfo, String> fileNameColumn = new TableColumn<>("Files");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>(fileProperty));
        TableColumn<MediaFileInfo, String> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("humanReadableSize"));

        fileNameColumn.prefWidthProperty().bind(files.widthProperty().multiply(0.85));
        fileSizeColumn.prefWidthProperty().bind(files.widthProperty().multiply(0.15));

        files.getColumns().add(fileNameColumn);
        files.getColumns().add(fileSizeColumn);

        files.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        files.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> populatePhotoDetails(
                        newValue, getActiveMetadataView(), getActiveImageView()));

        ContextMenu contextMenuFile = new ContextMenu();

        MenuItem contextOpenFile = new MenuItem("Open");
        MenuItem contextSelectAll = new MenuItem("Select all");
        MenuItem contextRenameFile = new MenuItem("Rename");
        MenuItem contextDeleteFile = new MenuItem("Delete file");

        contextOpenFile.setOnAction(
                e -> openFile(files));

        contextSelectAll.setOnAction(event -> files.getSelectionModel().selectAll());

        contextDeleteFile.setOnAction(event -> deleteFiles(files));

        contextMenuFile
                .getItems()
                .addAll(contextOpenFile, contextSelectAll, contextRenameFile,
                        contextDeleteFile);

        files.setContextMenu(contextMenuFile);

        files.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) change -> {
            if (change.getList().size() > 1 || change.getList().size() == 0) {
                contextOpenFile.setDisable(true);
                getActiveMetadataView().setVisible(false);
                getActiveImageView().setVisible(false);
            } else {
                contextOpenFile.setDisable(false);
                contextSelectAll.setDisable(false);
                getActiveMetadataView().setVisible(true);
                getActiveImageView().setVisible(true);
            }
        });

        files.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                getActiveMetadataView().getItems().clear();
            }
        });

        files.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openFile(files);
            }
        });

        return files;
    }

    private TableView<Map> createMetadataView() {
        TableView<Map> metadata = new TableView<>();

        TableColumn<Map, String> tagColumn = new TableColumn<>("Tag");
        TableColumn<Map, String> valueColumn = new TableColumn<>("Value");

        tagColumn.setCellValueFactory(new MapValueFactory<>("Tag"));
        tagColumn.setSortable(false);

        valueColumn.setCellValueFactory(new MapValueFactory<>("Value"));
        valueColumn.setSortable(false);

        metadata.setPlaceholder(new Label("No file selected"));

        metadata.getColumns().add(tagColumn);
        metadata.getColumns().add(valueColumn);

        metadata.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ContextMenu metadataContextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("Copy");

        copy.setOnAction(
                e -> copyMetadataValueToClipboard(metadata.getSelectionModel().getSelectedItem()));

        metadataContextMenu.getItems().add(copy);
        metadata.setContextMenu(metadataContextMenu);

        return metadata;
    }

    private void copyMetadataValueToClipboard(Map map) {
        Map.Entry<String, String> entry = (Map.Entry<String, String>) map.entrySet().iterator().next();

        System.out.println(entry.getValue());

        String value = entry.getValue();

        content.putString(value);
        clipboard.setContent(content);
    }

    private TableView<MediaFileInfo> getActiveFileView() {
        BorderPane borderPane = (BorderPane) tabPane.getSelectionModel().getSelectedItem().getContent();

        return (TableView<MediaFileInfo>) borderPane.getCenter();
    }

    private BorderPane getActiveDetailsView() {
        BorderPane borderPane = (BorderPane) tabPane.getSelectionModel().getSelectedItem().getContent();

        return (BorderPane) borderPane.getRight();
    }

    private TableView<Map> getActiveMetadataView() {
        return (TableView<Map>) getActiveDetailsView().getCenter();
    }

    private ImageView getActiveImageView() {
        return (ImageView) getActiveDetailsView().getBottom();
    }

    private void scanFolderRecursively(DirectoryInfo directory) {
        try {
            List<MediaFileInfo> scanResults = photoFileService.scanFolderForMediaFilesRecursively(directory);
            getActiveFileView().getItems().clear();
            populateFileView(scanResults);
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Error!");
            alert.showAndWait();
        }
    }

    private void addNewTab(String title, BorderPane borderPane) {
        Tab newTab = new Tab(title, borderPane);

        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private ListView<DuplicateSet> createDuplicateResultsView() {
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

        return duplicateResults;
    }

    private void openFolder(File directory) {
        DirectoryInfo directoryInfoTree = directoryService.readDirectoryTree(directory);

        TreeView<DirectoryInfo> folders = createFoldersView(directoryInfoTree);
        TableView<MediaFileInfo> files = createFilesView("filename");

        TreeItem<DirectoryInfo> root = new TreeItem<>();

        populateTreeView(root, directoryInfoTree);

        folders.setRoot(root);

        try {
            directoryInfoTree.setFiles(directoryService.read(directory));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        populateFileView(directoryInfoTree.getFiles());

        createNewTab(directory.getAbsolutePath(), folders, files);
    }

    private void scanForDuplicates(File folderToScan) {
        ListView<DuplicateSet> results = createDuplicateResultsView();
        TableView<MediaFileInfo> files = createFilesView("absolutePath");

        try {
            List<DuplicateSet> duplicates = photoFileService.scanFolderForDuplicates(folderToScan);
            if (duplicates.isEmpty()) {
                showNoDuplicatesFoundNotification();
                return;
            }
            duplicates.forEach(d -> results.getItems().add(d));
        } catch (IOException ex) {
            showInternalErrorAlert();
        }

        results.setOnMouseClicked(
                event -> populateFileViewFromDuplicateResults(results));

        createNewTab("Duplicates: " + folderToScan.getAbsolutePath(), results, files);
    }

    private void scanForDuplicatesRecursively(File folderToScan) {
        ListView<DuplicateSet> results = createDuplicateResultsView();
        TableView<MediaFileInfo> files = createFilesView("absolutePath");

        try {
            List<DuplicateSet> duplicates = photoFileService.scanFolderForDuplicatesRecursively(folderToScan);
            if (duplicates.isEmpty()) {
                showNoDuplicatesFoundNotification();
                return;
            }
            duplicates.forEach(d -> results.getItems().add(d));
        } catch (IOException ex) {
            showInternalErrorAlert();
        }

        results.setOnMouseClicked(
                event -> populateFileViewFromDuplicateResults(results));

        createNewTab("Duplicates: " + folderToScan.getAbsolutePath(), results, files);
    }

    private void showNoDuplicatesFoundNotification() {
        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle("Notification");
        alert.setHeaderText("No duplicates found");

        alert.showAndWait();
    }

    private void createNewTab(String title, Node left, Node center) {
        TableView<Map> metadata = createMetadataView();
        ImageView imageView = new ImageView();

        BorderPane borderPane = new BorderPane();

        BorderPane preview = new BorderPane();
        preview.setCenter(metadata);
        preview.setBottom(imageView);

        preview.setPrefWidth(400);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);

        borderPane.setLeft(left);
        borderPane.setCenter(center);
        borderPane.setRight(preview);

        borderPane.prefHeightProperty().bind(mainScene.heightProperty());
        borderPane.prefWidthProperty().bind(mainScene.widthProperty());

        addNewTab(title, borderPane);
    }

    private void populatePhotoDetails(MediaFileInfo selected, TableView<Map> metadata, ImageView imageView) {
        if (selected == null) {
            return;
        }

        FileInputStream input = null;

        try {
            input = new FileInputStream(selected.getAbsolutePath());
        } catch (FileNotFoundException fileNotFoundException) {
            showFileNotFoundError();
        }

        if (input != null) {
            setPhotoPreview(imageView, input);
        }

        if (selected.getMetadata() != null) {
            getActiveMetadataView().setItems(generateDataInMap(selected));
        } else {
            metadata.setPlaceholder(new Label("No metadata"));
            metadata.getItems().clear();
        }
    }

    private void showFileNotFoundError() {
        Alert fileNotFoundAlert = new Alert(AlertType.ERROR);
        fileNotFoundAlert.setTitle("Error");
        fileNotFoundAlert.setHeaderText("File not found");
    }

    private void setPhotoPreview(ImageView imageView, FileInputStream input) {
        Image image = new Image(input);
        imageView.setImage(image);
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
        ObservableList<Map> allData = FXCollections.observableArrayList();

        Map<String, String> metadata = mediaFileInfo.getMetadata().getMetadata();

        addMapDataToObservableList(allData, metadata);

        if (mediaFileInfo.getMetadata().hasGpsData()) {
            Map<String, String> gpsData = mediaFileInfo.getMetadata().getGpsData();

            addMapDataToObservableList(allData, gpsData);
        }

        return allData;
    }

    private void addMapDataToObservableList(ObservableList<Map> allData, Map<String, String> data) {
        for (String tag : data.keySet()) {
            Map<String, String> dataRow = new HashMap<>();

            String value = data.get(tag);

            dataRow.put("Tag", tag);
            dataRow.put("Value", value);

            allData.add(dataRow);
        }
    }

    private void deleteFiles(TableView<MediaFileInfo> table) {
        List<MediaFileInfo> selected = table.getSelectionModel().getSelectedItems();

        Optional<ButtonType> option = showDeleteConfirmationAlert(selected);

        if (option.isEmpty() || option.get() == ButtonType.CANCEL) {
            return;
        }

        photoFileService.deleteMany(selected);
        table.getItems().removeAll(selected);
    }

    private Optional<ButtonType> showDeleteConfirmationAlert(List<MediaFileInfo> selected) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText("Are you sure you want to delete the following files?");

        String filenames = buildStringListFromFilenames(selected);

        alert.getDialogPane().setContent(new TextArea(filenames));

        return alert.showAndWait();
    }

    private String buildStringListFromFilenames(List<MediaFileInfo> files) {
        StringBuilder sb = new StringBuilder();

        files.forEach(f -> sb.append(f.getAbsolutePath()).append("\n"));

        return sb.toString();
    }

    private void openFile(TableView<MediaFileInfo> table) {
        getHostServices().showDocument(table.getSelectionModel().getSelectedItem().getAbsolutePath());
    }

    private void populateFileViewFromFolder(TreeItem<DirectoryInfo> item) {
        if (item == null) {
            return;
        }

        DirectoryInfo dir = item.getValue();

        try {
            photoFileService.scanFolderForMediaFiles(dir);
        } catch (IOException ex) {
            System.out.println("Error");
        }

        getActiveFileView().getItems().clear();
        populateFileView(dir.getFiles());
    }

    private void populateFileViewFromDuplicateResults(ListView<DuplicateSet> results) {
        if (results == null) {
            return;
        }

        getActiveFileView().getItems().clear();
        populateFileView(results.getSelectionModel().getSelectedItem().getMediaFileInfos());
    }

    private void populateFileView(List<MediaFileInfo> mediaFiles) {
        for (MediaFileInfo file : mediaFiles) {
            getActiveFileView().getItems().add(file);
        }
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
