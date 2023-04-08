package org.example.analyzer;

import lombok.Data;

@Data
public class ResultData {
    private double normalCurrent;
    private double normalVoltage;
    private double faultCurrent;
    private double faultVoltage;
    private double faultTime;
    private String faultType;

    @Override
    public String toString() {
        return "ResultData:" +
                "\n\tnormalCurrent=" + normalCurrent +
                "\n\tnormalVoltage=" + normalVoltage +
                "\n\tfaultCurrent=" + faultCurrent +
                "\n\tfaultVoltage=" + faultVoltage +
                "\n\tfaultTime=" + faultTime +
                "\n\tfaultType=" + faultType;
    }
}
