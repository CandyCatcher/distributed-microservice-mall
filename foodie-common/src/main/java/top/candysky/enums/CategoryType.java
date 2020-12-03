package top.candysky.enums;

/*
 * @Description：分类级别 枚举
 */
public enum CategoryType {

    ROOT(1, "一级分类"),
    SECOND(2, "二级分类"),
    THIRD(3, "三级分类");

    public final Integer type;
    public final String value;

    CategoryType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
