#!/bin/bash

JAVAPATH=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.51/include

# 32 bit
rm -R src/main/resources/assets/nailedmumble/native/linux32
mkdir src/main/resources/assets/nailedmumble/native/linux32

echo "Compiling linux32"

gcc -o src/main/resources/assets/nailedmumble/native/linux32/libMumbleLink.so -m32 -shared -fPIC \
     -Wl,-soname,src/main/resources/assets/nailedmumble/native/linux32/libMumbleLink.so  \
     -I${JAVAPATH}/ \
     -I${JAVAPATH}/linux \
     -lrt \
     src/main/c/mumble.c

# 64 bit
rm -R src/main/resources/assets/nailedmumble/native/linux64
mkdir src/main/resources/assets/nailedmumble/native/linux64

echo "Compiling linux64"

gcc -o src/main/resources/assets/nailedmumble/native/linux64/libMumbleLink.so -m64 -shared -fPIC \
     -Wl,-soname,src/main/resources/assets/nailedmumble/native/linux64/libMumbleLink.so  \
     -I${JAVAPATH}/ \
     -I${JAVAPATH}/linux \
     -lrt \
     src/main/c/mumble.c
