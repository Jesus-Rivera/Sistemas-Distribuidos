import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.swing.JOptionPane;

public class Cliente extends Thread
{
	private int puerto;
	public Reloj reloj;	
	private InetAddress direccion_Servidor;
	private String[] lista_servidores;

	private String nombre;
	private GuiCliente ventana;

	/**
	* Constructor de la clase
	**/

	public void iniciar(String nombre)
	{
		this.puerto = 6250;

		this.lista_servidores = new String[2];
		this.lista_servidores[0] = "25.110.204.69";
		this.lista_servidores[1] = "25.1.94.8";


		try{
			this.direccion_Servidor = InetAddress.getByName(this.lista_servidores[0]);
		}catch(Exception e)
		{
			System.err.println(e.getMessage());
		}


		this.ventana.texto[0].setText("Conectando ...");
		String[] hora = enviarDatagrama("0$",new byte[2],new byte [12]).split(":");
		this.ventana.texto[0].setText("Saludos cliente, estos son los datos de su libro: ");

		reloj = new Reloj("Reloj_cliente",Integer.parseInt(hora[0]),Integer.parseInt(hora[1]),Integer.parseInt(hora[2]),Integer.parseInt(hora[3]));
		reloj.start();
		this.nombre = nombre;
		this.start();
		
	}

	/**
	*
	**/
	
	public void run()
	{
		try
		{
			String mensaje;	
			DatagramSocket socket = new DatagramSocket(400);
			DatagramPacket peticion;
			byte[] buffer;
			while(true)
			{
				buffer = new byte[100];
				peticion = new DatagramPacket(buffer,buffer.length);
				socket.receive(peticion);
				mensaje = new String(peticion.getData());


				//Modificar...
				if(mensaje.charAt(2) == ':')
				{
					try
					{
						System.out.println("Ajuste de relonj por parte des servidor");
						String[] tiempo_recibido = mensaje.split(":");
						System.out.println("Hora: "+tiempo_recibido[0]);
						System.out.println("min: "+tiempo_recibido[1]);
						System.out.println("sec: "+tiempo_recibido[2]);
						reloj.setTime(Integer.parseInt(tiempo_recibido[0]),Integer.parseInt(tiempo_recibido[1]),Integer.parseInt(tiempo_recibido[2]),0);
					}catch(Exception ex) {
		                System.out.println("Error: " + ex);
		            }
				}

				else
				{
					//Modificar...
					this.ventana.texto[1].setText(mensaje);
					if(mensaje.substring(0,30).equals("Los pedidos se han reiniciado."))
					{
						this.ventana.texto[2].setText("<html>Nombre: <br><br>Autor: <br><br>Precio: </html>");
						this.ventana.boton[0].setEnabled(false);
					}
				}
				buffer = null;
			}
		}catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	* Metodo que envia un mensaje al servidor para obtener la hora de este y actualizar la propia
	**/
	public String enviarDatagrama(String mensaje,byte[] buffer_mensaje, byte[] buffer_respuesta)
	{
		String mensaje_recibido = null;
		DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss:S");
		Date hora_inicial = Calendar.getInstance().getTime();
		int i;
		for(i = 0; i < 4; i ++)
		{
			try
			{
				
				
				DatagramSocket socket = new DatagramSocket();
				socket.setSoTimeout(2000);

				buffer_mensaje = mensaje.getBytes();
				DatagramPacket peticion = new DatagramPacket(buffer_mensaje,buffer_mensaje.length,this.direccion_Servidor,this.puerto);
				socket.send(peticion);
				DatagramPacket respuesta = new DatagramPacket(buffer_respuesta,buffer_respuesta.length);

				socket.receive(respuesta);
				mensaje_recibido = new String(respuesta.getData());
				break;
			}catch(SocketTimeoutException e){
				System.out.println("Error al conectar conectar: " + this.direccion_Servidor.getHostAddress());
				try{
					if(this.direccion_Servidor.getHostAddress().equals(this.lista_servidores[0]))
						this.direccion_Servidor = InetAddress.getByName(this.lista_servidores[1]);
					else
						this.direccion_Servidor = InetAddress.getByName(this.lista_servidores[0]);
				}catch(Exception e2)
				{
					System.err.println(e.getMessage());
				}
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		if(mensaje_recibido == null)
		{
			JOptionPane.showMessageDialog(null, "Error al conectar con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);	
		}
		return mensaje_recibido;
	}

	public void pedir_libro()
	{
		String[] tiempo = new String[4];
		tiempo = this.reloj.getFormatTime();

		String[] datos_libro = enviarDatagrama(tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0] + ":" + this.nombre + ":",new byte[2],new byte [100]).split("&");

		if(datos_libro[0].length()==32 || datos_libro[0].substring(0,32).equals("Todos los libros estan apartados"))
		{
			this.ventana.texto[1].setText(datos_libro[0]);
			this.ventana.boton[0].setEnabled(false);
			int confirmado = JOptionPane.showConfirmDialog(null, "No existen libros disponibles, Desea solicitar reiniciar pedidos?", "Sin libros", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (JOptionPane.OK_OPTION == confirmado)
			{
			   System.out.println("confirmado");
			   String temp = enviarDatagrama("0#",new byte[2],new byte[3]);
			   System.out.println(temp);
			   if(temp.equals("Oki"))
					JOptionPane.showMessageDialog(null, "Pedidos reiniciados, ahora puede pedir un libro.", "Exito", JOptionPane.INFORMATION_MESSAGE);
			   else if(temp.equals("Nop"))
			   		JOptionPane.showMessageDialog(null, "Los pedidos no se han reiniciado.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else
			this.ventana.texto[2].setText("<html>Nombre: " + datos_libro[0] + "<br><br>Autor: " + datos_libro[1] + "<br><br>Precio: " + datos_libro[2] + "</html>");
		//System.out.println(cadena);

	}


	public void regresar_libro()
	{
		String[] tiempo = new String[4];
		tiempo = this.reloj.getFormatTime();
		System.out.println(enviarDatagrama("0%:" + tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0],new byte[11],new byte[26]));
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

	public void set_gui(GuiCliente v)
	{
		this.ventana = v;
	}

	/**
	* Envia una hora al servidor para modificar el reloj al que esta conectado
	* @param direccion_Servidor direccion ip del servidor
	* @param puerto puerdo del cliente al que se conectara
	* @param buffer buferr de datos que se envirara por el socket
	* @param nueva_hora hora con la que se actualizara el servidor
	* @return arreglo de enteros con la nueva hora del servidor sincronizada 
	**/
	public int[] modificar(InetAddress direccion_Servidor, int puerto, String nueva_hora)
	{
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:S");
		try
		{
			DatagramSocket socket = new DatagramSocket();
			byte[] buffer = new byte[12];
			buffer = ("9300" + nueva_hora).getBytes();
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
		int[] nueva_hora = modificar(this.direccion_Servidor,this.puerto,aux);
	}
}