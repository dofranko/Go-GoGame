package Go.ServerClient.Hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DBManager {

	//final Session session;
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

	public DBManager() {
		/**
		 * Hibernate stuff
		 */
		//session = getSession();

	}

	public int insertGame(String blackID, String whiteID) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			 GamesEntity game = new GamesEntity();
			 game.setBlackPlayerId(Integer.parseInt(blackID));
			 game.setWhitePlayerId(Integer.parseInt(whiteID));
			 tx.commit();
			 return (int)session.save(game);
			 
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return 0;
	}
	
	public void insertMove(String playerID, String boardState, String typeOfMove, int moveCount, int gameID) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			MovesEntity move = new MovesEntity();
			move.setPlayerIdMove(Integer.parseInt(playerID));
			move.setBoard(boardState);
			move.setTypeOfMove(typeOfMove);
			move.setMove(moveCount);
			move.setGameid(gameID);
			session.save(move);
			tx.commit();
			
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}

}
