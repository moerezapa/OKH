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
	public void getTimeslotByHillClimbing(int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid, int iterasi) throws IOException {
		/*Course course = new Course(file);
        int jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = course.getConflictMatrix();
		
		// sort exam by degree
		int[][] course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);*/
		//for (int i=0; i<jumlahexam; i++)
			//System.out.println("Degree of course " + course_sorted[i][0] + " is " + course_sorted[i][1]);
		
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
	
	
	public static int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
	
	
	// another method
}
