package org.example.sv;

import lombok.NoArgsConstructor;

@lombok.Data
public class Data {
    private int value;
    private Quality quality = new Quality();
}
