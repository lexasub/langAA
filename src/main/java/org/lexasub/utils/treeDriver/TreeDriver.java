package org.lexasub.utils.treeDriver;

import java.util.Map;
import java.util.stream.Stream;

public interface TreeDriver {
    default void init() {
    }

    default void addChild() {
    }

    default void addChild(String s) {
    }

    default void finit() {
    }

    default void addChild(StringBuilder stringBuilder) {
    }

    default void fromEntrySetStream(Stream<Map.Entry<String, String>> stream) {
    }

    default StringBuilder emptyChilds() {
        return new StringBuilder();
    }

    default StringBuilder getRes() {
        return new StringBuilder();
    }

}
