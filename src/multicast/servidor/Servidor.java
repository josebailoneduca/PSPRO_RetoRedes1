/**
 * 
 */
package multicast.servidor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;

/**
 * 
 * Modelo/controlador del servidor multicast
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Servidor {

	/**
	 * Referencia a la vista
	 */
	private Ventana vista;

	/**
	 * Ruta del archivo de configuracion
	 */
	private String rutaConf;

	/**
	 * Puerto de envio multicast
	 */
	private int puerto;

	/**
	 * Direccion de envio multicast
	 */
	private InetAddress direccionMulticast;

	/**
	 * Socket multicast para enviar
	 */
	private MulticastSocket mulSoc;

	public Servidor(Ventana v, String rutaConf) {
		super();
		this.vista = v;
		this.vista.setServidor(this);
	}

	/**
	 * Inicia el servidor cargando o pidiendo la configuracion y lanzando la
	 * preparacion del socket
	 */
	public void iniciar() {
		if (rutaConf != null)
			cargarConfiguracion();
		else
			puerto = vista.pedirPuerto();

		prepararSocket();

		vista.setDatosConexion(puerto);

	}

	
	/**
	 * Carga la configuracion de un archivo. Si hay algun problema la pide al usuario
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
		} catch (IOException | NumberFormatException e) {
			vista.msgError(Textos.ERROR_LEYENDO_CONF + ":" + archConf.getAbsolutePath());
			puerto = vista.pedirPuerto();
		}
	}
	
	
	/**
	 *  Prepara el socket
	 */
	private void prepararSocket() {
		try {
			direccionMulticast = InetAddress.getByName(Config.IP_MULTICAST);
			mulSoc = new MulticastSocket();
		} catch (IOException e) {
			vista.msgError(Textos.ERROR_CREANDO_SOCKET);
		}
	}


	/**
	 * Envia un datagrama con el mensaje por el socket multicast 
	 * 
	 * @param msg  El mensaje a enviar
	 */
	public void enviar(String msg) {
		DatagramPacket paquete = new DatagramPacket(msg.getBytes(), msg.length(), direccionMulticast, puerto);
		try {
			mulSoc.send(paquete);
		} catch (IOException e) {
			vista.msgError(Textos.ERROR_ENVIANDO_PAQUETE);
		}

	}

	
	/**
	 * Cierra el socket y sale de la aplicacion
	 */
	public void salir() {
		mulSoc.close();
		System.exit(0);
	}
}
