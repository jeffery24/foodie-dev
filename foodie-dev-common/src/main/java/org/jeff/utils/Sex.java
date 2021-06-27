package org.jeff.utils;

/**
 * <h2>性别 枚举</h2>
 */
public enum Sex {
    women(0, "女"),
    man(1, "男"),
    screen(2, "保密");

    public final Integer type;
    public final String value;

    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
