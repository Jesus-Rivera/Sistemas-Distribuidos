import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.text.ParseException; 

/**
* Clase principal, funciona como cliente para sincronización  
* de hora con el algoritmo de Cristian utilizando sockets TCP
* @author Jesus Rivera
**/

public class ClienteTCP
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
			servidor = dateFormat.parse(hora_de_respuesta);
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

	public static void main(String[] args) {
		final String host = "10.0.0.16";
		final int puerto = 6000;
		DataInputStream in;
		DataOutputStream out;
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss S");

		try
		{
			//Crea un socket y se conecta al servidor
			Socket socket = new Socket(host,puerto);


			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			//Obtiene la hora inicial y envia un mensaje al servidor
			Date hora_inicial = Calendar.getInstance().getTime();
			out.writeUTF(dateFormat.format(hora_inicial));

			//Obtiene la horo en la que respondio el servidor
			String mensaje = (in.readUTF());
			Date hora_de_respuesta = Calendar.getInstance().getTime();

			//Calcula la hora sincronizada, muestra en pantalla y termina la conexion
			String nueva_hora = sincronizacion(dateFormat.format(hora_inicial),dateFormat.format(hora_de_respuesta),mensaje);
			System.out.println("Nueva hora: " + nueva_hora);
			socket.close();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}