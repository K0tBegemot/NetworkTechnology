package Handlers;

import Attachments.DnsAttachment;
import DNS.DomainNameResolver;
import Exceptions.HandlerException;
import Exceptions.HandlerException.Types;
import Logger.GlobalLogger;
import Logger.LogWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class DnsResponseHandler implements Handler {

    private static GlobalLogger workflowLogger = GlobalLogger.LoggerCreator.getLogger(GlobalLogger.LoggerType.WORKFLOW_LOGGER);
    @Override
    public void handle(SelectionKey key) throws HandlerException {
        SelectableChannel channel = key.channel();
        if(!(channel instanceof DatagramChannel) || !(key.attachment() instanceof DnsAttachment))
        {
            System.err.println("::::");
        }
        DatagramChannel dnsChannel = (DatagramChannel) channel;
        if(dnsChannel == null)
        {
            LogWriter.logWorkflow("FATAL ERROR. Datagram channel is null in DNSResponseHandler. Server will be terminated", workflowLogger);
            throw new HandlerException(null, Types.DNS_RESOLVER_ERROR, "FATAL ERROR. Datagram channel is null in DNSResponseHandler.", "");
        }
        DnsAttachment attachment = (DnsAttachment) key.attachment();
        ByteBuffer buffer = attachment.getBuffer();
        buffer.clear();
        // receive the datagram
        try {
            if (dnsChannel.receive(buffer) == null) {
                System.err.println("NULL " + Integer.toString(buffer.position()));
                buffer.clear();
                return;                                 // no datagram immediately available
            }
            System.err.println(Integer.toString(buffer.position()));
        }
        catch (IOException e) {
            LogWriter.logWorkflow("FATAL ERROR. IOException was catched while DNSResponceHandler executes. Server will be terminated", workflowLogger);
            throw new HandlerException(e, Types.DNS_RESOLVER_ERROR, "FATAL ERROR. DNS resolver exception. Server will be terminated.", "");
        }
        catch(Exception e){
            LogWriter.logWorkflow("FATAL ERROR. STRANGE EXCEPTION in DnsResponseHandler", workflowLogger);
            throw new HandlerException(e, Types.DNS_RESOLVER_ERROR, "FATAL ERROR. STRANGE EXCEPTION in DnsResponseHandler", "");
        }
        buffer.flip();
        byte[] respData = Arrays.copyOfRange(buffer.array(), 0, buffer.limit());
        buffer.clear();

        DomainNameResolver resolver = DomainNameResolver.getResolver();
        resolver.parseResponse(respData);
    }
}
