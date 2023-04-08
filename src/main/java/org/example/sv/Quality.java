package org.example.sv;

import lombok.Data;

@Data
public class Quality {
    private validity validity;
    private boolean overflow;
    private boolean outOfRange;
    private boolean badReference;
    private boolean oscillatory;
    private boolean failure;
    private boolean oldData;
    private boolean inconsistent;
    private boolean inaccurate;
    private source source;
    private boolean test;
    private boolean operatorBlocked;
    private boolean derived;

    public enum validity {
        GOOD
    }
    public enum source {
        PROCESS
    }
}
