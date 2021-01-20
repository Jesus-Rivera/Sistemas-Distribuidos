import java.awt.event.*;
import javax.swing.*;

public class GuiCliente extends JFrame implements ActionListener {

    private JLabel texto[] = new JLabel[8];           // etiqueta o texto no editable
    public JLabel hora;
    private JTextField caja[] = new JTextField[8];        // caja de texto, para insertar datos
    private JButton boton[]= new JButton[8];          // boton con una determinada accion
    int i, reloj;
    public Usuario servidor;

    /**
    * Constructor de la clase
    **/

    public GuiCliente() 
    {
        super();                    // usamos el contructor de la clase padre JFrame
        configurarVentana();        // configuramos la ventana
        iniciar_servidor();
        inicializarComponentes();   // inicializamos los atributos o componentes
        
    }

    /*
    * Configuraciones principales de la ventana
    */

    private void configurarVentana() 
    {
        this.setTitle("Control Cliente");                   // colocamos titulo a la ventana
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
        hora = new JLabel();
    
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
        this.remove(hora);
        this.repaint();
        this.configurarVentana();
        this.revalidate();
        for(i = 0; i < 8; i ++)
		{
			texto[i] = new JLabel();
			boton[i] = new JButton();
            caja[i] = new JTextField();
		}
        hora = new JLabel();
        this.revalidate(); 
    }

    /**
    * Agrega componentes de la ventana principal
    **/

    private void inicializarComponentes() 
    {
        
        this.setSize(310, 210);  

        // configuramos los componentes
        texto[0].setText("Saludos cliente");    // colocamos un texto a la etiqueta
        texto[0].setBounds(30, 30, 620, 20);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        
        texto[1].setText("Hora local:");
        texto[1].setBounds(30, 60, 620, 20);


        hora.setText("hh:mm:ss");
        hora.setBounds(65, 100, 620, 20);

        //caja.setBounds(150, 50, 100, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        boton[0].setText("Solicitar cambio");   // colocamos un texto al boton
        boton[0].setBounds(30, 130, 130, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[1].setText("Actualizar");   // colocamos un texto al boton
        boton[1].setBounds(160, 130, 130, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        
        //adicionamos los componentes a la ventana
        for(i = 0; i < 2; i ++)
		{
            boton[i].addActionListener(this); 
			this.add(texto[i]);
            this.add(boton[i]);
		} 
        this.add(hora);
        this.validate();
    }

    /**
    * Ventana para modificar el reloj del servidor
    **/

    private void modificarReloj() 
    {
        limpiarVentana();
        
        String[] hora = this.servidor.getHora();
 
        caja[0].setText(hora[3]);
        caja[1].setText(hora[2]);
        caja[2].setText(hora[1]);

        texto[0].setText("hh:");
        texto[1].setText("mm:");
        texto[2].setText("ss");
        texto[0].setBounds(75, 30, 30, 20);
        texto[1].setBounds(120, 30, 30, 20);
        texto[2].setBounds(174, 30, 30, 20);

        caja[0].setBounds(55, 50, 50, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        caja[1].setBounds(107, 50, 50, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        caja[2].setBounds(160, 50, 50, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)

        
        boton[3].setText("Guardar");   // colocamos un texto al boton
        boton[3].setBounds(20, 100, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[4].setText("Volver");   // colocamos un texto al boton
        boton[4].setBounds(170, 100, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[3].addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        boton[4].addActionListener(this); 

        for(i = 0; i < 3; i ++)
        {
            this.add(texto[i]);
            this.add(caja[i]);
        } 
        for(i = 3; i < 5; i ++)
        {
            this.add(boton[i]);
        } 
        this.validate();    
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {       
        if(e.getSource() == boton[0])
        {
            modificarReloj();
        }
        if(e.getSource() == boton[1])
        {
            this.servidor.enviarDatagrama();
        }
        if(e.getSource() == boton[3])
        {
            int[] relojActualizado = new int[3];
            for(i=0;i<3;i++)
            {
                relojActualizado[i]=Integer.parseInt(caja[i].getText());
            }
            if((relojActualizado[0]<0 || relojActualizado[0]>24) || (relojActualizado[1]<0 || relojActualizado[0]>60) || (relojActualizado[2]<0 || relojActualizado[2]>60))
            {
                JOptionPane.showMessageDialog(this, "Eh toro!, que el reloj no tiene formato");
                for(i=0;i<3;i++)
                {
                    relojActualizado[i]=0;
                }
            }
            this.servidor.setHora(relojActualizado[0],relojActualizado[1],relojActualizado[2]);
            System.out.println("Reloj Actualizado:"+relojActualizado[0]+":"+relojActualizado[1]+":"+relojActualizado[2]);
        }
        if(e.getSource() == boton[4])
        {
            //volver
            limpiarVentana();
            inicializarComponentes();

        }
    }

    /**
    * Inicia el servicio que mantendra comunicacion con el servidor y el reloj funcionando
    **/
    public void iniciar_servidor()
    {
        this.servidor = new Usuario();
        this.servidor.enviarDatagrama();
       
    }

}