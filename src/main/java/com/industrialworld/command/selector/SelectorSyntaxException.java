package com.industrialworld.command.selector;

public class SelectorSyntaxException extends Exception {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
