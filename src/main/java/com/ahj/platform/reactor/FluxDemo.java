package com.ahj.platform.reactor;

import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class MyEventProcessor<T> {
    private SingleThreadEventListener<T> listener;

    public void register(SingleThreadEventListener singleThreadEventListener) {
        listener = singleThreadEventListener;
    }

    public void onLine(T t) {
        listener.onDataChunk(List.of(t));
    }
}

public class FluxDemo {
    public static void main(String[] args) {
        handlerError();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法handlerError作用为：
     * 在Reactive Streams中，错误是终端事件。
     * 一旦发生错误，它就会停止 序列并沿着运算符链传播到最后一步，即 Subscriber的订阅服务器及其onError方法。
     *
     * @param
     * @return void
     * @throws
     * @author ziyu
     */
    public static void handlerError() {
        Flux.just("foo", "bar")
            .delayElements(Duration.ofMillis(1000))
            .map(s -> {
                throw new IllegalArgumentException(s);
            })
            // 感知异常情况出现，但不会影响异常处理与传播
            .doOnError(e -> System.out.println("ERROR: " + e))
            // // 错误后停止流  以错误结束  停止流即使该流有其他订阅者并进行了其他异常处理也会停止
            // .onErrorStop()
            // // 错误后停止流  以完成结束
            // .onErrorComplete()
            // 当出现异常时继续执行，不会影响后续其他元素的处理
            .onErrorContinue(IllegalArgumentException.class, (e, t) -> System.out.println("ERROR: " + e))
            // 当出现异常时整个流的后续元素不再处理，直接返回其他元素
            // .onErrorReturn(IllegalArgumentException.class, "zhale1111")
            // // 当出现异常时整个流的后续元素不再处理调用你传入的指定函数，返回一个新流
            // .onErrorResume(IllegalArgumentException.class, err -> Flux.just("zhale111","aaaa"))
            // // 当出现异常时整个流的后续元素不再处理调用你传入的指定函数，使其返回一个封装后的其他异常
            // .onErrorMap(IllegalArgumentException.class, err -> new IllegalArgumentException("err.getMessage()"))
            // .doOnError(e -> System.out.println("ERROR: " + e))
            // 无论是否出现异常都会执行
            .doFinally(signalType -> System.out.println("FINALLY: " + signalType))
            .log()
            .subscribe(v -> System.out.println("GOT VALUE"),
                       e -> System.out.println("ERROR: " + e));
    }

    /**
     * 方法zip作用为：
     * 1.将多个流合并成一个流 按照顺序关系形成元组
     *
     * @param
     * @return void
     * @throws
     * @author ziyu
     */
    public static void zip() {
        Flux<String> zip = Flux.zip(Flux.just(1, 2, 3)
                                        .delayElements(Duration.ofMillis(1000)), Flux.just("4", "5", "6", "7")
                                                                                     .delayElements(
                                                                                             Duration.ofMillis(2000)),
                                    (i1, i2) -> i1 + i2);
        zip.log()
           .subscribe(System.out::println);
    }

    /**
     * 方法merge作用为：
     * 1.将多个流合并成一个流 与concat方法不同在于 merge将两个流按照生产顺序进行合并
     * concat直接按照连接顺序进行顺序消费
     *
     * @param
     * @return void
     * @throws
     * @author ziyu
     */
    public static void merge() {
        Flux<Integer> just = Flux.just(1, 2, 3)
                                 .delayElements(Duration.ofMillis(1000));
        Flux<Integer> just1 = Flux.just(4, 5, 6)
                                  .delayElements(Duration.ofMillis(1500));
        // Flux.merge(just, just1)
        //         .log()
        //     .subscribe(System.out::println);
        Flux.concat(just, just1)
            .log()
            .subscribe(System.out::println);
    }

    public static void transform() {
        AtomicInteger counter = new AtomicInteger();
        Flux<Integer> transform = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                                      // .transform(x -> {   无论有多少个消费者转换逻辑只会执行一次
                                      .transformDeferred(x -> {   // 每有一个消费者都会执行一次转换逻辑
                                          if (counter.incrementAndGet() % 2 == 1) {
                                              return x.map(i -> i * 11);
                                          } else {
                                              return x;
                                          }
                                      });
        transform.subscribe(System.out::println);
        transform.subscribe(System.out::println);

    }

    /**
     * 方法self作用为：
     * 指定自定义线程池去执行中间操作
     *
     * @param
     * @return void
     * @throws
     * @author ziyu
     */
    public static void self() {
        Scheduler s = Schedulers.newParallel("parallel-schedu1er", 4);
        Flux.range(1, 3)
            .log()
            .publishOn(Schedulers.boundedElastic())
            .subscribeOn(s)
            .log()
            .subscribe();
    }

    public static void push() {
        MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
        Flux.push(sink -> {
                myEventProcessor.register(
                        new SingleThreadEventListener<String>() {

                            public void onDataChunk(List<String> chunk) {
                                for (String s : chunk) {
                                    sink.next(s);
                                }
                            }

                            public void processComplete() {
                                sink.complete();
                            }

                            public void processError(Throwable e) {
                                sink.error(e);
                            }
                        });
            })
            .log()
            .subscribe();
        myEventProcessor.onLine("hello");
        myEventProcessor.onLine("world");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myEventProcessor.onLine("world111111");
        }).start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void create() {
        List<MyListener> listeners = List.of(new MyListener(), new MyListener());
        Flux.create(x -> {
                for (MyListener listener : listeners) {
                    listener.setFluxSink(x);
                }
            })
            .log()
            .subscribe();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 0;
            for (MyListener listener : listeners) {
                listener.onLine("hello" + i++);
            }
        }).start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generate() {
        Flux<Integer> generate = Flux.generate(() -> 0, (integer, sink) -> {
            sink.next(integer);
            return integer + 1;
        });
        generate.subscribe(System.out::println);
    }

    public static void dispose() {
        Flux<Integer> log = Flux.just(1, 2, 3, 4)
                                .delayElements(Duration.ofMillis(1000))
                                .map(i -> i + 7)
                                .log();
        // 实际的消费者是一个实现了Disposable  可取消的
        Disposable subscribe = log.subscribe(System.out::println);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                subscribe.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void buffer() {
        Flux.range(1, 10)
            .buffer(3)// 消费者一次最多消费三个元素
            .subscribe(System.out::println);
    }

    public static void on(String[] args) {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 0)
            .doOnNext(i -> System.out.println("doOnNext:" + i))
            .doOnEach(System.out::println)
            .map(i -> 10 / i)
            .doOnError(err -> System.out.println("error:" + err))
            .onErrorComplete()
            .subscribe(System.out::println);
    }

    public static void fluxDoOn(String[] args) throws IOException {
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5);
        just.subscribe(System.out::println);
        // Flux<Long> interval = Flux.interval(Duration.ofMillis(1000));
        // interval.subscribe(System.out::println);

        Flux<Integer> range = Flux.range(1, 7);
        Flux<Integer> integerFlux = range.delayElements(Duration.ofMillis(1000));
        integerFlux.doOnComplete(() -> {
            System.out.println("complete");
        });
        integerFlux.subscribe(System.out::println);
        range.doOnComplete(() -> {
                 System.out.println("doOnComplete");
             })
             .doOnCancel(() -> {
                 System.out.println("doOnCancel");
             })
             .doOnError(throwable -> {
                 System.out.println("doOnError:" + throwable);
             })
             .doOnDiscard(Integer.class, integer -> {
                 System.out.println("doOnDiscard:" + integer);
             });
        range.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("hookOnSubscribe");
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("hookOnNext");
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("hookOnComplete");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("hookOn");
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("hookOn");
            }

            @Override
            protected void hookFinally(SignalType type) {
                System.out.println("hookFinally");
            }
        });
        System.in.read();
    }
}

class MyListener {
    private FluxSink fluxSink;

    public void setFluxSink(FluxSink fluxSink) {
        this.fluxSink = fluxSink;
    }

    public void onLine(String msg) {
        this.fluxSink.next(msg);
    }
}

interface SingleThreadEventListener<T> {
    void onDataChunk(List<T> chunk);

    void processComplete();

    void processError(Throwable e);
}