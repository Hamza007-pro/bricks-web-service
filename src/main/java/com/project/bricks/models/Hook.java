package com.project.bricks.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;


@Data
public class Hook {

    private String Id;
    private String name;
    private Runnable action;
    private int executionOrder;


    public Hook(String name, Runnable action, int executionOrder) {
        this.name = name;
        this.action = action;
        this.executionOrder = executionOrder;
    }

    public CompletableFuture<Void> executeAsync() {
        if (action == null) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(action);
    }

    public void execute() {
        if (action == null) {
            return;
        }
        action.run();
    }

}
