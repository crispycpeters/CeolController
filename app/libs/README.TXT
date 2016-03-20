Quick start to use the MobileTelnet SDK:

Step 1. unzip the tar file and copy the 4 platform folders and 
        mobiletelnetsdk.jar to libs folder under your Android project

Step 2. load the library via System.loadLibrary("mobiletelnetsdkjni") in onCreate()

Step 3. include package mobiletelnet.feng.gao in your java files

Step 4. implement notificationHandler and notificationConnectionStatus functions 
        in a class that implements interface TelnetNotification

Step 5. instantiate the new class that implements the TelnetNotification interface

Step 6. call TelnetAPIs.TelnetSetDataHandler(instance) with the instance of the new class

Step 7. init the telnet backed via TelnetAPIs.TelnetInit(telnet_ip, telnet_port)

Step 8. pumping the telnet messages via TelnetAPIs.TelnetPumpMessage() in a dedicated thread

Step 9. call TelnetAPIs.TelnetSend(cmd) to send user commands
		or call TelnetInternalCmd(cmd, arg) to send some internal special commands
		- int  CMD_LOGOUT    = 1;   		arg = ""
		- int  CMD_INTERUPT  = 2;   		arg = ""
		- int  CMD_ARROW_UP  = 3;   		arg = ""
		- int  CMD_ARROW_DN  = 4;   		arg = ""
		- int  CMD_CLEAR_CMD = 5;			arg is a command on remote console that has not be executed
		- int  CMD_TAB  	 = 6;			arg = arg + '\t'

Note: Your application will receive telnet messages in the thread pumping the messages(Step 8 above),
      Therefore you need to handle updating UI with received messages using cross-thread mechanism.
      For instance using Handler to send message to main thread.

Hope you will find it useful, enjoy!
