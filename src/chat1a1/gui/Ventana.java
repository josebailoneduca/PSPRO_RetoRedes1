/**
 * 
 */
package chat1a1.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import chat1a1.Config;
import chat1a1.controlador.Controlador;
import chat1a1.modelo.Conector;

/**
 * Interfaz grafica. Se encarga de recoger los mensajes y enviarlos al controlador así
 * como de mostrar los mensajes recibidos. Tambien muestra los diálogos de introduccion
 * de los parametros de conexion si el controlador lo solicita.
 * 
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame  implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Referencia al controlador
	 */
	private Controlador controlador;
	
	/**
	 * Area donde se mostrara el texto del historial de mensajes
	 */
	private JTextArea texto ;
	
	/**
	 * Boton de enviar mensaje
	 */
	private JButton btnEnviar;
	
	/**
	 * Campo de introduccion del mensaje a enviar
	 */
	private JTextField entradaTexto;
	
	/**
	 * Etiqueta de estado
	 */
	private JLabel lbEstado;
	
	/**
	 * Etiqueta de datos de conexion
	 */
	private JLabel lbConexion;
	
	
	/**
	 * Constructor
	 */
	public Ventana() {
		configuracionDeElementos();
		this.setTitle(Textos.TITULO);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//listener de cierre de ventana
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				//foco en la entrada de texto
				entradaTexto.grabFocus();
			}
			@Override
		    public void windowClosing(WindowEvent e) {
		        salir();
		    }
		});
	}

	/**
	 * Define la referencia al controlador
	 * @param controlador
	 */
	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}


	/**
     * Muestra un mensaje de error
     * @param msg  El mensaje
     */
    public void msgError(String msg) {
        JOptionPane.showMessageDialog(this, msg, Textos.ERROR, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje de informacion
     * @param msg  El mensaje
     */
    public void msgInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
	 * Pide la direccion remota con un joptionpane en el que muestra un listado de
	 *  direcciones ip locales
	 *
	 * @param direcionLocal Lista de direcciones locales a mostrar
	 * @return La direcion introducida
	 */
	public String pedirDireccionRemota(List<String> direcionLocal) {
		String direccion=null;
		while(direccion==null) {
			String mensaje= Textos.MSG_DIRECCIONES_LOCALES+"\n "+direcionLocal.stream().collect(Collectors.joining("\n"))+"\n "+Textos.MSG_DIRECCION_REMOTA;
			direccion= JOptionPane.showInputDialog(this, mensaje, Config.DIRECCION_REMOTA_DEFAULT);
			if (direccion==null||direccion.length()<1)
				direccion=null;
		}
		return direccion;
	}

	/**
	 * Pedir el puerto local de escucha
	 * 
	 * @return El puerto
	 */
	public int pedirPuertoLocal() {
		return pedirPuerto(Textos.MSG_PUERTO_LOCAL,Config.PUERTO_LOCAL_DEFAULT);
	}
	
	public int pedirPuertoRemoto() {
		return pedirPuerto(Textos.MSG_PUERTO_REMOTO,Config.PUERTO_REMOTO_DEFAULT);
	}
	

	/**
	 * Agrega un mensaje remoto al historial
	 * @param datos Contenido del mensaje
	 * @param origen Direccion origen del mensaje
	 * @param puerto Puerto desde el que se envio
	 */
	public void mensajeRecibido(String datos, String origen, int puerto) {
		agregarMsg(formateaMensaje(datos, origen,puerto));
	}
	
	/**
	 *  Desactiva los controles de entrada de mensajes
	 */
	public void desactivarEntrada() {
		btnEnviar.setEnabled(false);
		entradaTexto.setEnabled(false);
		
	}
	
	/**
	 * Activa los controles de entrada del mensaje y pone el foco
	 * en el campo de introduccion de mensajes
	 */
	public void activarEntrada() {
		btnEnviar.setEnabled(true);
		entradaTexto.setEnabled(true);
		entradaTexto.grabFocus();
	}


	/**
	 * Muestra en la interfaz los datos de conexion
	 * 
	 * @param puertoLocal El puerto en el que escucha el programa
	 * @param direccionRemota Direccion remota con la que esta conectada
	 * @param puertoRemoto Puerto remoto con el que esta conectado
	 */
	public void setDatosConexion(int puertoLocal, String direccionRemota, int puertoRemoto) {
		String stConexion= String.format(Textos.PUERTO_LOCAL+": %d      "+Textos.DIRECCION_REMOTA+":%s:%d", puertoLocal,direccionRemota,puertoRemoto);
		lbConexion.setText(stConexion);
	}
	
	
	/**
	 * Define el estado del sistema 
	 * 
	 * @param estado Numero referente al tipo de estado
	 */
	public void setEstado(int estado) {
		switch (estado) {
		case Conector.EST_DESCONECTADO-> lbEstado.setText(Textos.DESCONECTADO);
		case Conector.EST_ESPERANDO-> {lbEstado.setText(Textos.ESPERANDO);}
		case Conector.EST_CONECTADO-> lbEstado.setText(Textos.CONECTADO);
		}
	}

	/**
	 * Listener de los botones
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String ac=e.getActionCommand();
		
		switch(ac) {
		case "salir"-> salir();
		case "enviar"-> enviar();
		}
		
	}

	/**
	 * Configura los elementos de la interfaz
	 */
	private void configuracionDeElementos() {
		this.setBounds(0,0,Config.ANCHO_VENTANA,Config.ALTO_VENTANA);
		this.setLayout(new BorderLayout());
		//MENU
		JMenuBar barraMenu = new JMenuBar();
		JMenu menu = new JMenu(Textos.ARCHIVO);
		JMenuItem itemSalir = new JMenuItem(Textos.SALIR);
		itemSalir.setActionCommand("salir");
		menu.add(itemSalir);
		barraMenu.add(menu);
		this.add(barraMenu,BorderLayout.NORTH);
	
		//ZONA DE MENSAJES
		texto = new JTextArea();
		texto.setLineWrap(true);
		texto.setEditable(false);
		JScrollPane scroll = new JScrollPane(texto);
		this.add(scroll,BorderLayout.CENTER);
		
		
		//PANEL INFERIOR
		//fila de entrada de texto
		JPanel filaEntrada = new JPanel();
		BoxLayout bLayout= new BoxLayout(filaEntrada, BoxLayout.X_AXIS);
		filaEntrada.setLayout(bLayout);
		
		entradaTexto = new JTextField();
		entradaTexto.setActionCommand("enviar");
		filaEntrada.add(entradaTexto);
		
		btnEnviar=new JButton(Textos.BTN_ENVIAR);
		btnEnviar.setActionCommand("enviar");
		filaEntrada.add(btnEnviar);
		
		//fila de estado
		JPanel filaEstado=new JPanel();
		BoxLayout bLayout2= new BoxLayout(filaEstado, BoxLayout.X_AXIS);
		
		filaEstado.setLayout(bLayout2);
	
		
		JLabel textoEstado=new JLabel(Textos.ESTADO);
		lbEstado=new JLabel();
		filaEstado.add(textoEstado);
		filaEstado.add(lbEstado);
		filaEstado.add(Box.createRigidArea(new Dimension(50, 0)));
	
		lbConexion=new JLabel();
		filaEstado.add(lbConexion);
		
		
		//montar panel inferior
		JPanel panelInferior = new JPanel();
		GridLayout gLayout=new GridLayout(2, 1);
		panelInferior.setLayout(gLayout);
		panelInferior.add(filaEntrada);
		panelInferior.add(filaEstado);
		this.add(panelInferior,BorderLayout.SOUTH);
		
		
		//eventos
		itemSalir.addActionListener(this);
		btnEnviar.addActionListener(this);
		entradaTexto.addActionListener(this);
	
	}

	/**
	 * Muestra un mensaje de confirmacion
	 *
	 * @param msg El mensaje a mostrar
	 * @return True si ha pulsado SI. False si ha pulsado NO
	 */
	private boolean confirmar(String msg) {
	    int respuesta = JOptionPane.showConfirmDialog(this, msg, "", JOptionPane.YES_NO_OPTION);
	    return respuesta == JOptionPane.YES_OPTION;
	}

	/**
	 * Pide un puerto comprobando que la entrada es entre 1 y 65535
	 * @param msg Mensaje a mostrar
	 * @param puertoInicial Valor inicial del puerto
	 * @return El puerto introiducido
	 */
	private int pedirPuerto(String msg,int puertoInicial) {
		int puerto=-1;
		while(puerto<1 || puerto >65535) {
			String sPuerto= JOptionPane.showInputDialog(this, msg, puertoInicial);
			try {
				puerto = Integer.parseInt(sPuerto);
			}catch(NumberFormatException ex) {
				puerto=-1;
			}
		}
		return puerto;
	}

	/**
	 * Manda al controlador un mensaje de texto para que lo envie por red
	 */
	private void enviar() {
		String msg = entradaTexto.getText();
		if (msg.length()>0) {
			controlador.enviar(msg);
			agregarMsg(formateaMensaje(msg, Textos.TU,0));
		}
		entradaTexto.setText("");
		//foco inicial
		entradaTexto.grabFocus();
	}

	/**
	 * Agrega un mensaje al historial
	 * 
	 * @param msg
	 */
	private void agregarMsg(String msg) {
		texto.setText(texto.getText()+msg);
	}

	/**
	 * Muestra un mensaje de confirmacion y envia al controlador la orden de terminar
	 */
	private void salir() {
		if (confirmar(Textos.MENSAJE_SALIR))
			controlador.salir();
	}

	/**
	 * Devuelve un string con un formato apto para mostrar en el historial. 
	 * @param msg Mensaje
	 * @param origen Direccion origen
	 * @param puerto Puerto. Si el puerto es 0 no se escribe
	 * 
	 * @return El mensaje formateado
	 */
	private String formateaMensaje(String msg, String origen, int puerto) {
		String stPuerto="";
		if (puerto!=0)
			stPuerto=":"+puerto;
		return origen+stPuerto+"\n"+msg+"\n\n";
	}
	
}
