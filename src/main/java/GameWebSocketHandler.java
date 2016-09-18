

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.media.jfxmedia.events.PlayerEvent;

@WebSocket
public class GameWebSocketHandler {
	
	private static Map<Session, Player> playersMap = new ConcurrentHashMap<>();
	private static Map<Long, Player> playersIdMap = new ConcurrentHashMap<>();
	private static Map<Long, Food> foodIdMap = new ConcurrentHashMap<>();
	private static Set<Player> allPlayers = new ConcurrentHashSet<>();
	private static Set<Food> allFood = new ConcurrentHashSet<>();
	private static Set<Food> newFoods = new ConcurrentHashSet<>();
	private static Set<Long> goneFoodsIds = new ConcurrentHashSet<>();
	private static Set<Session> allSessions = new ConcurrentHashSet<>();
	private static Set<Long> idsToDie = new ConcurrentHashSet<>();
	private static AtomicLong playerCounter = new AtomicLong(0);
	private static AtomicLong foodCounter = new AtomicLong(0);
	
	private static double INITIAL_SIZE = 50.0;
	private static String PLAYER_NOT_FOUND = "99,player not found!";
	private static String OTHER_PLAYER_NOT_FOUND = "99,other player not found!";
	private static String UNKNOWN_MSG_TYPE = "99,unknown message type";
	private static String UNKNOWN_MSG_FORMAT = "99,invalid message format";
	
	private static Random random = new Random();
	
	private static Set<Integer> BLACKLISTED_MSG_TYPE;
	
	{
		BLACKLISTED_MSG_TYPE = new HashSet<>();
		BLACKLISTED_MSG_TYPE.add(2);
		List<Food> foods = FoodGenerator.generate1000Foods();
		allFood.addAll(foods);
		for (Food food : foods) {
			foodIdMap.put(food.getId(), food);
		}
		Timer timer = new Timer();
		Timer timer2 = new Timer();
		Timer timer3 = new Timer();
		Timer timer4 = new Timer();
		Timer timer5 = new Timer();
		timer.scheduleAtFixedRate(new ListPlayersTask(), 30, 30);
		timer2.scheduleAtFixedRate(new NewFoodTask(), 100, 100);
		timer3.scheduleAtFixedRate(new LeaderboardTask(), 1000, 1000);
		timer4.scheduleAtFixedRate(new GoneFoodTask(), 100, 100);
		timer5.scheduleAtFixedRate(new NewDeathTask(), 100, 100);
	}

	@OnWebSocketMessage
	public void onText(Session session, String message) throws IOException {
		
		if (session.isOpen()) {
			try {
				String[] args = message.split(",");
				int msgType = Integer.valueOf(args[0]);
				if (!BLACKLISTED_MSG_TYPE.contains(msgType)) {
					System.out.println("Message received:" + message);
				}
				switch (msgType) {
					case 1:			//birth 
					{
						int color = Integer.valueOf(args[1]);
						int material = Integer.valueOf(args[2]);
						String name = args[3];
						long id = playerCounter.incrementAndGet();
						Player player = new Player(id, name, random.nextDouble() * 1000 - 500, random.nextDouble() * 1000 - 500, random.nextDouble() * 1000 - 500, INITIAL_SIZE, 0, material, color);
						playersMap.put(session, player);
						playersIdMap.put(id, player);
						allPlayers.add(player);
						sendSyncMessage(session, generateBirthMessage(id, player.x, player.y, player.z, player.size));
						System.out.println(player.name + " joined the game!!");
						break;
					}
					case 2:			//movement
					{	
						double x = round(Double.valueOf(args[1]));
						double y = round(Double.valueOf(args[2]));
						double z = round(Double.valueOf(args[3]));
						Player player = playersMap.get(session);
						if (player == null) {
							sendError(session, PLAYER_NOT_FOUND);
						} else {
							player.setX(x);
							player.setY(y);
							player.setZ(z);
						}
						break;
					}
					case 3:		//eat_food
					{
						long foodId = Long.valueOf(args[1]);
						double newSize = Double.valueOf(args[2]);
						long newScore = Long.valueOf(args[3]);
						Player player = playersMap.get(session);
						Food food = foodIdMap.get(foodId);
						if (player == null) {
							sendError(session, PLAYER_NOT_FOUND);
						} else if (food == null) {
							sendError(session, OTHER_PLAYER_NOT_FOUND);
						} else {
							player.setScore(newScore);
							player.setSize(newSize);
							foodIdMap.remove(foodId);
							goneFoodsIds.add(foodId);
							allFood.remove(food);
							System.out.println(player.name + " ate food " + foodId);
						}
						break;
					}
					case 4:		//eat_player
					{
						long player2Id = Long.valueOf(args[1]);
						double newSize = Double.valueOf(args[2]);
						long newScore = Long.valueOf(args[3]);
						Player player = playersMap.get(session);
						Player player2 = playersIdMap.get(player2Id);
						if (player == null) {
							sendError(session, PLAYER_NOT_FOUND);
						} else if (player2 == null) {
							sendError(session, OTHER_PLAYER_NOT_FOUND);
						} else {
							player.setScore(newScore);
							player.setSize(newSize);
							if (playersIdMap.containsKey(player2Id)) {
								idsToDie.add(player.id);
							}
							playersIdMap.remove(player2Id);
							allPlayers.remove(player2);
							idsToDie.add(player2Id);
							System.out.println(player.name + " ate player " + player2.name);
						}
						break;
					}
					case 99:		//death
					{
						Player player = playersMap.get(session);
						if (player != null) {
							playersIdMap.remove(player.id);
							allPlayers.remove(player);
							if (idsToDie.contains(player.id)) {
								idsToDie.remove(player.id);
							}
						}
						playersMap.remove(session);
//						Player player = playersMap.get(session);
//						if (player == null) {
//							sendError(session, PLAYER_NOT_FOUND);
//						} else {
//							long id = player.getId();
//							playersIdMap.remove(id);
//							playersMap.remove(session);
//							allPlayers.remove(player);
//						}
						break;
					}
					default:
						sendError(session, UNKNOWN_MSG_TYPE);
				}
			} catch (NumberFormatException e) {
				System.err.println(e);
				sendError(session, UNKNOWN_MSG_FORMAT);
			}
					
		}
	}

	@OnWebSocketConnect
	public void onConnect(Session session) throws IOException {
		System.out.println(session.getRemoteAddress().getHostString() + " connected!");
		allSessions.add(session);
		sendMessage(session, generateFoodsMessage());
//		playersMap.put(session, null);
//		session.getRemote().sendString("1");
	}

	@OnWebSocketClose
	public void onClose(Session session, int status, String reason) {
		System.out.println(session.getRemoteAddress().getHostString() + " closed!");
		allSessions.remove(session);
		Player player = playersMap.get(session);
		if (player != null) {
			playersIdMap.remove(player.getId());
			allPlayers.remove(player);
			playersMap.remove(session);
			System.out.println(player.name + " logged out!");
		}
	}
	
	private void sendError(Session session, String message) throws IOException {
		System.out.println(session.getRemoteAddress().getHostString() + " error:" + message);
		session.getRemote().sendString(message);
	}
	
	private void sendSyncMessage(Session session, String message) throws IOException {
		System.out.println(session.getRemoteAddress().getHostString() + " info:" + message.substring(0, Math.min(200, message.length())));
		session.getRemote().sendString(message);
	}
	
	private void sendMessage(Session session, String message) throws IOException {
		System.out.println(session.getRemoteAddress().getHostString() + " info:" + message.substring(0, Math.min(200, message.length())));
		session.getRemote().sendStringByFuture(message);
	}
	
	static abstract class BroadCastTask extends TimerTask {
		@Override
		public void run() {
			String message = generateMessage();
			if (message == null) {
				return;
			}
			for (Session session : allSessions) {
				if (session.isOpen()) {
					session.getRemote().sendStringByFuture(message);
				}
			}
			if (random.nextDouble() < 0.05) {
//				System.out.println(message);
			}
		}
		
		abstract protected String generateMessage();
	}
	
	static class ListPlayersTask extends BroadCastTask {
		@Override
		protected String generateMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append("3,");
			for (Player player : allPlayers) {
				sb.append("[");
				sb.append(player.toString());
				sb.append("],");
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
	}
	
//	static class ListFoodTask extends BroadCastTask {
//		@Override
//		protected String generateMessage() {
//			StringBuilder sb = new StringBuilder();
//			sb.append("4,");
////			for (Food food : allFood) {
//			for (Food food : FoodGenerator.jitterFoods()) {
//				sb.append("[");
//				sb.append(food.toString());
//				sb.append("],");
//			}
//			sb.setLength(sb.length() - 1);
//			return sb.toString();
//		}
//	}
	
	protected String generateBirthMessage(long id, double x, double y, double z, double size) {
		return GameUtil.getRealCommaSeparatedFields("2", id, x, y, z, size);
		
	}
	
	protected String generateFoodsMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("6,");
//		for (Food food : allFood) {
		for (Food food : allFood) {
			sb.append("[");
			sb.append(food.toString());
			sb.append("],");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	static class NewDeathTask extends TimerTask {
		
		private static String DEATH_MSG = "9";
		@Override
		public void run() {
			if (idsToDie.isEmpty()) {
				return;
			}
			Set<Long> deadIdsSet = new HashSet<>(idsToDie);
			idsToDie.clear();
//			for (Food food : allFood) {
			for (Session session : allSessions) {
				Player player = playersMap.get(session);
				if (player != null) {
					if (deadIdsSet.contains(player.id)) {
						playersMap.remove(session);
						System.out.println("Notify death " + player.name);
						session.getRemote().sendStringByFuture(DEATH_MSG);
					}
				}
			}
		}
	}
	
	static class NewFoodTask extends BroadCastTask {
		@Override
		protected String generateMessage() {
			if (newFoods.isEmpty()) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("6,");
			List<Food> newFoodsList = new ArrayList<>(newFoods);
			newFoods.clear();
//			for (Food food : allFood) {
			for (Food food : newFoodsList) {
				sb.append("[");
				sb.append(food.toString());
				sb.append("],");
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
	}
	
	static class GoneFoodTask extends BroadCastTask {
		@Override
		protected String generateMessage() {
			if (goneFoodsIds.isEmpty()) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("7,");
			List<Long> goneFoodsList = new ArrayList<>(goneFoodsIds);
			if (goneFoodsList.size() > 0) {
				System.out.println(goneFoodsList.size() + " foods gone!");
			}
			goneFoodsIds.clear();
//			for (Food food : allFood) {
			for (long id : goneFoodsList) {
				sb.append(id);
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
	}
	
	static class LeaderboardTask extends BroadCastTask {
		@Override
		protected String generateMessage() {
			List<Player> players = new ArrayList<>(allPlayers);
			Collections.sort(players, new Comparator<Player>() {
				@Override
				public int compare(Player o1, Player o2) {
					return -Long.compare(o1.score, o2.score);
				}
			});
			
			StringBuilder sb = new StringBuilder();
			sb.append("5,");
			for (int i = 0 ; i < Math.min(5, players.size()); i++) {
				Player player = players.get(i);
				sb.append("[");
				sb.append(player.toShortString());
				sb.append("],");
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
	}
	
	public static double round(Double value) {
		return Math.round(value * 100.0) / 100.0;
	}
	
}