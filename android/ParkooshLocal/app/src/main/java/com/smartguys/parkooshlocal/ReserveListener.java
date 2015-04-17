package com.smartguys.parkooshlocal;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ReserveListener extends NanoHTTPD
{
    public static final String SlotNumberURLField=Reporter.SlotNumberURLField;
    public static final String SlotReserveURLField=Reporter.SlotReserveURLField;

    protected OutputStream BTOutstream;

    public ReserveListener(Integer port, OutputStream btout)
    {
        super(port);
        BTOutstream=btout;
    }

    @Override
    public Response serve(IHTTPSession session)
    {
        //return new Response("received:)");
        Integer slotNumber;
        String slotReserve;

        Map<String,String> parameters=session.getParms();
        if(!session.getMethod().equals(Method.GET))
            return new Response("failure\nonly post method.");
        if(parameters.get(SlotNumberURLField)==null)
            return new Response("failure\nno slot number.");
        if(parameters.get(SlotReserveURLField)==null)
            return new Response("failure\nno slot reserve status.");
        slotNumber=Integer.parseInt(parameters.get(SlotNumberURLField));
        slotReserve=parameters.get(SlotReserveURLField);
        System.err.println("just gotten data:\n"+SlotNumberURLField+" : "+slotNumber.toString()+"\n"+SlotReserveURLField+" : "+slotReserve);
        try
        {
            BTOutstream.write(reserveStatusReportForBT(slotNumber, slotReserve).getBytes());
            BTOutstream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
            return new Response("failure\ncould not send to bluetooth device");
        }
        return new Response("ok");
    }

    public static String reserveStatusReportForBT(Integer slotNumber, String r)
    {
        return slotNumber.toString()+":"+r+"\n";
    }
}
