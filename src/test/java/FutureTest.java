import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureTest {

    // 1.创建线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // 3.创建httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 4.创建FutureRequestExecutionService实例
        FutureRequestExecutionService futureRequestExecutionService = new FutureRequestExecutionService(httpclient,
                executorService);

        // 5.发起调用
        try {
            // 5.1请求参数
            HttpGet request1 = new HttpGet("http://172.20.10.2:9000/test1");
            HttpGet request2 = new HttpGet("http://172.20.10.2:9000/test2");
            // 5.2发起请求，不阻塞，马上返回
            HttpRequestFutureTask<String> task1 = futureRequestExecutionService.execute(request1,
                    HttpClientContext.create(), new MyResponseHandler(), new MyCallback());

            HttpRequestFutureTask<String> task2 = futureRequestExecutionService.execute(request2,
                    HttpClientContext.create(), new MyResponseHandler(), new MyCallback());

            // 5.3 do something

            // 5.4阻塞获取结果
            String str1 = task1.get();
            String str2 = task2.get();
            System.out.println("response:" + str1 + " " + str2);
        } finally {
            httpclient.close();
        }
    }

}

// 2.创建http回调函数
class MyResponseHandler implements ResponseHandler<String> {
    public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        // 2.1处理响应结果
        return EntityUtils.toString(response.getEntity());
    }
}

class MyCallback implements FutureCallback<String> {

    public void failed(final Exception ex) {
        System.out.println(ex.getLocalizedMessage());
    }

    public void completed(final String result) {
        System.out.println(result);
    }

    public void cancelled() {
        System.out.println("cancelled");
    }
}


