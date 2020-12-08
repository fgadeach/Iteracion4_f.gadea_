package uniandes.isis2304.parranderos.negocio;
public class Horario implements VOHorario{
	private long id;
	private String horaApertura;
	private String horaClausura;

	public Horario() {
		this.id=0;
		this.horaApertura=new String();
		this.horaClausura=new String();
	}
	public Horario(long i, String d, String da) {
		id=i;
		horaApertura=d;
		horaClausura=da;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getHoraApertura() {
		return horaApertura;
	}
	public void setHoraApertura(String horaApertura) {
		this.horaApertura = horaApertura;
	}
	public String getHoraClausura() {
		return horaClausura;
	}
	public void setHoraClausura(String horaClausura) {
		this.horaClausura = horaClausura;
	}
	@Override
	public String toString() {
		return "Horario [id=" + id + ", horaApertura=" + horaApertura + ", horaClausura=" + horaClausura + "]";
	}

}
