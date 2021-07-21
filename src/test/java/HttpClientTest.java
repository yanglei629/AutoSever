import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpClientTest {
    @Test
    public void test() throws ExecutionException, InterruptedException, IOException {
        //async
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();
        HttpGet request = new HttpGet("http://www.baidu.com");

        Future<HttpResponse> future = client.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                System.out.println(httpResponse);
            }

            @Override
            public void failed(Exception e) {

            }

            @Override
            public void cancelled() {

            }
        });
        System.out.println("1");
        System.out.println("2");
        for (int i = 3; i <= 10; i++) {
            System.out.println(i);
        }
        HttpResponse response = future.get();
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        client.close();
    }

    @Test
    public void test1() throws URISyntaxException, IOException {
        //sync
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /*CloseableHttpResponse response = httpClient.execute(new HttpGet(new URIBuilder("http://www.google.com/").build()));
        System.out.println("111");
        System.out.println(response);
        System.out.println("222");*/
        CloseableHttpResponse response1 = httpClient.execute(new HttpGet(new URIBuilder("http://www.google.com/").build()), new ResponseHandler<CloseableHttpResponse>() {
            @Override
            public CloseableHttpResponse handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                System.out.println(httpResponse);
                return null;
            }
        });
        System.out.println("333");
    }
}
