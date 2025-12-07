package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of BrickGenerator that uses a "bag" system for brick generation.
 * Ensures fair distribution by shuffling all brick types before reusing them.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        refillBag();
    }

    @Override
    public Brick getBrick() {
        ensureSupply();
        Brick current = nextBricks.pollFirst();
        ensureSupply();
        return current;
    }

    @Override
    public Brick getNextBrick() {
        ensureSupply();
        return nextBricks.peekFirst();
    }

    private void ensureSupply() {
        if (nextBricks.size() <= 1) {
            refillBag();
        }
    }

    private void refillBag() {
        List<Brick> bag = new ArrayList<>(brickList);
        Collections.shuffle(bag, ThreadLocalRandom.current());
        nextBricks.addAll(bag);
    }
}
