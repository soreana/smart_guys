package com.smartguys.parkooshlocal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Reporter extends IntentService
{
    public static final String DevNameParameter="TargetDeviceName";
    public static final String SharedPreferencesDBName="com.smartguys.parkooshlocal.reporter.preferences";
    public static final String ContinueCommandServiceBoolKey="ContinueCommandServiceBoolKey";
    public static final String RunningStatusKey="RunningStatusKey";
    protected static final UUID Reporter_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int ErrorMarginDelay	=	100;
    public static final String SlotNumberURLField="slot_no";
    public static final String SlotStatusURLField="slot_status";
    public static final String SlotReserveURLField="reserve";
    protected static final String ServerIP="192.168.43.49"; //TODO: get real server ip
    protected static final Integer ServerPort=3000;
    protected static final Integer ListeningPort=3000;

    protected BluetoothDevice TargetDevice;
    protected ReserveListener Listener;
    protected String TargetDeviceName;
    protected BluetoothAdapter BTAdapter;
    protected BluetoothSocket BTSocket;
    protected static SharedPreferences SharedDB;
    protected String TargetDeviceMacAdr;

    public static int getErrorMarginDelay()	{ return ErrorMarginDelay; }
    public String getTargetDeviceName() { return TargetDeviceName; }
    public String getTargetDeviceMacAdr() { return TargetDeviceMacAdr; }

    public Reporter()   {   super(Reporter.class.getSimpleName());  }

    @Override
    public void onCreate()
    {
        super.onCreate();
        reset();
    }

    public static void stopReporterService()
    {
        SharedDB.edit().putBoolean(ContinueCommandServiceBoolKey, false).apply();
        SharedDB.edit().putBoolean(RunningStatusKey, false).apply();
    }

    public static boolean isRunning()
        {   return SharedDB.getBoolean(RunningStatusKey, false);    }

    public void reset()
    {
        SharedDB=getSharedPreferences(SharedPreferencesDBName, MODE_PRIVATE);
        SharedDB.edit().putBoolean(ContinueCommandServiceBoolKey, true).apply();
        SharedDB.edit().putBoolean(RunningStatusKey, false).apply();
        Listener=null;
        resetBluetoothParameters();
    }

    protected void resetBluetoothParameters()
    {
        TargetDevice=null;
        TargetDeviceName=null;
        TargetDeviceMacAdr=null;
        BTAdapter=BluetoothAdapter.getDefaultAdapter();
        if(BTSocket!=null)
        {
            do {
                try{BTSocket.getInputStream().close();} catch (IOException e){continue;}
            } while (false);
            do {
                try{BTSocket.getOutputStream().close();} catch (IOException e){continue;}
            } while (false);
            do {
                try {BTSocket.close();}
                catch (IOException e) {continue;}
            }while(false);
        }
        BTSocket=null;
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        Scanner btscanner;
        System.err.println("Reporter: service entered.");
        reset();
        try
        {
            System.err.println("Reporter: in try block.");
            setReportingParamters(intent);
            setupBTConnection(intent);
            try { Listener = new ReserveListener(ListeningPort, BTSocket.getOutputStream());}
                catch (IOException e)   {throw new ReportError.BadBluetoothWrite();}
            try{Listener.start();}
                catch(IOException e)    { throw new ReportError.ServerStartFailure(); }
            try {   btscanner=new Scanner(BTSocket.getInputStream());   }
                catch (IOException e) {throw new ReportError.BadBluetoothRead();}
            confirmRunningStatus();
            System.err.println("Reporter: before main loop");
            while(SharedDB.getBoolean(ContinueCommandServiceBoolKey, true))
            {
                try {Thread.sleep(ErrorMarginDelay);}
                catch(InterruptedException e) {throw new ReportError.BadThreadDelay();}
                System.err.println("Reporter: in main loop");
                handleLocalChange(btscanner);
            }
            System.err.println("Reporter: after main loop");
        }
        catch(ReportError e)
        {
            informError(e.toString());
        }
        if(Listener!=null && Listener.isAlive())
            Listener.stop();
        resetBluetoothParameters();
        stopReporterService();
    }

    //gets lot status from bt device and informs it to the server. do not process anything
    protected void handleLocalChange(Scanner btscanner)
    {
        String rawString, lotStatus;
        Integer lotNumber;
        rawString=getNextRawString(btscanner);
        System.err.println("bt gottent string = "+rawString);
        try
        {
            lotStatus = getLotStatus(rawString);
            lotNumber = getLotNumber(rawString);
            sendLocalStatus(lotNumber,lotStatus);
        }
        catch (ReportError e)
        {
            informError(e.toString());
        }
    }

    //get & post method
    protected void sendLocalStatus(Integer lotNumber, String lotStatus) throws ReportError
    {
        HttpClient httpClient=new DefaultHttpClient();
        /*
        HttpPost httpPost=new HttpPost("http://"+ServerIP+":"+ServerPort+"/status/");
        ArrayList<NameValuePair> urlArgs=new ArrayList<NameValuePair>(2);
        urlArgs.add(new BasicNameValuePair(SlotNumberURLField, lotNumber.toString()));
        urlArgs.add(new BasicNameValuePair(SlotStatusURLField, lotStatus));
        */
        HttpGet httpGet = new HttpGet("http://"+ServerIP+":"+ServerPort+"/status/"+SlotNumberURLField+"="
                                +lotNumber.toString()+"&"+SlotStatusURLField+"="+lotStatus);



        try
        {
            /*
            httpPost.setEntity(new UrlEncodedFormEntity(urlArgs));
            httpClient.execute(httpPost);
            */
            httpClient.execute(httpGet);
        }
        catch (UnsupportedEncodingException e)	{	throw new ReportError.BadHttpPostRequest();	}
        catch (ClientProtocolException e)   	{   throw new ReportError.HttpPostSendFailure(e.getMessage());    }
        catch (IOException e)           	    {   throw new ReportError.HttpPostSendFailure(e.getMessage());    }
    }

    //receives the input string as int:string
    protected String getNextRawString(Scanner sc)
    {
        sc.useDelimiter("\\s*[$]\\s*");
        String result="";
        do	{result=sc.next();}while(result.length()==0);
        result.replaceAll("\\s","");//removing white space
        return result;
    }

    protected String getLotStatus(String rawString) throws ReportError
    {
        Scanner sc=new Scanner(rawString);
        sc.useDelimiter("\\s*[:]\\s*");
        if(!sc.hasNext())
            throw new ReportError.BTInputParseError(rawString);
        sc.next();
        if(!sc.hasNext())
            throw new ReportError.BTInputParseError(rawString);
        String result=sc.next();
        if(result.length()==0)
            throw new ReportError.BTInputParseError(rawString);
        return result;
    }

    protected int getLotNumber(String rawString) throws ReportError
    {
        Scanner sc=new Scanner(rawString);
        sc.useDelimiter("\\s*[:]\\s*");
        if(!sc.hasNextInt())
            throw new ReportError.BTInputParseError(rawString);
        return sc.nextInt();
    }

    protected void confirmRunningStatus()
    {
        SharedDB.edit().putBoolean(RunningStatusKey, true).apply();
        //TODO: display notification
    }

    protected void setReportingParamters(Intent intent) throws ReportError
        {   return ;	}


    protected void setupBTConnection(Intent intent) throws ReportError
    {
        if(!BTAdapter.isEnabled())
            BTAdapter.enable();
        if(BTAdapter.isDiscovering())
            BTAdapter.cancelDiscovery();
        String devName=intent.getExtras().getString(DevNameParameter);
        if(devName==null)
            throw new ReportError.BlueNoDeviceIntro();
        Set<BluetoothDevice> devices	=	 BTAdapter.getBondedDevices();
        for(BluetoothDevice bt : devices)
            if(bt.getName().equalsIgnoreCase(devName))
                TargetDevice=bt;
        if(TargetDevice==null)
            throw new ReportError.BlueBadDeviceIntro(devName);
        TargetDeviceName=TargetDevice.getName();
        TargetDeviceMacAdr=TargetDevice.getAddress();
        try {BTSocket=TargetDevice.createRfcommSocketToServiceRecord(Reporter_UUID); }
        catch (IOException e)
        {throw new ReportError.BlueSocketFailed();}
        try { BTSocket.connect(); }
        catch (IOException e) { throw new ReportError.BlueSocketConnectionFailed(); }
        System.err.println("successfully created & connected socket.");
        System.err.println("Bluetooth: TargetDevice mac: "+TargetDeviceMacAdr);
    }

    protected void informError(final String m)
    {
        System.err.println(m);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {@Override
                                     public void run() {Toast.makeText(Reporter.this, m, Toast.LENGTH_SHORT).show();}});
    }

}