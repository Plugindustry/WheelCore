package com.industrialworld.interfaces.world.multiblock;

// A bit similar to Definer, but it has its own duty(only change the value of "location" arg).
public interface Relocator {
    void relocate(Environment env);
}
