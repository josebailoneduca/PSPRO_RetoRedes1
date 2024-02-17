/**
 * 
 */
package multicast.cliente;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 
 * Modelo/controlador para la aplicacion cliente.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Cliente {

	/**
	 * Referencia a la vista
	 */
	private Ventana vista;

	/**
	 * Ruta del archivo de configuracion
	 */
	private String rutaConf;

	/**
	 * Puerto en el queescuchar
	 */
	private int puerto;

	/**
	 * Direccion multicast en la que escuchar
	 */
	private InetAddress direccionMulticast;

	/**
	 * Socket de escucha
	 */
	private MulticastSocket mulSoc;

	/**
	 * InetSocketAddrees del grupo
	 */
	private InetSocketAddress grupo;

	/**
	 * Interfaz de escucha
	 */
	private NetworkInterface interfaz;

	/**
	 * Direccion de la interfaz
	 */
	private String direccionInterfaz;

	/**
	 * Constructor
	 * 
	 * @param v        Referncia a la vista
	 * @param rutaConf Ruta del archivo de configuracion. Poner en null si no se
	 *                 quiere definir.
	 */
	public Cliente(Ventana v, String rutaConf) {
		super();
		this.vista = v;
		this.vista.setServidor(this);
		this.rutaConf=rutaConf;
	}

	/**
	 * Inicia el cliente cargando o pidiendo la configuracion, preparando el
	 * MulticastSocket de escucha e iniciando un bucle infinito de escucha
	 */
	public void iniciar() {
		if (rutaConf != null)
			cargarConfiguracion();
		else
			pedirConfiguracion();
		//Iniciar socket
		prepararSocket();
		//mostrar datos de la conexion en la GUI
		vista.setDatosConexion(puerto, interfaz.getDisplayName() + " " + direccionInterfaz);
		
		//Hilo independiente que se trata de un bucle infinito de escucha
		new Thread(() -> {
			while (true) {
				escuchar();
			}
		}).start();
	}

	/**
	 * Pide los datos de configuracion al usuario
	 */
	private void pedirConfiguracion() {
		puerto = vista.pedirPuerto();
		direccionInterfaz = vista.pedirInterfaz(getListaInterfaces());
	}

	/**
	 * Carga la configuracion de disco.
	 * Si hay algun problema lo pide al usuario
	 */
	private void cargarConfiguracion() {
		File archConf = null;
		try {
			archConf = new File(rutaConf);
			FileReader r = new FileReader(archConf);
			Properties prop = new Properties();
			// cargar config
			prop.load(r);
			puerto = Integer.parseInt((String) prop.get("puerto"));
			direccionInterfaz = (String) prop.get("direccion_interfaz");
		} catch (IOException | NumberFormatException e) {
			vista.msgError(Textos.ERROR_LEYENDO_CONF + ":" + archConf.getAbsolutePath());
			pedirConfiguracion();
		}
	}
	
	
	/**
	 * Devuelve una lista de direcciones asociadas a las interfaces de red
	 * 
	 * @return La lista
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
						listaInterfaces.add(direccion.toString().replace("/", ""));
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return listaInterfaces;
	}

	/**
	 *  Escucha la recepcion de un paquete multicast
	 */
	private void escuchar() {

		String msg = "";
		// RECIBIR MENSAJES DE TEXTO DEL SERVIDOR
		byte[] bufer = new byte[1000];
		DatagramPacket datagrama = new DatagramPacket(bufer, bufer.length);
		try {
			mulSoc.receive(datagrama);
			msg = new String(datagrama.getData());
			vista.agregarMsg(msg);
			System.out.println(msg);
		} catch (IOException e) {

		}
	}

	/**
	 * Inicia el MulticastSocket configurandolo y uniendolo a un grupo
	 */
	private void prepararSocket() {
		try {
			direccionMulticast = InetAddress.getByName(Config.IP_MULTICAST);
			mulSoc = new MulticastSocket(puerto);

			grupo = new InetSocketAddress(direccionMulticast, puerto);

			// Interfaz con la IP del cliente
			interfaz = NetworkInterface.getByInetAddress(InetAddress.getByName(direccionInterfaz));

			// UNION AL GRUPO
			mulSoc.joinGroup(grupo, interfaz);

		} catch (IOException e) {
			vista.msgError(Textos.ERROR_CREANDO_SOCKET);
		}
	}



	/**
	 * Cierra el socket y sale del programa
	 */
	public void salir() {
		try {
			mulSoc.leaveGroup(grupo, interfaz);
		} catch (IOException e) {
		}
		mulSoc.close();
		System.exit(0);
	}

}
