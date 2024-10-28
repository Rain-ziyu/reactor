package com.ahj.platform.reactor;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class FlowDemo {
    static class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {
        public Flow.Subscription flowSubscription;
        @Override
        public void onNext(String item) {
            System.out.println("processor onNext 接收到的数据:" + item);
            submit(item + "处理后");
            flowSubscription.request(1);
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("processor onSubscribe");
            subscription.request(1);
            this.flowSubscription = subscription;
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("processor onError:"+throwable.getLocalizedMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("processor onComplete");
        }
    }
    public static void main(String[] args) throws InterruptedException {
        // 发布者
        SubmissionPublisher<String> submissionPublisher = new SubmissionPublisher();

        // 订阅者
        Flow.Subscriber<String> subscriber = new Flow.Subscriber() {
            Flow.Subscription subscription;
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("subscribe onSubscribe");
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Object item) {
                System.out.println("subscribe onNext:" + item);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("subscribe onError");
            }

            @Override
            public void onComplete() {
                System.out.println("subscribe onComplete");

            }
        };
        // 绑定发布者与订阅者
        MyProcessor myProcessor = new MyProcessor();
        submissionPublisher.subscribe(myProcessor);
        myProcessor.subscribe(subscriber);
        for (int i = 0; i < 3; i++) {
            submissionPublisher.submit("aaa" + i);
        }
        Thread.sleep(100000);
    }
}
