package org.example;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class PcapHelper {

    static {
        try {
            for (PcapNetworkInterface nic: Pcaps.findAllDevs()) {
                log.info("nic {}", nic);
            }
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    @Setter
    private String nicName;
    private PcapHandle handle;

    private final List<PacketListener> listeners = new CopyOnWriteArrayList<>();
    private final PacketListener defaultPacketListener =
            packet -> listeners.forEach(listeners -> listeners.gotPacket(packet));


    @SneakyThrows
    public void action(int packetCount) {
        if (handle == null) {
            init();
            if (handle != null) {
                String filter = "ether proto 0x88ba && ether dst 01:0c:cd:04:00:01";
                handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

                Thread captureTread = new Thread(() -> {
                    try {
                        log.info("Starting packet capture");
                        handle.loop(packetCount * 2 -1, defaultPacketListener);
                    } catch (PcapNativeException |
                             InterruptedException |
                             NotOpenException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Packet capture finished");
                    handle.close();
                }, this.getClass().getSimpleName());
                captureTread.start();
            }
        }
    }

    public boolean isAction() {
        return handle.isOpen();
    }

    @SneakyThrows
    private void init() {
        Optional<PcapNetworkInterface> nic = Pcaps.findAllDevs().stream()
                .filter(i -> nicName.equals(i.getDescription()))
                .findFirst();
        if (nic.isPresent()) {
            handle = nic.get().openLive(1500,
                    PcapNetworkInterface.PromiscuousMode.PROMISCUOUS,
                    10);
            log.info("Network handler created: {}", nic);
        } else {
            log.error("Network interface is not found");
        }
    }

    public void addListener(PacketListener ... listener) {
        listeners.addAll(List.of(listener));
    }


}
