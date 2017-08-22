package org.tio.common;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class AbstractPacket implements Packet {

    protected byte type;

    protected byte reserved;

    protected byte optLen;

    protected short bodyLen;

    protected byte checkSum;

    protected byte[] optData;

    protected short packetSeq;

    protected byte[] body;

    @Override
    public byte packetType() {
        return type;
    }

    @Override
    public Packet setPacketType(byte packetType) {
        this.type = packetType;
        return this;
    }

    @Override
    public boolean needReply() {
        return (type & 0x80) == 0x80;
    }

    @Override
    public Packet setNeedReplay() {
        type &= 0x80;
        return this;
    }

    @Override
    public byte reserved() {
        return reserved;
    }

    @Override
    public Packet setReserved(byte reserved) {
        this.reserved = reserved;
        return this;
    }

    @Override
    public byte optionalLength() {
        return optLen;
    }

    @Override
    public Packet setOptionalLength(byte optionalLength) {
        this.optLen = optionalLength;
        return this;
    }

    @Override
    public short bodyLength() {
        return bodyLen;
    }

    @Override
    public Packet setBodyLength(short bodyLength) {
        this.bodyLen = bodyLength;
        return this;
    }

    @Override
    public byte checkSum() {
        return checkSum;
    }

    @Override
    public Packet setCheckSum(byte checkSum) {
        this.checkSum = checkSum;
        return this;
    }

    @Override
    public byte[] header() {
        return new byte[]{
                Packet.magic >> 8, (byte) Packet.magic, type, reserved, optLen, (byte) (this.bodyLen >> 8), (byte) bodyLen, checkSum
        };
    }

    @Override
    public byte[] optional() {
        return optData;
    }

    @Override
    public Packet setOptional(byte[] optional) {
        this.optData = optional;
        return this;
    }

    @Override
    public short packetSeq() {
        return packetSeq;
    }

    @Override
    public Packet setPacketSeq(short count) {
        this.packetSeq = count;
        return this;
    }

    @Override
    public byte[] body() {
        return body;
    }

    @Override
    public Packet setBody(byte[] body) {
        this.body = body;
        return this;
    }
}
