package org.apache.plc4x.java.examples.plclogger;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.connection.PlcConnection;
import org.apache.plc4x.java.api.connection.PlcReader;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.model.Address;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * TODO write comment
 *
 * @author julian
 * Created by julian on 02.08.18
 */
public class PlcDemo {

    public static void main(String[] args) throws Exception {
        PlcDriverManager manager = new PlcDriverManager();

        PlcConnection connection = manager.getConnection("s7://192.168.167.211/0/0");

        Address address = connection.parseAddress("DATA_BLOCKS/501/0");

        Address addressMarker = connection.parseAddress("FLAGS/0/0");

        //PlcReader reader = connection.getReader().get();

        PlcReadRequest request = PlcReadRequest.builder()
            .addItem(Integer.class, address, 100)
            .addItem(Byte.class, addressMarker, 1)
            .build();

        long start = System.currentTimeMillis();

        PlcReader plcReader = connection.getReader().get();

        System.out.println(plcReader.read(request).thenApply(response -> response.toString()).get());


        IntStream.rangeClosed(1, 1000).forEach(i -> {
            long sendTime = System.currentTimeMillis();
            plcReader.read(request)
                .thenApply(response -> {
                    long receive = System.currentTimeMillis();
                    System.out.println("i: " + i + " send at " + sendTime + " in flight " + (receive-sendTime));
                    System.out.println(response.getValue(request.getRequestItems().get(0)).get().getValues());
                    System.out.println(response.getValue(request.getRequestItems().get(1)).get().getValues());
                    return null;
                });
        });


//        for (int i = 0; i <= 1000; i++) {
//            CompletableFuture<? extends PlcReadResponse> future = plcReader.read(request);
//
//
//
//
//        }
        long stop = System.currentTimeMillis();
        System.out.println("Time: " + (double)(stop - start)/1000);

        Thread.sleep(1_000);



        connection.close();
    }

    public enum S7NativeRepresentations {

        SINT(new Representation(8,Collections.singletonList(Representation.Trait.SIGNED))),
        USINT(new Representation(8,Collections.singletonList(Representation.Trait.UNSIGNED))),
        INT(new Representation(16,Collections.singletonList(Representation.Trait.SIGNED))),
        UINT(new Representation(16,Collections.singletonList(Representation.Trait.UNSIGNED))),
        DINT(new Representation(32,Collections.singletonList(Representation.Trait.SIGNED))),
        UDINT(new Representation(32,Collections.singletonList(Representation.Trait.UNSIGNED))),
        REAL(new Representation(32, Collections.singletonList(Representation.Trait.DECIMAL))),
        LREAL(new Representation(64, Collections.singletonList(Representation.Trait.DECIMAL)));

        private Representation representation;

        S7NativeRepresentations(Representation representation) {
            this.representation = representation;
        }
    }



    private static class Representation {

        private final int width;
        private final List<Trait> modifier;

        public Representation(int width, List<Trait> modifier) {
            this.width = width;
            this.modifier = modifier;
        }

        public enum Trait {
            UNSIGNED('U'),
            SIGNED('S'),
            DECIMAL('D');

            Character c;

            Trait(Character c) {
                this.c = c;
            }
        }
    }
}
