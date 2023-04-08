package org.example.sv;

import lombok.Data;

@Data
public class SampledValues {
    private String appId;
    private int length;
    private String reserved1;
    private String reserved2;
    private APDU apdu;
}
