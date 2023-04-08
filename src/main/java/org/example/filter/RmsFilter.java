package org.example.filter;


import org.example.sv.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RmsFilter {

    private final int N;
    private final List<Double> buffer;

    public RmsFilter(int n) {
        N = n;
        buffer = new ArrayList<>(Collections.nCopies(n, 0.0));
    }

    public double process(double value) {
        buffer.add((value));
        if (buffer.size() > N) buffer.remove(0);
        return rms();
    }

    private double rms() {
        double x = 0;
        for (double value : buffer) {
            x = x + Math.pow(value, 2);
        }
        return Math.sqrt(x / N);
    }
}
