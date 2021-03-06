package com.yongche.driver.message;

public class GetPublicKeyResponseMessage extends ResponseMessage {

    public String publicKey;
    
    public GetPublicKeyResponseMessage() {
        this.functionId = FUNCTION_ID_GET_PUBLIC_KEY_RESPONSE;
    }
    
    public int decode(byte[] payload, byte status) throws GroundhogMessageException {
        this.publicKey = new String(payload, DEFAULT_CHARSET);        
        return payload.length;
    }
}
