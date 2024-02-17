/**
 * 
 */
package multicast.servidor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
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
 *  Vista del servidor multicast 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame implements ActionListener {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Referencia al controlador
	 */
	private Servidor servidor;

	/**
	 * Campo de entrada de los mensajes
	 */
	private JTextField entradaTexto;

	/**
	 * Boton de enviar mensaje
	 */
	private JButton btnEnviar;

	/**
	 * Etiqueta que muestra el puerto usado
	 */
	private JLabel lbPuerto;

	/**
	 * area de texto en la que mostrar el historial de mensajes enviados
	 */
	private JTextArea texto;

	/**
	 * Constructor
	 */
	public Ventana() {
		configuracionDeElementos();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// listener de ventana
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				// foco en la entrada de texto
				entradaTexto.grabFocus();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				salir();
			}
		});
	}

	/**
	 * Define la referencia al servidor
	 * 
	 * @param servidor El servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/**
	 * Configura los elementos de la interfaz
	 */
	private void configuracionDeElementos() {
		this.setTitle(Textos.TITULO_SERVIDOR);
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
		// fila de entrada de texto
		JPanel filaEntrada = new JPanel();
		BoxLayout bLayout = new BoxLayout(filaEntrada, BoxLayout.X_AXIS);
		filaEntrada.setLayout(bLayout);

		entradaTexto = new JTextField();
		entradaTexto.setActionCommand("enviar");
		filaEntrada.add(entradaTexto);

		btnEnviar = new JButton(Textos.BTN_ENVIAR);
		btnEnviar.setActionCommand("enviar");
		filaEntrada.add(btnEnviar);

		// fila de estado
		JPanel filaEstado = new JPanel();
		BoxLayout bLayout2 = new BoxLayout(filaEstado, BoxLayout.X_AXIS);

		filaEstado.setLayout(bLayout2);

		JLabel textoPuerto = new JLabel(Textos.PUERTO);
		lbPuerto = new JLabel();
		filaEstado.add(textoPuerto);
		filaEstado.add(Box.createRigidArea(new Dimension(5, 0)));
		filaEstado.add(lbPuerto);

		// montar panel inferior
		JPanel panelInferior = new JPanel();
		GridLayout gLayout = new GridLayout(2, 1);
		panelInferior.setLayout(gLayout);
		panelInferior.add(filaEntrada);
		panelInferior.add(filaEstado);
		this.add(panelInferior, BorderLayout.SOUTH);

		// eventos
		itemSalir.addActionListener(this);
		btnEnviar.addActionListener(this);
		entradaTexto.addActionListener(this);

	}

	/**
	 * Ordena a servidor terminar el programa tras una confirmacion
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
	 * Pide un puerto
	 * 
	 * @return El puerto introducido
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

	/**
	 * Escucha de los botones
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();

		switch (ac) {
		case "salir" -> salir();
		case "enviar" -> enviar();
		}

	}

	/**
	 * Ordena a servidor enviar el mensaje que hay en la caja de texto si tiene
	 * contenido
	 */
	private void enviar() {
		String msg = entradaTexto.getText();
		if (msg.length() > 0) {
			servidor.enviar(msg);
			agregarMsg(formateaMensaje(msg));
		}

		entradaTexto.setText("");
		// foco inicial
		entradaTexto.grabFocus();
	}

	
	/**
	 * Agrega un mensaje al historial
	 * @param msg El mensaje
	 */
	private void agregarMsg(String msg) {
		texto.setText(texto.getText() + msg);
	}

	/**
	 * Formatea un mensaje
	 * @param msg El mensaje a formatear
	 * @return El mensaje formateado
	 */
	public String formateaMensaje(String msg) {
		Date d = new Date();
		return d.toString() + "\n" + msg + "\n\n";
	}
 

	/**
	 * Actualiza la etiqueta de puerto actual
	 * 
	 * @param puerto El puerto usado
	 */
	public void setDatosConexion(int puerto) {
		lbPuerto.setText("" + puerto);
	}

}
