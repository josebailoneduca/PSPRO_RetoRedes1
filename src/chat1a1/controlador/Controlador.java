/**
 * 
 */
package chat1a1.controlador;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import chat1a1.Config;
import chat1a1.gui.Textos;
import chat1a1.gui.Ventana;
import chat1a1.modelo.Conector;

/**
 * Controlador de la aplicacion.
 * 
 * @author Jose Javier Bailon
 */
public class Controlador {


	/**
	 * Referencia al conector (modelo) que se encargara de hacer la comunicacion por
	 * red
	 */
	private Conector conector;

	/**
	 * Referencia a la vista
	 */
	private Ventana vista;

	/**
	 * Ruta del archivo de configuracion
	 */
	private String rutaConf;

	/**
	 * Puerto de escucha
	 */
	private int puertoLocal = Config.PUERTO_LOCAL_DEFAULT;

	/**
	 * Direccion remota
	 */
	private String direccionRemota = Config.DIRECCION_REMOTA_DEFAULT;

	/**
	 * Puerto remoto
	 */
	private int puertoRemoto = 50000;

	/**
	 * Constructor. Genera los enlaces de vista->controlador y conector->controlador ejecutando
	 * metodos adecuados en cada uno de ellos.
	 * 
	 * @param conector Referencia al conector (modelo) que se encargara de hacer la
	 *                 comunicacion por red
	 * @param vista    Referencia a la vista
	 * @param rutaConf Ruta del archivo de configuracion
	 */
	public Controlador(Conector conector, Ventana vista, String rutaConf) {
		this.conector = conector;
		this.vista = vista;
		this.conector.setControlador(this);
		this.vista.setControlador(this);
		this.rutaConf = rutaConf;
	}

	/**
	 * Carga la configuracion o la pide y tras eso lanza la configuracion de conexion
	 */
	public void iniciar() {
		// obtener los datos de configuracion
		if (rutaConf != null)
			cargarConfiguracion();
		else
			pedirDatosConexion();

		//Lanzar configuracion de la conexion
		configurarConexion(puertoLocal, direccionRemota, puertoRemoto);
	}

	/**
	 * Configura los datos de conexión en el conector e inicia la negociacion.
	 * 
	 * @param puertoLocal Puerto local para la conexion
	 * @param direccion Dirección remota
	 * @param puertoRemoto Puerto remoto
	 */
	public void configurarConexion(int puertoLocal, String direccion, int puertoRemoto) {
		
		//comprueba que los datos son validos
		boolean datosCorrectos = this.conector.configurarConexion(puertoLocal, direccion, puertoRemoto);
		if (datosCorrectos) {
			actualizarDatosConexion();
			vista.desactivarEntrada();
			conector.negociar();
			//tras terminar la negociacion se inicia la escucha o se activa la entrada de datos
			//dependiendo de a quien le toque hablar
			if (!conector.isHablar()) {
				vista.desactivarEntrada();
				recibirMensaje();
			} else {
				vista.activarEntrada();
			}
		} else {
			//si no son válidos es que elpuerto esta ocupado
			vista.msgError(Textos.PUERTO_INVALIDO + ":" + puertoLocal);
			pedirDatosConexion();
		}
	}

	/**
	 * Cierra la conexion y sale del programa
	 */
	public void salir() {
		conector.cerrar();
		System.exit(0);

	}

	/**
	 * Envía un mensaje e inicia un hilo para recibir un mensaje
	 * 
	 * @param mensaje El mensaje a enviar
	 */
	public void enviar(String mensaje) {
		conector.enviar(mensaje);
		new Thread(() -> recibirMensaje()).start();
		vista.desactivarEntrada();
	}

	/**
	 * Manda a la interfaz grafica el mensaje recibido

	 * @param datos Datos recibidos
	 * @param origen Dirección origen del envio
	 * @param puerto Puerto origen del envio
	 */
	public void mensajeRecibido(String datos, String origen, int puerto) {
		EventQueue.invokeLater(() -> {
			vista.mensajeRecibido(datos, origen, puerto);
		});
	}

	/**
	 *  Muestra en la interfaz un estado
	 * @param estado estado actual
	 */
	public void mostrarEstado(int estado) {
		vista.setEstado(estado);
	}

	/**
	 * Pide los datos de conexion a traves de la vista
	 * 
	 * */
	private void pedirDatosConexion() {
	
		puertoLocal = vista.pedirPuertoLocal();
		direccionRemota = vista.pedirDireccionRemota(conector.getListaInterfaces());
		puertoRemoto = vista.pedirPuertoRemoto();
	}

	/**
	 * Carga los datos de conexion desde el archivo de configuracion. Si no puede
	 * lanza la peticion de los parámetros al usuario
	 */
	private void cargarConfiguracion() {
		File archConf = null;
		try {
			archConf = new File(rutaConf);
			FileReader r = new FileReader(archConf);
			Properties prop = new Properties();
			prop.load(r);
			puertoLocal = Integer.parseInt((String) prop.get("puerto_local"));
			direccionRemota = (String) prop.get("direccion_remota");
			puertoRemoto = Integer.parseInt((String) prop.get("puerto_remoto"));
		} catch (IOException | NumberFormatException e) {
			//si ha habido algún error se avisa al usuario y se piden los datos de conexion
			vista.msgError(Textos.ERROR_LEYENDO_CONF + ":" + archConf.getAbsolutePath());
			pedirDatosConexion();
		}
	}

	/**
	 * Actualiza la vista con los datos de conexion actuales
	 */
	private void actualizarDatosConexion() {
		int puertoLocal = conector.getPuertoLocal();
		String direccionRemota = conector.getDireccionRemota();
		int puertoRemoto = conector.getPuertoRemoto();
		vista.setDatosConexion(puertoLocal, direccionRemota, puertoRemoto);
	}

	/**
	 *  Inicia la recepción de un mensaje
	 */
	private void recibirMensaje() {
		//poner el conector a escuchar 
		String msg = conector.recibir();
		//mostrar mensaje recibido y activar interfaz para que se pueda escribir
		if (msg != null) {
			vista.mensajeRecibido(msg, conector.getDireccionRemota(), conector.getPuertoRemoto());
		}
		vista.activarEntrada();
	}

}
