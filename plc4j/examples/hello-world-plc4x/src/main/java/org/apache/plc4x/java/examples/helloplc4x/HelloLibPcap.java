package org.apache.plc4x.java.examples.helloplc4x;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.plc4x.java.s7.readwrite.S7Driver;
import org.apache.plc4x.java.s7.readwrite.TPKTPacket;
import org.apache.plc4x.java.s7.readwrite.io.TPKTPacketIO;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Timestamp;

public class HelloLibPcap {

    private static final Logger logger = LoggerFactory.getLogger(HelloLibPcap.class);

    public static void main(String[] args) throws PcapNativeException {
        File pcapFile = new File("/tmp/s7-setup-communication-response.pcap");
        if (!pcapFile.exists()) {
            logger.error(String.format("Couldn't find PCAP capture file at: %s", pcapFile.getAbsolutePath()));
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Opening PCAP capture file at: %s", pcapFile.getAbsolutePath()));
        }

        final PcapHandle handle = Pcaps.openOffline(pcapFile.getAbsolutePath(),
            PcapHandle.TimestampPrecision.NANO);

        // If the address allows fine tuning which packets to process, set a filter to reduce the load.
//        String filter = config.getFilterString(localAddress, remoteAddress);
//        if(filter.length() > 0) {
//            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
//        }

        // Create a buffer where the raw socket worker can send data to.
        ByteBuf buffer = Unpooled.buffer();

        // Start a thread that processes the callbacks from the raw socket and simply
        // forwards the bytes read to the buffer.

        final S7Driver.ByteLengthEstimator byteLengthEstimator = new S7Driver.ByteLengthEstimator();
        final S7Driver.CorruptPackageCleaner corruptPackageCleaner = new S7Driver.CorruptPackageCleaner();

        try {
            handle.loop(-1, new PacketListener() {
                private Timestamp lastPacketTime = null;

                @Override
                public void gotPacket(Packet packet) {
                    Timestamp curPacketTime = handle.getTimestamp();

                    final EthernetPacket ethernetPacket = (EthernetPacket) packet;
                    final IpV4Packet ipV4Packet = (IpV4Packet) packet.getPayload();
                    final TcpPacket tcpPacket = (TcpPacket) ipV4Packet.getPayload();



                    // Send the bytes to the netty pipeline.
                    byte[] data = packet.getPayload().getPayload().getPayload().getRawData();
                    buffer.writeBytes(data);

                    // Now we should check if we have a valid package in there
                    final int estimatedLenght = byteLengthEstimator.applyAsInt(buffer);

                    if (estimatedLenght == -1) {
                        // Remove corrupt package
                        corruptPackageCleaner.accept(buffer);
                    } else if (estimatedLenght > 0) {
                        // Try to read the Package
                        // Read the packet data into a new ReadBuffer
                        byte[] bytes = new byte[estimatedLenght];
                        buffer.readBytes(bytes);
                        ReadBuffer readBuffer = new ReadBuffer(bytes);
                        try {
                            final TPKTPacket tpktPacket = TPKTPacketIO.staticParse(readBuffer);

                            logger.info("I received the Packet: " + tpktPacket);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    // Remember the timestamp of the current packet.
                    lastPacketTime = curPacketTime;
                }
            });
        } catch (PcapNativeException | NotOpenException e) {
            logger.error("PCAP loop thread died!", e);
        } catch (InterruptedException e) {
            logger.warn("PCAP loop thread was interrupted (hopefully intentionally)", e);
            Thread.currentThread().interrupt();
        }
    }
}
