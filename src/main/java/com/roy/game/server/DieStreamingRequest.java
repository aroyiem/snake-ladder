package com.roy.game.server;

import com.roy.game.Die;
import com.roy.game.GameState;
import com.roy.game.Player;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ThreadLocalRandom;

public class DieStreamingRequest implements StreamObserver<Die> {

    private final StreamObserver<GameState> gameStateStreamObserver;
    private Player client;
    private Player server;

    public DieStreamingRequest(StreamObserver<GameState> gameStateStreamObserver, Player client, Player server) {
        this.gameStateStreamObserver = gameStateStreamObserver;
        this.client = client;
        this.server = server;
    }

    @Override
    public void onNext(Die die) {
        this.client = this.getNewPlayerPosition(this.client, die.getValue());
        if (this.client.getPosition() != 100) {
            this.server = this.getNewPlayerPosition(this.server,
                    ThreadLocalRandom.current().nextInt(1, 7));
        }
        this.gameStateStreamObserver.onNext(this.getGameState());
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
    }

    private Player getNewPlayerPosition(Player player, int dieValue) {
        int position = player.getPosition() + dieValue;
        position = SnakesAndLaddersMap.getPosition(position);
        if (position <= 100) {
            player = player.toBuilder()
                    .setPosition(position)
                    .build();
        }
        return player;
    }

    private GameState getGameState() {
        return GameState.newBuilder()
                .addPlayers(this.client)
                .addPlayers(this.server)
                .build();
    }
}
