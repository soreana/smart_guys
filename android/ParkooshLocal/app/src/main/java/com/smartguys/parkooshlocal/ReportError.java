package com.smartguys.parkooshlocal;

import org.apache.http.HttpResponse;

public abstract class ReportError extends Throwable
{
    public static final long serialVersionUID=100L;
    public ReportError()	{}
    public String toString()
    { return declaration(); }
    public abstract String declaration();

    public static class BlueNoDeviceIntro extends ReportError
    {
        public static final long serialVersionUID=101L;
        public BlueNoDeviceIntro()	{}
        public String declaration()
        {	return "No device name given";	}
    }
    public static class BlueBadDeviceIntro extends ReportError
    {
        public static final long serialVersionUID=102L;
        protected String Name;
        public BlueBadDeviceIntro(String name)	{Name=name;}
        public String declaration()
        {	return "No bound device found with given name: "+Name;	}
    }
    public static class BlueSocketFailed extends ReportError
    {
        public static final long serialVersionUID=103L;
        public BlueSocketFailed()	{}
        public String declaration()	{return "could not create bluetooth socket.";}
    }
    public static class BlueSocketConnectionFailed extends ReportError
    {
        public static final long serialVersionUID=104L;
        public BlueSocketConnectionFailed()	{}
        public String declaration()	{ return "could not connect socket."; }
    }
    public static class NoNumOfLots extends ReportError
    {
        public static final long serialVersionUID=105L;
        public NoNumOfLots()	{}
        public String declaration()	{return "Number of lots is not provided or not valid.";}
    }
    public static class BadThreadDelay extends ReportError
    {
        public static final long serialVersionUID=106L;
        public BadThreadDelay()	{}
        public String declaration()	{return "The sleep was interrupted.";}
    }
    public static class BadBluetoothRead extends ReportError
    {
        public static final long serialVersionUID=107L;
        public BadBluetoothRead()	{}
        public String declaration()	{return "Could not create good bluetooth scanner or read.";}
    }
    public static class BadBluetoothWrite extends ReportError
    {
        public static final long serialVersionUID=108L;
        public BadBluetoothWrite()	{}
        public String declaration()	{return "Could not create good bluetooth output stream.";}
    }
    public static class BTInputParseError extends ReportError
    {
        public static final long serialVersionUID=109L;
        protected String IncorrectString;
        public BTInputParseError(String bad)    {IncorrectString=bad;}
        public String declaration() {return "The received input string from bt device has parse error:\n"+IncorrectString;};
    }
    public static class ServerStartFailure extends ReportError
    {
        public static final long serialVersionUID=110L;
        public ServerStartFailure() {}
        public String declaration() {return "could not start server.";}
    }
    public static class BadHttpPostRequest extends ReportError
    {
        public static final long serialVersionUID=111L;
        public BadHttpPostRequest() {}
        public String declaration() {return "internal error: bad http post request format.";}
    }

    public static class HttpPostSendFailure extends ReportError
    {
        public static final long serialVersionUID=112L;
        protected String MoreInfo;
        public HttpPostSendFailure(String info)
        {
           MoreInfo=info;
           if(info==null)
               MoreInfo="";
        }
        public HttpPostSendFailure()    {MoreInfo="";}
        public String declaration() {return "could not send http post request, more info:\n"+MoreInfo+"\n";}
    }

    public static class BadHttpListenMethod extends ReportError
    {
        public static final long serialVersionUID=113L;
        public BadHttpListenMethod()    {}
        public String declaration()     {return "the server did not call me (for reserving) with post method.";}
    }

    public static class BadHttpListenRequest extends ReportError
    {
        public static final long serialVesionUID=114L;
        public BadHttpListenRequest()   {}
        public String declaration()     {return "the reserve message from server is not valid.";}
    }
}
