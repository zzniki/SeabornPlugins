package com.oheers.fish.api.utils.system;


public enum JavaSpecVersion {
    JAVA_0_9(1.5F, "0.9"),
    JAVA_1_1(1.1F, "1.1"),
    JAVA_1_2(1.2F, "1.2"),
    JAVA_1_3(1.3F, "1.3"),
    JAVA_1_4(1.4F, "1.4"),
    JAVA_1_5(1.5F, "1.5"),
    JAVA_1_6(1.6F, "1.6"),
    JAVA_1_7(1.7F, "1.7"),
    JAVA_1_8(1.8F, "1.8"),
    JAVA_9(9.0F, "9"),
    JAVA_10(10.0F, "10"),
    JAVA_11(11.0F, "11"),
    JAVA_12(12.0F, "12"),
    JAVA_13(13.0F, "13"),
    JAVA_14(14.0F, "14"),
    JAVA_15(15.0F, "15"),
    JAVA_16(16.0F, "16"),
    JAVA_17(17.0F, "17"),
    JAVA_18(18.0F, "18"),
    JAVA_19(19.0F, "19"),
    JAVA_20(20.0F, "20"),
    JAVA_21(21.0F, "21"),
    JAVA_22(22.0F, "22"),
    JAVA_23(23.0F, "23"),
    JAVA_24(24.0F, "24"),
    JAVA_25(25.0F, "25"),
    JAVA_26(26.0F, "26"),
    JAVA_27(27.0F, "27");


    private final float value;
    private final String name;


    JavaSpecVersion(float value, String name) {
        this.name = name;
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
