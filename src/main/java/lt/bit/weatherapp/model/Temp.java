package lt.bit.weatherapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "temp")
@NamedQueries({
    @NamedQuery(name = "Temp.findAll", query = "SELECT t FROM Temp t"),
    @NamedQuery(name = "Temp.findAllSorted", query = "SELECT t FROM Temp t ORDER BY t.obstime ASC"),
    @NamedQuery(name = "Temp.findById", query = "SELECT t FROM Temp t WHERE t.id = :id"),
    @NamedQuery(name = "Temp.findByTemp", query = "SELECT t FROM Temp t WHERE t.temp = :temp"),
    @NamedQuery(name = "Temp.findByObstime", query = "SELECT t FROM Temp t WHERE t.obstime = :obstime")})
public class Temp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "temp")
    private int temp;
    @Column(name = "obstime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date obstime;

    public Temp() {
    }

    public Temp(Integer id) {
        this.id = id;
    }

    public Temp(Integer id, int temp) {
        this.id = id;
        this.temp = temp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public Date getObstime() {
        return obstime;
    }

    public void setObstime(Date obstime) {
        this.obstime = obstime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Temp)) {
            return false;
        }
        Temp other = (Temp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Temp{" + "id=" + id + ", temp=" + temp + ", obstime=" + obstime + '}';
    }
    
}
