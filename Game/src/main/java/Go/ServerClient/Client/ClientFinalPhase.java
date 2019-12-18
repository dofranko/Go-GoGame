package Go.ServerClient.Client;

import GUI.FinalPhase.FinalPhaseGUI;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class ClientFinalPhase extends JFrame {
	public enum Stage {
		DEADSTONES, TERRITORY, THEEND
	}

	protected String myColor;

	protected int myPoints;
	protected int enemyPoints;

	protected boolean isAccepted = false;

	protected Socket socket;
	protected DataOutputStream dos;
	protected DataInputStream dis;

	protected Socket chatSocket;
	protected DataOutputStream chatos;
	protected DataInputStream chatis;



	protected Stage stage;

	public ClientFinalPhase(int size, String color, Socket socket, Socket chatSocket) {
		this.myColor = color;
		this.socket = socket;
		this.chatSocket = chatSocket;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.chatos = new DataOutputStream(chatSocket.getOutputStream());
			this.chatis = new DataInputStream(chatSocket.getInputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		this.stage = Stage.DEADSTONES;
	}

	/**
	 * rozłączenie tego okna
	 * 
	 * @param status
	 */
	public void disconnect(String status) {
		this.socket = null;
		this.chatSocket = null;
		this.dos = null;
		this.dis = null;
		this.chatos = null;
		this.chatis = null;
		this.dispose();
	}


	/**
	 * wysłanie wybrangeo pola do swerwera
	 * 
	 *
	 */
	public void sendPickStones(int x, int y) {
		String received = "";
		String move = x + "," + y;
		if (stage.equals(Stage.DEADSTONES)) {
			try {
				dos.writeUTF("PickDeadStones");
				dos.writeUTF(move);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (stage.equals(Stage.TERRITORY)) {
			try {
				dos.writeUTF("PickTerritory");
				dos.writeUTF(move);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(received);
	}

	/**
	 * aktualizacja planszy gry
	 * 
	 * @param stonesInString
	 */
	protected abstract void updateGameBoard(String stonesInString);

	/**
	 * aktualizacja labela punktów
	 * @param points ilosc punktow
	 */
	protected abstract void updatePointsLabel(int points);

	/**
	 * Akceptacja przez gracza aktualnego stanu
	 */
	public void acceptStage() {
		try {
			dos.writeUTF("AcceptStage");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void declineStage() {
		try {
			dos.writeUTF("DeclineStage");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendExit() {
		try {
			dos.writeUTF("Exit");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed");
	}

	/**
	 * metoda pobierająca ilość punktów
	 * @return
	 */
	protected int getPoints() {
		try {
			dos.writeUTF("GetPoints");
			String[] pointsString = dis.readUTF().split(";");
			if (pointsString[0].equals(myColor)) {
				myPoints = Integer.parseInt(pointsString[1]);
				enemyPoints = Integer.parseInt(pointsString[3]);
			} else {
				myPoints = Integer.parseInt(pointsString[3]);
				enemyPoints = Integer.parseInt(pointsString[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return myPoints;
	}

	/**
	 * Wątek pobierający informacje o planszy i odświeżający ją. (@see
	 * waitingForTurnThread)
	 */
	protected void startRefreshingMapThread() {
		Thread refresh = new Thread(() -> {
			updatePointsLabel(getPoints());
			String stones;
			String whoAccepted;
			String status;
			while (!stage.equals(Stage.THEEND)) {
				try {
					Thread.sleep(1000);
					dos.writeUTF("MapRefresh");
					stones = dis.readUTF();
					dos.writeUTF("WhoAccepted");
					whoAccepted = dis.readUTF();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					break;
				} catch (IOException ex) {
					ex.printStackTrace();
					break;
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}
				System.out.println(myColor + "Hi:" + stones);
				try {
					Integer.parseInt(stones.split(";")[0]);
					updateGameBoard(stones);
					status = "";
				} catch (Exception ex) {
					status = stones.split(";")[0];
					stones = stones.substring(status.length() + 1);
					updateGameBoard(stones);
					switch (stage) {
						case DEADSTONES:
							if (status.equals("PickingTerritory")) {
								stage = Stage.TERRITORY;
								JOptionPane.showMessageDialog(this,
												"Następnie zaznacz wszystkie pola otoczone całkowicie Twoimi kamieniami.\n"
																+ "Jak poprzednio, możesz zaakceptować wybór lub go odrzucić.");
								isAccepted = false;
								updatePointsLabel(getPoints());
							}
							break;
						case TERRITORY:
							if (status.equals("End")) {
								stage = Stage.THEEND;
								updatePointsLabel(getPoints());
								if (myPoints > enemyPoints) {
									JOptionPane.showMessageDialog(this, "Wygrywasz - " + myColor + "!\n"
													+ String.valueOf(myPoints) + " punktów do " + String.valueOf(enemyPoints) + " punktów.");
								} else if (myPoints < enemyPoints) {
									JOptionPane.showMessageDialog(this, "Przegrywasz - " + myColor + "!\n"
											+ String.valueOf(myPoints) + " punktów do " + String.valueOf(enemyPoints) + " punktów.");
								} else {
									JOptionPane.showMessageDialog(this, "Remis!\nKażdy ma " + String.valueOf(myPoints) + " punktów.");
								}
							}
							break;
					default:
						break;
					}
				}
				if (whoAccepted.contains("Accepted") && !whoAccepted.contains(myColor) && !isAccepted) {
					JOptionPane.showMessageDialog(this, "Przeciwnik zaakceptował!");
					isAccepted = true;
				} 
				else if (isAccepted && whoAccepted.equals("Empty")) {
				//	JOptionPane.showMessageDialog(this, "Przeciwnik odrzucił wybór!");
					isAccepted = false;
				}
				if(status.contains("Wins") && status.contains(myColor)) {
					JOptionPane.showMessageDialog(this, "Przeciwnik wyszedł z gry!");
					stage = Stage.THEEND;
				}

			}
		});
		refresh.start();
	}

	/**
	 * Konwerter z String na tablice int
	 * @param stonesInString
	 * @return
	 */
	protected int[][] convertStonesToIntFromString(String stonesInString) {
		int[][] stones;
		String[] columns = stonesInString.split(";");
		stones = new int[columns.length][columns.length];
		int i = 0;
		int j = 0;
		for (String column : columns) {
			String[] fields = column.split(",");
			for (String field : fields) {
				stones[i][j] = Integer.parseInt(field);
				j++;
			}
			j = 0;
			i++;
		}
		return stones;
	}
}
