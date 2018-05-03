package cn.woniu.image;

public class Dimension {
	public int width = 0;
	public int height = 0;
	
	public Dimension(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	public Dimension inflated(int dx, int dy)
	{
		return new Dimension(width + dx, height + dy);
	}
	public void inflate(int dx, int dy)
	{
		width += dx;
		height += dy;
	}
}
