import com.alibaba.fastjson.JSON;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class Main extends Application {
    public static final Logger logger = LogManager.getLogger(Main.class);

    static MyContentMenu contextMenu;
    static HashMap<String, Client> clientMap = new HashMap<>(); //<ID,Client>
    static HashMap<String, Parent> UIMap = new HashMap<>(); //<ID,Parent>
    static List<Client> clientList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AutoServer.fxml"));
        Scene scene = new Scene(root, 1500, 800);
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        contextMenu = new MyContentMenu();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        contextMenu.getItems().addAll(cut, copy, paste);

        cut.setOnAction(event -> {
            contextMenu.hide();
            Parent source = MyContentMenu.source;
            clientMap.get(source.getId()).queryStatus();
        });
        copy.setOnAction(event -> System.out.println("Copy..."));
        paste.setOnAction(event -> System.out.println("Paste..."));
    }


    static {
        //读取json配置文件
        try {

            String clients = IOUtils.toString(Main.class.getResource("clients.json"), "utf-8");
            List<Client> clients1 = JSON.parseArray(clients, Client.class);
            clientList = clients1;
            clients1.forEach(client -> {
                clientMap.put(client.getID(), client);
            });
            System.out.println(clients1);

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
