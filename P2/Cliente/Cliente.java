public class Cliente
{
	/**
    * Metodo principal
    **/
    
	public static void main(String[] args)
	{
		GuiCliente V = new GuiCliente();      // creamos una ventana
        V.setVisible(true);             // hacemos visible la ventana creada
		System.out.println(V.hora.getText());
		String[] tiempo;
		int i;
		try
        {
            while(true)
            {
            	Thread.sleep(1000);
	            tiempo = V.servidor.getHora();
	            V.hora.setText(tiempo[3] + ":" + tiempo[2] + ":" + tiempo[1]);	
            }
        }catch(InterruptedException  e)
        {
            System.out.println("Reloj interrumpido");
        }
	}
}