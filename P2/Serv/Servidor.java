public class Servidor
{
    /**
    * Metodo principal
    **/

	public static void main(String[] args)
	{
		Gui V = new Gui();      // creamos una ventana
        V.setVisible(true);             // hacemos visible la ventana creada
		System.out.println(V.horas[2].getText());
		String[] hora;
		int i;
		//Cada segundo se actualizan las horas mostradas en la ventanda obteniendo la hora que se esta utilizando en cada uno de los relojes
        try
        {
            while(true)
            {
            	Thread.sleep(1000);
            	for(i = 0; i < 3; i ++)
            	{
	            	hora = V.servidor.getHora(i);
	            	V.horas[i].setText(hora[3] + ":" + hora[2] + ":" + hora[1]);	
            	}
            }
        }catch(InterruptedException  e)
        {
            System.out.println("Reloj interrumpido");
        }
	}
}