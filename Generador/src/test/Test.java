package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.github.javafaker.Faker;

public class Test {

	public static void main(String[] args) {
		Faker faker = new Faker();
		try {  
			File myObj = new File("datos.txt");  
			if (myObj.createNewFile()) 
			{  
				System.out.println("File created: " + myObj.getName());  
			} else 
			{  
				System.out.println("File already exists.");  
			}  

			///////////////////HORARIO///////////////////
			FileWriter myWriter = new FileWriter("datos.txt");
			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_HORARIO");
			myWriter.write("\n");
			myWriter.write("ID POSITION(1:2) INTEGER EXTERNAL, \n");
			myWriter.write("HORAENTRADA POSITION(3:5) INTEGER EXTERNAL, \n");
			myWriter.write("HORASALIDA POSITION(6:8) INTEGER EXTERNAL) \n");

			myWriter.write(1+" "+10+" "+22+"\n");

			for (int i = 2; i < 32; i++) 
			{
				int ini = faker.number().numberBetween(6, 12);
				int fin = faker.number().numberBetween(10, 24);

				myWriter.write(i+" "+ini+" "+fin+"\n");
			}

			myWriter.write("; \n");

			////////////// CENTRO COMERCIAL/////////

			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_CENTROCOMERCIAL");
			myWriter.write("\n");
			myWriter.write("ID POSITION(1:2) INTEGER EXTERNAL, \n");
			myWriter.write("NOMBRE POSITION(3:11) CHAR, \n");
			myWriter.write("AFOROMAX POSITION(12:17) INTEGER EXTERNAL, \n");
			myWriter.write("IDHORARIO POSITION(18:19) INTEGER EXTERNAL, \n");
			myWriter.write("ESTADO POSITION(20:25) CHAR) \n");
			myWriter.write(1+" "+"Uniandes "+ 10000+" "+ 1+" verde \n");
			myWriter.write("; \n");

			/////////////// LECTOR //////////
			myWriter.write("LOAD DATA APPEND \n");
			myWriter.write("INTO TABLE A_CENTROCOMERCIAL \n");
			myWriter.write("ID POSITION(1:5) INTEGER EXTERNAL, \n");
			myWriter.write("UBICACION POSITION(6:20) CHAR) \n");
			for(int i=1; i<1008;i++) 
			{
				String nombre = faker.code().isbn10();
				myWriter.write(i+"    "+nombre+"\n");
			}
			myWriter.write("; \n");

			////////////// VISITANTE////////////

			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_VISITANTE");
			myWriter.write("\n");
			myWriter.write("ID POSITION(1:7) INTEGER EXTERNAL, \n");
			myWriter.write("NOMBRE POSITION(8:31) CHAR, \n");
			myWriter.write("TIPO POSITION(32:44) CHAR, \n");
			myWriter.write("TELEFONO POSITION(45:60) INTEGER EXTERNAL, \n");
			myWriter.write("CORREO POSITION(61:95) CHAR, \n");
			myWriter.write("NOMCONTACTO POSITION(96:115) CHAR, \n");
			myWriter.write("NUMCONTACTO POSITION(116:130) CHAR, \n");
			myWriter.write("ESTADO POSITION(131:144) CHAR, \n");
			myWriter.write("TEMPERATURA POSITION(144:151) INTEGER EXTERNAL) \n");

			for (int i = 1; i < 100001; i++) 
			{
				String espacio1="       ";
				String name = faker.name().firstName()+" "+faker.name().lastName(); 
				String correo = faker.internet().emailAddress();
				String numTelefono = faker.number().digits(10);
				String name2 = faker.name().fullName(); 
				String numTelefono2 = faker.phoneNumber().cellPhone();
				Double temperatura = faker.number().randomDouble(1, 35, 40);
				Double alAzar = faker.number().randomDouble(0, 1, 4);
				String tipo = "";
				if(alAzar==1) {tipo="Empleado";}
				if(alAzar > 2) {tipo="Cliente";}
				if(alAzar==2) {tipo="Domiciliario";}
				String estado = "";
				if(alAzar==1) {estado="Positivo";}
				if(alAzar==2) {estado="Rojo";}
				if(alAzar==3) {estado="Naranja";}
				if(alAzar==4) {estado="Verde";}

				myWriter.write(i+espacio1+name+espacio1+tipo+espacio1+numTelefono+espacio1+correo+espacio1+name2+espacio1+numTelefono2+espacio1+estado+espacio1+temperatura+"\n");
			}
			myWriter.write("; \n");


			////////////// CARNET//////////////////
			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_CARNET");
			myWriter.write("\n");
			myWriter.write("ID POSITION(1:6) INTEGER EXTERNAL, \n");
			myWriter.write("IDVISITANTE POSITION(7:13) INTEGER EXTERNAL) \n");
			for(int i = 1; i<75001;i++) 
			{
				char[] test = Integer.toString(i).toCharArray();
				String espacio = "";
				for (int j = 0; j < 7-test.length; j++) 
				{
					espacio+=" ";
				}
				myWriter.write(Integer.toString(i)+espacio+Integer.toString(i)+"\n");

			}
			myWriter.write("; \n");

			/////////////////// ESPACIO ////////////////
			String esp = "          ";
			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_ESPACIO");
			myWriter.write("\n");
			myWriter.write("ID POSITION(1:7) INTEGER EXTERNAL, \n");
			myWriter.write("IDCC POSITION(8:31) INTEGER EXTERNAL, \n");
			myWriter.write("NOMBRE POSITION(32:44) CHAR, \n");
			myWriter.write("AREA POSITION(45:60) INTEGER EXTERNAL, \n");
			myWriter.write("TIPO POSITION(61:95) CHAR, \n");
			myWriter.write("ESTADO POSITION(131:144) CHAR) \n");

			for(int i= 1;i<1001;i++) 
			{
				String nom = faker.company().name();
				Double area = faker.number().randomDouble(1, 20, 100);
				Double alAzar = faker.number().randomDouble(0, 1, 5);
				String t = "";
				if(alAzar==1) {t="Desocupado";}
				if(alAzar==2) {t="Verde";}
				if(alAzar==3) {t="Deshabilitado";}
				if(alAzar==4) {t="Rojo";}
				if(alAzar==5) {t="Naranja";}	

				myWriter.write(i+esp+1+esp+nom+esp+area+esp+"null"+esp+t+"\n");

			}
			myWriter.write("; \n");

			////////////////// LOCAL////////////////////////


			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_ESTABLECIMIENTO");
			myWriter.write("\n");
			myWriter.write("ID POSITION(1:7) INTEGER EXTERNAL, \n");
			myWriter.write("IDESPACIO POSITION(8:31) INTEGER EXTERNAL, \n");
			myWriter.write("IDHORARIO POSITION(8:31) INTEGER EXTERNAL, \n");
			myWriter.write("NOMBRE POSITION(32:44) CHAR, \n");
			myWriter.write("TIPO POSITION(32:44) CHAR, \n");
			myWriter.write("AFOROMAX POSITION(8:31) INTEGER EXTERNAL) \n");

			for(int i= 1;i<1001;i++) 
			{
				String nom = faker.company().name();
				Double area = faker.number().randomDouble(1, 20, 100);
				Double alAzar = faker.number().randomDouble(0, 1, 5);
				String t = "";
				int aforo =0;
				if(alAzar==1) {t="Locales"; aforo= (int) (area/6);}
				if(alAzar==2) {t="Ascensor"; aforo = 2;}
				if(alAzar==3) {t="Bano"; aforo = 4;}
				if(alAzar==4) {t="Parqueadero"; aforo = 100;}
				if(alAzar==5) {t="Zona Circulacion"; aforo = 0;}
				int iHorario = faker.number().numberBetween(1, 30);

				myWriter.write(i+esp+i+esp+iHorario+esp+nom+esp+t+esp+aforo);
			}
			myWriter.write("; \n");

			////////////////////// LECTORCC ////////////////////////

			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_ESTABLECIMIENTO");
			myWriter.write("\n");
			myWriter.write("IDLECTOR POSITION(1:7) INTEGER EXTERNAL, \n");
			myWriter.write("IDCC POSITION(8:31) INTEGER EXTERNAL, \n");
			for(int i=1;i<7;i++) 
			{
				myWriter.write(i+esp+1+"\n");
			}
			myWriter.write("; \n");

			//////////////////////LECTORLOCAL ////////////////////////

			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_LECTORESPACIO");
			myWriter.write("\n");
			myWriter.write("IDLECTOR POSITION(1:7) INTEGER EXTERNAL, \n");
			myWriter.write("IDLOCAL POSITION(8:31) INTEGER EXTERNAL, \n");
			for(int i=1;i<1001;i++) 
			{
				int s = i+6;
				myWriter.write(s+esp+i+"\n");
			}
			myWriter.write("; \n");

			//////////////////////VISITA ////////////////////////

			myWriter.write("LOAD DATA APPEND");
			myWriter.write("\n");
			myWriter.write("INTO TABLE A_VISITA");
			myWriter.write("\n");
			myWriter.write("IDVISITANTE POSITION(1:7) INTEGER EXTERNAL, \n");
			myWriter.write("IDLECTOR POSITION(8:31) INTEGER EXTERNAL, \n");
			myWriter.write("FECHAENTRADA POSITION(32:44) CHAR, \n");
			myWriter.write("FECHASALIDA POSITION(45:60) CHAR)\n");

			for(int i = 0; i<830955;i++) 
			{
				int id_lector = faker.number().numberBetween(1, 1007);
				int id_visitante = faker.number().numberBetween(1,100000);

				Date dateI = new Date();
				int mes = (int)faker.number().numberBetween(1, 12);
				int dia = (int)faker.number().numberBetween(1, 28);
				int random3= (int)faker.number().randomDouble(0, 6, 12);
				int horas = (int)faker.number().randomDouble(0, 8, 12);

				dateI.setMonth(mes);
				dateI.setDate(dia);
				dateI.setHours(random3);

				Date dateF = dateI;

				if(dateI.getHours()+horas<24) 
				{
					dateF.setHours(dateI.getHours()+horas);
				}
				else 
				{
					dateF.setHours(24);
				}

				myWriter.write(id_visitante+esp+id_lector+esp+dateI+esp+dateF+"\n");
			}
			
			myWriter.write(";");
			System.out.println("Termino");
			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();  
		}  


	}

}
