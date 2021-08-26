import com.alibaba.fastjson.JSON;
import dto.Status;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class ApiTest {
    //client
    HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            //.proxy(ProxySelector.of(new InetSocketAddress(“proxy.example.com”,80)))
            //.authenticator(Authenticator.getDefault())
            .build();

    @Test
    public void status() {
        //request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://172.20.10.2:9000/client/status"))
                //.uri(URI.create("http://www.google.com"))
                .timeout(Duration.ofSeconds(5))
                //.header("Content-Type", "application / json")
                //.POST(HttpRequest.BodyPublishers.ofFile(Paths.get("e:\\test.stdf")))
                .build();

        /*client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println(response.statusCode());
                    return response;
                })
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);*/

        /*client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((res, err) -> {
                    if (null != err) {
                        System.out.println(err);
                    }
                    System.out.println(res);
                })
                .thenApply(response -> response)
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);*/
        /*client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((res, err) -> {
                    if (null != err) {
                        System.out.println(err);
                    }
                    System.out.println(res);
                })
                .thenApply(response -> {
                    System.out.println(response);
                    return response;
                })
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);*/

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((res, err) -> {
                    if (null != err) {
                        System.out.println(err);
                        System.out.println(err.getClass());
                        if (err instanceof CompletionException) {
                            System.out.println("err");
                        }
                        if (err instanceof HttpConnectTimeoutException) {
                            System.out.println("time out");
                        }
                    }
                }).thenApply(HttpResponse::body)
                .thenAccept(System.out::println);
    }

    public void close() {
        //request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://172.20.10.2:9000/client/close"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((res, err) -> {
                    if (null != err) {
                        System.out.println(err);
                        /*if (err instanceof HttpConnectTimeoutException) {
                            System.out.println("time out");
                        }*/
                    }
                }).thenApply(HttpResponse::body)
                .thenAccept(res -> {
                    System.out.println(res);
                    Status status = JSON.parseObject(String.valueOf(res), Status.class);
                    System.out.println(status.message);
                });
    }


    public void upload() {
        String uri = "http://192.168.0.104:9000/client/upload";
        String fileName = "b.png";
        URI build;
        try {
            build = new URIBuilder(uri).addParameter("fileName", fileName).build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(build)
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "image/png")
                    //.POST(HttpRequest.BodyPublishers.ofFile(Paths.get("C:\\Users\\allen\\Pictures\\Screenshots\\snap.png")))
                    //.POST(HttpRequest.BodyPublishers.ofString(""))
                    .PUT(HttpRequest.BodyPublishers.ofFile(Paths.get("C:\\Users\\allen\\Pictures\\Screenshots\\snap.png")))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((res, err) -> {
                        if (null != err) {
                            System.out.println(err);
                        }
                    }).thenApply(HttpResponse::body)
                    .thenAccept(res -> {
                        System.out.println(res);
                        Status status = JSON.parseObject(String.valueOf(res), Status.class);
                        System.out.println(status.message);
                    });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        Thread thread = new Thread();
        thread.setDaemon(true);
        ApiTest api = new ApiTest();

        //status
        //api.status();

        //close
        //api.close();

        //upload
        api.upload();

        /*model.Client client = new model.Client();
        client.setID("test");
        client.setName("test");
        client.setGroup("ni");
        client.setIp("192.168.0.104");
        client.queryStatus();*/

        Thread.sleep(10000);
    }


    @Test
    public void testNull() {
        String s = (String) null;
        System.out.println(s);
    }
}
