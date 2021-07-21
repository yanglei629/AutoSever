import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class RxJavaTest {
    public static void main(String[] args) throws Exception {
        Flowable.just("Hello world").subscribe(System.out::println);
        Flowable<Integer> flow = Flowable.range(1, 5)
                .map(v -> v * v)
                .filter(v -> v % 3 == 0);
        flow.subscribe(System.out::println);


        Flowable.fromCallable(() -> {
            //Thread.sleep(1000); //  imitate expensive computation
            HttpClientResult httpClientResult = HttpClientUtil.doGet("http://172.20.10.2:9000/");
            //return "Done";
            return httpClientResult;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(System.out::println, Throwable::printStackTrace);

        //HttpClientUtil.doGet("http://172.20.10.2:9000/");
        Thread.sleep(9000); // <--- wait for the flow to finish
    }


    @Test
    public void test1() {
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v -> v * v)
                .blockingSubscribe(System.out::println);
    }

    @Test
    public void test2() {
        //Parallel processing
        Flowable.range(1, 10)
                .flatMap(v ->
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> w * w)
                )
                .blockingSubscribe(System.out::println);
        System.out.println("hello");
        //Flowable.parallel ParallelFlowable
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v * v)
                .sequential()
                .blockingSubscribe(System.out::println);
    }

    @Test
    public void test3() {
        //Deferred-dependent
        AtomicInteger count1 = new AtomicInteger();

        Observable.range(1, 10)
                .doOnNext(ignored -> count1.incrementAndGet())
                .ignoreElements()
                .andThen(Single.just(count1.get()))
                .subscribe(System.out::println);

        AtomicInteger count = new AtomicInteger();

        Observable.range(1, 10)
                .doOnNext(ignored -> count.incrementAndGet())
                .ignoreElements()
                .andThen(Single.defer(() -> Single.just(count.get())))
                .subscribe(System.out::println);

        AtomicInteger count3 = new AtomicInteger();

        Observable.range(1, 10)
                .doOnNext(ignored -> count3.incrementAndGet())
                .ignoreElements()
                .andThen(Single.fromCallable(() -> count3.get()))
                .subscribe(System.out::println);
    }

    @Test
    public void test4() {

    }

    @Test
    public void test() throws InterruptedException {
        Flowable<String> source = Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        });

        Flowable<String> runBackground = source.subscribeOn(Schedulers.io());

        Flowable<String> showForeground = runBackground.observeOn(Schedulers.single());

        showForeground.subscribe(System.out::println, Throwable::printStackTrace);

        System.out.println("hello");

        Thread.sleep(3000);
    }

    @Test
    public void t() throws InterruptedException {
        Flowable.fromCallable(() -> {
            //Thread.sleep(1000); //  imitate expensive computation
            HttpClientResult httpClientResult = HttpClientUtil.doGet("http://172.20.10.2:9000/");
            //return "Done";
            return httpClientResult;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(System.out::println, Throwable::printStackTrace);

        //HttpClientUtil.doGet("http://172.20.10.2:9000/");
        Thread.sleep(9000); // <--- wait for the flow to finish
    }
}
