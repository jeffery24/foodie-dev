package org.jeff.enums;

/**
 * <h2> 性别 枚举</h2>
 */
public enum Sex {
    WOMEN(0, "女"),
    MAN(1, "男"),
    SCREEN(2, "保密");

    public final Integer type;
    public final String value;

    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
