/**
 * 
 */
package chat1a1;

import chat1a1.controlador.Controlador;
import chat1a1.gui.Ventana;
import chat1a1.modelo.Conector;

/**
 * Punto de entrada al programa Reto Red 1 CHAT 1 A 1
 * 
 * Se encarga de crear la vista, el modelo y el controlador.
 * 
 * Pasa la vista y el modelo como parametros al controlador.
 * 
 * 
 * Puede recibir una ruta que especifica el archivo de configuracion. 
 * En caso de no suministrarse se piden los datos al usuario: Puerto local, puerto remoto, direccion remota.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Main {
	
	/**
	 * Main
	 * @param args Como primer parametro del array se puede pasar la ruta de un archivo de configuracion. Opcional. 
	 */
	public static void main(String[] args) {
		String archivoConf=null;
		if (args!=null &&args.length>0)
			archivoConf=args[0];
		
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException|InstantiationException|IllegalAccessException |javax.swing.UnsupportedLookAndFeelException ex) {
        	System.out.println("Problema configurando look and feel");
        	System.exit(0);
        }
		
		//crear ventana
		Ventana v = new Ventana();
		v.setLocationRelativeTo(null);
        java.awt.EventQueue.invokeLater(()->v.setVisible(true));
		
		
		
		//Crear modelo
		Conector m = new Conector();
		
		//crear controlador
		Controlador c=new Controlador(m,v,archivoConf);
		c.iniciar();
	}
	
}
