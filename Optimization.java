import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Optimization {
	int[][] timeslotHillClimbing, timeslotSimulatedAnnealing, conflict_matrix, course_sorted;
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
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
		System.out.println("Penalti akhir : " + penaltiInitialFeasible); // print latest penalti
	}
	
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
	
	// another method
	public void getTimeslotBySimulatedAnnealing() {
		
		/*
		 * REFERENSI
		 * https://github.com/nsadawi/simulated-annealing/blob/master/SimulatedAnnealing.java
		 */
		// set initial temp
		double temp = 2;
		
		// set coolingRate
		double coolingRate = 0.0003;
		
		//create random intial solution
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		
		//create random intial timeslot
        int[][] currentTimeslot = schedule.getSchedule();
        
		// asume this is the best solution
		timeslotSimulatedAnnealing = currentTimeslot; 
		
		// get current penalty
		double initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
		System.out.println("Initial penalty is " + initialPenalty);
		
		// loop until temperature has cooled
		while (temp > 1) {		
			int[][] timeslotSimulatedAnnealingSementara = timeslotSimulatedAnnealing;
			
			// Get random positions in the tour
            int courseRandomPosition1 = randomNumber(0 , jumlahexam-1);
            int courseRandomPosition2 = randomNumber(0 , jumlahexam-1);
            System.out.println("Course yang dirandom " + courseRandomPosition1 + " dengan " + courseRandomPosition2);

//            int courseRandom1 = timeslotSimulatedAnnealing[courseRandomPosition1][0];
//            int courseRandom2 = timeslotSimulatedAnnealing[courseRandomPosition2][0];
            // make sure that tourPos1 and tourPos2 are different
    		while(courseRandomPosition1 == courseRandomPosition2)
    			courseRandomPosition2 = randomNumber(0 , jumlahexam-1);
    		
    		// get timeslot which course is swapped
    		int timeslot1 = timeslotSimulatedAnnealing[courseRandomPosition1][1];
    		int timeslot2 = timeslotSimulatedAnnealing[courseRandomPosition2][1];
    		System.out.println("Yang mau di swap");
    		System.out.println("Course " + timeslotSimulatedAnnealing[courseRandomPosition1][0] + " has timeslot: " + timeslotSimulatedAnnealing[courseRandomPosition1][1]);
    		System.out.println("Course " + timeslotSimulatedAnnealing[courseRandomPosition2][0] + " has timeslot: " + timeslotSimulatedAnnealing[courseRandomPosition2][1]);
    		
    		try {
    			timeslotSimulatedAnnealingSementara[courseRandomPosition1][0] = timeslotSimulatedAnnealing[courseRandomPosition2][0];
    			timeslotSimulatedAnnealingSementara[courseRandomPosition2][0] = timeslotSimulatedAnnealing[courseRandomPosition1][0];
    			
    			// swap if not crash
    			if (Schedule.checkRandomTimeslotForSA(courseRandomPosition1, courseRandomPosition2, timeslot1, timeslot2, conflict_matrix, timeslotSimulatedAnnealing)) {	
    				timeslotSimulatedAnnealingSementara[courseRandomPosition1][0] = timeslotSimulatedAnnealing[courseRandomPosition2][0];
    				timeslotSimulatedAnnealingSementara[courseRandomPosition2][0] = timeslotSimulatedAnnealing[courseRandomPosition1][0];
    				double penaltiAfterSimulatedAnnealing = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid);
    				System.out.println("Setelah di swap");
    	    		System.out.println("Course " + timeslotSimulatedAnnealingSementara[courseRandomPosition1][0] + " has timeslot: " + timeslotSimulatedAnnealingSementara[courseRandomPosition1][1]);
    	    		System.out.println("Course " + timeslotSimulatedAnnealingSementara[courseRandomPosition2][0] + " has timeslot: " + timeslotSimulatedAnnealingSementara[courseRandomPosition2][1]);
    	    		
    				// Decide if we should accept the neighbour
    	            double randomNumber = randomDouble();
    	            if (acceptanceProbability(initialPenalty, penaltiAfterSimulatedAnnealing, temp) > randomNumber) {
    	            	currentTimeslot[courseRandomPosition1][0] = timeslotSimulatedAnnealingSementara[courseRandomPosition2][0];
    	            	currentTimeslot[courseRandomPosition2][0] = timeslotSimulatedAnnealingSementara[courseRandomPosition1][0];
    	            	System.out.println("Simpan timeslot sementara dulu");
    	            	System.out.println("current course for swap 1 " + currentTimeslot[courseRandomPosition1][0] + " has timeslot: " + currentTimeslot[courseRandomPosition1][1]);
    	        		System.out.println("current course for swap 2 " + currentTimeslot[courseRandomPosition2][0] + " has timeslot: " + currentTimeslot[courseRandomPosition2][1]);
    	            }
    	            
    				// compare between penalti. replace initial with after if initial penalti is greater
    	            if (initialPenalty > penaltiAfterSimulatedAnnealing) {
    	            	timeslotSimulatedAnnealing = currentTimeslot;
    	            	double penaltinew = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
    	            	System.out.println("current timeslot for swap 3 " + timeslotSimulatedAnnealing[courseRandomPosition1][0]);
    	            	System.out.println("current timeslot for swap 4 " + timeslotSimulatedAnnealing[courseRandomPosition2][0]);
    	            }
    	            	else
    	            		System.out.println("Penalty lebih jelek, timeslotSimulatedAnnealing tidak berubah");
    	           
    			}
    				else {
    					timeslotSimulatedAnnealingSementara[courseRandomPosition1][0] = courseRandomPosition1;
    					timeslotSimulatedAnnealingSementara[courseRandomPosition2][0] = courseRandomPosition2;
					}
    		}
    			catch (Exception e) {
    				System.out.println(e.toString() + " Out of bound soalnya course yang di random: " + courseRandomPosition1 + " sama " + courseRandomPosition2);
    				System.out.println(e.toString() + " Out of bound soalnya timeslot yang di random: " + timeslot1 + " sama " + timeslot2);
    				break;
    			}
    		
    		
    		// swap 3 course
            
			// Cool system
            temp *= 1 - coolingRate;
		}
		
		// get final penalty
		double finalPenalty = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
		System.out.println("Final penalty is " + finalPenalty);
		//System.out.println("Initial penalty is " + initialPenalty);
	}
	
	public void getSimmulatedAnnealing(int temperature) throws IOException {
		double coolingrate = 0.001;
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		  
		//schedule.printSchedule();
		  
		timeslotSimulatedAnnealing = schedule.getSchedule(); // initial feasible solution
		int[][] timeslotSimulatedAnnealingSementara = new int[timeslotSimulatedAnnealing.length][2]; // handle temporary solution. if better than feasible, replace initial
		  
		  // copy timeslotHillClimbing to timeslotHillClimbingSementara
		  for (int i = 0; i < timeslotSimulatedAnnealingSementara.length; i ++) {
			  timeslotSimulatedAnnealingSementara[i][0] = timeslotSimulatedAnnealing[i][0]; // fill with course
			  timeslotSimulatedAnnealingSementara[i][1] = timeslotSimulatedAnnealing[i][1]; // fill with timeslot
		  }
		  
		  double penaltiInitialFeasible = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
		  
		  while (temperature > 1) {
//			  System.out.println("Jumlah exam : " + jumlahexam);
//			  System.out.println("Jumlah murid : " + jumlahmurid);
			  try {
				  randomCourse = randomNumber(0, jumlahexam); // random course
				  randomTimeslot = randomNumber(1, schedule.getHowManyTimeSlot(timeslot)); // random timeslot
				  timeslotSimulatedAnnealingSementara[randomCourse][1] = randomTimeslot;
		   
				  if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotSimulatedAnnealing)) { 
					  timeslotSimulatedAnnealingSementara[randomCourse][1] = randomTimeslot;
					  double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid);
		     
					  // compare between penalti. replace initial with after if initial penalti is greater
					  double randomNumber = randomDouble();
					  if(penaltiInitialFeasible > penaltiAfterHillClimbing) {
					      penaltiInitialFeasible = penaltiAfterHillClimbing;
					      timeslotSimulatedAnnealing[randomCourse][1] = timeslotSimulatedAnnealingSementara[randomCourse][1];
					  }
					  	else if (acceptanceProbability(penaltiInitialFeasible, penaltiAfterHillClimbing, temperature) > randomNumber)
					  		timeslotSimulatedAnnealing[randomCourse][1] = timeslotSimulatedAnnealingSementara[randomCourse][1];
		    }  
				temperature *= 1 - coolingrate;
//		    	System.out.println("jadwaltemp ke " + randomCourseIndex);
//		    	System.out.println("Random timeslot ke " + randomTimeslot);
		    	System.out.println("pada temperatur " + temperature + " memiliki penalti : " + penaltiInitialFeasible);
			  }
		   		catch (ArrayIndexOutOfBoundsException e) {
		   			//System.out.println("randomCourseIndex index ke- " + randomCourseIndex);
		   			//System.out.println("randomTimeslot index ke- " + randomTimeslot);
		   		}
		  }
		  
		  // print updated timeslot
		  System.out.println("\n================================================\n");
		  for (int course_index = 0; course_index < jumlahexam; course_index++)
			  System.out.println("Timeslot untuk course "+ timeslotSimulatedAnnealing[course_index][0] +" adalah timeslot: " + timeslotSimulatedAnnealing[course_index][1]);       
		     
		  System.out.println("\n================================================"); 
		  System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
		  System.out.println("Penalti akhir : " + penaltiInitialFeasible); // print latest penalti
	}
		 
	public int[][] getSimulatedAnnealing() { return timeslotSimulatedAnnealing; }
		 
	public void cobaSimmulatedAnnealing(double temperature, int iterasi) {
		double coolingrate = 0.001;
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		
		int[][] solusi = schedule.getSchedule();
//		int[][] timeslotSimulatedAnnealingSementara = solusi;
		int[][] timeslotSimulatedAnnealingSementara = Evaluator.copyTimeslot(solusi);
		int[][] best = schedule.getSchedule(); // initial feasible solution
		
//		LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(timeslotSimulatedAnnealingSementara);
		
		double currentTemperature = temperature;
		double penaltySementara = 0, penaltyLLH, penaltyBest = 0;
		
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
			
			currentTemperature = currentTemperature * (1 - coolingrate);
			penaltyLLH = Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid);
			if (Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid) <= Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid)) {
				timeslotSimulatedAnnealingSementara = Evaluator.copyTimeslot(timeslotLLH);
//				timeslotSimulatedAnnealingSementara = timeslotLLH; knapa cara kayak gini gabisa buat mindah array?
				penaltySementara = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid);
				if (Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, best, jumlahmurid)) {
					best = Evaluator.copyTimeslot(timeslotLLH);
//					best = timeslotLLH;
					penaltyBest = Evaluator.getPenalty(conflict_matrix, best, jumlahmurid);
				}
			}
				else if (acceptanceProbability(penaltySementara, penaltyLLH, temperature) > randomDouble())
					timeslotSimulatedAnnealingSementara = Evaluator.copyTimeslot(timeslotLLH);
//					timeslotSimulatedAnnealingSementara = timeslotLLH;
			
			System.out.println("Iterasi: " + (i+1) + " memiliki penalty " + Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealingSementara, jumlahmurid));
		}
		
//		penaltyBest = Evaluator.getPenalty(conflict_matrix, best, jumlahmurid);
		System.out.println("Penalty Terbaik : "+ penaltyBest);
		
		timeslotSimulatedAnnealing = best;
	}
	
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
