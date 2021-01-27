import java.awt.event.*;
import javax.swing.*;
import java.awt.Image;

public class Gui extends JFrame implements ActionListener {

    public JLabel texto[] = new JLabel[3];           // etiqueta o texto no editable
    private JButton boton[]= new JButton[4];          // boton con una determinada accion
    private JTextField caja[] = new JTextField[3];
    private int i;

    private Maestro servidor;


    /**
    * Constructor de la clase
    **/

    public Gui() 
    {
        super();                    // usamos el contructor de la clase padre JFrame
        configurarVentana();        // configuramos la ventana
        inicializarComponentes();   // inicializamos los atributos o componentes
        
        //this.servidor.start();
    }

    public void implementarServidor(Maestro serv)
    {
        this.servidor = serv;
        this.servidor.start();
    }

    /*
    * Configuraciones principales de la ventana
    */

    private void configurarVentana() 
    {
        this.setTitle("Servidor");                   // colocamos titulo a la ventan
        this.setLocationRelativeTo(null);                       // centramos la ventana en la pantalla
        this.setLayout(null);                                   // no usamos ningun layout, solo asi podremos dar posiciones a los componentes
        this.setResizable(false);                               // hacemos que la ventana no sea redimiensionable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // hacemos que cuando se cierre la ventana termina todo proceso
        for(i = 0; i < 3; i ++)
        {
            texto[i] = new JLabel();
            caja[i] = new JTextField();
        }
        for(i = 0; i < 4; i ++)
            boton[i] = new JButton();
    }

    /**
    * Elimina los componentes de la ventana
    **/

    private void limpiarVentana() 
    {
        
        for(i = 0; i < 3; i ++)
		{
			this.remove(texto[i]);
            this.remove(caja[i]);
		} 
        for(i = 0; i < 4; i ++)
            this.remove(boton[i]);

        this.repaint();
        this.configurarVentana();
        this.revalidate();
        for(i = 0; i < 3; i ++)
        {
            texto[i] = new JLabel();
            caja[i] = new JTextField();
        }
        for(i = 0; i < 4; i ++)
            boton[i] = new JButton();
        this.revalidate(); 
    }

    /**
    * Agrega componentes de la ventana principal
    **/

    private void inicializarComponentes() 
    {
        this.setTitle("Servidor");                   // colocamos titulo a la ventana
        this.setSize(370, 620); 
         this.setLocationRelativeTo(null);  

        String[] hora;
        // configuramos los componentes
        texto[0].setText("Titulo");    // colocamos un texto a la etiqueta
        texto[0].setBounds(85, 400, 200, 30);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        


        // Coniguracion de imagen
        Image img = new ImageIcon("cuaderno.jpg").getImage();
        ImageIcon img2 = new ImageIcon(img.getScaledInstance(240, 330, Image.SCALE_SMOOTH));

        texto[1].setIcon(img2);
        texto[1].setBounds(65, 50, 240, 330);

        //caja.setBounds(150, 50, 100, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        boton[0].setText("Reiniciar");   // colocamos un texto al boton
        boton[0].setBounds(135, 450, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[1].setText("Ajustar Reloj");   // colocamos un texto al boton
        boton[1].setBounds(110, 500, 150, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        //adicionamos los componentes a la ventana
        this.add(texto[0]);
        this.add(texto[1]);
        boton[0].addActionListener(this);
        this.add(boton[0]);
        boton[1].addActionListener(this);
        this.add(boton[1]);
        this.validate();
    }

        /**
    * Ventana para modificar un reloj en especifico
    * @param hilo reloj al que se conectara
    **/

    private void AjustarReloj() 
    {
        limpiarVentana();

        String[] hora = this.servidor.getHora();
        //System.out.println(hora[3]+":"+hora[2]+":"+hora[1]);
        caja[0].setText(hora[3]);
        caja[1].setText(hora[2]);
        caja[2].setText(hora[1]);

        this.setSize(290, 210); 
        this.setLocationRelativeTo(null);
        this.setTitle("Ajustar reloj");

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
        boton[2].setText("Volver");   // colocamos un texto al boton
        boton[2].setBounds(150, 100, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton[3].addActionListener(this);      // hacemos que el boton tenga una accion y esa accion estara en esta clase
        boton[2].addActionListener(this); 

        for(i = 0; i < 3; i ++)
        {
            this.add(texto[i]);
            this.add(caja[i]);
        } 
        for(i = 2; i < 4; i ++)
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
            this.servidor.Reiniciar();
        }
        if(e.getSource() == boton[1])
        {
            AjustarReloj();
        }
        if(e.getSource() == boton[2])
        {
            //volver
            limpiarVentana();
            inicializarComponentes();
        }
        if(e.getSource() == boton[3])
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
            this.servidor.setHora(relojActualizado[0],relojActualizado[1],relojActualizado[2]);
            System.out.println("Reloj Actualizado:"+relojActualizado[0]+":"+relojActualizado[1]+":"+relojActualizado[2]);
            this.servidor.modificar(caja[0].getText()+":"+caja[1].getText()+":"+caja[2].getText());
        }
    }
}