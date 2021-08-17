package application;

import com.alibaba.fastjson.JSON;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Client;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.ResizeHelper;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;


public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static MyContentMenu contextMenu;
    public static HashMap<String, Client> clientMap = new HashMap<>(); //<ID,model.Client>
    public static HashMap<String, Parent> UIMap = new HashMap<>(); //<ID,Parent>
    public static List<Client> clientList;

    public static Stage primaryStage; // **Declare static Stage**

    @FXML
    private StackPane app;

    @FXML
    private AnchorPane side_pane;

    @FXML
    private Parent root;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXDialog dialog;

    @FXML
    private JFXTextField input_1;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private StackPane contentArea;

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private void initialize() {
        //*****
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException exception) {
            logger.warn(exception.getMessage(), exception);
        }


        //检测客户端状态
        new Thread(
                () -> {
                    try {
                        while (true) {
                            logger.info("check client state");
                            clientList.forEach(client -> {
                                client.queryStatus();
                            });
                            Thread.sleep(30000);
                        }
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
        ).start();

        app.getChildren().remove(dialog);

        cancelButton.setOnAction(action -> dialog.close());
        acceptButton.setOnAction(action -> {
            String path = input_1.getText();
            dialog.close();
        });

        //右键菜单
        contextMenu = new MyContentMenu();
        MenuItem status = new MenuItem("eap status");
        MenuItem close = new MenuItem("close eap");
        MenuItem start = new MenuItem("start eap");
        MenuItem update = new MenuItem("update eap");
        MenuItem closeProcess = new MenuItem("close process");
        MenuItem startProcess = new MenuItem("start process");
        MenuItem delete = new MenuItem("delete file");
        MenuItem execute = new MenuItem("execute script");
        contextMenu.getItems().addAll(status, close, start, update, closeProcess, startProcess, delete, execute);

        status.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).queryStatus();
        });

        close.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).close();
        });

        start.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).start();
        });

        update.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).update();
        });

        closeProcess.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;

            JFXAlert alert = new JFXAlert((Stage) app.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("Modal Dialog using JFXAlert"));
            layout.setBody(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit,"
                    + " sed do eiusmod tempor incididunt ut labore et dolore magna"
                    + " aliqua. Utenim ad minim veniam, quis nostrud exercitation"
                    + " ullamco laboris nisi ut aliquip ex ea commodo consequat."));
            JFXButton closeButton = new JFXButton("ACCEPT");
            closeButton.getStyleClass().add("dialog-accept");
            closeButton.setOnAction(event1 -> alert.hideWithAnimation());
            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.show();

            clientMap.get(source.getId()).close();
        });

        startProcess.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;

            clientMap.get(source.getId()).startProcess();
        });


        delete.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).deleteFile();
        });

        execute.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;

            dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);

            dialog.show(app);


            clientMap.get(source.getId()).executeScript();
        });

        side_pane.setOnMousePressed(event -> {
            xOffset = event.getScreenX();
            yOffset = event.getScreenY();
        });

        side_pane.setOnMouseDragged(event -> {
            side_pane.getScene().getWindow().setX(event.getScreenX() - xOffset);
            side_pane.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });
    }


    @Override
    public void start(Stage stage) throws Exception {
        //setPrimaryStage(stage);
        primaryStage = stage;

        root = FXMLLoader.load(getClass().getResource("/fxml/app.fxml"));

        double width = 1200;
        double height = 800;

        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            //width = bounds.getWidth() / 2.5;
            //height = bounds.getHeight() / 1.35;
        } catch (Exception e) {
        }

        Scene scene = new Scene(root, width, height);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
                JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
                App.class.getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                App.class.getResource("/css/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(1100);

        ResizeHelper.addResizeListener(stage);

        stage.show();
    }

    private void setPrimaryStage(Stage stage) {
        App.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return App.primaryStage;
    }

    public void home(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void page2(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/fxml/page2.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }


    static {
        //读取json配置文件
        try {

            //String clients = IOUtils.toString(App.class.getResource("clients.json"), "utf-8");
            String clients = IOUtils.toString(App.class.getClassLoader().getResource("clients.json"), "utf-8");
            List<Client> clients1 = JSON.parseArray(clients, Client.class);
            clientList = clients1;
            clients1.forEach(client -> {
                clientMap.put(client.getId(), client);
            });
            //System.out.println(clients1);

            //获取本机ip
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                String ip = socket.getLocalAddress().getHostAddress();
                logger.info("IP:" + ip);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}