import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Optimization {
	int[][] timeslotHillClimbing, conflict_matrix, course_sorted;
	int[] timeslot;
	String file;
	int jumlahexam, randomCourse, randomTimeslot;
	
	Optimization(String file) { this.file = file; }
	
	// hill climbing method
	public void getTimeslotByHillClimbing(int iterasi) throws IOException {
		Course course = new Course(file);
        int jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = course.getConflictMatrix();
		
		// sort exam by degree
		int[][] course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		//for (int i=0; i<jumlahexam; i++)
			//System.out.println("Degree of course " + course_sorted[i][0] + " is " + course_sorted[i][1]);
		
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		//int[] timeslot = new int[jumlahexam];
		
		// start time
		//long starttime = System.nanoTime();
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		//long endtime = System.nanoTime();
		// end time
		//double runningtime = (double) (endtime - starttime)/1000000000;
		
		schedule.printSchedule();
		
		timeslotHillClimbing = schedule.getSchedule();
		int[][] timeslotHillClimbingSementara = new int[timeslotHillClimbing.length][2];
		
		// copy timeslotHillClimbing to timeslotHillClimbingSementara
		for (int i = 0; i < timeslotHillClimbingSementara.length; i ++) {
			timeslotHillClimbingSementara[i][0] = timeslotHillClimbing[i][0]; // fill with course
			timeslotHillClimbingSementara[i][1] = timeslotHillClimbing[i][1]; // fill with timeslot
		}
		
		double penalti = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, course.getJumlahMurid());
		
		for(int i = 0; i < iterasi; i++) {
			try {
				randomCourse = getRandomNumber(0, jumlahexam);
				randomTimeslot = getRandomNumber(0, schedule.getHowManyTimeSlot(timeslot));
				timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
			
				if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbing)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penalti2 = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, course.getJumlahMurid());
					// compare between penalti
					if(penalti > penalti2) {
						penalti = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, course.getJumlahMurid());
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				//System.out.println("jadwaltemp ke " + randomCourseIndex);
				//System.out.println("Random timeslot ke " + randomTimeslot);
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+penalti);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					//System.out.println("randomCourseIndex index ke- " + randomCourseIndex);
					//System.out.println("randomTimeslot index ke- " + randomTimeslot);
				}
			
		}
		
		// print updated timeslot
		System.out.println("\n================================================\n");
    	for (int i = 0; i < jumlahexam; i++)
    		System.out.println("Timeslot untuk course "+ timeslotHillClimbing[i][0] +" adalah timeslot: " + timeslotHillClimbing[i][1]);       
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
		System.out.println("Penalti akhir : " + penalti); // print latest penalti
	}
	
	
	public static int getRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
	
	
	// another method
}
