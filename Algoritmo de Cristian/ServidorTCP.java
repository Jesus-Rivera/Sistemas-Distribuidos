import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.text.ParseException; 

/**
* Clase principal, funciona como sevidor para sincronización  
* de hora con el algoritmo de Cristian utilizando sockets TCP
* @author Jesus Rivera
**/

public class ServidorTCP
{

	/**
	* Metodo principal
	**/

	public static void main(String[] args)
	{
		ServerSocket socket_servidor = null;
		Socket socket_cliente = null;
		DataInputStream in;
		DataOutputStream out;

		final int puerto = 6000;
		try
		{
			//Crea el socket por el puerto establecido
			socket_servidor = new ServerSocket(puerto);
			System.out.println("--- Servidor iniciado ---\n");
			while(true)
			{
				//Espera mensaje del cliente
				socket_cliente = socket_servidor.accept();

				//Muestra el mensaje recibido por el cliente
				in = new DataInputStream(socket_cliente.getInputStream());
				String mensaje = in.readUTF();

				//Obtine la hora actual
				Date date = Calendar.getInstance().getTime();
				DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss S");
				mensaje = dateFormat.format(date);

				System.out.println("Peticion de cliente recibida - " + mensaje);
				//Envia el mensaje al cliente con la hora
				out = new DataOutputStream(socket_cliente.getOutputStream());
				out.writeUTF(mensaje);
				socket_cliente.close(); //Termina la conexion con el cliente
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}	
}