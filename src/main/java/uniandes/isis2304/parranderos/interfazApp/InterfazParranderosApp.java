package uniandes.isis2304.parranderos.interfazApp;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.jdo.JDODataStoreException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import uniandes.isis2304.parranderos.negocio.AforoCC;
import uniandes.isis2304.parranderos.negocio.VOEspacio;
import uniandes.isis2304.parranderos.negocio.VOEstablecimiento;
import uniandes.isis2304.parranderos.negocio.VOLector;
import uniandes.isis2304.parranderos.negocio.VOVisita;
import uniandes.isis2304.parranderos.negocio.VOVisitante;



@SuppressWarnings("serial")
public class InterfazParranderosApp extends JFrame implements ActionListener{

	//CONSTANTES

	private static Logger log = Logger.getLogger(InterfazParranderosApp.class.getName());

	private static final String CONFIG_INTERFAZ = "./src/main/resources/config/interfaceConfigApp.json"; 

	private static final String CONFIG_TABLAS = "./src/main/resources/config/TablasBD_A.json"; 

	private static final int ADMINCC = 1;
	private static final int ADMIN = 2;
	private static final int VISITANTE = 3;

	//ATRIBUTOS

	private JsonObject tableConfig;

	private int quien = 3;

	private AforoCC parranderos;

	//ATRIBUTOS DE LA INTERFAZ

	private JsonObject guiConfig;

	private PanelDatos panelDatos;

	private JMenuBar menuBar;

	//METODOS

	public InterfazParranderosApp( )
	{
		guiConfig = openConfig ("Interfaz", CONFIG_INTERFAZ);

		configurarFrame ( );
		if (guiConfig != null) 	   
		{
			crearMenu( guiConfig.getAsJsonArray("menuBar") );
		}

		tableConfig = openConfig ("Tablas BD", CONFIG_TABLAS);
		parranderos = new AforoCC (tableConfig);

		String path = guiConfig.get("bannerPath").getAsString();
		panelDatos = new PanelDatos ( );

		setLayout (new BorderLayout());
		add (new JLabel (new ImageIcon (path)), BorderLayout.NORTH );          
		add( panelDatos, BorderLayout.CENTER );        
	}

	//METODOS DE LA INTERFAZ

	private JsonObject openConfig (String tipo, String archConfig)
	{
		JsonObject config = null;
		try 
		{
			Gson gson = new Gson( );
			FileReader file = new FileReader (archConfig);
			JsonReader reader = new JsonReader ( file );
			config = gson.fromJson(reader, JsonObject.class);
			log.info ("Se encontr� un archivo de configuraci�n v�lido: " + tipo);
		} 
		catch (Exception e)
		{
			e.printStackTrace ();
			log.info ("NO se encontr� un archivo de configuraci�n v�lido");			
			JOptionPane.showMessageDialog(null, "No se encontr� un archivo de configuraci�n de interfaz v�lido: " + tipo, "Parranderos App", JOptionPane.ERROR_MESSAGE);
		}	
		return config;
	}

	private void configurarFrame(  )
	{
		int alto = 0;
		int ancho = 0;
		String titulo = "";	

		if ( guiConfig == null )
		{
			log.info ( "Se aplica configuraci�n por defecto" );			
			titulo = "Parranderos APP Default";
			alto = 300;
			ancho = 500;
		}
		else
		{
			log.info ( "Se aplica configuraci�n indicada en el archivo de configuraci�n" );
			titulo = guiConfig.get("title").getAsString();
			alto= guiConfig.get("frameH").getAsInt();
			ancho = guiConfig.get("frameW").getAsInt();
		}

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setLocation (50,50);
		setResizable( true );
		setBackground( Color.WHITE );

		setTitle( titulo );
		setSize ( ancho, alto);        
	}

	private void crearMenu(  JsonArray jsonMenu )
	{    	
		menuBar = new JMenuBar();       
		for (JsonElement men : jsonMenu)
		{
			JsonObject jom = men.getAsJsonObject(); 

			String menuTitle = jom.get("menuTitle").getAsString();        	
			JsonArray opciones = jom.getAsJsonArray("options");

			JMenu menu = new JMenu( menuTitle);

			for (JsonElement op : opciones)
			{       	
				JsonObject jo = op.getAsJsonObject(); 
				String lb =   jo.get("label").getAsString();
				String event = jo.get("event").getAsString();

				JMenuItem mItem = new JMenuItem( lb );
				mItem.addActionListener( this );
				mItem.setActionCommand(event);

				menu.add(mItem);
			}       
			menuBar.add( menu );
		}        
		setJMenuBar ( menuBar );	
	}

	//CRUD DE ESPACIO

	public void adicionarEspacio ()
	{	
		if(quien == 1) {
			try
			{
				String nombreEspacio = JOptionPane.showInputDialog (this, "Nombre del espacio?", "Adicionar espacio", JOptionPane.QUESTION_MESSAGE);
				if (nombreEspacio != null)
				{
					String area = JOptionPane.showInputDialog (this, "Area?", "Adicionar area", JOptionPane.QUESTION_MESSAGE);
					if (area != null)
					{
						String tipo = JOptionPane.showInputDialog (this, "Tipo?", "Adicionar tipo", JOptionPane.QUESTION_MESSAGE);
						if (tipo != null)
						{
							String estado = JOptionPane.showInputDialog (this, "Estado?", "Adicionar estado", JOptionPane.QUESTION_MESSAGE);
							if (estado != null)
							{
								VOEspacio espacio = parranderos.adicionarEspacio(1, nombreEspacio, Double.parseDouble(area), tipo, estado);
								if (espacio == null)
								{
									throw new Exception ("No se pudo crear un espacio con nombre: " + nombreEspacio);
								}
								String resultado = "En adicionarEspacio\n\n";
								resultado += "Espacio adicionado exitosamente: " + espacio;
								resultado += "\n Operaci�n terminada";
								panelDatos.actualizarInterfaz(resultado);
							}
						}
					}
				}
				else
				{
					panelDatos.actualizarInterfaz("Operaci�n cancelada por el usuario");
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				String resultado = generarMensajeError(e);
				panelDatos.actualizarInterfaz(resultado);
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//CRUD DE ESTABLECIMIENTO

	public void adicionarEstablecimiento ()
	{
		if(quien == 1) {
			try
			{
				String idEspacio = JOptionPane.showInputDialog (this, "Id del espacio?", "Adicionar espacio", JOptionPane.QUESTION_MESSAGE);
				if (idEspacio != null)
				{
					String idHorario = JOptionPane.showInputDialog (this, "Id del horario?", "Adicionar horario", JOptionPane.QUESTION_MESSAGE);
					if (idHorario != null)
					{
						String nombre = JOptionPane.showInputDialog (this, "Nombre del establecimiento?", "Adicionar nombre", JOptionPane.QUESTION_MESSAGE);
						if (nombre != null)
						{
							String tipo = JOptionPane.showInputDialog (this, "Tipo del establecimiento?", "Adicionar tipo", JOptionPane.QUESTION_MESSAGE);
							if (tipo != null)
							{
								String aforoMax = JOptionPane.showInputDialog (this, "Aforo del establecimiento?", "Adicionar aforo", JOptionPane.QUESTION_MESSAGE);
								if (aforoMax != null)
								{
									VOEstablecimiento establecimiento = parranderos.adicionarEstablecimiento(Long.parseLong(idEspacio), Long.parseLong(idHorario), nombre, tipo, Integer.parseInt(aforoMax));
									if (establecimiento == null)
									{
										throw new Exception ("No se pudo crear un establecimiento con nombre: " + nombre);
									}
									String resultado = "En adicionarEstablecimiento\n\n";
									resultado += "Establecimiento adicionado exitosamente: " + establecimiento;
									resultado += "\n Operaci�n terminada";
									panelDatos.actualizarInterfaz(resultado);
								}

							}
						}
					}
				}
				else
				{
					panelDatos.actualizarInterfaz("Operaci�n cancelada por el usuario");
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				String resultado = generarMensajeError(e);
				panelDatos.actualizarInterfaz(resultado);
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void eliminarEstablecimiento( )
	{
		if(quien == 1) {
			try 
			{
				String establecimientoStr = JOptionPane.showInputDialog (this, "Establecimiento?", "Borrar establecimiento", JOptionPane.QUESTION_MESSAGE);
				if (establecimientoStr != null)
				{
					long establecimiento = Long.valueOf (establecimientoStr);
					long establecimientosEliminados = parranderos.eliminarEstablecimientoPorId (establecimiento);

					String resultado = "En eliminar Establecimiento\n\n";
					resultado += establecimientosEliminados + " Establecimientos eliminados\n";
					resultado += "\n Operaci�n terminada";
					panelDatos.actualizarInterfaz(resultado);
				}
				else
				{
					panelDatos.actualizarInterfaz("Operaci�n cancelada por el usuario");
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				String resultado = generarMensajeError(e);
				panelDatos.actualizarInterfaz(resultado);
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//CRUD DE VISITANTE

	public void adicionarVisitante ()
	{
		try
		{
			String nombre = JOptionPane.showInputDialog (this, "Nombre del visitante?", "Adicionar nombre", JOptionPane.QUESTION_MESSAGE);
			if (nombre != null)
			{
				String tipo = JOptionPane.showInputDialog (this, "Tipo del visitante?", "Adicionar tipo", JOptionPane.QUESTION_MESSAGE);
				if (tipo != null)
				{
					String numTelefono = JOptionPane.showInputDialog (this, "Numero de telefono del visitante?", "Adicionar numero de telefono", JOptionPane.QUESTION_MESSAGE);
					if (numTelefono != null)
					{
						String correo = JOptionPane.showInputDialog (this, "Correo del visitante?", "Adicionar correo", JOptionPane.QUESTION_MESSAGE);
						if (correo != null)
						{
							String nomContacto = JOptionPane.showInputDialog (this, "Nombre del contacto del visitante?", "Adicionar nombre de contacto", JOptionPane.QUESTION_MESSAGE);
							if (nomContacto != null)
							{
								String numContacto = JOptionPane.showInputDialog (this, "Numero del contacto del visitante?", "Adicionar numero de contacto", JOptionPane.QUESTION_MESSAGE);
								if (numContacto != null)
								{
									String estado = JOptionPane.showInputDialog (this, "Estado del visitante?", "Adicionar estado", JOptionPane.QUESTION_MESSAGE);
									if (estado != null)
									{
										String temperatura = JOptionPane.showInputDialog (this, "Temperatura del visitante?", "Adicionar temperatura", JOptionPane.QUESTION_MESSAGE);
										if (temperatura != null) 
										{
											VOVisitante visitante = parranderos.adicionarVisitantes(nombre, tipo, Integer.parseInt(numTelefono), correo, nomContacto, Integer.parseInt(numContacto), estado, Double.parseDouble(temperatura));
											if (visitante == null)
											{
												throw new Exception ("No se pudo crear un visitante con nombre: " + nombre);
											}
											String resultado = "En adicionarVisitante\n\n";
											resultado += "Visitante adicionado exitosamente: " + visitante;
											resultado += "\n Operaci�n terminada";
											panelDatos.actualizarInterfaz(resultado);
										}
									}
								}
							}
						}
					}
				}
			}
			else
			{
				panelDatos.actualizarInterfaz("Operaci�n cancelada por el usuario");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
	}

	//CRUD DE LECTOR

	public void adicionarLector ()
	{
		try
		{
			String espacio = JOptionPane.showInputDialog (this, "Id del espacio?", "Adicionar espacio", JOptionPane.QUESTION_MESSAGE);
			if (espacio != null)
			{
				VOLector lector = parranderos.adicionarLector(espacio);
				if (lector == null)
				{
					throw new Exception ("No se pudo crear un lector para el espacio: " + espacio);
				}
				String resultado = "En adicionarLector\n\n";
				resultado += "Lector adicionado exitosamente: " + lector;
				resultado += "\n Operaci�n terminada";
				panelDatos.actualizarInterfaz(resultado);
			}
			else
			{
				panelDatos.actualizarInterfaz("Operaci�n cancelada por el usuario");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
	}

	//CRUD DE VISITA

	public void adicionarVisita ()
	{
		String d = null;
		String da = null;
		try
		{
			String id_visitante = JOptionPane.showInputDialog (this, "Id del visitante?", "Adicionar visitante", JOptionPane.QUESTION_MESSAGE);
			if (id_visitante != null)
			{
				String id_lector = JOptionPane.showInputDialog (this, "Id del lector?", "Adicionar lector", JOptionPane.QUESTION_MESSAGE);
				if (id_lector != null) 
				{
					String FechaHoraEntrada = JOptionPane.showInputDialog (this, "Hora entrada?", "Adicionar hora entrada", JOptionPane.QUESTION_MESSAGE);
					if (FechaHoraEntrada != null) 
					{
						String FechaHoraSalida = JOptionPane.showInputDialog (this, "Hora salida?", "Adicionar hora salida", JOptionPane.QUESTION_MESSAGE);
						if (FechaHoraSalida != null) 
						{
							//VOVisita visita = parranderos.adicionarVisita(Long.parseLong(id_visitante), Long.parseLong(id_lector), String.parse(FechaHoraEntrada), String.parse(FechaHoraSalida));
							VOVisita visita = parranderos.adicionarVisita(Long.parseLong(id_visitante), Long.parseLong(id_lector), d, da);

							if (visita == null)
							{
								throw new Exception ("No se pudo crear una visita: " + visita);
							}
							String resultado = "En adicionarVisita\n\n";
							resultado += "Visita adicionada exitosamente: " + visita;
							resultado += "\n Operaci�n terminada";
							panelDatos.actualizarInterfaz(resultado);
						}
					}
				}
			}
			else
			{
				panelDatos.actualizarInterfaz("Operaci�n cancelada por el usuario");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
	}

	//METODOS DE CONSULTA

	public void darAdoro()
	{
		parranderos.darAforo();
	}

	public void cambiarEstadoVisitante()
	{
		String id_visitante = JOptionPane.showInputDialog (this, "Id del visitante?", "Adicionar visitante", JOptionPane.QUESTION_MESSAGE);
		if (id_visitante != null)
		{
			String estado = JOptionPane.showInputDialog (this, "Estado del visitante?", "Adicionar estado", JOptionPane.QUESTION_MESSAGE);
			if (estado != null)
			{
				parranderos.cambiarEstadoVisitante(Long.parseLong(id_visitante), estado);
			}
		}

	}

	public void darVisitasRealizadas ()
	{
		if(quien == 1) {
			String id_visitante = JOptionPane.showInputDialog (this, "Id del visitante?", "Mostrar visitas realizadas", JOptionPane.QUESTION_MESSAGE);
			if (id_visitante != null)
			{
				parranderos.darVisitasRealizadas(Long.parseLong(id_visitante));
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	public void login ()
	{
		String admin = JOptionPane.showInputDialog (this, "Dar contrasenia", "Login", JOptionPane.QUESTION_MESSAGE);
		if (admin == "hola")
		{
			quien = ADMINCC;
		}
		else if(admin == "adios")
		{
			quien = ADMIN;
		}
	}

	public void deshabilitarEspacio ()
	{
		if(quien == 1) {
			String espacio = JOptionPane.showInputDialog (this, "Id del espacio?", "deshabilitar espacio", JOptionPane.QUESTION_MESSAGE);
			if (espacio != null)
			{
				parranderos.cambiarEstadoNaranja(Long.parseLong(espacio));
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void cambiarEstadoEspacio ()
	{
		if(quien == 1) {
			String espacio = JOptionPane.showInputDialog (this, "Id del espacio?", "Adicionar espacio", JOptionPane.QUESTION_MESSAGE);
			if (espacio != null)
			{
				String estado = JOptionPane.showInputDialog (this, "Estado del espacio?", "Adicionar estado", JOptionPane.QUESTION_MESSAGE);
				if (estado != null)
				{
					parranderos.cambiarEstadoEspacio(Long.parseLong(espacio), estado);
				}
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void rehabilitarEspacio ()
	{
		if(quien == 1) {
			String espacio = JOptionPane.showInputDialog (this, "Id del espacio?", "Adicionar espacio", JOptionPane.QUESTION_MESSAGE);
			if (espacio != null)
			{
				parranderos.rehabilitarEspacio(Long.parseLong(espacio));
			}
		}
		else 
		{
			String estado = JOptionPane.showInputDialog (this, "No tiene permiso", "usuario invalido", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void establecimientoConAforoDisponible ()
	{
		parranderos.darEstablecimientosConAforoDisponible();
	}

	//METODOS ADMINISTRATIVOS

	public void mostrarLogParranderos ()
	{
		mostrarArchivo ("parranderos.log");
	}

	public void mostrarLogDatanuecleus ()
	{
		mostrarArchivo ("datanucleus.log");
	}

	public void limpiarLogParranderos ()
	{
		boolean resp = limpiarArchivo ("parranderos.log");

		String resultado = "\n\n************ Limpiando el log de parranderos ************ \n";
		resultado += "Archivo " + (resp ? "limpiado exitosamente" : "NO PUDO ser limpiado !!");
		resultado += "\nLimpieza terminada";

		panelDatos.actualizarInterfaz(resultado);
	}

	public void limpiarLogDatanucleus ()
	{
		boolean resp = limpiarArchivo ("datanucleus.log");

		String resultado = "\n\n************ Limpiando el log de datanucleus ************ \n";
		resultado += "Archivo " + (resp ? "limpiado exitosamente" : "NO PUDO ser limpiado !!");
		resultado += "\nLimpieza terminada";

		panelDatos.actualizarInterfaz(resultado);
	}

	public void limpiarBD ()
	{
		try 
		{
			long eliminados [] = parranderos.limpiarParranderos();

			String resultado = "\n\n************ Limpiando la base de datos ************ \n";
			resultado += eliminados [0] + " CC eliminados\n";
			resultado += eliminados [1] + " Carnet eliminados\n";
			resultado += eliminados [2] + " Horario eliminados\n";
			resultado += eliminados [3] + " Espacio eliminadas\n";
			resultado += eliminados [4] + " Establecimiento eliminados\n";
			resultado += eliminados [5] + " Visitante eliminados\n";
			resultado += eliminados [6] + " Visita eliminados\n";
			resultado += eliminados [7] + " Lector eliminados\n";
			resultado += eliminados [8] + " LectorCC eliminados\n";
			resultado += eliminados [9] + " LectorEspacio eliminados\n";

			resultado += "\nLimpieza terminada";

			panelDatos.actualizarInterfaz(resultado);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
	}

	public void mostrarPresentacionGeneral ()
	{
		mostrarArchivo ("data/00-ST-ParranderosJDO.pdf");
	}

	public void mostrarModeloConceptual ()
	{
		mostrarArchivo ("data/Modelo Conceptual Parranderos.pdf");
	}

	public void mostrarEsquemaBD ()
	{
		mostrarArchivo ("data/Esquema BD Parranderos.pdf");
	}

	public void mostrarScriptBD ()
	{
		mostrarArchivo ("data/EsquemaParranderos.sql");
	}

	public void mostrarArqRef ()
	{
		mostrarArchivo ("data/ArquitecturaReferencia.pdf");
	}

	public void mostrarJavadoc ()
	{
		mostrarArchivo ("doc/index.html");
	}

	public void acercaDe ()
	{
		String resultado = "\n\n ************************************\n\n";
		resultado += " * Universidad	de	los	Andes	(Bogot�	- Colombia)\n";
		resultado += " * Departamento	de	Ingenier�a	de	Sistemas	y	Computaci�n\n";
		resultado += " * Licenciado	bajo	el	esquema	Academic Free License versi�n 2.1\n";
		resultado += " * \n";		
		resultado += " * Curso: isis2304 - Sistemas Transaccionales\n";
		resultado += " * Proyecto: Parranderos Uniandes\n";
		resultado += " * @version 1.0\n";
		resultado += " * @author Germ�n Bravo\n";
		resultado += " * Julio de 2018\n";
		resultado += " * \n";
		resultado += " * Revisado por: Claudia Jim�nez, Christian Ariza\n";
		resultado += "\n ************************************\n\n";

		panelDatos.actualizarInterfaz(resultado);		
	}

	//METODOS PRIVADOS PARA LA PRESENTACION DE RESULTADOS Y OTRAS OPERACIONES

	private String darDetalleException(Exception e) 
	{
		String resp = "";
		if (e.getClass().getName().equals("javax.jdo.JDODataStoreException"))
		{
			JDODataStoreException je = (javax.jdo.JDODataStoreException) e;
			return je.getNestedExceptions() [0].getMessage();
		}
		return resp;
	}

	private String generarMensajeError(Exception e) 
	{
		String resultado = "************ Error en la ejecuci�n\n";
		resultado += e.getLocalizedMessage() + ", " + darDetalleException(e);
		resultado += "\n\nRevise datanucleus.log y parranderos.log para m�s detalles";
		return resultado;
	}

	private boolean limpiarArchivo(String nombreArchivo) 
	{
		BufferedWriter bw;
		try 
		{
			bw = new BufferedWriter(new FileWriter(new File (nombreArchivo)));
			bw.write ("");
			bw.close ();
			return true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}

	private void mostrarArchivo (String nombreArchivo)
	{
		try
		{
			Desktop.getDesktop().open(new File(nombreArchivo));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//METODOS DE INTERACCION

	@Override
	public void actionPerformed(ActionEvent pEvento)
	{
		String evento = pEvento.getActionCommand( );		
		try 
		{
			Method req = InterfazParranderosApp.class.getMethod ( evento );			
			req.invoke ( this );
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}

	//PROGRAMA PRINCIPAL

	public static void main( String[] args )
	{
		try
		{

			UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName( ) );
			InterfazParranderosApp interfaz = new InterfazParranderosApp( );
			interfaz.setVisible( true );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
		}
	}

}
