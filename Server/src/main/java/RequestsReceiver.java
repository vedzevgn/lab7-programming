import connection.DatagramConnection;
import requests.Request;

public class RequestsReceiver implements Runnable{
    Request request;
    DatagramConnection manager;

    public RequestsReceiver(DatagramConnection manager) {
        this.manager = manager;
    }

    public void run()
    {
        Server.setCheckSkip(false);
        request = (Request) manager.receive();
        //Server.setRequest(request);
    }
}
