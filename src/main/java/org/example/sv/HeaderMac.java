package org.example.sv;

import lombok.Data;

@Data
public class HeaderMac {
    private String macDst;
    private String macSrc;
    private String type;
}
