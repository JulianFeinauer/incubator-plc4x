package org.aparche.plc4x.java.encoders;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains Default Implementations of {@link Decoders}.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
public class Decoders {

    private static Map<Representation, Decoder> encoderMap = new HashMap<>();

    private static Decoder<Boolean> binaryEncoder = new Decoder<Boolean>() {
        @Override
        public Boolean decode(Class<Boolean> target, Representation description, byte[] data) {
            assert data.length == 1;
            return (data[0] & 0x01) == 0x01;
        }
    };

    static {
        encoderMap.put(Representations.BOOLEAN, binaryEncoder);
    }

}
