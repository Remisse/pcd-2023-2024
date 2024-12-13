package pcd.lab08.rx;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Test03a_sched_subscribeon {
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
         * subscribeOn:
         *
         * move the computational work of a flow on a specified scheduler
         * (that is, a separate thread pool)
         */
        System.out.println("\n=== TEST subscribeOn ===\n");
        Observable<Integer> src = Observable.just(100)
                .map(v -> {
                    log("map 1 " + v);
                    return v * v;
                })
                .map(v -> {
                    log("map 2 " + v);
                    return v + 1;
                });

        src
                .subscribeOn(Schedulers.computation())
                .subscribe(v -> log("sub 1 " + v));
        src
                .subscribeOn(Schedulers.computation())
                .subscribe(v -> log("sub 2 " + v));

        Thread.sleep(100);

        /*
         * Running independent flows on a different scheduler
         * and merging their results back into a single flow
         * warning: flatMap => no order in merging
         */
        System.out.println("\n=== TEST parallelism  ===\n");
        Flowable.range(1, 1000)
                .flatMap(v ->
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> {
                                    log("map " + w);
                                    return w * w;
                                })        // by the RX comp thread;
                )
                // osserva i valori col thread chiamante
                // (funge da punto di sincronizzazione "ordinato")
                .subscribe(v -> {
                    log("sub > " + v);
                });

        Thread.sleep(1_000);
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }
}
