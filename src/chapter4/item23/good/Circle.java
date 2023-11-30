package chapter4.item23.good;

public class Circle extends Figure {

	final double radius;

	public Circle(double radius) {
		this.radius = radius;
	}

	@Override
	double area() {
		return Math.PI * radius * radius;
	}
}
