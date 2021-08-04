import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


@ViewController(value = "/fxml/Main.fxml", title = "Material Design Example")
public final class MainController extends Main {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;

    @FXML
    private JButton ss;

    @FXML
    private JFXDialog dialog;

    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton acceptButton;

    @FXML
    private StackPane contentArea;

    public MainController() {
    }


    @FXML
    private void initialize() {
        context = new ViewFlowContext();

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
    }

    public void home(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("fxml/home.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void page2(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("fxml/page2.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }


    @PostConstruct
    public void init() {
        root.getChildren().remove(dialog);

        cancelButton.setOnAction(action -> dialog.close());
        acceptButton.setOnAction(action -> dialog.close());

        //右键菜单
        contextMenu = new MyContentMenu();
        MenuItem close = new MenuItem("close process");
        MenuItem start = new MenuItem("start process");
        MenuItem update = new MenuItem("update");
        MenuItem delete = new MenuItem("delete file");
        MenuItem execute = new MenuItem("execute script");
        contextMenu.getItems().addAll(close, start, update, delete, execute);

        close.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;

            //JFXAlert alert = new JFXAlert((Stage) alertButton.getScene().getWindow());
            JFXAlert alert = new JFXAlert((Stage) root.getScene().getWindow());
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
        start.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;

            clientMap.get(source.getId()).startProcess();
        });
        update.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).update();
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
    }
}