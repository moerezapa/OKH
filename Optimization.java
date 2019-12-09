import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Optimization {
	int[][] timeslotHillClimbing, timeslotSimulatedAnnealing, timeslotOtherMeta, conflict_matrix, course_sorted;
	int[] timeslot;
	String file;
	int jumlahexam, jumlahmurid, randomCourse, randomTimeslot;
	
	Optimization(String file, int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid) { 
		this.file = file; 
		this.conflict_matrix = conflict_matrix;
		this.course_sorted = course_sorted;
		this.jumlahexam = jumlahexam;
		this.jumlahmurid = jumlahmurid;
	}
	
	// hill climbing method
	public void getTimeslotByHillClimbing(int iterasi) throws IOException {
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		
		//schedule.printSchedule();
		
		timeslotHillClimbing = schedule.getSchedule(); // initial feasible solution
		double initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		int[][] timeslotHillClimbingSementara = new int[timeslotHillClimbing.length][2]; // handle temporary solution. if better than feasible, replace initial
		
		// copy timeslotHillClimbing to timeslotHillClimbingSementara
		for (int i = 0; i < timeslotHillClimbingSementara.length; i ++) {
			timeslotHillClimbingSementara[i][0] = timeslotHillClimbing[i][0]; // fill with course
			timeslotHillClimbingSementara[i][1] = timeslotHillClimbing[i][1]; // fill with timeslot
		}
		
		double penaltiInitialFeasible = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		
		for(int i = 0; i < iterasi; i++) {
			//System.out.println("Jumlah exam : " + jumlahexam);
			//System.out.println("Jumlah murid : " + jumlahmurid);
			try {
				randomCourse = randomNumber(0, jumlahexam); // random course
				randomTimeslot = randomNumber(0, schedule.getHowManyTimeSlot(timeslot)); // random timeslot
				timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
			
				if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbing)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
					
					// compare between penalti. replace initial with after if initial penalti is greater
					if(penaltiInitialFeasible > penaltiAfterHillClimbing) {
						penaltiInitialFeasible = penaltiAfterHillClimbing;
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				//System.out.println("jadwaltemp ke " + randomCourseIndex);
				//System.out.println("Random timeslot ke " + randomTimeslot);
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+penaltiInitialFeasible);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					//System.out.println("randomCourseIndex index ke- " + randomCourseIndex);
					//System.out.println("randomTimeslot index ke- " + randomTimeslot);
				}
			
		}
		
		// print updated timeslot
		System.out.println("\n================================================\n");
    	for (int course_index = 0; course_index < jumlahexam; course_index++)
    		System.out.println("Timeslot untuk course "+ timeslotHillClimbing[course_index][0] +" adalah timeslot: " + timeslotHillClimbing[course_index][1]);       
	    
    	System.out.println("=============================================================");
		System.out.println("		Metode HILL CLIMBING								 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ penaltiInitialFeasible); // print best penalty
		for	(int i = 0; i < timeslotHillClimbing.length; i++) {
			timeslot[i] = timeslotHillClimbing[i][1];
		}
		System.out.println("Timeslot yang dibutuhkan : " + Arrays.stream(timeslot).max().getAsInt() + "\n");
		System.out.println("=============================================================");
		
	}
	
	// another method
	public void getTimeslotBySimulatedAnnealing(double temperature, int iterasi) {
		double coolingrate = 0.001;
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		
		// initial solution
		timeslotSimulatedAnnealing = schedule.getSchedule();
		double initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
		
		int[][] timeslotSimulatedAnnealingSementara = Evaluator.getTimeslot(timeslotSimulatedAnnealing);
		
		for	(int i=0; i < iterasi; i++) {
			int llh = randomNumber(1, 5);
			int[][] timeslotLLH;
			switch (llh) {
				case 1:
					timeslotLLH = LowLevelHeuristics.move(timeslotSimulatedAnnealingSementara, 1);
					break;
				case 2:
					timeslotLLH = LowLevelHeuristics.swap(timeslotSimulatedAnnealingSementara, 2);
					break;
				case 3:
					timeslotLLH = LowLevelHeuristics.move(timeslotSimulatedAnnealingSementara, 2);
					break;
				case 4:
					timeslotLLH = LowLevelHeuristics.swap(timeslotSimulatedAnnealingSementara, 3);
					break;
				case 5:
					timeslotLLH = LowLevelHeuristics.move(timeslotSimulatedAnnealingSementara, 3);
					break;
				default:
					timeslotLLH = LowLevelHeuristics.swap(timeslotSimulatedAnnealingSementara, 1);
					break;
			}
			
			temperature = temperature * (1 - coolingrate);
			if (Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid) <= Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid)) {
				timeslotSimulatedAnnealingSementara = Evaluator.getTimeslot(timeslotLLH);
				if (Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid)) {
					timeslotSimulatedAnnealing = Evaluator.getTimeslot(timeslotLLH);
				}
			}
				else if (acceptanceProbability(Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid), Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid), temperature) > randomDouble())
					timeslotSimulatedAnnealingSementara = Evaluator.getTimeslot(timeslotLLH);
//			System.out.println("acceptance P : " + acceptanceProbability(Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid), Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid), currentTemperature));
//			System.out.println("temperature : " + currentTemperature);
			// print current penalty of each iteration
			System.out.println("Iterasi: " + (i+1) + " memiliki penalty " + Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid));
		}
		
		System.out.println("=============================================================");
		System.out.println("		Metode SIMULATED ANNEALING				 			 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid)); // print best penalty
		for	(int i = 0; i < timeslotSimulatedAnnealing.length; i++) {
			timeslot[i] = timeslotSimulatedAnnealing[i][1];
		}
		System.out.println("Timeslot yang dibutuhkan : " + Arrays.stream(timeslot).max().getAsInt() + "\n");
		System.out.println("=============================================================");
	}
	public void getTimeslotBy() {
		
	}
	
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
	public int[][] getTimeslotSimulatedAnnealing() { return timeslotSimulatedAnnealing; }
	public int[][] getTimeslotMeta() { return timeslotOtherMeta; }
	
	private static int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * this method returns a random number n such that
	 * 0.0 <= n <= 1.0
	 * @return random such that 0.0 <= random <= 1.0
	 */
	private static double randomDouble() {
		Random r = new Random();
		return r.nextInt(1000) / 1000.0;
	}
	
	/**
	 * Calculates the acceptance probability
	 * @param currentDistance the total distance of the current tour
	 * @param newDistance the total distance of the new tour
	 * @param temperature the current temperature
	 * @return value the probability of whether to accept the new tour
	 */
	private static double acceptanceProbability(double currentPenalty, double newPenalty, double temperature) {
		// If the new solution is better, accept it
		if (newPenalty < currentPenalty)
			return 1.0;
		
		// If the new solution is worse, calculate an acceptance probability
		return Math.exp((currentPenalty - newPenalty) / temperature);
	}
}
