package org.apache.plc4x.java.base.model;
/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

import org.apache.plc4x.java.api.model.Address;

import java.util.Objects;

public class TypedAddress implements Address {

    private final short byteOffset;
    private final Byte bitOffset;
    private final Type type;
    private final int numberOfBits;

    public TypedAddress(short byteOffset, Byte bitOffset, Type type, int numberOfBits) {
        this.byteOffset = byteOffset;
        this.bitOffset = bitOffset;
        this.type = type;
        this.numberOfBits = numberOfBits;
    }

    public short getByteOffset() {
        return byteOffset;
    }

    public Byte getBitOffset() {
        return bitOffset;
    }

    public Type getType() {
        return type;
    }

    public int getNumberOfBits() {
        return numberOfBits;
    }

    @Override
    public String toString() {
        return "TypedAddress{" +
            "byteOffset=" + byteOffset +
            ", bitOffset=" + bitOffset +
            ", type=" + type +
            ", numberOfBits=" + numberOfBits +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedAddress that = (TypedAddress) o;
        return byteOffset == that.byteOffset &&
            numberOfBits == that.numberOfBits &&
            Objects.equals(bitOffset, that.bitOffset) &&
            type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(byteOffset, bitOffset, type, numberOfBits);
    }

    /**
     * Enum of all different types of variables.
     */
    public enum Type {
        BOOLEAN,
        UNSIGNED_INTEGER,
        SIGNED_INTEGER,
        DECIMAL,
        STRING,
        DATE
    }

}
