package Go.ServerClient.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "games", schema = "gogame", catalog = "")
public class GamesEntity {
  private int id;
  private Integer blackPlayerId;
  private Integer whitePlayerId;
   private Timestamp regDate;

  @Id
  @Column(name = "ID", nullable = false)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Basic
  @Column(name = "BlackPlayerID", nullable = true)
  public Integer getBlackPlayerId() {
    return blackPlayerId;
  }

  public void setBlackPlayerId(Integer blackPlayerId) {
    this.blackPlayerId = blackPlayerId;
  }

  @Basic
  @Column(name = "WhitePlayerID", nullable = true)
  public Integer getWhitePlayerId() {
    return whitePlayerId;
  }

  public void setWhitePlayerId(Integer whitePlayerId) {
    this.whitePlayerId = whitePlayerId;
  }

  @Basic
  @Column(name = "reg_date", nullable = false)
  public Timestamp getRegDate() {
    return regDate;
  }

  public void setRegDate(Timestamp regDate) {
    this.regDate = regDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GamesEntity that = (GamesEntity) o;

    if (id != that.id) return false;
    if (blackPlayerId != null ? !blackPlayerId.equals(that.blackPlayerId) : that.blackPlayerId != null) return false;
    if (whitePlayerId != null ? !whitePlayerId.equals(that.whitePlayerId) : that.whitePlayerId != null) return false;
    if (regDate != null ? !regDate.equals(that.regDate) : that.regDate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (blackPlayerId != null ? blackPlayerId.hashCode() : 0);
    result = 31 * result + (whitePlayerId != null ? whitePlayerId.hashCode() : 0);
    result = 31 * result + (regDate != null ? regDate.hashCode() : 0);
    return result;
  }
}
