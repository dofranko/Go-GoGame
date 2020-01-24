package Go.ServerClient.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "moves", schema = "gogame", catalog = "")
public class MovesEntity {
  private int toIgnoreId;
  private Integer move;
  private Integer playerIdMove;
  private String typeOfMove;
  private String board;
  private Timestamp regDate;
  private Integer gameid;

  public Integer getGameid() {
    return gameid;
  }

  public void setGameid(Integer gameid) {
    this.gameid = gameid;
  }

  @Id
  @Column(name = "ToIgnoreID", nullable = false)
  public int getToIgnoreId() {
    return toIgnoreId;
  }

  public void setToIgnoreId(int toIgnoreId) {
    this.toIgnoreId = toIgnoreId;
  }

  @Basic
  @Column(name = "Move", nullable = true)
  public Integer getMove() {
    return move;
  }

  public void setMove(Integer move) {
    this.move = move;
  }

  @Basic
  @Column(name = "PlayerIDMove", nullable = true)
  public Integer getPlayerIdMove() {
    return playerIdMove;
  }

  public void setPlayerIdMove(Integer playerIdMove) {
    this.playerIdMove = playerIdMove;
  }

  @Basic
  @Column(name = "TypeOfMove", nullable = true)
  public String getTypeOfMove() {
    return typeOfMove;
  }

  public void setTypeOfMove(String typeOfMove) {
    this.typeOfMove = typeOfMove;
  }

  @Basic
  @Column(name = "Board", nullable = true, length = 511)
  public String getBoard() {
    return board;
  }

  public void setBoard(String board) {
    this.board = board;
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

    MovesEntity that = (MovesEntity) o;

    if (toIgnoreId != that.toIgnoreId) return false;
    if (move != null ? !move.equals(that.move) : that.move != null) return false;
    if (playerIdMove != null ? !playerIdMove.equals(that.playerIdMove) : that.playerIdMove != null) return false;
    if (typeOfMove != null ? !typeOfMove.equals(that.typeOfMove) : that.typeOfMove != null) return false;
    if (board != null ? !board.equals(that.board) : that.board != null) return false;
    if (regDate != null ? !regDate.equals(that.regDate) : that.regDate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = toIgnoreId;
    result = 31 * result + (move != null ? move.hashCode() : 0);
    result = 31 * result + (playerIdMove != null ? playerIdMove.hashCode() : 0);
    result = 31 * result + (typeOfMove != null ? typeOfMove.hashCode() : 0);
    result = 31 * result + (board != null ? board.hashCode() : 0);
    result = 31 * result + (regDate != null ? regDate.hashCode() : 0);
    return result;
  }
}
