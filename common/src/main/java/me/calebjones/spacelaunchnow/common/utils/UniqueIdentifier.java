package me.calebjones.spacelaunchnow.common.utils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIdentifier {
    private final static AtomicInteger c = new AtomicInteger((int) new Date().getTime());
    public static int getID() {
        return c.incrementAndGet();
    }
}
