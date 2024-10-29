package com.ahj.platform.reactor;

import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

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
        push();
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
        Flux.range(1, 10)
            .publishOn(Schedulers.boundedElastic())
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