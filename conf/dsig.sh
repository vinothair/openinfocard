#!/bin/sh

for i in ./lib/*.jar ; do

    export CLASSPATH=$CLASSPATH:$i;

done

java -cp $CLASSPATH org.xmldap.xmldsig.SignatureUtil
