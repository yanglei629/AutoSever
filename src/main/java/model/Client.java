package model;

import application.App;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import dto.Notify;
import dto.Status;
import enums.State;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@ContentStyle(verticalAlignment = VerticalAlignment.CENTER, horizontalAlignment = HorizontalAlignment.CENTER)
@HeadStyle(verticalAlignment = VerticalAlignment.CENTER, horizontalAlignment = HorizontalAlignment.CENTER)
public class Client {
    @ExcelIgnore
    public static final Logger logger = LogManager.getLogger(Client.class);

    @ExcelIgnore
    @JSONField(ordinal = 1)
    private String id;
    @ExcelProperty("机台名")
    @ColumnWidth(15)
    @JSONField(ordinal = 2)
    private String name;
    @ExcelProperty("IP地址")
    @ContentStyle(verticalAlignment = VerticalAlignment.CENTER, horizontalAlignment = HorizontalAlignment.CENTER)
    @ColumnWidth(20)
    @JSONField(ordinal = 4)
    private String ip;
    @ExcelIgnore
    @JSONField(ordinal = 5)
    private int port = 9000;
    @ExcelProperty("机型")
    @ColumnWidth(10)
    @JSONField(ordinal = 3)
    private String group;
    @ExcelIgnore
    @JSONField(ordinal = 6)
    private State state = State.OK;

    //client
    @ExcelIgnore
    HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    //状态
    public CompletableFuture<Status> queryStatus() {
        CompletableFuture<Status> future = new CompletableFuture<>();
        try {
            String uri = "http://" + getIp() + ":9000/" + "client/status";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((resp, err) -> {
                        if (null != err) {
                            //请求失败
                            future.complete(new Status(0, "客户端离线"));

                            if (this.getState() == State.OFFLINE) return;

                            //变为OFFLINE
                            this.changeState(State.OFFLINE);
                            return;
                        }
                    }).thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        //请求成功
                        logger.info(response);
                        Status status = JSON.parseObject(String.valueOf(response), Status.class);
                        future.complete(status);


                        if (status.status.equals("OFFLINE")) {
                            if (this.getState() == State.ONLINE) return;
                            //变为ONLINE
                            this.changeState(State.ONLINE);
                        }
                        if (status.status.equals("ONLINE")) {
                            if (this.getState() == State.OK) return;
                            //变为OK
                            this.changeState(State.OK);
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return future;
    }

    //发送通知
    public CompletableFuture<Status> sendNotify(Notify notify) {
        CompletableFuture<Status> future = new CompletableFuture<>();
        URI build;

        try {
            String uri = "http://" + getIp() + ":9000/" + "client/notify";
            build = new URIBuilder(uri).build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(build)
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(notify)))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((resp, err) -> {
                        if (null != err) {
                            //请求失败
                            future.complete(new Status(0, "客户端离线"));

                            if (this.getState() == State.OFFLINE) return;

                            //变为OFFLINE
                            this.changeState(State.OFFLINE);
                            return;
                        }
                    }).thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        //请求成功
                        logger.info(response);
                        Status status = JSON.parseObject(String.valueOf(response), Status.class);
                        future.complete(status);
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return future;
    }

    //传输文件
    public CompletableFuture<Status> uploadFile(Path path) {
        CompletableFuture<Status> future = new CompletableFuture<>();
        URI build;

        try {
            String uri = "http://" + getIp() + ":9000/" + "client/upload";
            build = new URIBuilder(uri).build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(build)
                    .timeout(Duration.ofSeconds(5))
                    .PUT(HttpRequest.BodyPublishers.ofFile(path))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((resp, err) -> {
                        if (null != err) {
                            //请求失败
                            future.complete(new Status(0, "客户端离线"));

                            if (this.getState() == State.OFFLINE) return;

                            //变为OFFLINE
                            this.changeState(State.OFFLINE);
                            return;
                        }
                    }).thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        //请求成功
                        Status status = JSON.parseObject(String.valueOf(response), Status.class);
                        future.complete(status);
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return future;
    }

    //关闭EAP
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

    //开启EAP
    public boolean start() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + getIp() + ":9000/" + "client/start"))
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

    public void changeState(State state) {
        //更改客户端状态和UI
        Platform.runLater(() -> {
            Pane pane = (Pane) App.UIMap.get(getId());
            StackPane pic = (StackPane) pane.lookup("#pic");
            pic.getChildren().clear();

            if (state.equals(State.OFFLINE)) {
                this.setState(State.OFFLINE);

                Image image = new Image("client_offline3.png", true);
                ImageView imageView = new ImageView(image);
                pic.getChildren().add(imageView);
            }
            if (state.equals(State.ONLINE)) {
                this.setState(State.ONLINE);

                Image image = new Image("client_offline2.png", true);
                ImageView imageView = new ImageView(image);
                pic.getChildren().add(imageView);
            }
            if (state.equals(State.OK)) {
                this.setState(State.OK);

                Image image = new Image("client_online2.png", true);
                ImageView imageView = new ImageView(image);
                pic.getChildren().add(imageView);
            }
        });
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        ArrayList<Object> objects = new ArrayList<>();
        objects.stream().findFirst().orElse("");
        return "model.Client{" +
                "ID='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", group='" + group + '\'' +
                ", state=" + state +
                '}';
    }


}
