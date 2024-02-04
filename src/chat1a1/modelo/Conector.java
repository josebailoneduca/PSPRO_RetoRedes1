/**
 * 
 */
package chat1a1.modelo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import chat1a1.controlador.Controlador;

/**
 * Clase que se encarga de gestionar el envio y recepcion de mensajes
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Conector {

	/**
	 * Referencia al controlador
	 */
	private Controlador controlador;

	/**
	 * Puerto local para la escucha
	 */
	private int puertoLocal = -1;

	/**
	 * Puerto remoto al que enviar los mensajes
	 */
	private int puertoRemoto = -1;

	/**
	 * Direccion remota a la que enviar los mensajes
	 */
	private InetAddress direccionRemota;

	/**
	 * Socket UDP local para la escucha y envio
	 */
	private DatagramSocket socketLocal;

	/**
	 * True si es el turno de hablar, false si es el turno de escuchar
	 */
	private boolean hablar = false;

	/**
	 * Estado de desconectado
	 */
	public static final int EST_DESCONECTADO = 0;

	/**
	 * Estado de esperando conexion
	 */
	public static final int EST_ESPERANDO = 10;

	/**
	 * Estado de conectado
	 */
	public static final int EST_CONECTADO = 20;

	// VALORES DE NEGOCIACION

	/**
	 * Valor de que hay que iniciar la negociacion
	 */
	private final String INICIAR_NEGOCIACION = "0";

	/**
	 * Valor de que se solicita hablar
	 */
	private final String QUIERO_HABLAR = "10";

	/**
	 * Valor de que se acepta hablar
	 */
	private final String ACEPTO_HABLAR = "20";

	/**
	 * Valor de que se solicita empezar
	 */
	private final String EMPIEZO_YO = "30";

	/**
	 * Tamano maximo del datagrama
	 */
	private final static int MAX_BYTES = 1500;

	/**
	 * Codificacion del texto para enviar
	 */
	private final static String COD_TEXTO = "UTF-8";

	/**
	 * Configura los datos para la conexion
	 * 
	 * @param puertoLocal  Puerto al que escuchar
	 * @param direccion    Direccion remota
	 * @param puertoRemoto Puerto remoto
	 * @return Devuelve true si se ha podido aplicar la configuracion
	 */
	public boolean configurarConexion(int puertoLocal, String direccion, int puertoRemoto) {
		// almacenar datos de configuracion
		this.puertoLocal = puertoLocal;
		try {
			this.direccionRemota = InetAddress.getByName(direccion);
		} catch (UnknownHostException e) {
			// e.printStackTrace();
			return false;
		}
		this.puertoRemoto = puertoRemoto;

		// crear el databramsocket local
		return crearSocketLocal();
	}

	/**
	 * Crea un datagramsocket segun el puerto local configurado
	 * 
	 * @return True si se ha podido crear el datagramsocket, false si no se ha
	 *         podido
	 */
	private boolean crearSocketLocal() {
		try {
			socketLocal = new DatagramSocket(puertoLocal);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Establece la referencia al controlador y establece el estado de desconectado
	 * 
	 * @param controlador El controlador
	 */
	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
		controlador.mostrarEstado(EST_DESCONECTADO);
	}

	/**
	 * Envia un mensaje por el socket local a la direccion y puertos remotos
	 * 
	 * @param msg El mensaje a enviar
	 * 
	 * @return Ture si se ha enviado, False si ha habido algun problema
	 */
	public boolean enviar(String msg) {
		byte[] datos = msg.getBytes();
		DatagramPacket envio = new DatagramPacket(datos, datos.length, direccionRemota, puertoRemoto);
		try {
			socketLocal.send(envio);
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Espera la recepcion de un datagrama
	 * 
	 * @return Los datos del datagrama convertidos a String
	 */
	public String recibir() {

		byte[] bufer = new byte[MAX_BYTES];
		DatagramPacket recibido = new DatagramPacket(bufer, bufer.length);

		try {
			socketLocal.receive(recibido);
			if (recibido.getAddress().equals(direccionRemota) && recibido.getPort() == puertoRemoto) {
				return new String(recibido.getData(), 0, recibido.getLength(), COD_TEXTO);
			} else {
				return null;
			}
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * Devuelve un listado con de direciones vinculadas con las interfaces de red
	 * 
	 * @return El listado de direcciones
	 */
	public List<String> getListaInterfaces() {
		ArrayList<String> listaInterfaces = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				Enumeration<InetAddress> direcciones = interfaces.nextElement().getInetAddresses();
				while (direcciones.hasMoreElements()) {
					InetAddress direccion = direcciones.nextElement();
					if (direccion instanceof Inet4Address)
						listaInterfaces.add(direccion.toString());
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return listaInterfaces;
	}

	/**
	 * Devuelve el puerto local del socket
	 * 
	 * @return
	 */
	public int getPuertoLocal() {
		return socketLocal.getLocalPort();
	}

	/**
	 * Devuelve si es el turno de hablar
	 * 
	 * @return
	 */
	public boolean isHablar() {
		return hablar;
	}

	/**
	 * Cierra el socket
	 */
	public void cerrar() {
		if (socketLocal != null)
			socketLocal.close();

	}

	/**
	 * Efectua la negociacion. Tras terminar la misma se habra establecido si es el
	 * turno de hablar o de esperar mensaje.
	 * 
	 * La negociacion se efectua siguiendo los mensajes:
	 * <ul>
	 * <li>INICIAR_NEGOCIACION = "0"</li>
	 * <li>QUIERO_HABLAR = "10"</li>
	 * <li>ACEPTO_HABLAR = "20"</li>
	 * <li>EMPIEZO_YO = "30"</li>
	 * </ul>
	 * 
	 * La negociacion se realiza en un bucle en el que se van enviando esos codigos
	 * a la direcciond de destino y seguidamente se escucha.
	 * 
	 * El proceso ideal es el siguiente suponiendo dos partes A y B que intentan
	 * negociar entre si para ver quien empieza a hablar:
	 * 
	 * <ul>
	 * <li>1- A envia a B: QUIERO_HABLAR</li>
	 * <li>2- B responde a A: ACEPTO_HABLAR</li>
	 * <li>3- A responde a B: EMPIEZO_YO</li>
	 * </ul>
	 * 
	 * Llegado a este punto cuando se envia el mensaje EMPIEZO_YO se establece que
	 * toca hablar y termina la negociacion. Por otro lado cuando se recibe el
	 * mensaje EMPIEZO_YO se establece que empieza a hablar el otro y se termina la
	 * negociacion.
	 * 
	 * En caso de que tras el punto 1 no se obtuviera respuesta se quedaria
	 * esperando a recibir un datagrama. Si el datagrama que se recibe es el de
	 * QUIERO_HABLAR porque otro programa se ha iniciado despues entonces empezaria
	 * la negociacion tal y como lo descrito.
	 * 
	 * Una vez terminada la negociacion se ejecuta el metodo "connect" con la
	 * direccion y puertos remotos para asegurar que no llegaran datagramas de otro
	 * destino ni se enviaran datagramas a otro destino.
	 * 
	 * 
	 * 
	 */
	public void negociar() {
		boolean negociar = true;
		String ultimo = INICIAR_NEGOCIACION;
		while (negociar) {
			if (ultimo == null) {
				ultimo = recibir();
			} else {
				switch (ultimo) {
				case INICIAR_NEGOCIACION:
					enviar(QUIERO_HABLAR);
					controlador.mostrarEstado(EST_ESPERANDO);
					ultimo = recibir();
					break;
				case QUIERO_HABLAR:
					enviar(ACEPTO_HABLAR);
					ultimo = recibir();
					break;
				case ACEPTO_HABLAR:
					enviar(EMPIEZO_YO);
					hablar = true;
					negociar = false;
					break;
				case EMPIEZO_YO:
					hablar = false;
					negociar = false;
					break;
				}
			}
		}
		socketLocal.connect(direccionRemota, puertoRemoto);
		controlador.mostrarEstado(EST_CONECTADO);
	}

	/**
	 * Devuelve la direccion remota de configuracion
	 * 
	 * @return La direccion
	 */
	public String getDireccionRemota() {
		return direccionRemota.toString();
	}

	/**
	 * Devuelve el puerto remoto de configuracion
	 * 
	 * @return El puerto
	 */
	public int getPuertoRemoto() {
		return puertoRemoto;
	}

}
