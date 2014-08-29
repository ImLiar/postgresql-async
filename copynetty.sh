#!/bin/bash

mkdir -p db-async-common/src/main/java/io/netty

rm -rf db-async-common/src/main/java/com/github/mauricio/netty/buffer
cp -r netty/buffer/src/main/java/io/netty/buffer db-async-common/src/main/java/io/netty

rm -rf db-async-common/src/main/java/com/github/mauricio/netty/handler
cp -r netty/codec/src/main/java/io/netty/handler db-async-common/src/main/java/io/netty

rm -rf db-async-common/src/main/java/com/github/mauricio/netty/util
cp -r netty/common/src/main/java/io/netty/util db-async-common/src/main/java/io/netty


rm -rf db-async-common/src/main/java/com/github/mauricio/netty/bootstrap
cp -r netty/transport/src/main/java/io/netty/bootstrap db-async-common/src/main/java/io/netty

rm -rf db-async-common/src/main/java/com/github/mauricio/netty/channel
cp -r netty/transport/src/main/java/io/netty/channel db-async-common/src/main/java/io/netty