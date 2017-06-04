package com.ftc4592;

import com.ftc4592.loops.PrintLoop;

public class Main {

    public static void main(String[] args) {
        Looper looper = new Looper();
        looper.register(new PrintLoop());
        looper.register(new PrintLoop());
        looper.register(new PrintLoop());
        looper.register(new PrintLoop());

        looper.start();

        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
        }

        looper.stop();
    }
}
