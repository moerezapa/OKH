import java.util.Arrays;
import java.util.Random;

public class LowLevelHeuristics {

	public static int[][] move(int[][] timeslot, int jumlahmove) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		for (int i = 0; i < jumlahmove; i++) {
			int randomCourse = randomNumber(1, timeslot.length);
			int randomTimeSlot = randomNumber(1, Arrays.stream(slot).max().getAsInt());
//			System.out.println("min number to random: " + 1 + " and max number to random: " + Arrays.stream(slot).max().getAsInt() + ", dalam method nextInt : " + (Arrays.stream(slot).max().getAsInt()-1));
			
			timeslotSementara[randomCourse][1] = randomTimeSlot;
		}
		
		return timeslotSementara;
	}
	
	public static int[][] swap(int[][] timeslot, int jumlahswap) {
		int[][] timeslotSementara = timeslot;
		
		for(int i=0; i < jumlahswap; i++) {
			int exam1 = randomNumber(0, timeslot.length);
			int exam2 = randomNumber(0, timeslot.length);
			
			int slot1 = timeslot[exam1][1];
			int slot2 = timeslot[exam2][1];
			
			timeslotSementara[exam1][1] = slot2;
			timeslotSementara[exam2][1] = slot1;
		}
		
		return timeslotSementara;
	}
	
	private static int randomNumber(int min, int max) {
		Random random = new Random();
		try {
			return random.nextInt(max - min) + min;	
		}
			catch(Exception e) {
//				System.out.println("ERROR di nextInt: " + (max-min));
				//return random.nextInt(Math.abs(max - min)) + min;
				if (Math.abs(max - min) == 0) {
					return random.nextInt(Math.abs(max - min)+1) + min;
				}
					else
						return random.nextInt(Math.abs(max - min)) + min;
			}
	}
}
