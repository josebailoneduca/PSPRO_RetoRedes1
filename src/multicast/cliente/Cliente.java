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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;



/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Cliente {
	Ventana vista;
	int puerto;
	String rutaConf;
	InetAddress direccionMulticast;
	MulticastSocket mulSoc;
	InetSocketAddress grupo;
	NetworkInterface interfaz;
	private String direccionInterfaz;
	
	public Cliente(Ventana v,String rutaConf) {
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
			pedirConfiguracion();
		
			
		prepararSocket();
		vista.setDatosConexion(puerto,interfaz.getDisplayName()+" "+direccionInterfaz);
		new Thread(() -> {
			while(true){
				escuchar();
			}
		}).start();
	}
	
	/**
	 * 
	 */
	private void pedirConfiguracion() {
		puerto=vista.pedirPuerto();
		direccionInterfaz=vista.pedirInterfaz(getListaInterfaces());
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
						listaInterfaces.add(direccion.toString().replace("/", ""));
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return listaInterfaces;
	}
	
	/**
	 * 
	 */
	private void escuchar() {
		
		 
	    String msg="";
	    
	    //RECIBIR MENSAJES DE TEXTO DEL SERVIDOR
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
	 * 
	 */
	private void prepararSocket() {
			try {
				direccionMulticast = InetAddress.getByName(Config.IP_MULTICAST);
				mulSoc = new MulticastSocket(puerto); 
				

				grupo = new InetSocketAddress(direccionMulticast, puerto); 
				 
				
				// Interfaz con la IP del cliente
				interfaz =  NetworkInterface.getByInetAddress(InetAddress.getByName(direccionInterfaz));
			       
			    //UNION AL GRUPO  
				mulSoc.joinGroup(grupo, interfaz);
				
				
				
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
			 direccionInterfaz=(String) prop.get("direccion_interfaz");
		} catch (IOException|NumberFormatException e) {
			vista.msgError(Textos.ERROR_LEYENDO_CONF+":"+archConf.getAbsolutePath());
			pedirConfiguracion();
		}
	}

	/**
	 * 
	 */
	public void salir() {
		try {
			mulSoc.leaveGroup(grupo,interfaz);
		} catch (IOException e) {
		}
	    mulSoc.close (); 
		System.exit(0);	
	}

 


 
	

}
