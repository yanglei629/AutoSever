import com.alibaba.fastjson.JSON;
import dto.Status;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletionException;

public class Client {
    public static final Logger logger = LogManager.getLogger(Client.class);

    private String ID;
    private String name;
    private String ip;
    private int port = 9000;
    private String group;
    private State state = State.OFFLINE;

    //client
    HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    //状态
    public void queryStatus() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + getIp() + ":9000/" + "client/status"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application / json")
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((resp, err) -> {
                        //请求失败
                        //logger.warn(err.getMessage());
                        if (this.getState() == State.OFFLINE) return;
                        this.setState(State.OFFLINE);
                        if (null != err) {
                            if (err instanceof CompletionException) {
                                System.out.println("err");
                            }
                            if (err instanceof HttpConnectTimeoutException) {
                                System.out.println("time out");
                            }
                            Platform.runLater(() -> {
                                BorderPane pane = (BorderPane) Main.UIMap.get(getID());
                                Image image = new Image("client_offline3.png", true);
                                pane.setTop(new ImageView(image));
                            });
                        }
                    }).thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        //请求成功
                        Status status = JSON.parseObject(String.valueOf(response), Status.class);
                        System.out.println(status);
                        Platform.runLater(() -> {
                            BorderPane pane = (BorderPane) Main.UIMap.get(getID());
                            Image image;
                            if (!status.message.equals("OFFLINE")) {
                                if (this.getState() == State.ONLINE) return;
                                this.setState(State.ONLINE);
                                image = new Image("client_offline2.png", true);
                            } else {
                                if (this.getState() == State.OK) return;
                                this.setState(State.OK);
                                image = new Image("client_online2.png", true);
                            }
                            pane.setTop(new ImageView(image));
                        });
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    //关闭
    public boolean close() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + getIp() + ":9000/" + "client/close"))
                .timeout(Duration.ofSeconds(5))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((resp, err) -> {
                    //请求失败
                    logger.warn(err.getMessage());
                    if (null != err) {

                    }
                }).thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    //请求成功
                });
        return true;
    }

    public boolean deleteFile() {

        System.out.println("delete");
        return false;
    }

    public boolean executeScript() {

        return true;
    }

    //启动程序
    public boolean startProcess() {
        return false;
    }


    //更新
    public boolean update() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + getIp() + ":9000/" + "client/update"))
                .timeout(Duration.ofSeconds(10))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((resp, err) -> {
                    //请求失败
                    logger.warn(err.getMessage());
                    if (null != err) {

                    }
                }).thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    //请求成功
                });
        return true;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", group='" + group + '\'' +
                ", state=" + state +
                '}';
    }
}
