package com.github.mauricio.async.db.mysql.message.client

import com.github.mauricio.netty.buffer.ByteBuf

case class SendLongDataMessage (
                                 statementId : Array[Byte],
                                 value : ByteBuf,
                                 paramId : Int )