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

public enum S7BitWidth {
    X((short) 1),
    B((short) 8),
    W((short) 16),
    D((short) 32),
    L((short) 64);

    private short bitWidth;

    S7BitWidth(short bitWidth) {
        this.bitWidth = bitWidth;
    }

    public short getBitWidth() {
        return bitWidth;
    }
}
