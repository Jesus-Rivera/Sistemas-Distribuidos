public class Reloj extends Thread
{

	private int hora;
	private int minutos;
	private int segundos;
	private int ms;

	private String horas_formato;
	private String minutos_formato;
	private String segundos_formato;
	private String ms_formato;

	private boolean ejecutar;

	/**
	* Constructor de la clase reloj
	* @param nombre nombre del reloj
	* @param hora hora en la que se inicia el reloj
	* @param minnutos minutos en la que se inicia el reloj
	* @param segundos segundos en la que se inicia el reloj
	* @param ms mili segundos en la que se inicia el reloj
	**/

	public Reloj(String nombre, int hora, int minutos, int segundos,int ms)
	{
		super(nombre);
		this.hora = hora;
		this.minutos = minutos;
		this.segundos = segundos;
		this.ms = ms;
		this.segundos_formato = "";
		this.minutos_formato = "";
		this.horas_formato = "";
		
	}

	/**
	* Meto de ejecucion del hilo
	* En este metodo se correra el reloj
	**/

	public void run()
	{
		int i;
		this.ejecutar = true;
		while(this.ejecutar)
		{
			try
			{
				this.sleep(1);
			}catch(InterruptedException  e)
			{
				System.out.println("Reloj interrumpido");
			}
			this.ms_formato = (this.ms < 10)?("00" + this.ms):String.valueOf(this.ms);
			this.segundos_formato = (this.segundos < 10)?("0" + this.segundos):String.valueOf(this.segundos);
			this.minutos_formato = (this.minutos < 10)?("0" + this.minutos):String.valueOf(this.minutos);
			this.horas_formato = (this.hora < 10)?("0" + this.hora):String.valueOf(this.hora);
			this.ms ++;
			if(this.ms == 1000)
			{
				this.ms = 0;
				this.segundos ++;
			}
			if(this.segundos == 60)
			{
				this.segundos = 0;
				this.minutos ++;
			}
			if(this.minutos == 60)
			{
				this.minutos = 0;
				this.hora ++;
			}
			if(this.hora == 24)
			{
				this.hora = 0;
			}
		}
	}

	/**
	* Detiene la ejecucion del reloj
	**/

	public void detener()
	{
		this.ejecutar = false;
	}

	/**
	* Cambia la hora del reloj
	* @param hora nueva hora del reloj
	* @param minnutos nuevos minutos del reloj
	* @param segundos nuevos segundos del reloj
	* @param ms  nuevos milisegundos del reloj
	**/

	public void setTime(int hora, int minutos, int segundos, int ms)
	{
		//System.out.println("Se modifico la hora");
		this.segundos = segundos;
		this.minutos = minutos;
		this.hora = hora;
		this.ms = ms;
	} 

	/**
	* Metodo para obtener la hora en la que se esta ejecutanndo el reloj al momento de ser llamado
	* @return arreglo de enteros con los datos del reloj
	**/

	public int[] getTime()
	{
		int[] tiempo = {this.hora,this.minutos,this.segundos,this.ms};
		return tiempo; 
	}

	/**
	* Metodo para obtener la hora en la que se esta ejecutanndo el reloj al momento de ser llamado, con formato hh:mm:ss
	* @return arreglo de cadena con los datos del reloj
	**/

	public String[] getFormatTime()
	{
		String[] tiempo = {this.ms_formato,this.segundos_formato,this.minutos_formato,this.horas_formato};
		return tiempo; 
	}
}