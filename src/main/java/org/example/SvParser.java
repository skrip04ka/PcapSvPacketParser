package org.example;

import org.example.sv.*;
import org.pcap4j.core.PcapPacket;

import java.util.ArrayList;
import java.util.List;

public class SvParser {

    public SvPacket parse(PcapPacket packet) {
        byte[] data = packet.getRawData();
        SvPacket svPacket = new SvPacket();

        svPacket.setHeaderMac(getHeaderMac(data));
        svPacket.setSampledValues(getSampledValues(data, 14));
        return svPacket;
    }

    private HeaderMac getHeaderMac(byte[] b) {
        HeaderMac headerMac = new HeaderMac();
        headerMac.setMacDst(byteArrayToMac(b, 0));
        headerMac.setMacSrc(byteArrayToMac(b, 6));
        headerMac.setType("0x" + String.format("%02x%02x", b[12], b[13]));
        return headerMac;
    }


    private SampledValues getSampledValues(byte[] b, int offset) {
        SampledValues sampledValues = new SampledValues();
        sampledValues.setAppId("0x" + String.format("%02x%02x", b[offset], b[offset + 1]));
        sampledValues.setLength(byteArrayToInt(b, offset + 2, 2));
        sampledValues.setReserved1("0x" + String.format("%02x%02x", b[offset + 4], b[offset + 5]));
        sampledValues.setReserved2("0x" + String.format("%02x%02x", b[offset + 6], b[offset + 7]));

        sampledValues.setApdu(getAPDU(b, offset + 8));

        return sampledValues;
    }


    private APDU getAPDU(byte[] b, int offset) {
        APDU apdu = new APDU();
        int noAsduLen = byteArrayToInt(b, offset + 3, 1);
        apdu.setNoASDU(byteArrayToInt(b, offset + 4, noAsduLen));
        List<ASDU> ASDUs = new ArrayList<>();

        offset = offset + + 4 + noAsduLen + 3;
        for (int i = 0; i < apdu.getNoASDU(); i++) {
            int len = byteArrayToInt(b, offset, 1);
            ASDUs.add(getASDU(b, offset + 1, len));
            offset = offset + len;
        }


        apdu.setASDUs(ASDUs);
        return apdu;
    }

    private ASDU getASDU(byte[] b, int offset, int len) {
        ASDU asdu = new ASDU();
        int count = 0;
        while (count < len) {
            int i = setValues(b, offset + count, asdu);
            count = count + i;
        }

        return asdu;
    }

    private int setValues(byte[] b, int offset, ASDU asdu) {

        int len = byteArrayToInt(b, offset+1, 1);
        switch (b[offset]) {
            case (byte) 0x80 -> {
                StringBuilder id = new StringBuilder("0x");
                for (int i = 0; i < len; i++) {
                    id.append(String.format("%02x", b[offset + 2 + i]));
                }
                asdu.setSvId(id.toString());
            }
            case (byte) 0x82 -> {
                asdu.setSmpCnt(byteArrayToInt(b, offset + 2, len));
            }
            case (byte) 0x83 -> {
                asdu.setConfRev(byteArrayToInt(b, offset + 2, len));
            }
            case (byte) 0x85 -> {
                asdu.setSmpSynh(byteArrayToInt(b, offset + 2, len));
            }
            case (byte) 0x87 -> {
                List<Data> dataSet = new ArrayList<>();
                for (int i = 0; i < len; i+=8) {
                    dataSet.add(getData(b, offset + 2 + i));
                }
                asdu.setDataSet(dataSet);
            }
            default -> System.out.println("Unsupported value: 0x" + String.format("%02x", b[offset]));
        };
        return 2 + len;

    }

    public Data getData(byte[] b, int offset) {
        Data data = new Data();

        data.setValue(byteArrayToInt(b, offset, 4));
        data.setQuality(getQuality(byteArrayToInt(b, offset+4, 4)));

        return data;
    }

    public Quality getQuality(int a) {
        Quality quality = new Quality();

        quality.setValidity(getValidity(a & 0b11));
        a = a >> 2;
        quality.setOverflow((a & 0b1) == 1);
        a = a >> 1;
        quality.setOutOfRange((a & 0b1) == 1);
        a = a >> 1;
        quality.setBadReference((a & 0b1) == 1);
        a = a >> 1;
        quality.setOscillatory((a & 0b1) == 1);
        a = a >> 1;
        quality.setFailure((a & 0b1) == 1);
        a = a >> 1;
        quality.setOldData((a & 0b1) == 1);
        a = a >> 1;
        quality.setInconsistent((a & 0b1) == 1);
        a = a >> 1;
        quality.setInaccurate((a & 0b1) == 1);
        a = a >> 1;
        quality.setSource(getSource(a & 0b1));
        a = a >> 1;
        quality.setTest((a & 0b1) == 1);
        a = a >> 1;
        quality.setOperatorBlocked((a & 0b1) == 1);
        a = a >> 1;
        quality.setDerived((a & 0b1) == 1);

        return quality;
    }

    private Quality.validity getValidity(int a) {
        return switch (a) {
            case 0 -> Quality.validity.GOOD;
            default -> null;
        };
    }

    private Quality.source getSource(int a) {
        return switch (a) {
            case 0 -> Quality.source.PROCESS;
            default -> null;
        };
    }

    private String byteArrayToMac(byte[] b, int offset) {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                b[offset],
                b[offset + 1],
                b[offset + 2],
                b[offset + 3],
                b[offset + 4],
                b[offset + 5]
                );
    }

    private int byteArrayToInt(byte[] b, int offset, int len) {
        int value = b[offset + len - 1] & 0xFF;
        for (int i = 1; i < len; i++) {
            value = value | (b[offset + (len - i - 1)] & 0xFF) << 8 * i;
        }
        return value;
    }


}
