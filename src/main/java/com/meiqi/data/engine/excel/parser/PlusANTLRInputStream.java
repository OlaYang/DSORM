package com.meiqi.data.engine.excel.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * User: 
 * Date: 13-10-31
 * Time: 上午11:11
 */
public class PlusANTLRInputStream extends ANTLRInputStream {
    final String input;

    public PlusANTLRInputStream(String input) {
        super(input);
        this.input = input;
    }

    @Override
    public String getText(Interval interval) {
        int start = interval.a;
        int stop = interval.b;
        if (stop >= n) stop = n - 1;
        if (start >= n) return "";
        if (start < 0) start = 0;

        return input.substring(start, stop + 1);
    }
}
