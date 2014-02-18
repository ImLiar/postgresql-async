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
package com.github.mauricio.netty.channel;

import java.lang.annotation.*;

/**
 * Handles or intercepts a {@link ChannelInboundInvoker} or {@link ChannelOutboundInvoker} operation, and forwards it
 * to the next handler in a {@link ChannelPipeline}.
 *
 * <h3>Sub-types</h3>
 * <p>
 * {@link com.github.mauricio.netty.channel.ChannelHandler} itself does not provide many methods.  To handle a
 * a {@link ChannelInboundInvoker} or {@link ChannelOutboundInvoker} operation
 * you need to implement its sub-interfaces.  There are many different sub-interfaces
 * which handles inbound and outbound operations.
 *
 * But the most useful for developers may be:
 * <ul>
 * <li>{@link ChannelInboundHandlerAdapter} handles and intercepts inbound operations</li>
 * <li>{@link com.github.mauricio.netty.channel.ChannelOutboundHandlerAdapter} handles and intercepts outbound operations</li>
 * </ul>
 *
 * You will also find more detailed explanation from the documentation of
 * each sub-interface on how an event is interpreted when it goes upstream and
 * downstream respectively.
 *
 * <h3>The context object</h3>
 * <p>
 * A {@link com.github.mauricio.netty.channel.ChannelHandler} is provided with a {@link com.github.mauricio.netty.channel.ChannelHandlerContext}
 * object.  A {@link com.github.mauricio.netty.channel.ChannelHandler} is supposed to interact with the
 * {@link ChannelPipeline} it belongs to via a context object.  Using the
 * context object, the {@link com.github.mauricio.netty.channel.ChannelHandler} can pass events upstream or
 * downstream, modify the pipeline dynamically, or store the information
 * (attachment) which is specific to the handler.
 *
 * <h3>State management</h3>
 *
 * A {@link com.github.mauricio.netty.channel.ChannelHandler} often needs to store some stateful information.
 * The simplest and recommended approach is to use member variables:
 * <pre>
 * public interface Message {
 *     // your methods here
 * }
 *
 * public class DataServerHandler extends {@link com.github.mauricio.netty.channel.SimpleChannelInboundHandler}&lt;Message&gt; {
 *
 *     <b>private boolean loggedIn;</b>
 *
 *     {@code @Override}
 *     public void channelRead({@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx, Message message) {
 *         {@link com.github.mauricio.netty.channel.Channel} ch = e.getChannel();
 *         if (message instanceof LoginMessage) {
 *             authenticate((LoginMessage) message);
 *             <b>loggedIn = true;</b>
 *         } else (message instanceof GetDataMessage) {
 *             if (<b>loggedIn</b>) {
 *                 ch.write(fetchSecret((GetDataMessage) message));
 *             } else {
 *                 fail();
 *             }
 *         }
 *     }
 *     ...
 * }
 * </pre>
 * Because the handler instance has a state variable which is dedicated to
 * one connection, you have to create a new handler instance for each new
 * channel to avoid a race condition where a unauthenticated client can get
 * the confidential information:
 * <pre>
 * // Create a new handler instance per channel.
 * // See {@link ChannelInitializer#initChannel(com.github.mauricio.netty.channel.Channel)}.
 * public class DataServerInitializer extends {@link ChannelInitializer}&lt{@link com.github.mauricio.netty.channel.Channel}&gt {
 *     {@code @Override}
 *     public void initChannel({@link com.github.mauricio.netty.channel.Channel} channel) {
 *         channel.pipeline().addLast("handler", <b>new DataServerHandler()</b>);
 *     }
 * }
 *
 * </pre>
 *
 * <h4>Using an attachment</h4>
 *
 * Although it's recommended to use member variables to store the state of a
 * handler, for some reason you might not want to create many handler instances.
 * In such a case, you can use an <em>attachment</em> which is provided by
 * {@link com.github.mauricio.netty.channel.ChannelHandlerContext}:
 * <pre>
 * public interface Message {
 *     // your methods here
 * }
 *
 * {@code @Sharable}
 * public class DataServerHandler extends {@link com.github.mauricio.netty.channel.SimpleChannelInboundHandler}&lt;Message&gt; {
 *   private final {@link com.github.mauricio.netty.util.AttributeKey}&lt{@link Boolean}&gt auth =
 *           new {@link com.github.mauricio.netty.util.AttributeKey}&lt{@link Boolean}&gt("auth");
 *
 *   // This handler will receive a sequence of increasing integers starting
 *   // from 1.
 *   {@code @Override}
 *   public void channelRead({@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx, {@link Integer} integer) {
 *     {@link com.github.mauricio.netty.util.Attribute}&lt{@link Boolean}&gt attr = ctx.getAttr(auth);
 *
 *     {@code @Override}
 *     public void channelRead({@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx, Message message) {
 *         {@link com.github.mauricio.netty.channel.Channel} ch = ctx.channel();
 *         if (message instanceof LoginMessage) {
 *             authenticate((LoginMessage) o);
 *             <b>attr.set(true)</b>;
 *         } else (message instanceof GetDataMessage) {
 *             if (<b>Boolean.TRUE.equals(attr.get())</b>) {
 *                 ch.write(fetchSecret((GetDataMessage) o));
 *             } else {
 *                 fail();
 *             }
 *         }
 *     }
 *     ...
 * }
 * </pre>
 * Now that the state of the handler is stored as an attachment, you can add the
 * same handler instance to different pipelines:
 * <pre>
 * public class DataServerInitializer extends {@link ChannelInitializer}&lt{@link com.github.mauricio.netty.channel.Channel}&gt {
 *
 *     private static final DataServerHandler <b>SHARED</b> = new DataServerHandler();
 *
 *     {@code @Override}
 *     public void initChannel({@link com.github.mauricio.netty.channel.Channel} channel) {
 *         channel.pipeline().addLast("handler", <b>SHARED</b>);
 *     }
 * }
 * </pre>
 *
 *
 * <h4>The {@code @Sharable} annotation</h4>
 * <p>
 * In the examples above which used an attachment,
 * you might have noticed the {@code @Sharable} annotation.
 * <p>
 * If a {@link com.github.mauricio.netty.channel.ChannelHandler} is annotated with the {@code @Sharable}
 * annotation, it means you can create an instance of the handler just once and
 * add it to one or more {@link ChannelPipeline}s multiple times without
 * a race condition.
 * <p>
 * If this annotation is not specified, you have to create a new handler
 * instance every time you add it to a pipeline because it has unshared state
 * such as member variables.
 * <p>
 * This annotation is provided for documentation purpose, just like
 * <a href="http://www.javaconcurrencyinpractice.com/annotations/doc/">the JCIP annotations</a>.
 *
 * <h3>Additional resources worth reading</h3>
 * <p>
 * Please refer to the {@link com.github.mauricio.netty.channel.ChannelHandler}, and
 * {@link ChannelPipeline} to find out more about inbound and outbound operations,
 * what fundamental differences they have, how they flow in a  pipeline,  and how to handle
 * the operation in your application.
 */
public interface ChannelHandler {

    /**
     * Gets called after the {@link com.github.mauricio.netty.channel.ChannelHandler} was added to the actual context and it's ready to handle events.
     */
    void handlerAdded(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called after the {@link com.github.mauricio.netty.channel.ChannelHandler} was removed from the actual context and it doesn't handle events
     * anymore.
     */
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if a {@link Throwable} was thrown.
     *
     * @deprecated  Will be removed in the future and only {@link com.github.mauricio.netty.channel.ChannelInboundHandler} will receive
     *              exceptionCaught events. For {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} the {@link com.github.mauricio.netty.channel.ChannelPromise}
     *              must be failed.
     */
    @Deprecated
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;

    /**
     * Indicates that the same instance of the annotated {@link com.github.mauricio.netty.channel.ChannelHandler}
     * can be added to one or more {@link ChannelPipeline}s multiple times
     * without a race condition.
     * <p>
     * If this annotation is not specified, you have to create a new handler
     * instance every time you add it to a pipeline because it has unshared
     * state such as member variables.
     * <p>
     * This annotation is provided for documentation purpose, just like
     * <a href="http://www.javaconcurrencyinpractice.com/annotations/doc/">the JCIP annotations</a>.
     */
    @Inherited
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Sharable {
        // no value
    }
}
