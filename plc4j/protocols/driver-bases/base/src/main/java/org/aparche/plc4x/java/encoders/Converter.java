package org.aparche.plc4x.java.encoders;

import java.util.HashMap;
import java.util.Map;

/**
 * Converter Instance that can be used to encode and decode between java values and byte arrays.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
public class Converter {

    private final Map<Representation, Decoder> decoderMap;
    private final Map<Representation, Encoder> encoderMap;

    public Converter(Map<Representation, Decoder> decoderMap, Map<Representation, Encoder> encoderMap) {
        this.decoderMap = decoderMap;
        this.encoderMap = encoderMap;
    }

    public <T> T decode(Class<T> target, Representation representation, byte[] data) {
        // Fetch decoder
        if (!decoderMap.containsKey(representation)) {
            throw new UnsupportedOperationException("No decoder for representation " + representation + " registered.");
        }
        Decoder decoder = decoderMap.get(representation);
        return decoder.decode(target, representation, data);
    }

    public <T> byte[] encode(T value, Representation representation) {
        if (!encoderMap.containsKey(representation)) {
            throw new UnsupportedOperationException("No encoder for representation " + representation + " registered.");
        }
        Encoder encoder = encoderMap.get(representation);
        return encoder.encode(value, representation);
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {

        private Map<Representation, Decoder> decoderMap = new HashMap<>();
        private Map<Representation, Encoder> encoderMap = new HashMap<>();

        private Builder() {
        }

        public Builder withDecoderMap(Map<Representation, Decoder> decoderMap) {
            this.decoderMap.putAll(decoderMap);
            return this;
        }

        public Builder withEncoderMap(Map<Representation, Encoder> encoderMap) {
            this.encoderMap.putAll(encoderMap);
            return this;
        }

        public Builder withEncoder(Representation representation, Encoder encoder) {
            if (this.encoderMap == null) {
                this.encoderMap = new HashMap<>();
            }
            this.encoderMap.put(representation, encoder);
            return this;
        }

        public Builder withDecoder(Representation representation, Decoder decoder) {
            if (this.decoderMap == null) {
                this.decoderMap = new HashMap<>();
            }
            this.decoderMap.put(representation, decoder);
            return this;
        }

        public Converter build() {
            return new Converter(decoderMap, encoderMap);
        }
    }
}
