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
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;



/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Servidor {
	Ventana vista;
	int puerto;
	String rutaConf;
	InetAddress direccionMulticast;
	MulticastSocket mulSoc;
	
	public Servidor(Ventana v,String rutaConf) {
		super();
		this.vista = v;
		this.vista.setServidor(this);
	}

	/**
	 * 
	 */
	public void iniciar() {
		if (rutaConf!=null)
			cargarConfiguracion();
		else
			puerto=vista.pedirPuerto(); 
		
		prepararSocket();
		
		vista.setDatosConexion(puerto);
		
	}
	
	/**
	 * 
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
	 * 
	 */
	private void cargarConfiguracion() {
		File archConf=null;
		try {
		archConf = new File(rutaConf);
		FileReader r = new FileReader(archConf);
 		Properties prop = new Properties();
		//cargar config
			prop.load(r);
			 puerto=Integer.parseInt((String) prop.get("puerto"));
		} catch (IOException|NumberFormatException e) {
			vista.msgError(Textos.ERROR_LEYENDO_CONF+":"+archConf.getAbsolutePath());
			puerto=vista.pedirPuerto();
		}
	}

	/**
	 * 
	 */
	public void salir() {
		mulSoc.close ();
		System.exit(0);	
	}

	/**
	 * @param msg
	 */
	public void enviar(String msg) {
		  DatagramPacket paquete = new DatagramPacket(msg.getBytes(), msg.length(), direccionMulticast, puerto);
		  try {
			mulSoc.send(paquete);
		} catch (IOException e) {
			vista.msgError(Textos.ERROR_ENVIANDO_PAQUETE);
		} 
		
	}


 
	

}
