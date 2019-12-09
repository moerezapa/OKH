import java.util.Random;

public class Evaluator {
	
	public static int[][] getTimeslot(int[][] timeslot) {
		int[][] copySolution = new int[timeslot.length][2];
		
		for(int i = 0; i < timeslot.length; i++) {
			copySolution[i][0] = timeslot[i][0];
			copySolution[i][1] = timeslot[i][1];
		}
		
		return copySolution;
	}
	
	public static double getPenalty(int[][] matrix, int[][] jadwal, int jumlahMurid) {
		double penalty = 0;
		
		for(int i = 0; i < matrix.length - 1; i++) {
			for(int j = i+1; j < matrix.length; j++) {
				if(matrix[i][j] != 0) {
					if(Math.abs(jadwal[j][1] - jadwal[i][1]) >= 1 && Math.abs(jadwal[j][1] - jadwal[i][1]) <= 5) {
						penalty = penalty + (matrix[i][j] * (Math.pow(2, 5-(Math.abs(jadwal[j][1] - jadwal[i][1])))));
					}
				}
			}
		}
		
		return penalty/jumlahMurid;
	}
	
	public static int getRandomNumber(int min, int max) {
	    Random random = new Random();
	    return random.nextInt(max - min) + min;
	}
}
