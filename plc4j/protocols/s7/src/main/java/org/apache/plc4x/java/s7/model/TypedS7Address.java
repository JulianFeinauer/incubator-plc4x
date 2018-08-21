package org.apache.plc4x.java.s7.model;
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

import org.apache.plc4x.java.base.model.TypedAddress;
import org.apache.plc4x.java.s7.netty.model.types.MemoryArea;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a S7 Address with Type Information.
 */
public class TypedS7Address extends TypedAddress {

    private final MemoryArea memoryArea;
    private final short blockId;

    public TypedS7Address(MemoryArea memoryArea, short blockId, short byteOffset, Byte bitOffset, Type type, int numberOfBits) {
        super(byteOffset, bitOffset, type, numberOfBits);
        this.memoryArea = memoryArea;
        this.blockId = blockId;
    }

    /**
     * Static factory method.
     * <p>
     * %DB8.DBW7:INT
     * %DB3.DBX0.1
     * <p>
     * TODO describe all cases allowed here
     *
     * @param s string to parse
     * @return Parsed TypedS7Address
     * @throws S7ParseException When the string cannot be parsed
     */
    public static TypedS7Address parse(String s) throws S7ParseException {
        Pattern pattern = Pattern.compile("^%(?<memoryArea>\\w*?)(?<blockId>\\d+)\\.DB(?<widthIdentifier>\\w)(?<byteOffset>\\d{1,4})(\\.(?<bitOffset>[0-7]))?(:(?<type>\\w+))?$");

        Matcher matcher = pattern.matcher(s);
        check(matcher.matches(), "Address string doesn't match S7 adress format");
        // Parse Memory Area
        String memoryArea = matcher.group("memoryArea");
        MemoryArea area = null;
        try {
            area = InternalMemoryArea.valueOf(memoryArea).getArea();
        } catch (IllegalArgumentException e) {
            check(false, "Unknown Memory Area '" + memoryArea + "'");
        }
        short memoryAreaIndex = Short.valueOf(matcher.group("blockId"));
        String widthIdentifier = matcher.group("widthIdentifier");
        // Parse bit width
        S7BitWidth bitWidth = null;
        try {
            bitWidth = S7BitWidth.valueOf(widthIdentifier);
        } catch (IllegalArgumentException e) {
            check(false, "Unknwon bit width '" + widthIdentifier + "'");
        }
        // Parse byte offset
        short byteOffset = Short.valueOf(matcher.group("byteOffset"));
        // Get bit Offset, if present
        Byte bitOffset = null;
        if (matcher.group("bitOffset") != null) {
            bitOffset = Byte.valueOf(matcher.group("bitOffset"));
        }
        if (matcher.group("type") == null) {
            check(bitOffset != null, "Type parameter can only be ommited for Boolean!");
            check(bitWidth == S7BitWidth.X, "Boolean values are specified as DBX{byte-offset}.{bit-offset}");
            return new TypedS7Address(area, memoryAreaIndex, byteOffset, bitOffset, Type.BOOLEAN, (short) 1);
        }
        S7Type type = S7Type.valueOf(matcher.group("type"));

        // bit Information only for boolean
        if (type == S7Type.BOOL) {
            check(bitOffset != null, "Please specify bit offset for boolean value");
        } else {
            check(bitOffset == null, "Bit offset can only be specified for boolean values");
        }
        // Assert that bit Offset matches with bit width, except for "base types"
        if (type == S7Type.INT || type == S7Type.UINT) {
            // TODO Info that we do bit width inference?
            return new TypedS7Address(area, memoryAreaIndex, byteOffset, bitOffset, type.getType(), bitWidth.getBitWidth());
        } else {
            check(bitWidth.getBitWidth() == type.getBitWidth(), "Specified type '" + type + "' does not match with byte width '" + bitWidth + "'");
            return new TypedS7Address(area, memoryAreaIndex, byteOffset, bitOffset, type.getType(), type.getBitWidth());
        }
    }

    /**
     * Helper method that throws an Exception if the condition is false.
     *
     * @param condition Condition to compare
     * @param message   Error message
     */
    private static void check(boolean condition, String message) throws S7ParseException {
        if (condition == false) {
            throw new S7ParseException(message);
        }
    }

    public MemoryArea getMemoryArea() {
        return memoryArea;
    }

    public short getBlockId() {
        return blockId;
    }

    @Override
    public String toString() {
        return "TypedS7Address{" +
            "memoryArea=" + memoryArea +
            ", blockId=" + blockId +
            ", " + super.toString() +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypedS7Address that = (TypedS7Address) o;
        return blockId == that.blockId &&
            memoryArea == that.memoryArea;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), memoryArea, blockId);
    }

    /**
     * TODO fix the InternalMemoryArea Enum and remove this
     */
    public enum InternalMemoryArea {
        DB(MemoryArea.DATA_BLOCKS);

        private final MemoryArea area;

        InternalMemoryArea(MemoryArea area) {
            this.area = area;
        }

        public MemoryArea getArea() {
            return area;
        }
    }
}
