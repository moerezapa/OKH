import java.util.Arrays;
import java.util.Random;

public class LowLevelHeuristics {

	static int[][] conflict_matrix;
	LowLevelHeuristics(int[][] conflictmatrix) {
		conflict_matrix = conflictmatrix;
	}
	
	public static int[][] move(int[][] timeslot, int jumlahmove) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		for (int i = 0; i < jumlahmove; i++) {
//			int randomCourse = randomNumber(1, timeslot.length);
//			int randomTimeSlot = randomNumber(1, Arrays.stream(slot).max().getAsInt());
			int randomCourse = random(timeslot.length);
			int randomTimeSlot = random(Arrays.stream(slot).max().getAsInt());
			System.out.println("number to random: " + Arrays.stream(slot).max().getAsInt());
			
			timeslotSementara[randomCourse][1] = randomTimeSlot;
		}
		
		return timeslotSementara;
	}
	public int[][] move1(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		
		int randomCourse = random(timeslot.length);
		int randomTimeSlot = random(Arrays.stream(slot).max().getAsInt());
//		System.out.println("number timeslot to random: " + randomTimeSlot);
		if (Schedule.checkRandomTimeslot(randomCourse, randomTimeSlot, conflict_matrix, timeslot))
			timeslotSementara[randomCourse][1] = randomTimeSlot;
		
		return timeslotSementara;
	}
	public int[][] move2(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		int randomCourse1 = random(timeslot.length);
		int randomCourse2 = random(timeslot.length);
		int randomTimeSlot1 = random(Arrays.stream(slot).max().getAsInt());
		int randomTimeSlot2 = random(Arrays.stream(slot).max().getAsInt());
//		System.out.println("number timeslot to random: " + randomTimeSlot1 + " , " + randomTimeSlot2);
		if (Schedule.checkRandomTimeslot(randomCourse1, randomTimeSlot1, conflict_matrix, timeslot))
			timeslotSementara[randomCourse1][1] = randomTimeSlot1;
	
		if (Schedule.checkRandomTimeslot(randomCourse2, randomTimeSlot2, conflict_matrix, timeslot))
			timeslotSementara[randomCourse2][1] = randomTimeSlot2;
		
		return timeslotSementara;
	}
	public int[][] move3(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		int randomCourse1 = random(timeslot.length);
		int randomCourse2 = random(timeslot.length);
		int randomCourse3 = random(timeslot.length);
		int randomTimeSlot1 = random(Arrays.stream(slot).max().getAsInt());
		int randomTimeSlot2 = random(Arrays.stream(slot).max().getAsInt());
		int randomTimeSlot3 = random(Arrays.stream(slot).max().getAsInt());
//		System.out.println("number timeslot to random: " + randomTimeSlot1 + " , " + randomTimeSlot2 + " , " + randomTimeSlot3);
		if (Schedule.checkRandomTimeslot(randomCourse1, randomTimeSlot1, conflict_matrix, timeslot))
			timeslotSementara[randomCourse1][1] = randomTimeSlot1;
		
		if (Schedule.checkRandomTimeslot(randomCourse2, randomTimeSlot2, conflict_matrix, timeslot))
			timeslotSementara[randomCourse2][1] = randomTimeSlot2;
		
		if (Schedule.checkRandomTimeslot(randomCourse3, randomTimeSlot3, conflict_matrix, timeslot))
			timeslotSementara[randomCourse3][1] = randomTimeSlot3;
		
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
	public int[][] swap2(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		
		
		int randomcourse1 = random(timeslot.length);
		int randomcourse2 = random(timeslot.length);
		while (randomcourse2 == randomcourse1) {
			randomcourse2 = random(timeslot.length);
		}
		
		int slot1 = timeslot[randomcourse1][1];
		int slot2 = timeslot[randomcourse2][1];
		
		if (Schedule.checkRandomTimeslot(randomcourse1, slot2, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse1][1] = slot2;
		}
		if (Schedule.checkRandomTimeslot(randomcourse2, slot1, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse2][1] = slot1;
		}
		return timeslotSementara;
	}
	public int[][] swap3(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		
		int randomcourse1, randomcourse2, randomcourse3;
		do {
			randomcourse1 = random(timeslot.length);
			randomcourse2 = random(timeslot.length);
			randomcourse3 = random(timeslot.length);
		} while (randomcourse2 == randomcourse1 || randomcourse2 == randomcourse3 || randomcourse1 == randomcourse3);
		
		int slot1 = timeslot[randomcourse1][1];
		int slot2 = timeslot[randomcourse2][1];
		int slot3 = timeslot[randomcourse3][1];
			
//		timeslotSementara[randomcourse1][1] = slot2;
//		timeslotSementara[randomcourse2][1] = slot1;
		
		if (Schedule.checkRandomTimeslot(randomcourse1, slot2, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse1][1] = slot2;
		}
		if (Schedule.checkRandomTimeslot(randomcourse2, slot3, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse2][1] = slot3;
		}
		if (Schedule.checkRandomTimeslot(randomcourse3, slot1, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse3][1] = slot1;
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
	
	private static int random(int number) {
		Random random = new Random();
		return random.nextInt(number);
	}
}

