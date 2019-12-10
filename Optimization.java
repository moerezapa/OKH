import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Optimization {
	int[][] timeslotHillClimbing, timeslotSimulatedAnnealing, timeslotTabuSearch, conflict_matrix, course_sorted;
	int[] timeslot;
	String file;
	int jumlahexam, jumlahmurid, randomCourse, randomTimeslot;
	double initialPenalty, bestPenalty, deltaPenalty;
	
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
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		//schedule.printSchedule();
		int[][] initialTimeslot = schedule.getSchedule();
		timeslotHillClimbing = Evaluator.getTimeslot(initialTimeslot);
		initialPenalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
		double penalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
		int[][] timeslotHillClimbingSementara = new int[timeslotHillClimbing.length][2]; // handle temporary solution. if better than feasible, replace initial
		
		timeslotHillClimbingSementara = Evaluator.getTimeslot(timeslotHillClimbing);
		
		double bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		for(int i = 0; i < iterasi; i++) {
			//System.out.println("Jumlah exam : " + jumlahexam);
			//System.out.println("Jumlah murid : " + jumlahmurid);
			try {
				randomCourse = randomNumber(0, jumlahexam); // random course
				randomTimeslot = randomNumber(0, schedule.getHowManyTimeSlot(timeslot)); // random timeslot
//				timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
			
				if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbingSementara)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
					
					// compare between penalti. replace initial with after if initial penalti is greater
					if(penalty > penaltiAfterHillClimbing) {
						penalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				//System.out.println("jadwaltemp ke " + randomCourseIndex);
				//System.out.println("Random timeslot ke " + randomTimeslot);
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+ penalty);
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
		System.out.println("Penalty Terbaik : "+ penalty); // print best penalty
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
		timeslot = schedule.schedulingByDegree(course_sorted);
		LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(conflict_matrix);
		
		// initial solution
		timeslotSimulatedAnnealing = schedule.getSchedule();
		initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
		
		int[][] timeslotSimulatedAnnealingSementara = Evaluator.getTimeslot(timeslotSimulatedAnnealing);
		
		for	(int i=0; i < iterasi; i++) {
			int llh = randomNumber(1, 5);
			int[][] timeslotLLH;
			switch (llh) {
				case 1:
					timeslotLLH = lowLevelHeuristics.move1(timeslotSimulatedAnnealingSementara);
					break;
				case 2:
					timeslotLLH = lowLevelHeuristics.swap2(timeslotSimulatedAnnealingSementara);
					break;
				case 3:
					timeslotLLH = lowLevelHeuristics.move2(timeslotSimulatedAnnealingSementara);
					break;
				case 4:
					timeslotLLH = lowLevelHeuristics.swap3(timeslotSimulatedAnnealingSementara);
					break;
				case 5:
					timeslotLLH = lowLevelHeuristics.move3(timeslotSimulatedAnnealingSementara);
					break;
				default:
					timeslotLLH = lowLevelHeuristics.swap2(timeslotSimulatedAnnealingSementara);
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
		bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotSimulatedAnnealing, jumlahmurid);
		deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
		System.out.println("=============================================================");
		System.out.println("		Metode SIMULATED ANNEALING				 			 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : " + bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi");
		for	(int i = 0; i < timeslotSimulatedAnnealing.length; i++) {
			timeslot[i] = timeslotSimulatedAnnealing[i][1];
		}
		System.out.println("Timeslot yang dibutuhkan : " + Arrays.stream(timeslot).max().getAsInt() + "\n");
		System.out.println("=============================================================");
	}
	public void getTimeslotByTabuSearch(int iterasi) {
		/*
		 * need:
		 * conflict matrix
		 * jumlah murid
		 * jumlahcourse
		 * max timeslot
		 * timeslot awal
		 * penalty awal (?)
		 */
		
		//inisiasi random
        Random r = new Random();
        int rindex1,rindex2,rindex3 = 0;
        int rslot1,rslot2,rslot3 = 0;
        
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		// initial solution
		timeslotTabuSearch = schedule.getSchedule();
		double initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
		
		int[][] bestTimeslot = Evaluator.getTimeslot(timeslotTabuSearch); // handle current best timeslot
		int[][] bestcandidate  = Evaluator.getTimeslot(timeslotTabuSearch);
		int[][] timeslotTabuSearchSementara = Evaluator.getTimeslot(timeslotTabuSearch);
		
//		int timeslot_dibutuhkan = Arrays.stream(timeslot).max().getAsInt();
		
		//inisiasi tabulist
        LinkedList<int[][]> tabulist = new LinkedList<int[][]>();
        int maxtabusize = 10;
        tabulist.addLast(Evaluator.getTimeslot(timeslotTabuSearch));
        
      //inisiasi iterasi
        int maxiteration = 1000;
        int iteration=0;
        
      //inisasi itung penalty
        double penalty1 = 0;
        double penalty2 = 0;
        double penalty3 = 0;
        
        boolean terminate = false;
        
        while(!terminate){
            iteration++;
            
//            search candidate solution / search neighbor
//            sneighborhood = getneighbor(bestcandidate)
           ArrayList<int[][]> sneighborhood = new ArrayList<>();
                
              
//        		int[][] timeslotLLH;
        		LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(conflict_matrix);
        		timeslotTabuSearchSementara = lowLevelHeuristics.move1(timeslotTabuSearchSementara);
				sneighborhood.add(timeslotTabuSearchSementara);
				timeslotTabuSearchSementara = lowLevelHeuristics.swap2(timeslotTabuSearchSementara);
				sneighborhood.add(timeslotTabuSearchSementara);
				timeslotTabuSearchSementara = lowLevelHeuristics.move2(timeslotTabuSearchSementara);
				sneighborhood.add(timeslotTabuSearchSementara);
				timeslotTabuSearchSementara = lowLevelHeuristics.swap3(timeslotTabuSearchSementara);
				sneighborhood.add(timeslotTabuSearchSementara);
				timeslotTabuSearchSementara = lowLevelHeuristics.move3(timeslotTabuSearchSementara);
				sneighborhood.add(timeslotTabuSearchSementara);
				
        		
        		//membandingkan neighbor, pilih best neighbor, membandingkan juga apa ada di tabu list
                int j = 0;
                while (sneighborhood.size() > j) {
                	if(!(tabulist.contains(sneighborhood.get(j))) && Evaluator.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahmurid) < Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid))
                        bestcandidate = sneighborhood.get(j);
                	
                   j++;
                }
                
                sneighborhood.clear();
                
                //bandingkan best neighbor dengan best best solution
                if(Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, bestTimeslot, jumlahmurid))
                	timeslotTabuSearch = Evaluator.getTimeslot(bestcandidate);
                
                //masukkan best neighbor tadi ke tabu
                tabulist.addLast(bestcandidate);
                if(tabulist.size() > maxtabusize)
                    tabulist.removeFirst();
                
                //return sbest;
                
                System.out.println("Iterasi: " + (iteration+1) + " memiliki penalty " + Evaluator.getPenalty(conflict_matrix, timeslotTabuSearchSementara, jumlahmurid));
              
            if (iteration == maxiteration)
                terminate = true;
        }
        
        System.out.println("=============================================================");
		System.out.println("		Metode TABU SEARCH						 			 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid)); // print best penalty
		for	(int i = 0; i < timeslotTabuSearch.length; i++) {
			timeslot[i] = timeslotTabuSearch[i][1];
		}
		System.out.println("Timeslot yang dibutuhkan : " + Arrays.stream(timeslot).max().getAsInt() + "\n");
		System.out.println("=============================================================");
	}
	
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
	public int[][] getTimeslotSimulatedAnnealing() { return timeslotSimulatedAnnealing; }
	public int[][] getTimeslotTabuSearch() { return timeslotTabuSearch; }
	
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
