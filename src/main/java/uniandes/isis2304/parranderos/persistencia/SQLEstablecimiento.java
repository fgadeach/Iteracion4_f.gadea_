package uniandes.isis2304.parranderos.persistencia;

import java.util.List;


import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import uniandes.isis2304.parranderos.negocio.Establecimiento;


public class SQLEstablecimiento {

	private final static String SQL = PersistenciaAforoCC.SQL;

	private PersistenciaAforoCC pp;

	public SQLEstablecimiento (PersistenciaAforoCC pp)
	{
		this.pp = pp;
	}

	public long adicionarEstablecimiento (PersistenceManager pm, long i, long iEspacio, long iHorario, String nom, String type, int aforo) 
	{
		Query q = pm.newQuery(SQL, "INSERT INTO " + pp.darSeqEstablecimiento() + "(id, idEspacio, idHorario, nombre, tipo, aforo) values (?, ?, ?, ?, ?, ?)");
		q.setParameters(i, iEspacio, iHorario, nom, type, aforo);
		return (long)q.executeUnique();            
	}

	public long eliminarEstablecimiento (PersistenceManager pm, long id) 
	{
		Query q = pm.newQuery(SQL, "DELETE FROM " + pp.darSeqEstablecimiento () + " WHERE id = ?");
		q.setParameters(id);
		return (long) q.executeUnique();            
	}

	public List<Establecimiento> darEstablecimientos (PersistenceManager pm)
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + pp.darSeqEstablecimiento());
		q.setResultClass(Establecimiento.class);
		return (List<Establecimiento>) q.execute();
	}

	public Establecimiento darEstablecimientoPorId (PersistenceManager pm, long id) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + pp.darSeqEstablecimiento() + " WHERE id = ?");
		q.setResultClass(Establecimiento.class);
		q.setParameters(id);
		return (Establecimiento) q.executeUnique();
	}

	public List<Establecimiento> establecimientoConAforoDisponible (PersistenceManager pm)
	{		
		String sql1 = "SELECT est.nombre, lece.idEspacio, lece.id, vis.id_lector";
		sql1 += " FROM ";
		sql1+= pp.darSeqEstablecimiento() + " est, ";
		sql1 += pp.darLectoresEspacio() + " lece, ";
		sql1 += pp.darVisitas() + " vis ";
		sql1+=" WHERE ";
		sql1 += "AND est.id = lece.idEspacio";
		sql1 += "AND lece.id = vis.id_lector";
		sql1 += "AND vis.id_lector = lec.id";
		sql1 += "AND est.aforo > COUNT(vis.id_visitante)";

		Query q = pm.newQuery(SQL, sql1);
		return (List<Establecimiento>) q.executeList();
	}

	public List<Establecimiento> rf12(PersistenceManager pm){
		String sql1="";
		sql1+="CREATE TABLE LOCALES_VISITADOS(\n" + 
				"NOMBRE VARCHAR2(255 BYTE) NOT NULL,\n" + 
				"TIPO VARCHAR2(255 BYTE) NOT NULL,\n" + 
				"CONTADOR NUMBER NOT NULL,\n" + 
				"FECHA VARCHAR2(255 BYTE) NOT NULL\n" + 
				");\n" + 
				"DECLARE\n" + 
				"V_ITERATOR NUMBER;\n" + 
				"V_DATE VARCHAR2(255 BYTE);\n" + 
				"BEGIN\n" + 
				"V_ITERATOR:= 0;\n" + 
				"V_DATE:= TO_DATE('01/01/2020', 'DD/MM/YYYY');\n" + 
				"LOOP\n" + 
				"V_ITERATOR:= V_ITERATOR+7;\n" + 
				"INSERT INTO LOCALES_VISITADOS\n" + 
				"SELECT ESTABLECIMIENTO.nombre, ESTABLECIMIENTO.tipo_establecimiento as Tipo,contadorVisitas,V_DATE\n" + 
				"FROM(SELECT IDESPACIO as IDESPACIO, COUNT( ID_VISITANTE)as contadorVisitas\n" + 
				"    FROM" + pp.darSeqVisita()+
				"    WHERE VISITA.FECHAENTRADA BETWEEN V_DATE AND V_DATE+V_ITERATOR\n" + 
				"    GROUP BY IDESPACIO\n" + 
				"    HAVING COUNT( ID_VISITANTE)>0)\n" + 
				"    INNER JOIN LOCAL_COMERCIAL ON IDESPACIO=LOCAL_COMERCIAL.idespacio\n" + 
				"    WHERE ROWNUM<=1\n" + 
				"    ORDER BY contadorVisitas DESC;\n" + 
				"V_DATE:= V_DATE+V_ITERATOR+1;\n" + 
				"    EXIT WHEN V_ITERATOR=364;\n" + 
				"END LOOP;\n" + 
				"END;\n" + 
				"SELECT * FROM LOCALES_VISITADOS;\n" + 
				"CREATE TABLE LOCALES_MENOS_VISITADOS(\n" + 
				"NOMBRE VARCHAR2(255 BYTE) NOT NULL,\n" + 
				"TIPO VARCHAR2(255 BYTE) NOT NULL,\n" + 
				"CONTADOR NUMBER NOT NULL,\n" + 
				"FECHA VARCHAR2(255 BYTE) NOT NULL\n" + 
				");\n" + 
				"DECLARE\n" + 
				"V_ITERATOR NUMBER;\n" + 
				"V_DATE VARCHAR2(255 BYTE);\n" + 
				"BEGIN\n" + 
				"V_ITERATOR:= 0;\n" + 
				"V_DATE:= TO_DATE('01/01/2020', 'DD/MM/YYYY');\n" + 
				"LOOP\n" + 
				"V_ITERATOR:= V_ITERATOR+7;\n" + 
				"INSERT INTO LOCALES_MENOS_VISITADOS\n" + 
				"SELECT ESTABLECIMIENTO.nombre, ESTABLECIMIENTO.tipo as Tipo,contadorVisitas,V_DATE\n" + 
				"FROM(SELECT IDESPACIO as IDESPACIO, COUNT( ID_VISITANTE)as contadorVisitas\n" + 
				"    FROM" + pp.darSeqVisita()+
				"    WHERE VISITA.FECHAENTRADA BETWEEN V_DATE AND V_DATE+V_ITERATOR\n" + 
				"    GROUP BY IDESPACIO\n" + 
				"    HAVING COUNT( ID_VISITANTE)>0)\n" + 
				"    INNER JOIN LOCAL_COMERCIAL ON IDESPACIO=LOCAL_COMERCIAL.idespacio\n" + 
				"    ORDER BY contadorVisitas ASC, V_DATE ASC;\n" + 
				"V_DATE:= V_DATE+V_ITERATOR+1;\n" + 
				"    EXIT WHEN V_ITERATOR=364;\n" + 
				"END LOOP;\n" + 
				"END;\n" + 
				"CREATE TABLE LOCALES_MAS_VISITADOS(\n" + 
				"NOMBRE VARCHAR2(255 BYTE) NOT NULL,\n" + 
				"TIPO VARCHAR2(255 BYTE) NOT NULL,\n" + 
				"CONTADOR NUMBER NOT NULL,\n" + 
				"FECHA DATE NOT NULL\n" + 
				");\n" + 
				"DECLARE\n" + 
				"V_ITERATOR NUMBER;\n" + 
				"V_DATE VARCHAR2(255 BYTE);\n" + 
				"BEGIN\n" + 
				"V_ITERATOR:= 0;\n" + 
				"V_DATE:= TO_DATE('01/01/2020', 'DD/MM/YYYY');\n" + 
				"LOOP\n" + 
				"V_ITERATOR:= V_ITERATOR+7;\n" + 
				"INSERT INTO LOCALES_MAS_VISITADOS\n" + 
				"SELECT * FROM LOCALES_MENOS_VISITADOS WHERE CONTADOR =(SELECT MIN(contador) FROM LOCALES_MENOS_VISITADOS WHERE FECHA BETWEEN V_DATE AND V_DATE+V_ITERATOR);\n" + 
				"V_DATE:= V_DATE+V_ITERATOR+1;\n" + 
				"    EXIT WHEN V_ITERATOR=364;\n" + 
				"END LOOP;\n" + 
				"END;\n" + 
				"SELECT DISTINCT * FROM LOCALES_MAS_VISITADOS;";	
		Query q = pm.newQuery(SQL,sql1);
		return (List<Establecimiento>) q.executeList();
	}
}
