package org.example;


import org.example.analyzer.ModeAnalyser;
import org.example.filter.ThreePhaseFilter;
import org.example.filter.ThreePhaseDto;
import org.example.sv.SvPacket;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static int packetCount = 12000;

    public static void main(String[] args) throws InterruptedException {
        PcapHelper helper = new PcapHelper();
        SvParser parser = new SvParser();

        ThreePhaseDto current = new ThreePhaseDto();
        ThreePhaseDto voltage = new ThreePhaseDto();

        ThreePhaseFilter currentFilter = new ThreePhaseFilter(80, 100);
        ThreePhaseFilter voltageFilter = new ThreePhaseFilter(80, 100);

        currentFilter.setData(current);
        voltageFilter.setData(voltage);

        ModeAnalyser analyser = new ModeAnalyser(80, current, voltage);
        analyser.setMaxCurrent(10000);
        analyser.setMinVoltage(90000);

        AtomicInteger prevSmpCnt = new AtomicInteger();
        helper.setNicName("Intel(R) Wireless-AC 9560 160MHz");
        helper.addListener(packet -> {
            SvPacket svPacket = parser.parse(packet);

            int smpCnt = svPacket.getSampledValues().getApdu().getASDUs().get(0).getSmpCnt();
            if (smpCnt != prevSmpCnt.get()) {

                prevSmpCnt.set(smpCnt);

                currentFilter.process(new ThreePhaseDto(
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(0),
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(1),
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(2),
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(3)
                ));
                voltageFilter.process(new ThreePhaseDto(
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(4),
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(5),
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(6),
                        svPacket.getSampledValues().getApdu().getASDUs().get(0).getDataSet().get(7)
                ));

                analyser.process();
            }
        });
        helper.action(packetCount);

        while (helper.isAction());
        System.out.println(analyser.getResult());


    }
}