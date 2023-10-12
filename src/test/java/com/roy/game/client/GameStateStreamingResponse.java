package com.roy.game.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.roy.game.Die;
import com.roy.game.GameState;
import com.roy.game.Player;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameStateStreamingResponse implements StreamObserver<GameState> {

    private final CountDownLatch latch;
    private StreamObserver<Die> dieStreamObserver;

    public GameStateStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(GameState gameState) {
        List<Player> list = gameState.getPlayersList();
        list.forEach(p -> System.out.println(p.getName() + ":" + p.getPosition()));
        boolean anyWinner = list.stream()
                .anyMatch(p -> p.getPosition() == 100);
        if(anyWinner) {
            System.out.println("Game Over!");
            this.dieStreamObserver.onCompleted();
        } else {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            this.roll();
        }
        System.out.println("-----------------------------");
    }

    @Override
    public void onError(Throwable throwable) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        this.latch.countDown();
    }

    public void roll() {
        int dieValue = ThreadLocalRandom.current().nextInt(1, 7);
        Die die = Die.newBuilder().setValue(dieValue).build();
        this.dieStreamObserver.onNext(die);
    }

    public void setDieStreamObserver(StreamObserver<Die> dieStreamObserver) {
        this.dieStreamObserver = dieStreamObserver;
    }
}
