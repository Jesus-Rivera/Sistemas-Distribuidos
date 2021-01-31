import java.time.format.*;
import java.time.*; 
import java.util.*;
import java.lang.*;
public class PrincipalTiempo
{
    /**
    * Metodo principal
    **/

	public static void main(String[] args)
	{
		Gui V = new Gui();      // creamos una ventana
        V.setVisible(true);     // hacemos visible la ventana creada
		
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM ).withZone( ZoneId.systemDefault() );
		Instant instant = Instant.now();
		System.out.println(formatter.toString());
		
		String output = formatter.format( instant );
		System.out.println("output: " + output );

		String strNew = output.replaceFirst("[[a-zA-Z]*[0-9]*[ ]*[.]]{13}", "");
	
		String[] hora;
		int i;

		
		V.servidor.actualizador.setUTC(V);
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
				hora = V.servidor.getUTC();
	            V.horas[3].setText(hora[3] + ":" + hora[2] + ":" + hora[1]);
            }
        }catch(InterruptedException  e)
        {
            System.out.println("Reloj interrumpido");
        }
		
	}
}