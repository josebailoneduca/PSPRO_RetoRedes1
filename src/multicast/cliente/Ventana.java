/**
 * 
 */
package multicast.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Iterator;
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

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Cliente servidor;
	JTextField entradaTexto;
	JButton btnEnviar;
	private JLabel lbPuerto;

	JTextArea texto;

	private JLabel lbInterfaz;

	public Ventana() {
		configuracionDeElementos();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// listener de cierre
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salir();
			}
		});
	}

	public void setServidor(Cliente servidor) {
		this.servidor = servidor;
	}

	/**
	 * 
	 */
	private void configuracionDeElementos() {
		this.setBounds(0, 0, Config.ANCHO_VENTANA, Config.ALTO_VENTANA);
		this.setLayout(new BorderLayout());
		// MENU
		JMenuBar barraMenu = new JMenuBar();
		JMenu menu = new JMenu(Textos.ARCHIVO);
		JMenuItem itemSalir = new JMenuItem(Textos.SALIR);
		itemSalir.setActionCommand("salir");
		menu.add(itemSalir);
		barraMenu.add(menu);
		this.add(barraMenu, BorderLayout.NORTH);

		// ZONA DE MENSAJES
		texto = new JTextArea();
		texto.setLineWrap(true);
		texto.setEditable(false);
		JScrollPane scroll = new JScrollPane(texto);
		this.add(scroll, BorderLayout.CENTER);

		// PANEL INFERIOR

		// fila de estado
		JPanel filaEstado = new JPanel();
		BoxLayout bLayout2 = new BoxLayout(filaEstado, BoxLayout.X_AXIS);

		filaEstado.setLayout(bLayout2);

		JLabel textoPuerto = new JLabel(Textos.PUERTO);
		lbPuerto = new JLabel();

		JLabel textoInterfaz= new JLabel(Textos.INTERFAZ);
		lbInterfaz = new JLabel();
		
		filaEstado.add(textoPuerto);
		filaEstado.add(Box.createRigidArea(new Dimension(5, 0)));
		filaEstado.add(lbPuerto);
		filaEstado.add(Box.createRigidArea(new Dimension(30, 0)));
		filaEstado.add(textoInterfaz);
		filaEstado.add(Box.createRigidArea(new Dimension(5, 0)));
		filaEstado.add(lbInterfaz);



		// montar panel inferior
		JPanel panelInferior = new JPanel();
		GridLayout gLayout = new GridLayout(2, 1);
		panelInferior.setLayout(gLayout);
		panelInferior.add(filaEstado);
		this.add(panelInferior, BorderLayout.SOUTH);

		// eventos
		itemSalir.addActionListener(this);

	}

	/**
	 * 
	 * @return
	 */
	public void salir() {
		if (confirmar(Textos.MENSAJE_SALIR))
			servidor.salir();
	}

	/**
	 * Muestra un mensaje de error
	 * 
	 * @param msg El mensaje
	 */
	public void msgError(String msg) {
		JOptionPane.showMessageDialog(this, msg, Textos.ERROR, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un mensaje de informacion
	 * 
	 * @param msg El mensaje
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
	 * @param string
	 * @return
	 */
	public int pedirPuerto() {
		int puerto = -1;
		while (puerto < 1 || puerto > 65535) {
			String sPuerto = JOptionPane.showInputDialog(this, Textos.PEDIR_PUERTO, Config.PUERTO_DEFAULT);
			try {
				puerto = Integer.parseInt(sPuerto);
			} catch (NumberFormatException ex) {
				puerto = -1;
			}
		}
		return puerto;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();

		switch (ac) {
		case "salir" -> salir();
		}

	}



	public void agregarMsg(String msg) {
		texto.setText(texto.getText()+formateaMensaje(msg));
	}

	public String formateaMensaje(String msg) {
		Date d = new Date();
		return d.toString() + "\n" + msg + "\n\n";
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
	 * @param string 
	 * @param puertoLocal
	 * @param direccionRemota
	 * @param puertoRemoto
	 */
	public void setDatosConexion(int puerto, String interfaz) {
		lbPuerto.setText("" + puerto);
		lbInterfaz.setText(interfaz);
	}

	/**
	 * @param listaInterfaces
	 * @return
	 */
	public String pedirInterfaz(List<String> listaInterfaces) {
		
		Object[] interfaces = listaInterfaces.toArray();
		Object opcion=null;
		while(opcion==null) {
		opcion = JOptionPane.showInputDialog(null,"Selecciona una interfaz", "Elegir",JOptionPane.QUESTION_MESSAGE,null,interfaces, interfaces[0]);
		System.out.println(opcion);
		}
		return opcion.toString();
	}


}
