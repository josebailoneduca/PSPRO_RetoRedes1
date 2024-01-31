/**
 * 
 */
package multicast;

import multicast.servidor.Servidor;
import multicast.servidor.Ventana;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MainServidor {
	
	
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
	Servidor servidor= new Servidor(v, archivoConf);
	servidor.iniciar();
	
	}
}
