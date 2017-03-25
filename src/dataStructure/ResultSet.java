package dataStructure;

public class ResultSet {
	public String name;
	public double precision;
	public double recall;
	public double calFMeasure(){
		double FMeasure=2*precision*recall/(precision+recall);
		return FMeasure;
	}
}
