package application;

import com.alibaba.fastjson.JSON;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Client;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.ResizeHelper;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;


public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);
    public static final String HOME = System.getProperty("user.dir");
    public static DirectoryChooser directoryChooser = new DirectoryChooser();


    public static HashMap<String, Client> clientMap = new HashMap<>(); //<ID,model.Client>
    public static HashMap<String, Parent> UIMap = new HashMap<>(); //<ID,Parent>
    public static List<Client> clientList;

    public static Stage primaryStage; // **Declare static Stage**

    @FXML
    public StackPane app;

    @FXML
    private AnchorPane side_pane;

    @FXML
    private Parent root;


    @FXML
    private StackPane contentArea;

    @FXML
    private JFXSnackbar snackbar;

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

        //弹出提示结果
        snackbar = new JFXSnackbar(app);
        snackbar.setPrefWidth(300);


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
       /* primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("hello");
            }
        });*/

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
        directoryChooser.setTitle("Auto Server");
        File defaultDirectory = new File(HOME);
        directoryChooser.setInitialDirectory(defaultDirectory);

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