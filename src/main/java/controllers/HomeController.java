package controllers;

import application.App;
import enums.StatePicMap;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import model.Client;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController extends App implements Initializable {
    public static final Logger logger = LogManager.getLogger(HomeController.class);

    @FXML
    private FlowPane flowPane;

    @FXML
    private Button minimum;

    @FXML
    private Button maximum;

    @FXML
    private Button exit;

    @FXML
    private Button notify;

    @FXML
    private Button distribute_file;

    private List<Client> selected = new ArrayList<>();
    private HashMap<String, Client> selected2 = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("生成客户端UI");

        //最小化
        minimum.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                //primaryStage.setIconified(true);
                //App.getPrimaryStage().setIconified(true);
                primaryStage.setIconified(true);
            }
        });

        //最大化
        maximum.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();

                primaryStage.setX(bounds.getMinX());
                primaryStage.setY(bounds.getMinY());
                primaryStage.setWidth(bounds.getWidth());
                primaryStage.setHeight(bounds.getHeight());
            }
        });

        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                Platform.exit();
                System.exit(0);
            }
        });

        notify.s
        clientList.forEach(client -> {
            generateClient(client);
        });
    }

    public void generateClient(Client client) {
        Pane pane = new Pane();
        pane.setId(client.getId() == null ? "" : client.getId());
        pane.setPrefSize(150, 150);
        pane.setStyle("-fx-background-radius:10;-fx-border-radius: 10;" +
                "-fx-alignment: center;-fx-background-insets:5;-fx-border-insets:5");
        pane.setOnMouseEntered(mouseEvent -> {
            pane.setStyle("-fx-background-color: #FE774D");
        });
        pane.setOnMouseExited(mouseEvent -> {
            pane.setStyle("-fx-background-color: #FFFFFF");
        });

        VBox vBox = new VBox();
        vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                "-fx-background-radius:5;-fx-border-radius: 5;");
        //点击
        vBox.setOnMousePressed(mouseEvent -> {
            /*if (mouseEvent.isPrimaryButtonDown()) {
                vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                        "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                        "-fx-background-radius:5;-fx-border-radius: 5;-fx-background-color: #d0d0d0");
            }*/
        });
        pane.getChildren().add(vBox);

        //选择
        StackPane select_pane = new StackPane();
        Button select_btn = new Button();
        select_btn.setPrefSize(16, 16);
        Image select_img = new Image("/images/select.png", true);
        ImageView select_i = new ImageView(select_img);
        select_i.setFitHeight(16);
        select_i.setPreserveRatio(true);
        select_btn.setGraphic(select_i);
        select_pane.getChildren().add(select_btn);
        vBox.getChildren().add(select_pane);

        //图片
        StackPane pic = new StackPane();
        pic.setId("pic");
        pic.setStyle("-fx-alignment: center;-fx-pref-width: 150;-fx-pref-height: 100");
        //Image image = new Image("client_online2.png", true);
        Image image = new Image(StatePicMap.getPicByState(client.getState()), true);
        ImageView imageView = new ImageView(image);
        pic.getChildren().add(imageView);
        vBox.getChildren().add(pic);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        vBox.getChildren().add(separator);

        //名字
        Label name = new Label();
        name.setText("客户端:" + (client.getName() == null ? "" : client.getName()));
        name.setAlignment(Pos.CENTER);
        name.setStyle("-fx-background-color: transparent;-fx-min-width: 20");
        name.setTextFill(Paint.valueOf("black"));
        vBox.getChildren().add(name);

        //IP
        Label ip = new Label();
        ip.setText("IP:" + (client.getIp() == null ? "" : client.getIp()));
        ip.setAlignment(Pos.CENTER);
        ip.setStyle("-fx-background-color: transparent;-fx-min-width: 20");
        ip.setTextFill(Paint.valueOf("black"));
        vBox.getChildren().add(ip);

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(pane, event.getScreenX(), event.getScreenY(), (Parent) event.getSource());
            }
        });

        flowPane.getChildren().add(pane);
        App.UIMap.put(pane.getId(), pane);
    }
}
