//javac -cp .;"driver-connector\mysql-connector-java-5.1.49.jar" Principal.java
//java -cp .;"driver-connector\mysql-connector-java-5.1.49.jar" Principal

public class Principal {

	public static void main(String[] args) {
		Gui V = new Gui();      // creamos una ventana
        V.setVisible(true);             // hacemos visible la ventana creada
		Maestro servidor = new Maestro();

		servidor.implementarVentana(V);
		V.implementarServidor(servidor);
	}

}
