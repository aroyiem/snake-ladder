package com.roy.game.client;

import com.roy.game.Die;
import com.roy.game.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameClientTest {

    private GameServiceGrpc.GameServiceStub stub;

    @BeforeAll
    void setup() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().build();
        this.stub = GameServiceGrpc.newStub(channel);
    }

    @Test
    void clientGame() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        GameStateStreamingResponse gameStateStreamingResponse = new GameStateStreamingResponse(latch);
        StreamObserver<Die> dieStreamObserver = this.stub.roll(gameStateStreamingResponse);
        gameStateStreamingResponse.setDieStreamObserver(dieStreamObserver);
        gameStateStreamingResponse.roll();
        latch.await();
    }
}
