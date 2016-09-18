import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FoodGenerator {
	private static Random random = new Random();
	private static List<Food> foods = generate1000Foods();
	
	public static List<Food> generate1000Foods(){
		List<Food> foods = new ArrayList<>();
		for (long i = 0; i < 2000; i++) {
			foods.add(
					new Food(
							i, 
							round(random.nextDouble() * 40000.0 - 20000.0), 
							round(random.nextDouble() * 40000.0 - 20000.0),
							round(random.nextDouble() * 40000.0 - 20000.0),
							round(random.nextDouble() * 90.0 + 10.0), 0, 0)
					);
		}
		return foods;
	}
	
	public static List<Food> jitterFoods() {
//		for (int i = 0; i < 2000; i++) {
//			foods.get(i).setX(jitter(foods.get(i).getX())); 
//			foods.get(i).setY(jitter(foods.get(i).getY())); 
//			foods.get(i).setZ(jitter(foods.get(i).getZ())); 
//		}
		return foods;
	}
	
	static double jitter(double value) {
		return value + (random.nextDouble() * 20.0 - 10);
	}
	
	static double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
