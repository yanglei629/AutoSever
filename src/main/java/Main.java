import com.alibaba.fastjson.JSON;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
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
    public static HashMap<String, Parent> UIMap = new HashMap<>(); //<ID,Parent>
    static List<Client> clientList;

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //方式1
        //Parent root = FXMLLoader.load(getClass().getResource("AutoServer.fxml"));
        //FXMLLoader loader = FXMLLoader.load(getClass().getResource("AutoServer.fxml"));

        //方式2
        Flow flow = new Flow(MainController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(""));

        stage.setTitle("Auto Server");

        double width = 800;
        double height = 600;
        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            width = bounds.getWidth() / 2.5;
            height = bounds.getHeight() / 1.35;
        } catch (Exception e) {
        }

        //Scene scene = new Scene(root, 1500, 800);
        //Scene scene = new Scene(root, width, height);
        Scene scene = new Scene(decorator, width, height);

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
                JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
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
