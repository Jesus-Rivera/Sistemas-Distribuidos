import java.net.*;
import java.io.*; 
import java.time.format.*;
import java.time.*; 
import java.util.*;

public class Maestro extends Thread
{
	public Reloj reloj[];
	public ActualizaTiempo actualizador;

	/**
	* Constructor de la clase
	**/

	public Maestro()
	{
		int i;
		int hora = 0;
		int minutos = 0;
		int segundos = 0;
		int milisegundos = 0;
		this.reloj = new Reloj[3];
		
		for(i = 0; i < 3; i ++)
		{
			this.reloj[i] = new Reloj("Reloj " + i,hora,minutos,segundos,milisegundos);
			this.reloj[i].start();
		}
	}

	/**
	* Meto de ejecucion del hilo
	* En este metodo mantendra el servidor a la espera del mensaje de algun cliente
	* Gestionando el tipo de mensaje recibido y la informacion que regresara utilizando sockets udp
	**/

	public void run()
	{
		
		int i;

		final int puerto = 6969;
		byte[] buffer = new byte[12];
		
		System.out.println("--- Servidor iniciado ---\n");	


		try
		{	
			DatagramSocket socket = new DatagramSocket(puerto);
			this.actualizador = new ActualizaTiempo(socket);
			this.actualizador.start();
			while(true)
			{
				// Recibe un mensaje de algun cliente, este puede estar solicitando la hora de algun reloj o la modificacion
				
				DatagramPacket peticion = new DatagramPacket(buffer,buffer.length);
				socket.receive(peticion);
				


				String mensaje = new String(peticion.getData());
				int aux = Integer.parseInt(mensaje.substring(0,1));
				
				System.out.println("Auxiliar: "+aux);
				
				
				if(aux == 9) //En caso de solicitar modificar algun reloj
				{
					
				System.out.println("Auxiliar: "+aux);
					aux = Integer.parseInt(mensaje.substring(1,2));
					String[] nueva_hora = mensaje.substring(4).split(":");
	
					this.reloj[aux - 1].setTime(Integer.parseInt(nueva_hora[0]),Integer.parseInt(nueva_hora[1]),Integer.parseInt(nueva_hora[2]),0);
					Thread.sleep(5);
				}
				
				String[] tiempo = new String[4];
				tiempo = this.reloj[aux - 1].getFormatTime();

				mensaje = tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0];
				buffer = mensaje.getBytes();

				int puertoCliente = peticion.getPort();
				InetAddress direccion = peticion.getAddress();

				DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length,direccion,puertoCliente);
				socket.send(respuesta);

				
				
			}
		}catch (Exception e) {
			System.err.println("Error socket maestro: "+e.getMessage());
			System.exit(1);
		}
	}

	/**
	* Metodo para obtener la hora de un reloj especifico
	* @param identificador reloj al cual se accedera
	* @return arreglo de cadenas con los datos del reloj
	**/

	public String[] getHora(int identificador)
	{
		return this.reloj[identificador].getFormatTime();
	}
	public String[] getUTC()
	{
		return this.actualizador.getFormatTime();
	}

	/**
	* Cambia la hora de un reloj en especifico
	* @param identificador reloj al que se le cambiara la hora
	* @param hora nueva hora del reloj
	* @param minutos nuevos minutos del reloj
	* @param segundos nuevos segundos del reloj
	**/

	public void setHora(int identificador,int hora, int minutos, int segundos)
	{
		this.reloj[identificador].setTime(hora,minutos,segundos,00);
		
	}

	public void setUTC()
	{
		String [] utc = getUTC();
		for(int i=0; i<2; i++)
		{
			this.reloj[i].setTime( Integer.parseInt(utc[3]),Integer.parseInt(utc[2]), Integer.parseInt(utc[1]),00);
		}
	}

}