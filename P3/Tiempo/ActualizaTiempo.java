import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.time.format.*;
import java.time.*; 
import java.util.*;
import java.net.*;
import java.io.*; 
public class ActualizaTiempo extends Thread
{
	
	private boolean ejecutar;
	private String horas_formato;
	private String minutos_formato;
	private String segundos_formato;
	private String ms_formato;
	private DatagramSocket socketo;
	private Reloj reloj[];
	private Gui V;
	public ActualizaTiempo(DatagramSocket socket)
	{
		this.segundos_formato = "";
		this.minutos_formato = "";
		this.horas_formato = "";
		this.ms_formato = "";
		this.socketo=socket;

	}
	/* @Override
	public void start()
	{
		this.run(socketo);
	} */
	public void run()
	{

		int i;
		this.ejecutar = true;
		byte[] buffer = new byte[12];
		while(this.ejecutar)
		{
			try
			{
				//DatagramSocket socket = new DatagramSocket(6250);
				while(true)
				{
					DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM ).withZone( ZoneId.systemDefault() );
					Instant instant = Instant.now();
					System.out.println(formatter.toString());
					String mensaje = new String();
					
					String output = formatter.format( instant );
					//System.out.println("output: " + output );
					
					String strNew = output.replaceFirst("[[a-zA-Z]*[0-9]*[ ]*[.]]{13}", "");
					this.enviarReloj(2,strNew,1);
					
					/* String[] tiempo = strNew.toString().split(":");
					
					this.horas_formato=tiempo[0];
					this.minutos_formato=tiempo[1];
					this.segundos_formato=tiempo[2];
					this.ms_formato="10";

					System.out.println("Horas: "+horas_formato);
					System.out.println("min: "+minutos_formato);
					System.out.println("seg: "+segundos_formato);

					// Recibe un mensaje de algun cliente, este puede estar solicitando la hora de algun reloj o la modificacion

					mensaje = tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0] + ":" + "0";
					buffer = mensaje.getBytes();
					
					int puertoCliente = 6250;
					InetAddress direccion = InetAddress.getByName("25.7.179.21");
					//InetAddress direccion = InetAddress.getByName("25.110.204.69");

					DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length,direccion,puertoCliente);
					socketo.send(respuesta);
					if (V!=null)
						setUTC(V); */

					Thread.sleep(15000);
					
					
					
				}
			}catch(Exception  e)
			{
				System.err.println(e.getMessage());
				System.out.println("CHAVAL NOOO");
			}

			


		}
	}

	public void enviarReloj(int identificador, String strNew, int flag)
	{
		byte[] buffer = new byte[12];
		String mensaje = new String();
			try
			{
					String[] tiempo = strNew.toString().split(":");
					
					/* System.out.println("El tiempo es: "+tiempo[0]);
					System.out.println(tiempo[0]);
					System.out.println(tiempo[1]);
					System.out.println(tiempo[2]); */

					this.horas_formato=tiempo[0];
					this.minutos_formato=tiempo[1];
					this.segundos_formato=tiempo[2];
					InetAddress direccion;
					DatagramPacket respuesta;
					this.ms_formato="10";

					System.out.println("Horas: "+horas_formato);
					System.out.println("min: "+minutos_formato);
					System.out.println("seg: "+segundos_formato);

					// Recibe un mensaje de algun cliente, este puede estar solicitando la hora de algun reloj o la modificacion

					mensaje = tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0] + ":" + "0";
					buffer = mensaje.getBytes();
					
					int puertoCliente = 6250;
					
					if(identificador==0)
					{

						direccion = InetAddress.getByName("25.110.204.69");
						//direccion = InetAddress.getByName("25.7.179.21");
					}
					else if(identificador==1)
					{
						direccion = InetAddress.getByName("25.1.94.8");
						//direccion = InetAddress.getByName("25.7.179.21");
					}
					else
					{
						 direccion = InetAddress.getByName("25.110.204.69");
						 respuesta = new DatagramPacket(buffer,buffer.length,direccion,puertoCliente);
						 socketo.send(respuesta);
						 direccion = InetAddress.getByName("25.1.94.8");
						 //direccion = InetAddress.getByName("25.7.179.21");
						//InetAddress direccion = InetAddress.getByName("25.7.179.21");
					}
					respuesta = new DatagramPacket(buffer,buffer.length,direccion,puertoCliente);
					socketo.send(respuesta);
					if (V!=null && flag != 0)
						setUTC(V);

					
				}catch(Exception  e)
			{
				System.err.println("Actualizador / Enviar()+ :"+e.getMessage());
			}

	}
	/**
	* Detiene la ejecucion del reloj
	**/

	public void detener()
	{
		this.ejecutar = false;
	}



	public void setUTC(Gui V)
	{
		this.V=V;
		String [] utc = V.servidor.getUTC();		
		for(int i=0; i<3; i++)
		{
			this.V.servidor.setHora(i, Integer.parseInt(utc[3]),Integer.parseInt(utc[2]), Integer.parseInt(utc[1]));
			//this.V.servidor.setHora(1, Integer.parseInt("14"),Integer.parseInt("32"), Integer.parseInt("10"));
		
		}
	}

	public String[] getFormatTime()
	{
		String[] tiempo = {this.ms_formato,this.segundos_formato,this.minutos_formato,this.horas_formato};
		return tiempo; 
	}

}