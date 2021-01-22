import java.awt.event.*;
import javax.swing.*;

public class Gui extends JFrame implements ActionListener {

    private JLabel texto[] = new JLabel[8];           // etiqueta o texto no editable
    public JLabel horas[] = new JLabel[3];  // Etiqueta editable
    private JTextField caja[] = new JTextField[8];        // caja de texto, para insertar datos
    private JButton boton[]= new JButton[8];          // boton con una determinada accion
    private int i, reloj;
    public Maestro servidor;

    /**
    * Constructor de la clase
    **/

    public Gui() 
    {
        super();                    // usamos el contructor de la clase padre JFrame
        iniciar_servidor();
        configurarVentana();        // configuramos la ventana
        inicializarComponentes();   // inicializamos los atributos o componentes
    }

    /*
    * Configuraciones principales de la ventana
    */

    private void configurarVentana() 
    {
        this.setTitle("Control Maestro");                   // colocamos titulo a la ventana
        this.setSize(310, 210);                                 // colocamos tamanio a la ventana (ancho, alto)
        this.setLocationRelativeTo(null);                       // centramos la ventana en la pantalla
        this.setLayout(null);                                   // no usamos ningun layout, solo asi podremos dar posiciones a los componentes
        this.setResizable(false);                               // hacemos que la ventana no sea redimiensionable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // hacemos que cuando se cierre la ventana termina todo proceso
        for(i = 0; i < 8; i ++)
		{
			texto[i] = new JLabel();
			boton[i] = new JButton();
            caja[i] = new JTextField();
		} 
        for(i = 0; i < 3; i ++)
            horas[i] = new JLabel();
    
    }

    /**
    * Elimina los componentes de la ventana
    **/

    private void limpiarVentana() 
    {
        
        for(i = 0; i < 8; i ++)
		{
			this.remove(texto[i]);
            this.remove(boton[i]);
            this.remove(caja[i]);
		} 
        for(i = 0; i < 3; i ++)
            this.remove(horas[i]);
        this.repaint();
        this.configurarVentana();
        this.revalidate();
        for(i = 0; i < 8; i ++)
		{
			texto[i] = new JLabel();
			boton[i] = new JButton();
            caja[i] = new JTextField();
		}
        for(i = 0; i < 3; i ++)
            horas[i] = new JLabel();
        this.revalidate(); 
    }

    /**
    * Agrega componentes de la ventana principal
    **/

    private void inicializarComponentes() 
    {
        this.setTitle("Control Maestro");                   // colocamos titulo a la ventana
        this.setSize(620, 210);  

        String[] hora;
        // configuramos los componentes
        texto[0].setText("Relojes ejecutando");    // colocamos un texto a la etiqueta
        texto[0].setBounds(30, 30, 620, 20);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        
        texto[1].setText("Hora de los servidores:");
        texto[1].setBounds(30, 60, 620, 20);

        horas[0].setText("hh:mm:ss");
        horas[0].setBounds(65, 100, 620, 20);

        horas[1].setText("hh:mm:ss");
        horas[1].setBounds(185, 100, 620, 20);

        horas[2].setText("hh:mm:ss");
        horas[2].setBounds(325, 100, 620, 20);

        //caja.setBounds(150, 50, 100, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        boton[0].setText("Cliente 1");   // colocamos un texto al boton
        boton[0].setBounds(50, 130, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[1].setText("Cliente 2");   // colocamos un texto al boton
        boton[1].setBounds(170, 130, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[2].setText("Cliente 3");   // colocamos un texto al boton
        boton[2].setBounds(310, 130, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[3].setText("Todos");   // colocamos un texto al boton
        boton[3].setBounds(450, 130, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        //adicionamos los componentes a la ventana
        for(i = 0; i < 8; i ++)
		{
            boton[i].addActionListener(this); 
			this.add(texto[i]);
            this.add(boton[i]);

		}
        for(i = 0; i < 3; i ++)
            this.add(horas[i]);
        this.validate();
    }

    /**
    * Inicia ventana desde la cual se podra acceder a la modificacion del reloj
    * @param hilo reloj al que se conectara para realizar la configuracion
    **/
    private void seleccionarAccionReloj(int hilo) 
    {
        
        // configurarVentana();
        // creamos los componentes
        limpiarVentana();
        this.setTitle("Control Maestro");                   // colocamos titulo a la ventana
        this.setSize(310, 210);  

        for(i = 0; i < 8; i ++)
		{
			texto[i] = new JLabel();
			boton[i] = new JButton();
		}   
        // configuramos los componentes
        if(hilo!=0)
            texto[0].setText("Que hacemos al reloj del esclavo "+hilo+"?");    // colocamos un texto a la etiqueta
        else
            texto[0].setText("Configuracion global de relojes");
        texto[0].setBounds(30, 50, 310, 15);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        boton[4].setText("Modificar");   // colocamos un texto al boton
        boton[4].setBounds(50, 100, 90, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        //boton[5].setText("Enviar");   // colocamos un texto al boton
        //boton[5].setBounds(105, 100, 90, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[6].setText("Volver");   // colocamos un texto al boton
        //boton[6].setBounds(200, 100, 90, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[6].setBounds(150, 100, 90, 30);
        boton[4].addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        boton[5].addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        boton[6].addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        // adicionamos los componentes a la ventana
       for(i = 0; i < 2; i ++)
		{
			this.add(texto[i]);
		} 
        for(i = 4; i < 8; i ++)
		{
            this.add(boton[i]);
		} 
        this.validate();
    }


    /**
    * Ventana para modificar un reloj en especifico
    * @param hilo reloj al que se conectara
    **/

    private void modificarReloj(int hilo) 
    {
        limpiarVentana();
        
        if(hilo != 0)
        {
            String[] hora = this.servidor.getHora(hilo - 1);
                 //System.out.println(hora[0]);
         
                 //Aqui se llenan las textboxes con el tiempo del hilo.
            caja[0].setText(hora[3]); //Los puse pa probar jeje
            caja[1].setText(hora[2]);
            caja[2].setText(hora[1]);
        }
        else
        {
            caja[0].setText("00"); //Los puse pa probar jeje
            caja[1].setText("00");
            caja[2].setText("00");
        }

        texto[0].setText("hh:");
        texto[1].setText("mm:");
        texto[2].setText("ss");
        texto[0].setBounds(75, 30, 30, 20);
        texto[1].setBounds(120, 30, 30, 20);
        texto[2].setBounds(174, 30, 30, 20);

        caja[0].setBounds(55, 50, 50, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        caja[1].setBounds(107, 50, 50, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        caja[2].setBounds(160, 50, 50, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)

        
        boton[7].setText("Guardar");   // colocamos un texto al boton
        boton[7].setBounds(20, 100, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[6].setText("Volver");   // colocamos un texto al boton
        boton[6].setBounds(170, 100, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[7].addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        boton[6].addActionListener(this); 

        for(i = 0; i < 3; i ++)
		{
            this.add(texto[i]);
			this.add(caja[i]);
		} 
        for(i = 6; i < 8; i ++)
		{
            this.add(boton[i]);
		} 
        this.validate();

        //return reloj;
    
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {       
        if(e.getSource() == boton[0])
        {
            this.reloj = 1;
            seleccionarAccionReloj(this.reloj);

        }
        if(e.getSource() == boton[1])
        {
            this.reloj =2;
            seleccionarAccionReloj(this.reloj);

        }
        if(e.getSource() == boton[2])
        {
            this.reloj =3;
            seleccionarAccionReloj(this.reloj);

        }
        if(e.getSource() == boton[3])
        {
            this.reloj = 0;
            seleccionarAccionReloj(this.reloj);

        }
        if(e.getSource() == boton[4])
        {
            modificarReloj(this.reloj);

        }
        if(e.getSource() == boton[5])
        {
            //enviar nuevo reloj


        }
        if(e.getSource() == boton[6])
        {
            //volver
            limpiarVentana();
            inicializarComponentes();

        }
        if(e.getSource() == boton[7])
        {
            //Guardar datos del reloj
            int[] relojActualizado = new int[3];
            for(i=0;i<3;i++)
            {
                relojActualizado[i]=Integer.parseInt(caja[i].getText());
            }
            if((relojActualizado[0]<0 || relojActualizado[0]>24) || (relojActualizado[1]<0 || relojActualizado[0]>60) || (relojActualizado[2]<0 || relojActualizado[2]>60))
            {
                JOptionPane.showMessageDialog(this, "Formato erroneo");
                for(i=0;i<3;i++)
                {
                    relojActualizado[i]=0;
                }
            }

            if(this.reloj == 0)
            {
                for(i = 0; i < 3; i ++)
                    this.servidor.setHora(i,relojActualizado[0],relojActualizado[1],relojActualizado[2]);
            }
            else
                this.servidor.setHora(this.reloj - 1,relojActualizado[0],relojActualizado[1],relojActualizado[2]);
            System.out.println("Reloj Actualizado:"+relojActualizado[0]+":"+relojActualizado[1]+":"+relojActualizado[2]);

        }
    }

    /**
    * Inicia el servidor en el que se ejecutaran los relojs y se esperaran llamadas de clientes
    **/
    public void iniciar_servidor()
    {
        this.servidor = new Maestro();
        this.servidor.start();
    }
}