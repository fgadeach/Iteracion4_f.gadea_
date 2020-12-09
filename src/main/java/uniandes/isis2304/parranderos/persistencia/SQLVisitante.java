package uniandes.isis2304.parranderos.persistencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;

import java.util.List;
import java.util.Scanner;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import uniandes.isis2304.parranderos.negocio.Visitante;


public class SQLVisitante {

	private final static String SQL = PersistenciaAforoCC.SQL;

	private PersistenciaAforoCC pp;

	public SQLVisitante(PersistenciaAforoCC pp)
	{
		this.pp = pp;
	}

	public long adicionarVisitante (PersistenceManager pm, long id, String nombre,String tipo,int numTelefono,String correo,String nomContacto,int numContacto,String estado, Double temperatura) 
	{
		Query q = pm.newQuery(SQL, "INSERT INTO " + pp.darSeqVisitante() + "(id, nombre, tipo, numTelefono, correo, nomContacto, numContacto, estado, temperatura) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		q.setParameters(id, nombre, tipo, numTelefono,correo,nomContacto,numContacto,estado, temperatura);
		return (long)q.executeUnique();            
	}

	public List<Visitante> darVisitantes (PersistenceManager pm)
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + pp.darSeqVisitante ());
		q.setResultClass(Visitante.class);
		return (List<Visitante>) q.execute();
	}

	public long eliminarVisitante(PersistenceManager pm, long id) 
	{
		Query q = pm.newQuery(SQL, "DELETE FROM " + pp.darSeqVisitante() + " WHERE id = ?");
		q.setParameters(id);
		return (long) q.executeUnique();            
	}

	public Visitante darVisitantePorIdVisitantente (PersistenceManager pm, long id) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + pp.darSeqVisitante() + " WHERE id = ?");
		q.setResultClass(Visitante.class);
		q.setParameters(id);
		return (Visitante) q.executeUnique();
	}

	public List<Visitante> darVisitasRealizadas (PersistenceManager pm, long idVisitante)
	{
		String sql = "SELECT lec.id, lece.id, lece.idEstablecimiento, est.id, vis.id_lector";
		sql += " FROM ";
		sql += pp.darSeqVisitante() + " visi, ";
		sql += pp.darSeqEstablecimiento() + " est, ";
		sql += pp.darLectores() + " lec, ";
		sql += pp.darLectoresEspacio() + " lece, ";
		sql += pp.darVisitas() + " vis ";
		sql	+= " WHERE ";
		sql += "visi.id = ?";
		sql += " AND visi.id = vis.id_visitante";
		sql += " AND vis.id_lector = lec.id";
		sql += " AND lec.id = lece.id";
		sql += " AND lece.idEstablecimiento = est.id";

		Query q = pm.newQuery(SQL, sql);
		q.setParameters(idVisitante);
		return (List<Visitante>)q.executeList();
	}

	public long cambiarEstadoVisitante (PersistenceManager pm, long idVisitante, String estado) 
	{	
		String sql1 = "set transaction isolation level read serializable;";
		sql1+= "UPDATE " + pp.darSeqVisitante() + " SET estado = ? WHERE id = ?";

		darVisitasRealizadas(pm,idVisitante);

		Query q = pm.newQuery(SQL, sql1);
		q.setParameters(estado, idVisitante);
		return (long) q.executeUnique();            
	}


	public List<Visitante>  darAforo (PersistenceManager pm)
	{		
		String sql1 = "SELECT idvisitante, count (*) as numVisitas";
		sql1 += " FROM " + pp.darSeqVisita();
		sql1 += " GROUP BY id_visitante";

		String sql = "SELECT id, nombre, NVL (numVisitas, 0)";
		sql += " FROM " + pp.darSeqVisitante() + " LEFT OUTER JOIN (" + sql1 + ")";
		sql += " ON id = id_visitante";
		Query q = pm.newQuery(SQL, sql);
		return (List<Visitante>) q.executeList();
	}


	public List<Visitante> RFC10(PersistenceManager pm, Long id_local, String fechaInicio, String fechaFin, String ordenar)
	{ 
		String sql = "SELECT *";
		sql +=" FROM " +pp.darSeqVisitante();
		sql+=" WHERE ";
		sql+="id IN";
		sql+="(SELECT id_visitante FROM" + pp.darSeqVisita()+ "WHERE FechaHoraEntrada BETWEEN\n" + fechaInicio +"AND" + fechaFin;
		sql+="AND id_lector IN";
		sql+="(SELECT id FROM"+pp.darLectoresEspacio();
		sql+="WHERE idEstablecimiento="+id_local+"));";

		if(ordenar !="" && ordenar !=null) 
		{
			sql+="ORDER BY"+ ordenar +";";
		}		

		Query q = pm.newQuery(SQL, sql);
		return (List<Visitante>) q.executeList();
	}

	public List<Visitante> RFC11(PersistenceManager pm, Long id_local, String fechaInicio, String fechaFin, String ordenar)
	{ 
		String sql = "SELECT *";
		sql +=" FROM " +pp.darSeqVisitante();
		sql+=" WHERE ";
		sql+="id IN";
		sql+="(SELECT id_visitante FROM" + pp.darSeqVisita()+ "WHERE FechaHoraEntrada BETWEEN\n" + fechaInicio +"AND" + fechaFin;
		sql+="AND id_lector NOT IN";
		sql+="(SELECT id FROM"+pp.darLectoresEspacio();
		sql+="WHERE idEstablecimiento="+id_local+"));";

		if(ordenar !="" && ordenar !=null) 
		{
			sql+="ORDER BY"+ ordenar +";";
		}		
		Query q = pm.newQuery(SQL, sql);
		return (List<Visitante>) q.executeList();
	}


	public List<Visitante> buenosVisitantes(PersistenceManager pm)
	{
		String sql = "(SELECT A_VISITANTE.ID as id, A_VISITANTE.NOMBRE as nombre, A_VISITANTE.TELEFONO as telefono, "
				+ "A_VISITANTE.NOMCONTACTO as contacto, A_VISITANTE.NUMCONTACTO as contactoTelefono, A_VISITANTE.CORREO as correo";
		sql+="FROM(SELECT c, COUNT(DISTINCT mes)";
		sql+="ROM(SELECT EXTRACT(MONTH FROM CAST(FECHAENTRADA as DATE)) as mes,ID_VISITANTE as c";
		sql+="FROM " + pp.darSeqVisita();
		sql+="GROUP BY EXTRACT(MONTH FROM CAST(FECHAENTRADA as DATE)),ID_VISITANTE)";
		sql+="GROUP BY c";
		sql+="HAVING COUNT (DISTINCT mes)='12')";
		sql+="INNER JOIN A_VISITANTE ON A_VISITANTE.ID=VISITA.id_visitante) UNION";
		sql+="SELECT A_VISITANTE.ID as id, A_VISITANTE.NOMBRE as nombre, A_VISITANTE.TELEFONO as telefono,"
				+ " A_VISITANTE.NOMCONTACTO as contacto, A_VISITANTE.NUMCONTACTO as contactoTelefono, A_VISITANTE.CORREO as correo";
		sql+="FROM (SELECT dia, c, COUNT (DISTINCT Establecimiento) as espaciosVisitados";
		sql+="FROM(SELECT EXTRACT(DAY FROM CAST(FECHAENTRADA as DATE)) as dia, ID_VISITANTE as c, IDESTABLECIMIENTO as Establecimiento, TIPO ";
		sql+="FROM " +  pp.darSeqVisita();
		sql+="GROUP BY FECHAENTRADA, ID_VISITANTE, IDESTABLECIMIENTO, TIPO, EXTRACT(DAY FROM CAST(FECHAENTRADA as DATE))";
		sql+="HAVING EXTRACT(MONTH FROM CAST(FECHAENTRADA as DATE))='12' AND EXTRACT(YEAR FROM CAST(FECHAENTRADA as DATE))='2020' AND TIPO='Entrada')";
		sql+="GROUP BY dia, c";
		sql+="HAVING COUNT (DISTINCT Establecimiento)>=4)";
		sql+="INNER JOIN A_VISITANTE ON A_VISITANTE.ID=VISITA.id_visitante) UNION(";
		sql+="SELECT A_VISITANTE.ID as id, A_VISITANTE.NOMBRE as nombre, A_VISITANTE.TELEFONO as telefono, "
				+ "A_VISITANTE.NOMCONTACTO as contacto, A_VISITANTE.NUMCONTACTO as contactoTelefono, A_VISITANTE.CORREO as correo";
		sql+="FROM(SELECT c, COUNT (DISTINCT tipo) tipoEstablecimiento";
		sql+="FROM(SELECT dia, c, Establecimiento, TIPO as tipo";
		sql+="FROM(SELECT EXTRACT(DAY FROM CAST(FECHAENTRADA as DATE)) as dia, ID_VISITANTE as c, IDESTABLECIMIENTO as Establecimiento, TIPO";
		sql+="FROM " +  pp.darSeqVisita();
		sql+="GROUP BY FECHAENTRADA, ID_VISITANTE, IDESTABLECIMIENTO, EXTRACT(DAY FROM CAST(FECHAENTRADA as DATE))";
		sql+="HAVING EXTRACT(MONTH FROM CAST(FECHAENTRADA as DATE))='12' AND EXTRACT(YEAR FROM CAST(FECHAENTRADA as DATE))='2020')";
		sql+="INNER JOIN ESTABLECIMIENTO ON establecimiento.idespacio=Establecimiento)";
		sql+="GROUP BY c";
		sql+="HAVING COUNT (DISTINCT tipo)>=2)";
		sql+="INNER JOIN A_VISITANTE ON A_VISITANTE.ID=VISITA.ID_visitante);";

		Query q = pm.newQuery(SQL, sql);
		return (List<Visitante>) q.executeList();
	}
	
	public void LoadData(PersistenceManager pm) 
	{
		String sql = "";
		File fr = new File("Iteracion4_f.gadea_/data/datos.txt");
		try {
			Scanner myReader = new Scanner(fr);
			while(myReader.hasNextLine()) 
			{
				sql += myReader.nextLine();
				if(myReader.nextLine()==";") 
				{
					Query q = pm.newQuery(SQL, sql);
					q.executeList();
					sql="";
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
