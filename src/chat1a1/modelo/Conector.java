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
 * 
 * @author Bailon
 */
public class Conector {

	Controlador controlador;
	int puertoLocal = -1;
	int puertoRemoto = -1;
	InetAddress direccionRemota;
	DatagramSocket socketLocal;
	boolean hablar = false;
	
	public static final int EST_DESCONECTADO = 0;
	public static final int EST_ESPERANDO = 10;
	public static final int EST_CONECTADO = 20;
	
	//VALORES DE NEGOCIACION
	private final String INICIAR_NEGOCIACION = "0";
	private final String QUIERO_HABLAR = "10";
	private final String ACEPTO_HABLAR = "20";
	private final String EMPIEZO_YO = "30";

	private final static int MAX_BYTES = 1500;
	private final static String COD_TEXTO = "UTF-8";

	/**
	 * @param direccion
	 * @param puerto
	 */
	public boolean configurarConexion(int puertoLocal, String direccion, int puertoRemoto) {
		this.puertoLocal = puertoLocal;
		try {
			this.direccionRemota = InetAddress.getByName(direccion);
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			return false;
		}
		this.puertoRemoto = puertoRemoto;

		return crearSocketLocal();
	}

	/**
	 * @return
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

	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
		controlador.mostrarEstado(EST_DESCONECTADO);
	}

	public boolean enviar(String msg) {
		byte[] datos = msg.getBytes();
		DatagramPacket envio = new DatagramPacket(datos, datos.length, direccionRemota, puertoRemoto);
		try {
			socketLocal.send(envio);
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public String recibir() {
		
		byte[] bufer = new byte[MAX_BYTES];
		DatagramPacket recibido = new DatagramPacket(bufer, bufer.length);

		try {
			socketLocal.receive(recibido);
			if(recibido.getAddress().equals(direccionRemota)&&recibido.getPort()==puertoRemoto) {
				return new String(recibido.getData(), 0, recibido.getLength(), COD_TEXTO);
			}else {
				return null;
			}
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
	}

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

	public int getPuertoLocal() {
		return socketLocal.getLocalPort();
	}

	public boolean isHablar() {
		return hablar;
	}

	/**
	 * 
	 */
	public void cerrar() {
		if (socketLocal != null)
			socketLocal.close();

	}

	/**
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
		socketLocal.connect(direccionRemota,puertoRemoto);
		controlador.mostrarEstado(EST_CONECTADO);
	}

	/**
	 * @return
	 */
	public String getDireccionRemota() {
		return direccionRemota.toString();
	}

	/**
	 * @return
	 */
	public int getPuertoRemoto() {
		return puertoRemoto;
	}

}
