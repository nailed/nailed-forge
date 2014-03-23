#!/bin/bash

JAVAPATH=/Library/Java/JavaVirtualMachines/jdk1.7.0_40.jdk/Contents/Home/include

# 32 bit
rm -R src/main/resources/assets/nailedmumble/native/osx32
mkdir src/main/resources/assets/nailedmumble/native/osx32

echo "Compiling osx32"

gcc -dynamiclib -Wl,-headerpad_max_install_names,-undefined,dynamic_lookup,-compatibility_version,1.0,-current_version,1.0,-install_name,/usr/local/lib/libMumbleLink.dylib \
    -o src/main/resources/assets/nailedmumble/native/osx32/libMumbleLink.dylib \
    -I${JAVAPATH}/ \
    -I${JAVAPATH}/darwin/ \
    -m32 \
    src/main/c/mumble.c

# 64 bit
rm -R src/main/resources/assets/nailedmumble/native/osx64
mkdir src/main/resources/assets/nailedmumble/native/osx64

echo "Compiling osx64"

gcc -dynamiclib -Wl,-headerpad_max_install_names,-undefined,dynamic_lookup,-compatibility_version,1.0,-current_version,1.0,-install_name,/usr/local/lib/libMumbleLink.dylib \
    -o src/main/resources/assets/nailedmumble/native/osx64/libMumbleLink.dylib \
    -I${JAVAPATH}/ \
    -I${JAVAPATH}/darwin/ \
    -m64 \
    src/main/c/mumble.c
