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
package com.github.mauricio.netty.handler.codec.string;

import com.github.mauricio.netty.buffer.ByteBuf;
import com.github.mauricio.netty.channel.ChannelHandler.Sharable;
import com.github.mauricio.netty.channel.ChannelHandlerContext;
import com.github.mauricio.netty.handler.codec.LineBasedFrameDecoder;
import com.github.mauricio.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Decodes a received {@link com.github.mauricio.netty.buffer.ByteBuf} into a {@link String}.  Please
 * note that this decoder must be used with a proper {@link com.github.mauricio.netty.handler.codec.ByteToMessageDecoder}
 * such as {@link com.github.mauricio.netty.handler.codec.DelimiterBasedFrameDecoder} or {@link LineBasedFrameDecoder}
 * if you are using a stream-based transport such as TCP/IP.  A typical setup for a
 * text-based line protocol in a TCP/IP socket would be:
 * <pre>
 * {@link com.github.mauricio.netty.channel.ChannelPipeline} pipeline = ...;
 *
 * // Decoders
 * pipeline.addLast("frameDecoder", new {@link LineBasedFrameDecoder}(80));
 * pipeline.addLast("stringDecoder", new {@link com.github.mauricio.netty.handler.codec.string.StringDecoder}(CharsetUtil.UTF_8));
 *
 * // Encoder
 * pipeline.addLast("stringEncoder", new {@link StringEncoder}(CharsetUtil.UTF_8));
 * </pre>
 * and then you can use a {@link String} instead of a {@link com.github.mauricio.netty.buffer.ByteBuf}
 * as a message:
 * <pre>
 * void channelRead({@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx, {@link String} msg) {
 *     ch.write("Did you say '" + msg + "'?\n");
 * }
 * </pre>
 */
@Sharable
public class StringDecoder extends MessageToMessageDecoder<ByteBuf> {

    // TODO Use CharsetDecoder instead.
    private final Charset charset;

    /**
     * Creates a new instance with the current system character set.
     */
    public StringDecoder() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates a new instance with the specified character set.
     */
    public StringDecoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(msg.toString(charset));
    }
}
