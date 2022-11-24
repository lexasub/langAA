package org.lexasub.frontend.utils;

public class FrontendBaseBlock {
    public FrontendBaseBlock parent;

    public void addChild(FrontendBaseBlock newblock) {
    }

    public enum TYPE {LAMBDA};
    public TYPE type;

    public Object CONTINUE() {
        return null;
    }

    public Object BREAK() {
    }

    public Object RETURN() {
    }
}
