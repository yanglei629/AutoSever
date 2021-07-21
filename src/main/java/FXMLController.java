import javafx.fxml.FXML;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class FXMLController extends Main {
    private static final Logger logger = LogManager.getLogger(FXMLController.class);

    @FXML
    private FlowPane flowPane;

    public FXMLController() {
    }

    @FXML
    private void initialize() {

        clientList.forEach(client -> {
            generateClient(client);
        });

        new Thread(
                () -> {
                    try {
                        //client
                        HttpClient httpClient = HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_1_1)
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .connectTimeout(Duration.ofSeconds(10))
                                //.proxy(ProxySelector.of(new InetSocketAddress(“proxy.example.com”,80)))
                                //.authenticator(Authenticator.getDefault())
                                .build();
                        while (true) {
                            clientList.forEach(client -> {
                                //request
                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create("http://" + client.getIp() + ":9000/"))
                                        .timeout(Duration.ofSeconds(10))
                                        .header("Content-Type", "application / json")
                                        .build();

                                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                        .whenComplete((resp, err) -> {
                                            if (err != null) {

                                                BorderPane pane = (BorderPane) UIMap.get(client.getID());
                                                System.out.println(pane);
                                                Image image = new Image("client_offline3.png", true);
                                                pane.setTop(new ImageView(image));
                                                logger.warn(err.getMessage());
                                            } else {
                                                System.out.println(resp.body());
                                                System.out.println(resp.statusCode());
                                            }
                                        })
                                        .thenApply(response -> {
                                            BorderPane pane = (BorderPane) UIMap.get(client.getID());
                                            Image image = new Image("client_online2.png", true);
                                            pane.setTop(new ImageView(image));
                                            return response;
                                        })
                                        .thenApply(HttpResponse::body)
                                        .thenAccept(System.out::println);
                            });
                            Thread.sleep(20000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    public void generateClient(Client client) {
        BorderPane pane = new BorderPane();

        Image image = new Image("client_offline2.png", true);
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