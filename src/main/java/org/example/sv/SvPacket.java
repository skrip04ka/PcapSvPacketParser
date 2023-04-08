package org.example.sv;

import lombok.Data;

@Data
public class SvPacket {
    private HeaderMac headerMac = new HeaderMac();
    private SampledValues sampledValues = new SampledValues();

}
