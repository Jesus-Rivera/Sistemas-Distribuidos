import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.text.ParseException; 

/**
* Clase principal, funciona como cliente para sincronización  
* de hora con el algoritmo de Cristian utilizando sockets UDP
* @author Jesus Rivera
**/

public class ClienteUDP
{

	/**
	* Método  encargado de sincronizar las horas obtenidas
	* @param hora_inicial Primera hora tomada al momento de sincronizar
	* @param hora_de_respuesta Hora tomada al momento de recibir respuesta del servidor
	* @param hora_servidor Hora recibida del servidor
	* @return Hora final, después de la sincronización
	**/
	
	private static String sincronizacion(String hora_inicial,String hora_de_respuesta,String hora_servidor)
	{

		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss S");
		Date inicial = null,servidor = null,respuesta = null;
		
		// Convierte los sring en Date con el formato establecido
		try {
			inicial = dateFormat.parse(hora_inicial);
			respuesta = dateFormat.parse(hora_de_respuesta);
			servidor = dateFormat.parse(hora_servidor);
		} 
		catch (ParseException ex) 
		{
			System.out.println(ex);
		}

		// Obtiene la diferencia entre la hora inicial y de respuesta
		int diff = (int)((respuesta.getTime() - inicial.getTime())/2);

		//Agrega la diferenia a la hora del servidor
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(servidor);
		calendar.add(Calendar.MILLISECOND, diff);
		Date sincronizada = calendar.getTime();
		return new String(dateFormat.format(sincronizada));
	}


	/**
	* Metodo principal
	**/
	
	public static void main(String[] args)
	{
		final int puerto = 6525;
		byte[] buffer = new byte[12];
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss S");

		try
		{
			//Crea el socket al que enviara el datagrama, y se crea la direccion
			InetAddress direccion_Servidor = InetAddress.getByName("10.0.0.16");
			DatagramSocket socket = new DatagramSocket();

			//Obtiene la hora actual con milisegundos
			Date hora_inicial = Calendar.getInstance().getTime();

			//Da formato a la hora, y la convierte en un arreglo de bytes para enviarla
			buffer = dateFormat.format(hora_inicial).getBytes();
			System.out.println("Hora de envio: " + dateFormat.format(hora_inicial));

			//Crea el datagrama con el mensaje y lo envia cpor el socket
			DatagramPacket peticion = new DatagramPacket(buffer,buffer.length,direccion_Servidor,puerto);
			socket.send(peticion);

			//Crea un nuevo datagrama por donde recivira la respuesta del servidor
			DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length);
			socket.receive(respuesta);

			Date hora_de_respuesta = Calendar.getInstance().getTime();

			//Muestra el mensaje de respuesta del servidor en pantalla
			String mensaje = new String(respuesta.getData());
			String nueva_hora = sincronizacion(dateFormat.format(hora_inicial),dateFormat.format(hora_de_respuesta),mensaje);

			System.out.println("Nueva hora: " + nueva_hora);

			//cierra el socket
			socket.close();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}	

	
}