package com.roy.game.server;

import com.roy.game.Die;
import com.roy.game.GameServiceGrpc;
import com.roy.game.GameState;
import com.roy.game.Player;
import io.grpc.stub.StreamObserver;

public class GameService extends GameServiceGrpc.GameServiceImplBase {

    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {
        Player client = Player.newBuilder().setName("client").setPosition(0).build();
        Player server = Player.newBuilder().setName("server").setPosition(0).build();
        return new DieStreamingRequest(responseObserver, client, server);
    }
}
