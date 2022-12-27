package com.example.chessapp.misc;

import com.example.chessapp.board.Board;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimateUtils {

    private static ScheduledExecutorService colorChange;
    public static void partyMode(ActionEvent actionEvent, Board board) {
        CheckBox source = (CheckBox) actionEvent.getSource();

        System.out.println(source.isSelected());
        if (source.isSelected()) {
            AtomicInteger r = new AtomicInteger(0), g = new AtomicInteger(0), b = new AtomicInteger(0);
            AtomicBoolean rDes = new AtomicBoolean(false);
            AtomicBoolean gDes = new AtomicBoolean(false);
            AtomicBoolean bDes = new AtomicBoolean(false);
            colorChange = Executors.newScheduledThreadPool(4);
            colorChange.scheduleAtFixedRate(() -> {
                Platform.runLater(() -> {
                    board.setLightSquareColor(Color.rgb(r.get(),g.get(), b.get()).brighter());
                    board.setDarkSquareColor(Color.rgb(r.get(),g.get(), b.get()).darker());
                });

            }, 0, 30, TimeUnit.MILLISECONDS);

            colorChange.scheduleAtFixedRate(() -> {
                if (r.get() == 255) {
                    rDes.set(true);
                } else if (r.get() == 0) {
                    rDes.set(false);
                }

                if (rDes.get())
                    r.decrementAndGet();
                else
                    r.incrementAndGet();

            }, 0, 40, TimeUnit.MILLISECONDS);

            colorChange.scheduleAtFixedRate(() -> {
                if (g.get() == 255) {
                    gDes.set(true);
                } else if (g.get() == 0) {
                    gDes.set(false);
                }

                if (gDes.get())
                    g.decrementAndGet();
                else
                    g.incrementAndGet();


            }, 0, 5, TimeUnit.MILLISECONDS);

            colorChange.scheduleAtFixedRate(() -> {
                if (b.get() == 255) {
                    bDes.set(true);
                } else if (b.get() == 0) {
                    bDes.set(false);
                }

                if (bDes.get())
                    b.decrementAndGet();
                else
                    b.incrementAndGet();


            }, 0, 25, TimeUnit.MILLISECONDS);
        } else {
            // remember this! it's great way to shutdown an executor service (SUGGESTED BY ORACLE OMGGG)!
            colorChange.shutdown();
            try {
                if (colorChange.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    colorChange.shutdownNow();
                }

            } catch (InterruptedException e) {
                System.err.println("thread interrupted:" + e);
            }

            board.resetColor();

        }


    }
}
