import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.ResourceBundle;

public class Home extends Main implements Initializable {
    @FXML
    private FlowPane flowPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("生成客户端UI");
        clientList.forEach(client -> {
            generateClient(client);
        });
    }

    public void generateClient(Client client) {
        BorderPane pane = new BorderPane();

        Image image = new Image("client_offline3.png", true);
        ImageView imageView = new ImageView(image);
        BorderPane.setAlignment(imageView, Pos.CENTER);
        pane.setTop(imageView);
        Label label = new Label();
        label.setText(client.getName() == null ? "" : client.getName());
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: transparent");
        label.setTextFill(Paint.valueOf("black"));
        BorderPane.setAlignment(label, Pos.CENTER);
        pane.setBottom(label);
        pane.setStyle("-fx-background-color: blanchedalmond");
        pane.setStyle("-fx-background-color: transparent");
        pane.setPrefSize(100, 100);
        pane.setId(client.getID() == null ? "" : client.getID());
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(pane, event.getScreenX(), event.getScreenY(), (Parent) event.getSource());
            }
        });

        flowPane.getChildren().add(pane);
        UIMap.put(pane.getId(), pane);
    }


}
