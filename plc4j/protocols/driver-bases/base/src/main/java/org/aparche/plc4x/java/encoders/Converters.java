package org.aparche.plc4x.java.encoders;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains Default Implementations of {@link Converters}.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
public class Converters {

    private static Map<Representation, Decoder> decoderMap = new HashMap<>();
    private static Map<Representation, Encoder> encoderMap = new HashMap<>();

    static {
        decoderMap.put(Representations.BOOLEAN, BinaryConverter.INSTANCE);
        encoderMap.put(Representations.BOOLEAN, BinaryConverter.INSTANCE);
    }

    public static Converter getDefaultConverter() {
        return Converter.builder()
            .withEncoderMap(getDefaultEncoders())
            .withDecoderMap(getDefaultDeoders())
            .build();
    }

    public static Map<Representation, Encoder> getDefaultEncoders() {
        return encoderMap;
    }

    public static Map<Representation, Decoder> getDefaultDeoders() {
        return decoderMap;
    }

}
