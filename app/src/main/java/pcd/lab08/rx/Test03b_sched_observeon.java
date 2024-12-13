package pcd.lab08.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Test03b_sched_observeon {
    public static void main(String[] args) throws Exception {
        /*
         * Without using schedulers, by default all the computation
         * is done by the calling thread.
         *
         */
        System.out.println("\n=== TEST No schedulers ===\n");
        Observable.just(100)
                .map(v -> {
                    log("map 1 " + v);
                    return v * v;
                })
                .map(v -> {
                    log("map 2 " + v);
                    return v + 1;
                })
                .subscribe(v -> {
                    log("sub " + v);
                });

        /*
         * observeOn:
         *
         * move the downstream computation to the specified scheduler.
         * That is, every computation following observeOn will be executed by another thread.
         */
        System.out.println("\n=== TEST observeOn ===\n");
        Observable.just(100)
                .map(v -> { log("map 1 " + v); return v * v; }) // by the current thread (main thread)
                .observeOn(Schedulers.computation())                 // => use RX comp thread(s) downstream
                .map(v -> { log("map 2 " + v); return v + 1; }) // by the RX comp thread
                .subscribe(v -> log("sub " + v));              // by the RX comp thread

        Thread.sleep(100);

        System.out.println("\n=== TEST observeOn with blockingSubscribe ===\n");
        Observable.just(100)
                .map(v -> { log("map 1 " + v); return v * v; }) // by the current thread (main thread)
                .observeOn(Schedulers.computation())                       // => use RX comp thread(s) downstream
                .map(v -> { log("map 2 " + v); return v + 1; }) // by the RX comp thread
                .blockingSubscribe(v -> log("sub " + v));       // by the current thread (main thread = invoker)


        System.out.println("\n=== TEST observeOn with multiple subs ===\n");
        Observable<Integer> src2 = Observable.just(100)
                .map(v -> { log("map 1 " + v); return v * v; }) // by the current thread (main thread)
                .observeOn(Schedulers.computation())                        // => use RX comp thread(s) downstream
                .map(v -> { log("map 2 " + v); return v + 1; }); // by the RX comp thread;

        src2.subscribe(v -> log("sub 1 " + v)); // by the RX comp thread
        src2.subscribe(v -> log("sub 2 " + v)); // by the RX comp thread

        Thread.sleep(100);
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }
}
