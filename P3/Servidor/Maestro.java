import java.net.*;
import java.io.*; 

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Image;
import javax.swing.ImageIcon;

import javax.swing.JOptionPane;



public class Maestro extends Thread
{
	public Reloj reloj;
	public Conexion Conect = new Conexion();
	private Statement st;
    private ResultSet rs;
    private int total_libros = 0;

    private Gui Ventana;
    private Sincronizador_BD s_BD;

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
		this.reloj = new Reloj("Reloj Servidor",hora,minutos,segundos,milisegundos);
		this.reloj.start();

		this.s_BD = new Sincronizador_BD(this.Conect);
		this.s_BD.start();
	}

	public void implementarVentana(Gui v)
	{
		this.Ventana = v;
	}

	/**
	* Meto de ejecucion del hilo
	* En este metodo mantendra el servidor a la espera del mensaje de algun cliente
	* Gestionando el tipo de mensaje recibido y la informacion que regresara utilizando sockets udp
	**/

public void run()
	{
		
		int i,tam;

		final int puerto = 6250;
		byte[] buffer;
		System.out.println("--- Servidor iniciado ---\n");	


		try
		{	
			DatagramSocket socket = new DatagramSocket(puerto);
			DatagramPacket peticion;
			DatagramPacket respuesta;
			InetAddress direccion;
			int puertoCliente;

			String[] tiempo;
			String mensaje,envio = "";

			while(true)
			{
				//System.out.println("Nueva iteracion, puerto: " + puerto);
				//System.out.println("Nueva iteracion, puerto: " + puertoCliente);
				tam = 0;
				tiempo = new String[4];
				tiempo = this.reloj.getFormatTime();

				// Recibe un mensaje de algun cliente, este puede estar solicitando la hora de algun reloj o la modificacion
				buffer = new byte[100];
				peticion = new DatagramPacket(buffer,buffer.length);
				socket.receive(peticion);
				mensaje = new String(peticion.getData());
				
				System.out.println(peticion.getAddress());
				System.out.println(mensaje);
				if(mensaje.charAt(1) == '$')
				{
					
					tam = 12;
					System.out.println("Mensaje para pedir la hora");
					envio = tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0];
				}
				else if (mensaje.charAt(1) == '%')
				{
					tam = 26;
					System.out.println("Libro recibido.");
					String[] tiempo_recibido = {mensaje.split(":")[1],mensaje.split(":")[2],mensaje.split(":")[3],mensaje.split(":")[4]};
					envio = "Gracias, vuelva pronto :).";
					try
					{
						this.st = this.Conect.conn.createStatement();
						this.rs = this.st.executeQuery("SELECT idUsuario FROM usuario WHERE IP = INET6_ATON('"+ peticion.getAddress().getHostAddress() +"')");
						while(this.rs.next())
		                {
		                	this.Conect.terminar_pedidos(this.rs.getInt(1),tiempo_recibido);
		                }
					}catch(Exception ex) {
		                System.out.println("Error: " + ex);
		            }
				}else if(mensaje.charAt(1) == '#')
				{
					int confirmado = JOptionPane.showConfirmDialog(null, "Se solicito un reinicio de pedidos", "Sin libros", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (JOptionPane.OK_OPTION == confirmado)
					{
						this.Reiniciar();
						envio = "Oki";
					}
					else
						envio = "Nop";
				}else
				{
					tam = 100;
					try
		            {
		                this.st = this.Conect.conn.createStatement();
		                int opcion = this.Conect.asignar_libro(mensaje,peticion.getAddress().getHostAddress());
		                if(opcion != -1)
		                {
		                	this.rs = this.st.executeQuery("SELECT * FROM libro where idLibro = " + opcion);
			                while(this.rs.next())
			                {

			                    java.sql.Blob blob = rs.getBlob("Portada");
			                    System.out.println(this.rs.getString("Nombre"));
			                    envio = this.rs.getString("Nombre") + "&" + this.rs.getString("Autor") + "&" + this.rs.getFloat("Precio") + "&";
			                    this.Ventana.setTitle(this.rs.getString("Nombre"));
			                    InputStream in = blob.getBinaryStream();  
			                    BufferedImage image = ImageIO.read(in);
			                    ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(240, 330, Image.SCALE_SMOOTH));

			                    this.Ventana.setTitle(this.rs.getString("Nombre"));
			                    this.Ventana.texto[0].setText("Autor: " + this.rs.getString("Autor"));
			                    this.Ventana.texto[1].setIcon(imageIcon);
			                }
		                }
		                else
		                	envio = "Todos los libros estan apartados";
		                

		            }catch(Exception ex) {
		                System.out.println("Error: " + ex);
		            }
				}
				if(mensaje.charAt(2) == ':' && mensaje.charAt(1) != '%' && peticion.getPort() == 6969)
				{
					try
					{
						System.out.println("Ajuste automatico UTC");
						String[] tiempo_recibido = {mensaje.split(":")[0],mensaje.split(":")[1],mensaje.split(":")[2]};
						System.out.println("Hora: "+tiempo_recibido[2]);
						System.out.println("min: "+tiempo_recibido[1]);
						System.out.println("sec: "+tiempo_recibido[0]);
						reloj.setTime(Integer.parseInt(tiempo_recibido[2]),Integer.parseInt(tiempo_recibido[1]),Integer.parseInt(tiempo_recibido[0]),0);
					}catch(Exception ex) {
		                System.out.println("Error: " + ex);
		            }
				}else
				{
					buffer = new byte[tam];
					buffer = envio.getBytes();
					puertoCliente = peticion.getPort();
					direccion = peticion.getAddress();

					mensaje = tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1] + ":" + tiempo[0];
					this.Enviar_mensaje(mensaje,new byte[8],direccion);

					System.out.println("Mensaje enviado: " + envio);
					respuesta = new DatagramPacket(buffer,buffer.length,direccion,puertoCliente);
					socket.send(respuesta);
					buffer = null;
				}
			}
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void modificar(String nueva_hora)
	{
		
		int i,tam;

		final int puerto = 6969;
		byte[] buffer;	


		try
		{	
			DatagramSocket socket = new DatagramSocket(puerto);
			DatagramPacket peticion;
			DatagramPacket respuesta;
			InetAddress direccion;
			int puertoCliente;

			String[] tiempo;
			String mensaje;

			//InetAddress direccion_Servidor = InetAddress.getByName("192.168.1.116");
			InetAddress direccion_Servidor = InetAddress.getByName("25.0.35.33");
		
			socket = new DatagramSocket();
			buffer = ("9200" + nueva_hora).getBytes();
			peticion = new DatagramPacket(buffer,buffer.length,direccion_Servidor,puerto);
			socket.send(peticion);

		}catch (Exception e) {
			System.err.println("Servidor UTC reloj:"+e.getMessage());
			System.exit(1);
		}
	}

	public void Reiniciar()
	{
		System.out.println("Se reiniciaron los pedidos");
		String[] temp = this.reloj.getFormatTime();
		String[] tiempo = {temp[3],temp[2],temp[1]};
		int i;
		byte[] ip_byte;
		String ip_cadena;
		try
		{
			this.st = this.Conect.conn.createStatement();
			System.out.println();
			this.rs = this.st.executeQuery("SELECT usuario.idUsuario,usuario.IP FROM pedido INNER JOIN usuario ON pedido.Usuario_idUsuario = usuario.idUsuario WHERE Hora_final IS NULL");
			while(this.rs.next())
			{
				ip_byte = this.rs.getBytes(2); 
				ip_cadena = "";
				for (i = 0; i < 4; i ++) {
					ip_cadena += ip_byte[i] & 255;
					ip_cadena += ".";
				}
				ip_cadena = ip_cadena.substring(0,ip_cadena.length() - 1);
				this.Conect.terminar_pedidos(this.rs.getInt(1),tiempo);
				this.Enviar_mensaje("Los pedidos se han reiniciado.",new byte[30],InetAddress.getByName(ip_cadena));
			}
		}catch(Exception ex) {
			System.out.println("Error: " + ex);
		}
		System.out.println(tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1]);
	}

	private void Enviar_mensaje (String mensaje,byte[] buffer_mensaje, InetAddress direccion)
	{
		try
		{
			DatagramSocket socket = new DatagramSocket();				
			
			buffer_mensaje = mensaje.getBytes();
			DatagramPacket peticion = new DatagramPacket(buffer_mensaje,buffer_mensaje.length,direccion,400);
			socket.send(peticion);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	/**
	* Metodo para obtener la hora de un reloj especifico
	* @param identificador reloj al cual se accedera
	* @return arreglo de cadenas con los datos del reloj
	**/

	public String[] getHora()
	{
		return this.reloj.getFormatTime();
	}

	/**
	* Cambia la hora de un reloj en especifico
	* @param identificador reloj al que se le cambiara la hora
	* @param hora nueva hora del reloj
	* @param minnutos nuevos minutos del reloj
	* @param segundos nuevos segundos del reloj
	**/

	public void setHora(int hora, int minutos, int segundos)
	{
		this.reloj.setTime(hora,minutos,segundos,00);
	}

}