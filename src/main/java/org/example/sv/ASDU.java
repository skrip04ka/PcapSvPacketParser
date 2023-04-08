package org.example.sv;


import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class ASDU {
    private String svId;
    private int smpCnt;
    private int confRev;
    private int smpSynh;
    private List<Data> dataSet = new ArrayList<>();

}
