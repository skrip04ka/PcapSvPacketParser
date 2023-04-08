package org.example.filter;

import lombok.Getter;
import lombok.Setter;

public class ThreePhaseFilter {
    private final RmsFilter filterPhA;
    private final RmsFilter filterPhB;
    private final RmsFilter filterPhC;

    @Setter
    @Getter
    private ThreePhaseDto data;

    private final int div;
    public ThreePhaseFilter(int n, int div) {
        filterPhA = new RmsFilter(n);
        filterPhB = new RmsFilter(n);
        filterPhC = new RmsFilter(n);
        this.div = div;
    }
    public ThreePhaseFilter(int n) {
        this(n, 1);
    }

    public void process (ThreePhaseDto value) {
        data.setPhA(filterPhA.process(value.getPhA()/div));
        data.setPhB(filterPhB.process(value.getPhB()/div));
        data.setPhC(filterPhC.process(value.getPhC()/div));
    }
}
