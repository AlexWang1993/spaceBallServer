
public class Player {
	long id;
	String name;
	double x,y,z,size;
	long score;
	int material,color;

	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}


	public double getZ() {
		return z;
	}


	public void setZ(double z) {
		this.z = z;
	}


	public double getSize() {
		return size;
	}


	public void setSize(double size) {
		this.size = size;
	}


	public long getScore() {
		return score;
	}


	public void setScore(long score) {
		this.score = score;
	}


	public int getMaterial() {
		return material;
	}


	public void setMaterial(int material) {
		this.material = material;
	}


	public int getColor() {
		return color;
	}


	public void setColor(int color) {
		this.color = color;
	}


	public Player(long id, String name, double x, double y, double z,
			double size, long score, int material, int color) {
		super();
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.score = score;
		this.material = material;
		this.color = color;
	}


	@Override
	public String toString() {
		return GameUtil.getCommaSeparatedFields(id, x, y, z, color, material, size, name);
	}
	
	public String toShortString() {
		return GameUtil.getCommaSeparatedFields(id, name, score);
	}
}
