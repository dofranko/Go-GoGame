import Go.ServerClient.Hibernate.GamesEntity;
import Go.ServerClient.Hibernate.MovesEntity;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.util.Map;

/* TODO
CREATE TABLE games (
 ID int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
 BlackPlayerID int(11) DEFAULT NULL,
WhitePlayerID int(11) DEFAULT NULL,
 reg_date timestamp DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE moves (
 ToIgnoreID int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
 GameID int(11) DEFAULT NULL,
 Move int(11) DEFAULT NULL,
 PlayerIDMove int(11) DEFAULT NULL,
 TypeOfMove enum('ruch','pass','poddanie sie') DEFAULT 'ruch',
 Board varchar(750) DEFAULT NULL,
 reg_date timestamp DEFAULT current_timestamp,
 FOREIGN KEY (GameID) REFERENCES games(ID)
)
TODO
 */






public class Main {
  private static final SessionFactory ourSessionFactory;
  static {
    try {
      Configuration configuration = new Configuration();
      configuration.configure();

      ourSessionFactory = configuration.buildSessionFactory();
    } catch (Throwable ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static Session getSession() throws HibernateException {
    return ourSessionFactory.openSession();
  }

  public static void main(final String[] args) throws Exception {

    Session session = getSession();
    Transaction tx = null;
    int gameId = -1;

    try {
      tx = session.beginTransaction();
      System.out.println("My stuff");

      GamesEntity game = new GamesEntity();
      game.setBlackPlayerId(123);
      game.setWhitePlayerId(12);
      //game.setId(0);
      gameId = (int)session.save(game);

      MovesEntity move = new MovesEntity();
      move.setGameid(gameId);
      move.setPlayerIdMove(123);
      move.setBoard("1,1,1,1,1,0,0,0,0,1,1,1,");
      move.setTypeOfMove("ruch");
      move.setMove(1);


      session.save(move);
      tx.commit();
      System.out.println(gameId);
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }

  }


}
