package org.example.filter;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.sv.Data;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreePhaseDto {
    private double phA;
    private double phB;
    private double phC;
    private double neut;

    public ThreePhaseDto(Data dataPhA, Data dataPhB, Data dataPhC, Data dataNeut) {
        phA = dataPhA.getValue();
        phB = dataPhB.getValue();
        phC = dataPhC.getValue();
        neut = dataNeut.getValue();
    }
}
