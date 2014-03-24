#!/bin/bash

JAVAPATH=/usr/lib/jvm/java-6-sun-1.6.0.24/include

# 32 bit
rm -R src/main/resources/assets/nailedmumble/native/linux32
mkdir src/main/resources/assets/nailedmumble/native/linux32

echo "Compiling linux32"

gcc -o src/main/resources/assets/nailedmumble/native/linux32/libMumbleLink.so -shared \
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
     src/main/c/mumble.c \
       -m64 \
       -I/usr/include/c++/4.4/i686-linux-gnu
