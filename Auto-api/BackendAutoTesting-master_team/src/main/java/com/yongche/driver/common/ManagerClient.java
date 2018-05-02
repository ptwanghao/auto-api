package com.yongche.driver.common;

import java.io.IOException;

import com.yongche.driver.message.MessageException;
import com.yongche.driver.message.AcknowledgeMessage;
import com.yongche.driver.message.AssignWorkerRequestMessage;
import com.yongche.driver.message.AssignWorkerResponseMessage;
import com.yongche.driver.message.GroundhogMessage;

public class ManagerClient extends GroundhogClient {
    
    private String workerIp;
    private int workerPort;
    private int maxAge;

    public ManagerClient() {
    }

    public void requestWorker() throws IOException, MessageException {
        
        AssignWorkerRequestMessage requestWorker = new AssignWorkerRequestMessage();
        
        long sequence = GroundhogClient.getNextSequence(); 
        requestWorker.sequenceId = sequence;
        requestWorker.userType = this.userType;
        requestWorker.userId = this.userId;
        requestWorker.deviceId = this.deviceId;
        
        this.parser.sendMsg(requestWorker, this.out);
        
        //set read timeout
        this.socket.setSoTimeout(this.responseTimeout);
        GroundhogMessage nextMsg = this.parser.getMsg(this.in);
        //reset read timeout
        this.socket.setSoTimeout(0);
        
        if (!(nextMsg instanceof AssignWorkerResponseMessage)) {
            throw new ClientException("not assign worker response when request to assgin a worker, msg:"
                    + ((AcknowledgeMessage)nextMsg).message);
        }
        
        AssignWorkerResponseMessage response = (AssignWorkerResponseMessage) nextMsg;
        
        this.workerIp = response.workerIp;
        this.workerPort = response.port;
        this.maxAge = response.maxAge * 1000; //millisecond
        return;
    }

    public String getWorkerIp() {
        return this.workerIp;
    }

    public int getWorkerPort() {
        return this.workerPort;
    }

    public int getMaxAge() {
        return this.maxAge;
    }
    
}
