import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Cluster;
import org.apache.hadoop.hbase.rest.client.RemoteHTable;
import org.apache.hadoop.hbase.util.Bytes;

public class Server
{
	private static Socket socket1;
	private static Socket socket2;
	
	public static void main(String[] args){
		try{
			
			int rowNo = 1;
			float humidity,tc,tf;
			
			int port1 = 4445;
			int port2 = 4446;
			
			ServerSocket serverSocket1 = new ServerSocket(port1);
			ServerSocket serverSocket2 = new ServerSocket(port2);
			socket2 = serverSocket2.accept();
			
			Configuration conf = HBaseConfiguration.create();
		    Cluster cluster = new Cluster();
		    cluster.add("192.168.1.4", 8080); // Cluster Set - up a cluster list adding all known REST server hosts.
		    Client client = new Client(cluster); // Client Create the client handling the HTTP communication.
		    RemoteHTable table = new RemoteHTable(client, "SensData"); // Table Create a remote table instance, wrapping the REST access into a familiar interface.
		    
			while(true) {
				socket1 = serverSocket1.accept();
				InputStream is1 = socket1.getInputStream();
				InputStreamReader isr1 = new InputStreamReader(is1);
				BufferedReader br1 = new BufferedReader(isr1);
				String msg1 = br1.readLine();
				System.out.println("Message received from client connected to port: "+port1+": "+msg1);
				
				String[] readings1 = msg1.split("\t+");
				
				InputStream is2 = socket2.getInputStream();
				InputStreamReader isr2 = new InputStreamReader(is2);
				BufferedReader br2 = new BufferedReader(isr2);
				String msg2 = br2.readLine();
				System.out.println("Message received from client connected to port: "+port2+": "+msg2);
				
				String[] readings2 = msg2.split("\t+");
				
				//Compute Average of two Sensor
				humidity = (Float.parseFloat(readings1[0])+Float.parseFloat(readings2[0]))/2;
				tc = (Float.parseFloat(readings1[1])+Float.parseFloat(readings2[1]))/2;
				tf = (Float.parseFloat(readings1[2])+Float.parseFloat(readings2[2]))/2;
				
				System.out.println("----> AVERAGE: "+humidity+"\t"+tc+"\t"+tf+"\t"+readings1[3]);
				
				Put p = new Put(Bytes.toBytes("row"+rowNo)); 
				p.add(Bytes.toBytes("ambiance"), Bytes.toBytes("humidity"), Bytes.toBytes(""+humidity));    
				p.add(Bytes.toBytes("ambiance"), Bytes.toBytes("tempc"), Bytes.toBytes(""+tc));
				p.add(Bytes.toBytes("ambiance"), Bytes.toBytes("tempf"), Bytes.toBytes(""+tf));
				p.add(Bytes.toBytes("soil"), Bytes.toBytes("moisture"),Bytes.toBytes(readings1[3]));
				
				try {
					table.put(p);
					System.out.println("data inserted");
				} catch (IOException e) {
					System.out.println("ERROR: cant store data in Hbase...");
					e.printStackTrace();
				}
				
				rowNo++;
			}
		}catch (Exception e) {
			System.out.println("Server Failed.... can't open the port.");
			//e.printStackTrace();
		}finally {
			try {
				socket1.close();
				socket2.close();
			} catch (IOException e) {
				System.out.println("Server Failed.... can't close the socket.");
				//e.printStackTrace();
			}
		}
	}
}