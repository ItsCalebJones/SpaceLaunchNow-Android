package me.calebjones.spacelaunchnow.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIdentifier {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
