package com.sg.flooringmastery.ui;

import java.math.BigDecimal;

public interface UserIO {
    void print(String msg);

    String readString(String prompt);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    BigDecimal readBigDecimal(String prompt);

    BigDecimal readBigDecimal(String prompt, BigDecimal min);
}
