package pcd.lab08.rx;

import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test04a_swing_pubsub {
    static public void main(String[] args) {
        PublishSubject<Integer> clickStream = PublishSubject.create();
        SwingUtilities.invokeLater(() -> {
            new MyFrame(clickStream);
        });

        clickStream
                .observeOn(Schedulers.computation())
                .subscribe(v -> {
                    System.out.println(Thread.currentThread().getName() + "click: " + System.currentTimeMillis());
                });
        clickStream
                // considera tutti i clic avvenuti entro 250 ms dal precedente
                .buffer(clickStream.throttleWithTimeout(250, TimeUnit.MILLISECONDS))
                .map(List::size)
                .filter(v -> v >= 2)
                .subscribe(v -> {
                    System.out.println(Thread.currentThread().getName() + ": Multi-click: " + v);
                });
    }

    static class MyFrame extends JFrame {
        private final PublishSubject<Integer> stream;

        public MyFrame(PublishSubject<Integer> stream) {
            super("Swing + RxJava");
            this.stream = stream;
            setSize(150, 60);
            setVisible(true);
            JButton button = new JButton("Press me");
            button.addActionListener((ActionEvent ev) -> {
                stream.onNext(1);
            });
            getContentPane().add(button);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    System.exit(-1);
                }
            });
        }
    }
}
