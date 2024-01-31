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
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame  implements ActionListener{

 
	private static final long serialVersionUID = 1L;
	private Controlador controlador;
	JTextArea texto ;
	JButton btnEnviar;
	JTextField entradaTexto;
	JLabel lbEstado;
	JLabel lbConexion;
	
	public Ventana() {
		configuracionDeElementos();
		this.setTitle(Textos.TITULO);
		//listener de cierre
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				//foco en la entrada de texto
				entradaTexto.grabFocus();
			}

			@Override
		    public void windowClosing(WindowEvent e) {
		        controlador.salir();
		    }
		});
	}


	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}


	/**
	 * 
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
	 * 
	 * @return
	 */
	public void salir() {
		if (confirmar(Textos.MENSAJE_SALIR))
			controlador.salir();
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
	 * 
	 */
	public String pedirDireccionRemota(List<String> direcionLocal) {
		//pedir direccion remota
		String direccion=null;
		while(direccion==null) {
			String mensaje= Textos.MSG_DIRECCIONES_LOCALES+"\n "+direcionLocal.stream().collect(Collectors.joining("\n"))+"\n "+Textos.MSG_DIRECCION_REMOTA;
			direccion= JOptionPane.showInputDialog(this, mensaje, Config.DIRECCION_REMOTA_DEFAULT);
			if (direccion==null||direccion.length()<1)
				direccion=null;
		}
		return direccion;

	}

	public int pedirPuertoLocal() {
		return pedirPuerto(Textos.MSG_PUERTO_LOCAL,Config.PUERTO_LOCAL_DEFAULT);
	}
	
	public int pedirPuertoRemoto() {
		return pedirPuerto(Textos.MSG_PUERTO_REMOTO,Config.PUERTO_REMOTO_DEFAULT);
	}
	

	/**
	 * @param string
	 * @return
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





	@Override
	public void actionPerformed(ActionEvent e) {
		String ac=e.getActionCommand();
		
		switch(ac) {
		case "salir"-> salir();
		case "enviar"-> enviar();
		}
		
	}





	/**
	 * @return
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



private void agregarMsg(String msg) {
	texto.setText(texto.getText()+msg);
}

	/**
	 * @param datos
	 * @param puerto 
	 * @return
	 */
	public void mensajeRecibido(String datos, String origen, int puerto) {
		agregarMsg(formateaMensaje(datos, origen,puerto));
	}
	
	
	public String formateaMensaje(String msg, String origen, int puerto) {
		String stPuerto="";
		if (puerto!=0)
			stPuerto=":"+puerto;
		return origen+stPuerto+"\n"+msg+"\n\n";
	}





	/**
	 * 
	 */
	public void desactivarEntrada() {
		btnEnviar.setEnabled(false);
		
	}
	public void activarEntrada() {
		btnEnviar.setEnabled(true);
		
	}


	/**
	 * @param puertoLocal
	 * @param direccionRemota
	 * @param puertoRemoto
	 */
	public void setDatosConexion(int puertoLocal, String direccionRemota, int puertoRemoto) {
		String stConexion= String.format(Textos.PUERTO_LOCAL+": %d      "+Textos.DIRECCION_REMOTA+":%s:%d", puertoLocal,direccionRemota,puertoRemoto);
		lbConexion.setText(stConexion);
	}
	
	
	public void setEstado(int estado) {
		switch (estado) {
		case Conector.EST_DESCONECTADO-> lbEstado.setText(Textos.DESCONECTADO);
		case Conector.EST_ESPERANDO-> lbEstado.setText(Textos.ESPERANDO);
		case Conector.EST_CONECTADO-> lbEstado.setText(Textos.CONECTADO);
		}
	}
	
}
