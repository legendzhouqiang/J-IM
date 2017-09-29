package org.tio.coding;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/15
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class TlvEncoder {

    private List<ITlv> tlvs = Lists.newArrayList();
    private int length;

    public TlvEncoder addTLV(byte type, byte len, byte[] val) {
        tlvs.add(new ITlv.Tlv(type, len, val));
        this.length += len + 2;
        return this;
    }

    public byte[] toByteArray() {
        byte[] data = new byte[this.length];
        int srcPos = 0;
        for (ITlv tlv : tlvs) {
            data[srcPos++] = tlv.type();
            data[srcPos++] = tlv.len();
            System.arraycopy(tlv.val(), 0, data, srcPos, tlv.len());
            this.length += tlv.len();
        }
        return data;
    }

}
