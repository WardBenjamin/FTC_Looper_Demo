/**
 * Created by Benjamin Ward on 5/29/2017.
 */
package com.ftc4592.loops;

import com.ftc4592.Loop;

import java.util.UUID;

public class PrintLoop extends Loop {

    private String uuid;

    public PrintLoop() {
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public void onLoop() {
        System.out.println(uuid + ", " + "OnLoop");
    }

    @Override
    public void onStart() {
        System.out.println(uuid + ", " + "OnStart");
    }

    @Override
    public void onStop() {
        System.out.println(uuid + ", " + "OnStop");
    }
}