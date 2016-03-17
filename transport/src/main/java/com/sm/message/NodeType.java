/*
 *
 *
 * Copyright 2012-2015 Viant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 */

package com.sm.message;

public enum NodeType {

        // Node CollectorType
        Group ((byte) 1), Configuration ((byte) 2), NameService ((byte) 3), Collector ((byte) 4), PrimaryCollector ((byte) 5) ,
        Client ((byte) 6), StandByCoolector( (byte) 7), FinalCollector( (byte) 8),
        Unknown ((byte) 0);

        final byte value;

        NodeType(byte value) {
            this.value = value;
        }

        public static NodeType getNodeType(byte type) {
            switch (type) {
                case 1 : return Group;
                case 2 : return Configuration;
                case 3 : return NameService;
                case 4 : return Collector;
                case 5 : return PrimaryCollector;
                case 6 : return Client;
                case 7 : return StandByCoolector;
                case 8 : return FinalCollector;
                default: return Unknown;

            }

    }

}
