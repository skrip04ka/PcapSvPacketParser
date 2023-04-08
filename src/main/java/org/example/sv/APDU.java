package org.example.sv;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class APDU {
    private int noASDU;
    private List<ASDU> ASDUs = new ArrayList<>();
}
