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
package com.github.mauricio.netty.handler.codec;

import com.github.mauricio.netty.buffer.ByteBuf;
import com.github.mauricio.netty.channel.ChannelDuplexHandler;
import com.github.mauricio.netty.channel.ChannelHandlerContext;
import com.github.mauricio.netty.channel.ChannelPromise;
import com.github.mauricio.netty.util.internal.TypeParameterMatcher;

import java.util.List;

/**
 * A Codec for on-the-fly encoding/decoding of bytes to messages and vise-versa.
 *
 * This can be thought of as a combination of {@link com.github.mauricio.netty.handler.codec.ByteToMessageDecoder} and {@link MessageToByteEncoder}.
 *
 * Be aware that sub-classes of {@link com.github.mauricio.netty.handler.codec.ByteToMessageCodec} <strong>MUST NOT</strong>
 * annotated with {@link @Sharable}.
 */
public abstract class ByteToMessageCodec<I> extends ChannelDuplexHandler {

    private final TypeParameterMatcher outboundMsgMatcher;
    private final MessageToByteEncoder<I> encoder;

    private final ByteToMessageDecoder decoder = new ByteToMessageDecoder() {
        @Override
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ByteToMessageCodec.this.decode(ctx, in, out);
        }

        @Override
        protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ByteToMessageCodec.this.decodeLast(ctx, in, out);
        }
    };

    /**
     * @see {@link #ByteToMessageCodec(boolean)} with {@code true} as boolean parameter.
     */
    protected ByteToMessageCodec() {
        this(true);
    }

    /**
     * @see {@link #ByteToMessageCodec(Class, boolean)} with {@code true} as boolean value.
     */
    protected ByteToMessageCodec(Class<? extends I> outboundMessageType) {
        this(outboundMessageType, true);
    }

    /**
     * Create a new instance which will try to detect the types to match out of the type parameter of the class.
     *
     * @param preferDirect          {@code true} if a direct {@link com.github.mauricio.netty.buffer.ByteBuf} should be tried to be used as target for
     *                              the encoded messages. If {@code false} is used it will allocate a heap
     *                              {@link com.github.mauricio.netty.buffer.ByteBuf}, which is backed by an byte array.
     */
    protected ByteToMessageCodec(boolean preferDirect) {
        outboundMsgMatcher = TypeParameterMatcher.find(this, ByteToMessageCodec.class, "I");
        encoder = new Encoder(preferDirect);
    }

    /**
     * Create a new instance
     *
     * @param outboundMessageType   The type of messages to match
     * @param preferDirect          {@code true} if a direct {@link com.github.mauricio.netty.buffer.ByteBuf} should be tried to be used as target for
     *                              the encoded messages. If {@code false} is used it will allocate a heap
     *                              {@link com.github.mauricio.netty.buffer.ByteBuf}, which is backed by an byte array.
     */
    protected ByteToMessageCodec(Class<? extends I> outboundMessageType, boolean preferDirect) {
        checkForSharableAnnotation();
        outboundMsgMatcher = TypeParameterMatcher.get(outboundMessageType);
        encoder = new Encoder(preferDirect);
    }

    private void checkForSharableAnnotation() {
        if (getClass().isAnnotationPresent(Sharable.class)) {
            throw new IllegalStateException("@Sharable annotation is not allowed");
        }
    }

    /**
     * Returns {@code true} if and only if the specified message can be encoded by this codec.
     *
     * @param msg the message
     */
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return outboundMsgMatcher.match(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        decoder.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        encoder.write(ctx, msg, promise);
    }

    /**
     * @see MessageToByteEncoder#encode(com.github.mauricio.netty.channel.ChannelHandlerContext, Object, com.github.mauricio.netty.buffer.ByteBuf)
     */
    protected abstract void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception;

    /**
     * @see com.github.mauricio.netty.handler.codec.ByteToMessageDecoder#decode(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.buffer.ByteBuf, java.util.List)
     */
    protected abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;

    /**
     * @see com.github.mauricio.netty.handler.codec.ByteToMessageDecoder#decodeLast(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.buffer.ByteBuf, java.util.List)
     */
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        decode(ctx, in, out);
    }

    private final class Encoder extends MessageToByteEncoder<I> {
        Encoder(boolean preferDirect) {
            super(preferDirect);
        }

        @Override
        public boolean acceptOutboundMessage(Object msg) throws Exception {
            return ByteToMessageCodec.this.acceptOutboundMessage(msg);
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception {
            ByteToMessageCodec.this.encode(ctx, msg, out);
        }
    }
}
