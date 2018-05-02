package com.yongche.driver.message;

import com.yongche.driver.inter.IMessage;

public class AcknowledgeMessage extends ResponseMessage {
	public String message;

    public AcknowledgeMessage() {
        this.functionId = FUNCTION_ID_ACK;
    }

    public int decode(byte[] payload, byte status) throws GroundhogMessageException {
        try {
            this.message = new String(payload, IMessage.DEFAULT_CHARSET);
        } catch (NullPointerException e) {
            throw new GroundhogMessageException(e);
        }
        return payload.length;
    }
}
