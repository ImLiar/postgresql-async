/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.mauricio.netty.handler.codec.base64;

import com.github.mauricio.netty.buffer.ByteBuf;
import com.github.mauricio.netty.channel.ChannelHandler.Sharable;
import com.github.mauricio.netty.channel.ChannelHandlerContext;
import com.github.mauricio.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Decodes a Base64-encoded {@link com.github.mauricio.netty.buffer.ByteBuf} or US-ASCII {@link String}
 * into a {@link com.github.mauricio.netty.buffer.ByteBuf}.  Please note that this decoder must be used
 * with a proper {@link com.github.mauricio.netty.handler.codec.ByteToMessageDecoder} such as {@link com.github.mauricio.netty.handler.codec.DelimiterBasedFrameDecoder}
 * if you are using a stream-based transport such as TCP/IP.  A typical decoder
 * setup for TCP/IP would be:
 * <pre>
 * {@link com.github.mauricio.netty.channel.ChannelPipeline} pipeline = ...;
 *
 * // Decoders
 * pipeline.addLast("frameDecoder", new {@link com.github.mauricio.netty.handler.codec.DelimiterBasedFrameDecoder}(80, {@link com.github.mauricio.netty.handler.codec.Delimiters#nulDelimiter()}));
 * pipeline.addLast("base64Decoder", new {@link com.github.mauricio.netty.handler.codec.base64.Base64Decoder}());
 *
 * // Encoder
 * pipeline.addLast("base64Encoder", new {@link com.github.mauricio.netty.handler.codec.base64.Base64Encoder}());
 * </pre>
 */
@Sharable
public class Base64Decoder extends MessageToMessageDecoder<ByteBuf> {

    private final Base64Dialect dialect;

    public Base64Decoder() {
        this(Base64Dialect.STANDARD);
    }

    public Base64Decoder(Base64Dialect dialect) {
        if (dialect == null) {
            throw new NullPointerException("dialect");
        }
        this.dialect = dialect;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(Base64.decode(msg, msg.readerIndex(), msg.readableBytes(), dialect));
    }
}
