package uniandes.isis2304.parranderos.negocio;

public class Visita implements VOVisita{

	private long id_visitante;
	private long id_lector;
	private String FechaHoraEntrada;
	private String FechaHoraSalida;

	public Visita(){

		this.id_visitante=0;
		this.id_lector=0;
		this.FechaHoraEntrada=new String();
		this.FechaHoraSalida=new String();
	}

	public Visita(long idV, long idL, String en, String sal) {

		id_visitante=idV;
		id_lector=idL;
		FechaHoraEntrada=en;
		FechaHoraSalida=sal;
	}

	public long getId_visitante() {
		return id_visitante;
	}

	public void setId_visitante(long id_visitante) {
		this.id_visitante = id_visitante;
	}

	public long getId_lector() {
		return id_lector;
	}

	public void setId_lector(long id_lector) {
		this.id_lector = id_lector;
	}

	public String getFechaHoraEntrada() {
		return FechaHoraEntrada;
	}

	public void setFechaHoraEntrada(String fechaHoraEntrada) {
		FechaHoraEntrada = fechaHoraEntrada;
	}

	public String getFechaHoraSalida() {
		return FechaHoraSalida;
	}

	public void setFechaHoraSalida(String fechaHoraSalida) {
		FechaHoraSalida = fechaHoraSalida;
	}

	@Override
	public String toString() 
	{
		return "Visita [id_visitante=" + id_visitante + ", id_lector=" + id_lector +", FechaHoraEntrada=" + FechaHoraEntrada + ", FechaHoraSalida"+FechaHoraSalida+"]";
	}
}
