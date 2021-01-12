import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
/**
* Clase principal, funciona como sevidor para sincronización  
* de hora con el algoritmo de Cristian utilizando sockets UDP
* @author Jesus Rivera
**/

public class ServidorUDP
{
	/**
	* Metodo principal
	**/
	
	public static void main(String[] args)
	{

		final int puerto = 6525;
		byte[] buffer = new byte[12];

		try
		{
			System.out.println("--- Servidor iniciado ---\n");

			//Crea el socket al que enviara el datagrama
			DatagramSocket socket = new DatagramSocket(puerto);

			while(true)
			{
				//Crea un datagrama, y espera a recibir una nueva peticion del cliente
				DatagramPacket peticion = new DatagramPacket(buffer,buffer.length);
				socket.receive(peticion);

				//Obtiene la informacion del datagrama y la muestra en consola
				String mensaje = new String(peticion.getData());

				//Obtiene el puerto y la direccion del cliente
				int puertoCliente = peticion.getPort();
				InetAddress direccion = peticion.getAddress();
				
				//Crea un nuevo mensaje el cual sera enviado al cliente
				Date date = Calendar.getInstance().getTime();
				DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss S");
				mensaje = dateFormat.format(date);
				mensaje = "2:00:24 236";
				System.out.println("Peticion de cliente recibida - " + mensaje);
				buffer = mensaje.getBytes();
				
				//Crea un datagrama con el nuevo mensaje y lo envia al cliente
				DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length,direccion,puertoCliente);
				socket.send(respuesta);
			}		
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}	
}