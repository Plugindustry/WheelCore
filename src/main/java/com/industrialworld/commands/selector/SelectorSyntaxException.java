package com.industrialworld.commands.selector;

public class SelectorSyntaxException extends Exception {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
