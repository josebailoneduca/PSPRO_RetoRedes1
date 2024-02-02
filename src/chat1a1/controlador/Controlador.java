/**
 * 
 */
package chat1a1.controlador;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import chat1a1.gui.Textos;
import chat1a1.gui.Ventana;
import chat1a1.modelo.Conector;

/**
 * 
 * @author Jose Javier Bailon
 */
public class Controlador {
	private boolean puedeEnviar = true;
	private Conector conector;
	Ventana vista;
	String rutaConf;
	public Controlador(Conector conector, Ventana vista, String rutaConf) {
		this.conector = conector;
		this.vista = vista;
		this.conector.setControlador(this);
		this.vista.setControlador(this);
		this.rutaConf=rutaConf;
	}

	public void iniciar() {
		if (rutaConf!=null)
			cargarConfiguracion();
		else
			pedirDatosConexion(); 
	}

	/**
	 * @param listaInterfaces
	 */
	private void pedirDatosConexion() {
		
		int puertoLocal=vista.pedirPuertoLocal();
		String direccionRemota=vista.pedirDireccionRemota(conector.getListaInterfaces());
		int puertoRemoto=vista.pedirPuertoRemoto();
		configurarConexion(puertoLocal,direccionRemota, puertoRemoto);
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
			int puertoLocal=Integer.parseInt((String) prop.get("puerto_local"));
			String direccionRemota=(String) prop.get("direccion_remota");
			int puertoRemoto=Integer.parseInt((String) prop.get("puerto_remoto"));
			configurarConexion(puertoLocal,direccionRemota, puertoRemoto);
		} catch (IOException|NumberFormatException e) {
			vista.msgError(Textos.ERROR_LEYENDO_CONF+":"+archConf.getAbsolutePath());
			pedirDatosConexion();
		}
	}

	public void configurarConexion(int puertoLocal, String direccion, int puertoRemoto) {
		boolean datosCorrectos = this.conector.configurarConexion(puertoLocal, direccion, puertoRemoto);
		if (datosCorrectos) {
			actualizarDatosConexion();
			vista.desactivarEntrada();
			conector.negociar();
			if (!conector.isHablar()) {
				vista.desactivarEntrada();
				recibirMensaje();
			}else {
				vista.activarEntrada();
			}
		} else {
			vista.msgError(Textos.PUERTO_INVALIDO+":"+puertoLocal);
			pedirDatosConexion();
		}
	}

	
	/**
	 * 
	 */
	private void actualizarDatosConexion() {
		int puertoLocal=conector.getPuertoLocal();
		String direccionRemota=conector.getDireccionRemota();
		int puertoRemoto=conector.getPuertoRemoto();
		vista.setDatosConexion(puertoLocal,direccionRemota,puertoRemoto);
	}

	/**
	 * 
	 */
	private void recibirMensaje() {

		String msg = conector.recibir();
		if(msg!=null) {
			vista.mensajeRecibido(msg, conector.getDireccionRemota(), conector.getPuertoRemoto());
		}
		vista.activarEntrada();
	}

	
 
	/**
	 * 
	 */
	public void salir() {
		conector.cerrar();
		System.exit(0);

	}

	/**
	 * @param string
	 */
	public void enviar(String mensaje) {
		conector.enviar(mensaje);
		new Thread(() -> recibirMensaje()).start();
		vista.desactivarEntrada();
	}

	/**
	 * @param datos
	 */
	public void mensajeRecibido(String datos, String origen,  int puerto) {
		EventQueue.invokeLater(() -> {
			vista.mensajeRecibido(datos, origen, puerto);
		});
	}

	/**
	 * @param estDesconectado
	 */
	public void mostrarEstado(int estado) {
		vista.setEstado(estado);
	}

}
