/*
 *
 *  * Copyright 2012-2015 Viant.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package com.sm;

public interface Service {
    public static enum State { Unknown ((int) 0), Start ((int) 1), Shutdown ((int) 2), Starting ((int) 3), Stopping ((int) 4),
        Staged ((int) 5), Admin ((int) 6);

        final int value ;

        State(int value) {
            this.value = value;
        }

        public static State getState(int value) {
            switch (value) {
                case 1 : return Start;
                case 2 : return Shutdown;
                case 3 : return Starting;
                case 4 : return Stopping;
                case 5 : return Staged;
                case 6 : return Admin;
                default: return Unknown;
            }
        }
    }

    public void start();
    public void stop();
    public State getStatus();
}
