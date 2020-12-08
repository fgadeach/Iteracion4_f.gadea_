package uniandes.isis2304.parranderos.negocio;

public interface VOVisita {
	public long getId_visitante();
	public long getId_lector();
	public String getFechaHoraEntrada();
	public String getFechaHoraSalida();
	@Override
	public String toString();
}
