

public class Food {
	long id;
	double x,y,z,size;
	int color, material;
	public Food(long id, double x, double y, double z, double size, int color,
			int material) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.color = color;
		this.material = material;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getMaterial() {
		return material;
	}
	public void setMaterial(int material) {
		this.material = material;
	}
	
	@Override
	public String toString() {
		return GameUtil.getCommaSeparatedFields(id, x, y, z, color, material, size);
	}
}
