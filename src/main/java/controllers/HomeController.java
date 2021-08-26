package controllers;

import application.App;
import application.MyContentMenu;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.jfoenix.controls.*;
import dto.Notify;
import dto.Status;
import enums.StatePicMap;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Client;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HomeController extends App implements Initializable {
    public static final Logger logger = LogManager.getLogger(HomeController.class);

    @FXML
    private StackPane root;

    public static MyContentMenu contextMenu;

    @FXML
    private JFXDialog dialog;

    @FXML
    private JFXTextField input_1;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXDialog export_dialog;

    @FXML
    private JFXDialog add_dialog;

    @FXML
    private ToggleGroup exportGroup;

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

    @FXML
    private Button add_client;

    @FXML
    private Button import_client;

    @FXML
    private Button export_client;


    @FXML
    private JFXSnackbar snackbar;

    private HashMap<String, Client> selected = new HashMap<>();

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


        //初始化客户端UI
        clientList.forEach(client -> {
            generateClient(client);
        });


        /*****************************右键菜单*******************************/
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
            Client client = clientMap.get(source.getId());
            CompletableFuture<Status> future = client.queryStatus();
            future.whenComplete((resp, err) -> {
                Platform.runLater(() -> {
                    //弹出提示结果
                    snackbar = new JFXSnackbar(app);
                    snackbar.setPrefWidth(300);

                    snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
                            new JFXSnackbarLayout(client.getName() + ":" + resp.message, "CLOSE", action -> snackbar.close()),
                            Duration.millis(3000), null));
                });
            });
        });

        close.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).close();

            //弹出提示结果
            snackbar = new JFXSnackbar(app);
            snackbar.setPrefWidth(300);

            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
                    new JFXSnackbarLayout("Snackbar Message Persistent " + "hello", "CLOSE", action -> snackbar.close()),
                    Duration.millis(3000), null));
        });

        start.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).start();

            //弹出提示结果
            snackbar = new JFXSnackbar(app);
            snackbar.setPrefWidth(300);

            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
                    new JFXSnackbarLayout("Snackbar Message Persistent " + "hello", "CLOSE", action -> snackbar.close()),
                    Duration.millis(3000), null));
        });

        update.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).update();

            //弹出提示结果
            snackbar = new JFXSnackbar(app);
            snackbar.setPrefWidth(300);

            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
                    new JFXSnackbarLayout("Snackbar Message Persistent " + "hello", "CLOSE", action -> snackbar.close()),
                    Duration.millis(3000), null));
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
                    + " sed do eiusmod tempor incididunt ut labore et dolore magna"));
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

            dialog.show(root);


            clientMap.get(source.getId()).executeScript();
        });

        /*****************************工具栏*******************************/
        //发送通知
        notify.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (selected.isEmpty()) {
                    showSnackMessage("您还没有选择客户端");
                    return;
                }
                Notify notify = new Notify();
                notify.time = new Date().toString();
                notify.text = "hello";
                Collection<Client> clients = selected.values();
                clients.forEach(client -> {
                    CompletableFuture<Status> future = client.sendNotify(notify);
                    future.whenComplete((status, throwable) -> {
                        showSnackMessage(status.toString());
                    });
                });
            }
        });

        //分发文件
        distribute_file.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (selected.isEmpty()) {
                    showSnackMessage("您还没有选择客户端");
                    return;
                }

                Collection<Client> clients = selected.values();
                clients.forEach(client -> {
                    CompletableFuture<Status> future = client.uploadFile(new File("d:\\text.txt").toPath());

                    future.whenComplete((status, throwable) -> {
                        Platform.runLater(() -> {
                            System.out.println(status);
                        });
                    });
                });
            }
        });


        import_client.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });

        export_client.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                export_dialog.show(root);
            }
        });


        /*****************************对话框*******************************/
        root.getChildren().remove(dialog);

        cancelButton.setOnAction(action -> dialog.close());
        acceptButton.setOnAction(action -> {
            String path = input_1.getText();
            dialog.close();
        });


        //root.getChildren().remove(export_dialog);

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
        vBox.setId("vbox");
        vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                "-fx-background-radius:5;-fx-border-radius: 5;");
        //点击
        vBox.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                //选择
                if (!selected.containsKey(client.getId())) {
                    select(client);
                    /*selected2.put(client.getId(), client);
                    vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                            "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                            "-fx-background-radius:5;-fx-border-radius: 5;-fx-background-color: #d0d0d0");*/
                } else { //取消选择
                    unSelect(client);
                    /*selected2.remove(client.getId());
                    vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                            "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                            "-fx-background-radius:5;-fx-border-radius: 5;");*/
                }
            }
        });

        pane.getChildren().add(vBox);

        //选择
        /*StackPane select_pane = new StackPane();
        Button select_btn = new Button();
        select_btn.setPrefSize(16, 16);
        Image select_img = new Image("/images/select.png", true);
        ImageView select_i = new ImageView(select_img);
        select_i.setFitHeight(16);
        select_i.setPreserveRatio(true);
        select_btn.setGraphic(select_i);
        select_pane.getChildren().add(select_btn);
        vBox.getChildren().add(select_pane);*/

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
            if (event.getButton() == MouseButton.SECONDARY && selected.size() == 0) {
                contextMenu.show(pane, event.getScreenX(), event.getScreenY(), (Parent) event.getSource());
            }
        });

        flowPane.getChildren().add(pane);
        App.UIMap.put(pane.getId(), pane);
    }


    public void select(Client client) {
        selected.put(client.getId(), client);
        Parent parent = UIMap.get(client.getId());
        Node vBox = parent.lookup("#vbox");
        vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                "-fx-background-radius:5;-fx-border-radius: 5;-fx-background-color: #d0d0d0");
    }

    public void unSelect(Client client) {
        selected.remove(client.getId());
        Parent parent = UIMap.get(client.getId());
        Node vBox = parent.lookup("#vbox");
        vBox.setStyle("-fx-pref-height:150;-fx-pref-width:150;-fx-spacing:3;" +
                "-fx-alignment: center;-fx-background-color:#FFFFFF;" +
                "-fx-background-radius:5;-fx-border-radius: 5;");
    }

    public void selectAll(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();

        //全选
        if (selected.size() < clientMap.size()) {
            ImageView imageView = new ImageView(new Image("images/select_all.png"));
            imageView.setFitHeight(32);
            imageView.setFitWidth(32);
            button.setGraphic(imageView);
            clientMap.values().forEach(client -> {
                select(client);
            });
        } else { //取消全选
            ImageView imageView = new ImageView(new Image("images/un_select_all.png"));
            imageView.setFitHeight(32);
            imageView.setFitWidth(32);
            button.setGraphic(imageView);
            button.setGraphic(imageView);
            clientMap.values().forEach(client -> {
                unSelect(client);
            });
        }
    }

    public void export_dialog_cancel(ActionEvent actionEvent) {
        export_dialog.close();
    }

    public void export_dialog_submit(ActionEvent actionEvent) {
        RadioButton selectedToggle = (RadioButton) exportGroup.getSelectedToggle();
        export_dialog.close();

        //选择保存路径
        directoryChooser.setTitle("导出");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);


        //导出为excel
        if (selectedToggle.getId().equals("export_to_excel")) {
            File file = new File(selectedDirectory.getPath() + "客户端.xlsx");
            if (file.exists()) {
                file.delete();
            }

            ArrayList<Client> exports = new ArrayList<>(clientMap.values());
            exports.sort(Comparator.comparing(Client::getGroup));

            EasyExcel.write(file, Client.class).sheet("客户端").doWrite(exports);
        }

        //导出为Json
        if (selectedToggle.getId().equals("export_to_json")) {

            File file = new File(selectedDirectory.getPath() + "客户端.json");
            if (file.exists()) {
                file.delete();
            }

            ArrayList<Client> exports = new ArrayList<>(clientMap.values());
            exports.sort(Comparator.comparing(Client::getGroup));

            String eqpsJson = JSON.toJSONString(exports);
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write(eqpsJson);
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    bufferedWriter.close();
                } catch (IOException exception) {

                }
            }
        }

        showSnackMessage("导出成功");
    }


    public void showSnackMessage(String message) {
        Platform.runLater(() -> {
            //弹出提示结果
            snackbar = new JFXSnackbar(root);
            snackbar.setPrefWidth(300);

            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
                    new JFXSnackbarLayout(message, "CLOSE", action -> snackbar.close()),
                    Duration.millis(3000), null));
        });
    }

    public void add_client(ActionEvent actionEvent) {
        add_dialog.show(root);
    }

    public void add_dialog_cancel(ActionEvent actionEvent) {
        add_dialog.close();
    }

    public void add_dialog_submit(ActionEvent actionEvent) {
        add_dialog.close();
        showSnackMessage("新增成功");
    }
}
