import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class Conexion {
		public static Connection conn;
		private static final String driver = "com.mysql.jdbc.Driver";
		private static final String user = "root";
		private static final String password = "";
		private static final String url = "jdbc:mysql://localhost:3306/servidor";

		private Statement st;
    	private ResultSet rs;
   		private int total_libros = 0;
		
		public Conexion()
		{
			conn = null;
			try
			{
				Class.forName(driver);
				conn = DriverManager.getConnection(url,user,password);
				if (conn != null)
				{
					System.out.println("Conexion establecida...");
				}
			}catch(ClassNotFoundException | SQLException e)
			{
				System.out.println("Error al conectar: " + e);
			}
		}
		
		public Connection getConnection()
		{
			return conn;
		}
		
		public void Desconectar()
		{
			conn = null;
		}

		public int asignar_libro(String datos_recibidos,String ip)
		{	
			int opcion = -1,id = 0;
			String[] datos = datos_recibidos.split(":");

			try
			{

				this.st = this.conn.createStatement();
				this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM usuario");
				while(this.rs.next())
					id = this.rs.getInt("Cantidad");

				Crear_usuario(id,ip,datos[4]);

				this.rs = this.st.executeQuery("SELECT idLibro FROM libro WHERE idLibro NOT IN (SELECT idLibro FROM libro INNER JOIN pedido ON Libro_idLibro = idLibro WHERE pedido.Hora_final IS NULL) ORDER BY RAND() LIMIT 1");
				while(this.rs.next())
					opcion = this.rs.getInt(1);
				
				this.rs = this.st.executeQuery("SELECT idUsuario FROM usuario WHERE IP = INET6_ATON('" + ip + "')");
				while(this.rs.next())
					id = this.rs.getInt("idUsuario");

				terminar_pedidos(id,datos);
				if(opcion != -1)
					Crear_Pedido(opcion,id,datos);
			}catch(Exception ex) {
				System.out.println("Error asignar: " + ex);
			}
			return opcion;
		}

		public void Crear_usuario(int id, String ip, String nombre)
		{
			try
			{
				this.st = this.conn.createStatement();
				String insert = "INSERT INTO usuario (idUsuario,IP,Nombre) SELECT ";
				insert += id + ",INET6_ATON('";
				insert += ip + "'),'";
				insert += nombre + "' WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE idUsuario = '";
				insert += id + "' OR IP = INET6_ATON('";
				insert += ip + "'))";

				this.st.executeUpdate(insert);
			}catch(Exception ex) {
				System.out.println("Error crear usuario: " + ex);
			}
		}

		public void Crear_Pedido(int libro, int usuario, String[] hora_inicio)
		{
			Date fechaActual = new Date();
			DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
			String hora = hora_inicio[0] + ":" + hora_inicio[1] + ":" + hora_inicio[2]; 
			System.out.println(formatoFecha.format(fechaActual));
			int id = 0;
			try
			{
				this.st = this.conn.createStatement();
				this.rs = this.st.executeQuery("SELECT count(*) AS Cantidad FROM pedido");
				while(this.rs.next())
					id = this.rs.getInt("Cantidad");
				String insert = "INSERT INTO pedido VALUES (";
				insert += id + ",'";
				insert += formatoFecha.format(fechaActual) + "','";
				insert += hora + "',null,";
				insert += libro + ",";
				insert += usuario + ")";

				this.st.executeUpdate(insert);
			}catch(Exception ex) {
				System.out.println("Error crear pedido: " + ex);
			}
		}

		public void Cargar_pedido(int id, String fecha, String hora_inicio, String hora_final, int libro, int usuario)
		{
			try
			{
				this.st = this.conn.createStatement();
				String insert = "INSERT INTO pedido (idPedido,Fecha,Hora_inicio,Hora_final,Libro_idLibro,Usuario_idUsuario) SELECT ";
				insert += id + ",";
				insert += fecha + ",";
				insert += hora_inicio + ",";
				insert += hora_final + ",";
				insert += libro + ",";
				insert += usuario + " WHERE NOT EXISTS (SELECT 1 FROM pedido WHERE idPedido = ' ";
				insert += id + "')";

				//System.out.println(insert);
				this.st.executeUpdate(insert);
			}catch(Exception ex) {
				System.out.println("Error crear pedido: " + ex);
			}
		}

		public void actualizar_fechas(int id, String hora)
		{
			if(!hora.equals("null"))
			{
				try
				{
					String str = hora.substring(1, hora.length() - 1);
					String sql = "UPDATE Pedido SET Hora_final = ? WHERE idPedido = ? ";
					PreparedStatement stmt = this.conn.prepareStatement(sql);
					stmt.setString(1,str);
					stmt.setInt(2,id);
					stmt.executeUpdate();
				}catch(Exception ex) {
					System.out.println("Error actualizando fechas: " + ex);
				}
			}
		}

		public void Cargar_libro(int id, String nombre, String autor, Float precio,byte[] imagen, int tam)
		{
			try
			{
				String sql = "INSERT INTO Libro (idLibro,Nombre,Autor,Precio,Portada) VALUES (?,?,?,?,?)";
				PreparedStatement stmt = this.conn.prepareStatement(sql);
				stmt.setInt(1,id);
				stmt.setString(2,nombre);
				stmt.setString(3,autor);
				stmt.setFloat(4,precio);


				InputStream targetStream = new ByteArrayInputStream(imagen);
				stmt.setBinaryStream(5, targetStream, tam);

				stmt.executeUpdate();


				//FileInputStream   fis = new FileInputStream(imagen);
				//stmt.setBinaryStream(1, fis, (int) imagen.length());

				//System.out.println(insert);
				//this.st.executeUpdate(insert);
			}catch(Exception ex) {
				System.out.println("Error crear pedido: " + ex);
			}
		}

		public void terminar_pedidos(int idUsuario,String[] hora_final)
		{
			System.out.println(idUsuario);
			System.out.println(hora_final[0]+":"+hora_final[1]+":"+hora_final[2]);

			String insert;
			String hora = hora_final[0] + ":" + hora_final[1] + ":" + hora_final[2];
			try
			{
				Statement st_cerrar = this.conn.createStatement(); 
				this.st = this.conn.createStatement();
				this.rs = this.st.executeQuery("SELECT idPedido,Hora_final FROM `pedido` WHERE Usuario_idUsuario = " + idUsuario);
				while(this.rs.next())
				{
					if (this.rs.getTime("Hora_final") == null)
					{
						insert = "UPDATE pedido SET Hora_final = '" + hora + "' WHERE idPedido = " + this.rs.getInt("idPedido");
						st_cerrar.executeUpdate(insert);
					}
				}
			}catch(Exception ex) {
				System.out.println("Error crear terminar: " + ex);
			}
		}
}
