package org.tio.coding;

class Tlv implements ITlv {

    private byte type;
    private byte len;
    private byte[] value;

    Tlv(byte type, byte len, byte[] value) {
        this.type = type;
        this.len = len;
        this.value = value;
    }

    @Override
    public byte type() {
        return type;
    }

    @Override
    public byte len() {
        return len;
    }

    @Override
    public byte[] val() {
        return value;
    }
}