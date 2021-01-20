import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.text.ParseException; 

public class Usuario
{
	private final int puerto;
	private byte[] buffer;
	private String conexion;
	public Reloj reloj;	
	private InetAddress direccion_Servidor;

	/**
	* Constructor de la clase
	**/

	public Usuario()
	{
		this.puerto = 6250;
		this.buffer = new byte[12];
		this.conexion = "100000000000";
		enviarDatagrama();
	}

	/**
	* Metodo que envia un mensaje al servidor para obtener la hora de este y actualizar la propia
	**/
	public void enviarDatagrama()
	{
		DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss:S");
		Date hora_inicial = Calendar.getInstance().getTime();
		try
		{
			this.direccion_Servidor = InetAddress.getByName("25.110.204.69");
			DatagramSocket socket = new DatagramSocket();				
			
			this.buffer = this.conexion.getBytes();
			DatagramPacket peticion = new DatagramPacket(this.buffer,this.buffer.length,this.direccion_Servidor,this.puerto);
			socket.send(peticion);


			DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length);
			socket.receive(respuesta);


			Date hora_de_respuesta = Calendar.getInstance().getTime();

			String mensaje = new String(respuesta.getData());
			String hora_servidor = sincronizacion(dateFormat.format(hora_inicial),dateFormat.format(hora_de_respuesta),mensaje);

			String tiempo[] = hora_servidor.split(":");
			socket.close();

			reloj = new Reloj("Reloj",Integer.parseInt(tiempo[0]),Integer.parseInt(tiempo[1]),Integer.parseInt(tiempo[2]),Integer.parseInt(tiempo[3]));
			reloj.start();
	
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

	/**
	* Metodo para sincronizar relojes, utilizando el algoritmo de Cristian
	@param hora_inical hora en que se envio el mensaje al servidor
	@param hora_de_respuesta hora en la quese recibio la repsuesta del servidor
	@param hora_servidor hora obtenida del servidor
	@return hora sincronizada
	**/
	
	private String sincronizacion(String hora_inicial,String hora_de_respuesta,String hora_servidor)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm:ss:S");
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

		String aux = new String(dateFormat.format(sincronizada));
		String[] aux2 = aux.split(":");
		String tiempo = (aux2[0].equals("24"))?("00" + aux.substring(2)):aux;
		return tiempo;
	}

	/**
	* Envia una hora al servidor para modificar el reloj al que esta conectado
	* @param direccion_Servidor direccion ip del servidor
	* @param puerto puerdo del cliente al que se conectara
	* @param buffer buferr de datos que se envirara por el socket
	* @param nueva_hora hora con la que se actualizara el servidor
	* @return arreglo de enteros con la nueva hora del servidor sincronizada 
	**/
	public int[] modificar(InetAddress direccion_Servidor, int puerto, byte[] buffer, String nueva_hora)
	{
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:S");
		try
		{
			DatagramSocket socket = new DatagramSocket();
			buffer = ("9100" + nueva_hora).getBytes();
			DatagramPacket peticion = new DatagramPacket(buffer,buffer.length,direccion_Servidor,puerto);
			socket.send(peticion);

			Date hora_inicial = Calendar.getInstance().getTime();

			DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length);
			socket.receive(respuesta);


			Date hora_de_respuesta = Calendar.getInstance().getTime();

			String mensaje = new String(respuesta.getData());

			String hora_servidor = sincronizacion(dateFormat.format(hora_inicial),dateFormat.format(hora_de_respuesta),mensaje);

			String[] tiempo = hora_servidor.split(":");
			int[] hora_modificada = {Integer.parseInt(tiempo[0]),Integer.parseInt(tiempo[1]),Integer.parseInt(tiempo[2]),Integer.parseInt(tiempo[3])};
			this.reloj.setTime(hora_modificada[0],hora_modificada[1],hora_modificada[2],hora_modificada[3]);
			return hora_modificada;
		}
		catch (Exception e) 
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return null;
	}

	/**
	* Metodo para obtener la hora del reloj ejecutandose
	* @return arreglo de cadenas con los valores del reloj al momento de hacer la llamad delmetodo
	**/
	public String[] getHora()
	{
		return this.reloj.getFormatTime();
	}

	/**
	* Metodo para cambiar la hora del reloj ejecutandose
	* @param horas nueva hora del reloj
	* @param minutos nuevos minutos del reloj
	* @param segundos nuevos segundos del relojc	
	**/
	public void setHora(int horas, int minutos, int segundos)
	{
		String horas_formato = (horas < 10)?("0" + horas):String.valueOf(horas);
		String minutos_formato = (minutos < 10)?("0" + minutos):String.valueOf(minutos);
		String segundos_formato = (segundos < 10)?("0" + segundos):String.valueOf(segundos);
		String aux = horas_formato + ":" + minutos_formato + ":" + segundos_formato;
		int[] nueva_hora = modificar(this.direccion_Servidor,this.puerto,this.buffer,aux);
	}
}