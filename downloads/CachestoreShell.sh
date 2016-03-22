#!/bin/bash

# Copyright 2012-2015 Viant.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.


BASE_DIR=$(dirname $0)/..

export CLASSPATH=$CLASSPATH:$BASE_DIR/dist/resources
export CLASSPATH=$CLASSPATH:$BASE_DIR/dist/
export CLASSPATH=$CLASSPATH:$BASE_DIR/lib


for file in $BASE_DIR/dist/resources/*.jar; do
  export CLASSPATH=$CLASSPATH:$file
done


for file in $BASE_DIR/dist/*.jar; do
  export CLASSPATH=$CLASSPATH:$file
done

for file in $BASE_DIR/lib/*.jar; do
  export CLASSPATH=$CLASSPATH:$file
done

java -cp $CLASSPATH org.codehaus.groovy.tools.shell.Main $BASE_DIR/dist/resources/Utils.groovy $BASE_DIR/dist/resources/startup.groovy