import java.net.*;
import java.io.*; 

import java.util.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Blob;
import java.nio.charset.StandardCharsets;

import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.nio.ByteBuffer;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;


public class Sincronizador_BD extends Thread
{
	private int puerto;
	private InetAddress direccion_Servidor;
	private Conexion Conect;
	private Statement st;
	private ResultSet rs;
	private String resultado;

	private boolean bandera;

	public Sincronizador_BD(Conexion conect)
	{
		try
		{
			this.direccion_Servidor = InetAddress.getByName("25.1.94.8");
		}catch (Exception e) {
			System.err.println("Ip creation: " + e.getMessage());
		}

		this.puerto = 13;
		this.Conect = conect;
		this.bandera = true;
	}

	public void run()
	{
		DatagramSocket socket;
		DatagramPacket peticion;
		DatagramPacket respuesta;

		ServerSocket socket_servidor = null;
		Socket socket_cliente = null;
		DataInputStream in;
		DataOutputStream out;

		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formato_hora = new SimpleDateFormat("HH:mm:ss");
		String consulta;
		int i;
		BufferedImage image;

		String mensaje;
		int tipo = 0, cantidad = 0;
		byte[] buffer = new byte[1];
		while(this.bandera == true)
		{
			try
			{
				//-----------------------------------------------------------------------------------------
				mensaje = "01";
				buffer = mensaje.getBytes();
				//System.out.println("Inicio de sincronizacion");
				socket = new DatagramSocket(this.puerto);
				peticion = new DatagramPacket(buffer,buffer.length,this.direccion_Servidor,this.puerto);
				socket.send(peticion);
				
				respuesta = new DatagramPacket(buffer,buffer.length);
				socket.receive(respuesta);
				//System.out.println("Sincronizando datos de BD");
				this.st = this.Conect.conn.createStatement();

				mensaje = new String(respuesta.getData());
				if(mensaje.equals("01"))
				{
					mensaje = "02";
					buffer = mensaje.getBytes();
					peticion = new DatagramPacket(buffer,buffer.length,peticion.getAddress(),peticion.getPort());
					socket.send(peticion);
					tipo = 0;
				}
				else
					tipo = 1;


				socket.close();

				//-----------------------------------------------------------------------------------------
				switch(tipo)
				{
					case 0: //Servidor
						int temp;
						socket_servidor = new ServerSocket(this.puerto);
						socket_cliente = socket_servidor.accept();
						in = new DataInputStream(socket_cliente.getInputStream());
						out = new DataOutputStream(socket_cliente.getOutputStream());


						//Enviando datos de usuario
						cantidad = 0;
						mensaje = in.readUTF();
						//System.out.println("Tabla: " + mensaje);
						this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM " + mensaje);
						while(this.rs.next())
							cantidad = this.rs.getInt("Cantidad");
						
						temp = in.readInt();
						cantidad = cantidad - temp;
						out.writeUTF(cantidad + "");
						consulta = "SELECT * FROM " + mensaje + " WHERE id" + mensaje + " > " + (temp - 1);
						this.rs = this.st.executeQuery(consulta);
						while(this.rs.next())
						{
							out.writeUTF(this.rs.getInt(1) + "");

							byte[] ip_byte = this.rs.getBytes(2); 
							String ip_cadena = "";
							for (i = 0; i < 4; i ++) {
								ip_cadena += ip_byte[i] & 255;
								ip_cadena += ".";
							}
							//System.out.println(ip_cadena);
							out.writeUTF(ip_cadena.substring(0,ip_cadena.length() - 1));
							out.writeUTF(this.rs.getString(3));
						}

						//Enviar datos de pedido
						cantidad = 0;
						mensaje = in.readUTF();
						//System.out.println("Tabla: " + mensaje);
						this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM " + mensaje);
						while(this.rs.next())
							cantidad = this.rs.getInt("Cantidad");
						
						//temp = in.readInt();
						//cantidad = cantidad - temp;
						out.writeUTF(cantidad + "");
						//System.out.println(temp - 1);
						consulta = "SELECT * FROM " + mensaje;// + " WHERE id" + mensaje + " > " + (temp - 1);
						this.rs = this.st.executeQuery(consulta);
						while(this.rs.next())
						{
							out.writeUTF(this.rs.getInt(1) + "");

							out.writeUTF(formato.format(this.rs.getDate(2)));
							out.writeUTF(formato_hora.format(this.rs.getTime(3)));

							Date tiempo_auxiliar = this.rs.getTime(4);
							if (tiempo_auxiliar != null)
								out.writeUTF("'" + formato_hora.format(tiempo_auxiliar) + "'");
							else
								out.writeUTF("null");

							out.writeUTF(this.rs.getInt(5) + "");
							out.writeUTF(this.rs.getInt(6) + "");
						}


						//Enviar datos de libro
						cantidad = 0;
						mensaje = in.readUTF();
						//System.out.println("Tabla: " + mensaje);
						this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM " + mensaje);
						while(this.rs.next())
							cantidad = this.rs.getInt("Cantidad");
						
						temp = in.readInt();
						cantidad = cantidad - temp;
						out.writeUTF(cantidad + "");
						consulta = "SELECT * FROM " + mensaje + " WHERE id" + mensaje + " > " + (temp - 1);
						this.rs = this.st.executeQuery(consulta);

						OutputStream outputStream = socket_cliente.getOutputStream();
						InputStream aux;
						Blob blob;
						ByteArrayOutputStream byteArrayOutputStream;
						
						
						//byte[] size;
						while(this.rs.next())
						{
							out.writeInt(this.rs.getInt(1));
							out.writeUTF(this.rs.getString(2));
							out.writeUTF(this.rs.getString(3));


							out.writeFloat(this.rs.getFloat(4));
							blob = this.rs.getBlob(5);
							

							
							byteArrayOutputStream = new ByteArrayOutputStream();
							
							aux = blob.getBinaryStream(); 
							image = ImageIO.read(aux);


							//ImageIO.write(image, "jpg", byteArrayOutputStream);

							
							//size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
							out.writeInt((int) blob.length());
							out.write(byteArrayOutputStream.toByteArray());

						}
						socket_servidor.close();
						socket_cliente.close();
					break;
					case 1: //Cliente
						int id,id_libro,id_usuario;
						String ip,fecha,hora_inicio,hora_fin;
						String nombre, autor;
						float precio;
						Blob portada;

						cantidad = 0;
						// Inicia comunicacion con el servidor
						socket_cliente = new Socket(this.direccion_Servidor,this.puerto);
						in = new DataInputStream(socket_cliente.getInputStream());
						out = new DataOutputStream(socket_cliente.getOutputStream());

						//Recibir datos usuario
						out.writeUTF("Usuario");
						this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM Usuario");
						while(this.rs.next())
							cantidad = this.rs.getInt("Cantidad");
						out.writeInt(cantidad);

						cantidad = Integer.parseInt(in.readUTF());
						for(i = 0; i < cantidad; i ++)
						{
							id = Integer.parseInt(in.readUTF());
							ip = in.readUTF();
							nombre = in.readUTF();
							//System.out.println("id: " + id + ", IP: " + ip + ", Nombre: " + nombre);

							this.Conect.Crear_usuario(id,ip,nombre);

						}

						//Recibir datos pedido
						out.writeUTF("Pedido");
						//this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM Pedido");
						//while(this.rs.next())
						//	cantidad = this.rs.getInt("Cantidad");
						//out.writeInt(cantidad);

						cantidad = Integer.parseInt(in.readUTF());
						for(i = 0; i < cantidad; i ++)
						{
							id = Integer.parseInt(in.readUTF());

							fecha = in.readUTF();
							hora_inicio = in.readUTF();
							hora_fin = in.readUTF();

							id_libro = Integer.parseInt(in.readUTF());
							id_usuario = Integer.parseInt(in.readUTF());

							this.rs = this.st.executeQuery("SELECT 1 FROM pedido WHERE idPedido = " + id + " AND Hora_final IS NULL");
							while(this.rs.next())
								this.Conect.actualizar_fechas(id,hora_fin);
							
							this.Conect.Cargar_pedido(id,"'" + fecha + "'","'" + hora_inicio + "'",hora_fin, id_libro,id_usuario);
						}

						//Recibir datos libro
						out.writeUTF("Libro");
						this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM Libro");
						while(this.rs.next())
							cantidad = this.rs.getInt("Cantidad");
						out.writeInt(cantidad);

						cantidad = Integer.parseInt(in.readUTF());



						byte[] imageAr;
						int valor;
						for(i = 0; i < cantidad; i ++)
						{
							try{
								id = in.readInt();
								nombre = in.readUTF();
								autor = in.readUTF();
								precio = in.readFloat();

								valor = in.readInt();
								imageAr = new byte[valor];
								in.read(imageAr);
								
								System.out.println(imageAr.length);

								//InputStream targetStream = new ByteArrayInputStream(imageAr);
								//image = ImageIO.read(new ByteArrayInputStream(imageAr));

								//System.out.println("id: " + id + ", Nombre: " + nombre + ", Autor: " + autor + ", Precio: " + precio + ", Portada: " + image);
								this.Conect.Cargar_libro(id,nombre,autor,precio,imageAr,valor);
							}catch(Exception exep)
							{
								System.out.println("Error tabla libro: " + exep);
							}	
						}

						//  Cierra el socket
						socket_cliente.close();
					break;
				}
			}catch (Exception e) {
				System.err.println("Error sincronizacion: " + e.getMessage());
			}
			if(tipo == 0)
			{
				try{
					this.sleep(5000);
				}catch(Exception e){
					System.err.println("Error al reiniciar");
				}
			}
		}
	}
}
