package io.github.plugindustry.wheelcore.command.selector;

public class SelectorSyntaxException extends Exception {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
