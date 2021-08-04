import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.fastjson.JSON;
import dto.Status;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletionException;

@ContentStyle(verticalAlignment = VerticalAlignment.CENTER, horizontalAlignment = HorizontalAlignment.CENTER)
@HeadStyle(verticalAlignment = VerticalAlignment.CENTER, horizontalAlignment = HorizontalAlignment.CENTER)
public class Client {
    @ExcelIgnore
    public static final Logger logger = LogManager.getLogger(Client.class);

    @ExcelIgnore
    private String ID;
    @ExcelProperty("机台名")
    @ColumnWidth(15)
    private String name;
    @ExcelProperty("IP地址")
    @ContentStyle(verticalAlignment = VerticalAlignment.CENTER, horizontalAlignment = HorizontalAlignment.CENTER)
    @ColumnWidth(20)
    private String ip;
    @ExcelIgnore
    private int port = 9000;
    @ExcelProperty("机型")
    @ColumnWidth(10)
    private String group;
    @ExcelIgnore
    private State state = State.OFFLINE;

    //client
    @ExcelIgnore
    HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    //状态
    public void queryStatus() {
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
                            if (err instanceof CompletionException) {
                                //logger.warn("err");
                            }
                            if (err instanceof HttpConnectTimeoutException) {
                                //logger.warn("time out");
                            }

                            if (this.getState() == State.OFFLINE) return;
                            this.setState(State.OFFLINE);

                            Platform.runLater(() -> {
                                BorderPane pane = (BorderPane) Main.UIMap.get(getID());
                                Image image = new Image("client_offline3.png", true);
                                pane.setTop(new ImageView(image));
                            });
                        }
                    }).thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        //请求成功
                        logger.info("request status success");
                        logger.info(response);
                        Status status = JSON.parseObject(String.valueOf(response), Status.class);
                        Platform.runLater(() -> {
                            BorderPane pane = (BorderPane) Main.UIMap.get(getID());
                            Image image;
                            if (!status.status.equals("OFFLINE")) {
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
