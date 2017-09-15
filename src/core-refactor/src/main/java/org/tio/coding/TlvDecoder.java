package org.tio.coding;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/15
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class TlvDecoder {

    private byte[] data;

    public TlvDecoder setData(byte[] data) {
        this.data = data;
        return this;
    }


    public Iterator iterater() {
        return new tlvIterator();
    }

    private class tlvIterator implements Iterator {

        int len = 0;

        @Override
        public boolean hasNext() {
            if (len < data.length) {
                return true;
            }
            return false;
        }

        @Override
        public Itlv next() {
            byte type = data[len++];
            byte len = data[this.len++];
            byte[] val = new byte[len];
            System.arraycopy(data, this.len, val, 0, len);
            this.len += len;
            return new Tlv(type, len, val);
        }
    }

    private class Tlv implements Itlv {

        private byte type;
        private byte len;
        private byte[] value;

        private Tlv(byte type, byte len, byte[] value) {
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
}
