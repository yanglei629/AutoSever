import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReactiveTest {
    public static void main(String[] args) throws Throwable {
        CompletableFuture<String> future = (CompletableFuture<String>) calculateAsync();
        //System.out.println(future.get());
        future.whenComplete((resp, err) -> {
            System.out.println(resp);
        });

        //System.out.println(c2().get());
        System.out.println("world");
    }

    public static Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(5000);
            completableFuture.complete("Hello");
            return null;
        });

        return completableFuture;
    }


    public static Future c2() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.complete("sss");
        return completableFuture;
    }
}
