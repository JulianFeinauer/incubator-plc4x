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

public enum S7Type {
    BOOL(TypedAddress.Type.BOOLEAN, (short) 1),
    BYTE(TypedAddress.Type.SIGNED_INTEGER, (short) 8),
    UBYTE(TypedAddress.Type.UNSIGNED_INTEGER, (short) 8),
    INT(TypedAddress.Type.SIGNED_INTEGER, (short) 16),
    UINT(TypedAddress.Type.UNSIGNED_INTEGER, (short) 16),
    DINT(TypedAddress.Type.SIGNED_INTEGER, (short) 32),
    UDINT(TypedAddress.Type.UNSIGNED_INTEGER, (short) 32),
    REAL(TypedAddress.Type.DECIMAL, (short) 32),
    LINT(TypedAddress.Type.SIGNED_INTEGER, (short) 64),
    ULINT(TypedAddress.Type.UNSIGNED_INTEGER, (short) 64),
    LREAL(TypedAddress.Type.DECIMAL, (short) 64);
    private TypedAddress.Type type;

    private short bitWidth;

    S7Type(TypedAddress.Type type, short bitWidth) {
        this.type = type;
        this.bitWidth = bitWidth;
    }

    public TypedAddress.Type getType() {
        return type;
    }

    public short getBitWidth() {
        return bitWidth;
    }
}
