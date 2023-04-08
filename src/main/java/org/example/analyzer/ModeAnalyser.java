package org.example.analyzer;

import lombok.Getter;
import lombok.Setter;
import org.example.filter.ThreePhaseDto;

import java.util.ArrayList;
import java.util.List;

public class ModeAnalyser {

    @Getter
    @Setter
    private double maxCurrent;
    @Getter
    @Setter
    private double minVoltage;
    private final int N;
    private double curTime;
    private double startFaultTime;

    private final ThreePhaseDto current;
    private final ThreePhaseDto voltage;
    private final FaultState faultState = new FaultState();
    @Getter
    private final ResultData result = new ResultData();

    public ModeAnalyser(int N, ThreePhaseDto current, ThreePhaseDto voltage) {
        this.current = current;
        this.voltage = voltage;
        this.N = N;
        normalCurrentBuffer = new ThreePhaseBuffer(N);
        normalVoltageBuffer = new ThreePhaseBuffer(N);
        faultCurrentBuffer = new ThreePhaseBuffer(N);
        faultVoltageBuffer = new ThreePhaseBuffer(N);
    }

    private final ThreePhaseBuffer normalCurrentBuffer;
    private final ThreePhaseBuffer normalVoltageBuffer;
    private final ThreePhaseBuffer faultCurrentBuffer;
    private final ThreePhaseBuffer faultVoltageBuffer;

    public void process() {
        faultState.setStatePhA(current.getPhA() > maxCurrent);
        faultState.setStatePhB(current.getPhB() > maxCurrent);
        faultState.setStatePhC(current.getPhC() > maxCurrent);

        if (!faultState.getState()) {

            if (result.getNormalCurrent() != 0 && result.getFaultTime() == 0) {
                result.setFaultTime(curTime - startFaultTime);
                StringBuilder fault = new StringBuilder();
                double av = getMaxValue(faultCurrentBuffer.getBufferA());
                System.out.println(av);
                if (av > maxCurrent) {
                    fault.append("A");
                    result.setFaultCurrent(av);
                    result.setFaultVoltage(getAverageValue(faultVoltageBuffer.getBufferA()));
                }
                av = getMaxValue(faultCurrentBuffer.getBufferB());
                if (av > maxCurrent) {
                    fault.append("B");
                    result.setFaultCurrent(av);
                    result.setFaultVoltage(getAverageValue(faultVoltageBuffer.getBufferB()));
                }
                av = getMaxValue(faultCurrentBuffer.getBufferC());
                if (av > maxCurrent) {
                    fault.append("C");
                    result.setFaultCurrent(av);
                    result.setFaultVoltage(getAverageValue(faultVoltageBuffer.getBufferC()));
                }
                result.setFaultType(fault.toString());
            }

            normalCurrentBuffer.add(current.getPhA(), current.getPhB(), current.getPhC());
            normalVoltageBuffer.add(voltage.getPhA(), voltage.getPhB(), voltage.getPhC());

        } else {

            if (result.getNormalCurrent() == 0) {
                result.setNormalCurrent(getAverageValue(normalCurrentBuffer.getBufferA()));
                result.setNormalVoltage(getAverageValue(normalVoltageBuffer.getBufferA()));
                startFaultTime = curTime;
            }

            faultCurrentBuffer.add(current.getPhA(), current.getPhB(), current.getPhC());
            faultVoltageBuffer.add(voltage.getPhA(), voltage.getPhB(), voltage.getPhC());

        }

        curTime = curTime + 0.02/N;

    }


    private double getAverageValue(List<Double> buffer) {
        double sum = 0;
        for (double v: buffer){
            sum = sum + v;
        }
        return sum/buffer.size();
    }

    private double getMaxValue(List<Double> buffer) {
        double max = 0;
        for (double v: buffer) {
            if (v > max) max = v;
        }
        return max;
    }

    @Getter @Setter
    private class FaultState {
        boolean statePhA;
        boolean statePhB;
        boolean statePhC;

        public boolean getState() {
            return statePhA || statePhB || statePhC;
        }
        public String getFaultType() {
            if (statePhA && statePhB && statePhC) return "ABC";
            if (statePhA && statePhB) return "AB";
            if (statePhA && statePhC) return "AC";
            if (statePhB && statePhC) return "BC";
            if (statePhA) return "A";
            if (statePhB) return "B";
            if (statePhC) return "C";
            return "normal";
        }
    }

    @Getter
    private class ThreePhaseBuffer {
        private final List<Double> bufferA;
        private final List<Double> bufferB;
        private final List<Double> bufferC;
        private final int N;
        public ThreePhaseBuffer(int N) {
            this.N = N * 10;
            this.bufferA = new ArrayList<>();
            this.bufferB = new ArrayList<>();
            this.bufferC = new ArrayList<>();
        }
        public void add(double a, double b, double c) {
            if(bufferA.size() >= N) bufferA.remove(0);
            if(bufferB.size() >= N) bufferB.remove(0);
            if(bufferC.size() >= N) bufferC.remove(0);
            bufferA.add(a);
            bufferB.add(b);
            bufferC.add(c);
        }
    }


}
