package org.tio.coding;

import java.util.Iterator;

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

    private class tlvIterator implements Iterator<ITlv> {

        int length = 0;

        @Override
        public boolean hasNext() {
            return length < data.length;
        }

        @Override
        public ITlv next() {
            byte type = data[length++];
            byte len = data[this.length++];
            byte[] val = new byte[len];
            System.arraycopy(data, this.length, val, 0, len);
            this.length += len;
            return new ITlv.Tlv(type, len, val);
        }
    }
}
