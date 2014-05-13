package cn.tongji.lab.model;

public class Integer2 {

	private int today = 0;
	private int yesterday = 0;
	private int sum = 0;
	private double proportion = 0;
	private double result = 0;

	public Integer2(int today, int yesterday) {
		// TODO Auto-generated constructor stub
		this.today = today;
		this.yesterday = yesterday;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public int getToday() {
		return today;
	}

	public void setToday(int today) {
		this.today = today;
	}

	public int getYesterday() {
		return yesterday;
	}

	public void setYesterday(int yesterday) {
		this.yesterday = yesterday;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public double getProportion() {
		return proportion;
	}

	public void setProportion(double proportion) {
		this.proportion = proportion;
	}

}
